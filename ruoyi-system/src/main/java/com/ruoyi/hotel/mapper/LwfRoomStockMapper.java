package com.ruoyi.hotel.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 房型每日库存 Mapper（按天记录已售量，原子占用防超卖）
 *
 * @author liwangfu
 */
public interface LwfRoomStockMapper
{
    /** 查某房型某天已售数量（无记录返回 null） */
    Integer selectSold(@Param("roomId") Long roomId, @Param("date") String date);

    /** 确保库存行存在（不存在则建 sold=0），配合 tryReserve 使用 */
    int ensureRow(@Param("roomId") Long roomId, @Param("date") String date);

    /**
     * 原子占用：仅当 当日已售 + count 不超过房型每日库存 时才增加已售。
     * 返回影响行数：1=占用成功，0=已订满。InnoDB 对该行的并发 UPDATE 串行化，故无超卖。
     */
    int tryReserve(@Param("roomId") Long roomId, @Param("date") String date, @Param("count") int count);

    /** 减少某天已售（下限 0），用于取消/退款/超时释放库存 */
    int reduceSold(@Param("roomId") Long roomId, @Param("date") String date, @Param("count") int count);
}
