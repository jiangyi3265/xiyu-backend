package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfOrderMapper;
import com.ruoyi.hotel.domain.LwfOrder;
import com.ruoyi.hotel.service.ILwfOrderService;

/**
 * 订单Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfOrderServiceImpl implements ILwfOrderService
{
    @Autowired
    private LwfOrderMapper lwfOrderMapper;

    @Override
    public LwfOrder selectLwfOrderByOrderId(Long orderId)
    {
        return lwfOrderMapper.selectLwfOrderByOrderId(orderId);
    }

    @Override
    public List<LwfOrder> selectLwfOrderList(LwfOrder lwfOrder)
    {
        return lwfOrderMapper.selectLwfOrderList(lwfOrder);
    }

    @Override
    public int insertLwfOrder(LwfOrder lwfOrder)
    {
        return lwfOrderMapper.insertLwfOrder(lwfOrder);
    }

    @Override
    public int updateLwfOrder(LwfOrder lwfOrder)
    {
        return lwfOrderMapper.updateLwfOrder(lwfOrder);
    }

    @Override
    public int deleteLwfOrderByOrderIds(Long[] orderIds)
    {
        return lwfOrderMapper.deleteLwfOrderByOrderIds(orderIds);
    }

    @Override
    public int deleteLwfOrderByOrderId(Long orderId)
    {
        return lwfOrderMapper.deleteLwfOrderByOrderId(orderId);
    }
}
