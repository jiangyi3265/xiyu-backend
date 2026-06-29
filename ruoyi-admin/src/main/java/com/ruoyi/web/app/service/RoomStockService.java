package com.ruoyi.web.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.hotel.mapper.LwfRoomStockMapper;

/**
 * 房型库存（按日期防超卖）：下单锁定、取消/退款/超时释放、可售量查询。
 * 可售量 = 房型每日 stock - 当日已售 sold；区间可售取各晚最小值。
 *
 * @author liwangfu
 */
@Service
public class RoomStockService
{
    @Autowired
    private LwfRoomStockMapper stockMapper;

    /**
     * 占用库存：入住到离店之间每一晚，用 SQL 级原子条件更新（sold+count&lt;=stock）占位。
     * 任一晚订满则回退已占的前几晚并整单拒绝。日期非法时跳过（不做库存控制）。
     * 不依赖事务/应用层锁，靠 InnoDB 对单行 UPDATE 的并发串行化彻底防超卖。
     */
    public void reserve(Long roomId, String checkIn, String checkOut, int count)
    {
        List<String> nights = nights(checkIn, checkOut);
        if (roomId == null || nights.isEmpty())
        {
            return;
        }
        List<String> reserved = new ArrayList<>();
        for (String d : nights)
        {
            if (!reserveOneNight(roomId, d, count))
            {
                // 该晚订满：回退本单已占的前几晚，整单失败
                for (String r : reserved)
                {
                    stockMapper.reduceSold(roomId, r, count);
                }
                throw new ServiceException("所选日期（" + d + "）房型已订满，请调整日期或房型");
            }
            reserved.add(d);
        }
    }

    /** 占用某一晚：成功 true / 订满 false；遇并发死锁自动重试，避免把原始 SQL 异常抛给前端 */
    private boolean reserveOneNight(Long roomId, String date, int count)
    {
        for (int attempt = 1; attempt <= 5; attempt++)
        {
            try
            {
                stockMapper.ensureRow(roomId, date);
                return stockMapper.tryReserve(roomId, date, count) > 0;
            }
            catch (ConcurrencyFailureException e)
            {
                // 并发死锁/锁等待属瞬时冲突，短暂退避后重试
                if (attempt == 5)
                {
                    throw new ServiceException("当前预订较火爆，请稍后重试");
                }
                try
                {
                    Thread.sleep(20L * attempt);
                }
                catch (InterruptedException ie)
                {
                    Thread.currentThread().interrupt();
                    throw new ServiceException("预订被中断，请重试");
                }
            }
        }
        return false;
    }

    /** 释放库存（取消/退款/超时） */
    public void release(Long roomId, String checkIn, String checkOut, int count)
    {
        if (roomId == null)
        {
            return;
        }
        for (String d : nights(checkIn, checkOut))
        {
            stockMapper.reduceSold(roomId, d, count);
        }
    }

    /** 区间内最小可售量（即可订房数）；日期非法时返回每日 stock */
    public int availability(Long roomId, int dailyStock, String checkIn, String checkOut)
    {
        List<String> nights = nights(checkIn, checkOut);
        if (roomId == null || nights.isEmpty())
        {
            return dailyStock;
        }
        int min = dailyStock;
        for (String d : nights)
        {
            min = Math.min(min, dailyStock - sold(roomId, d));
        }
        return Math.max(0, min);
    }

    private int sold(Long roomId, String date)
    {
        Integer s = stockMapper.selectSold(roomId, date);
        return s == null ? 0 : s;
    }

    /** [checkIn, checkOut) 每一晚的日期串（离店当天不占用） */
    private List<String> nights(String checkIn, String checkOut)
    {
        List<String> list = new ArrayList<>();
        if (StringUtils.isEmpty(checkIn) || StringUtils.isEmpty(checkOut))
        {
            return list;
        }
        try
        {
            LocalDate in = LocalDate.parse(checkIn.trim());
            LocalDate out = LocalDate.parse(checkOut.trim());
            for (LocalDate d = in; d.isBefore(out); d = d.plusDays(1))
            {
                list.add(d.toString());
            }
        }
        catch (Exception ignored)
        {
            // 日期格式非法则视为无库存控制
        }
        return list;
    }
}
