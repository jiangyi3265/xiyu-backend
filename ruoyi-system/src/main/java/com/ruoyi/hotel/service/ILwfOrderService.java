package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfOrder;

/**
 * 订单Service接口
 *
 * @author liwangfu
 */
public interface ILwfOrderService
{
    public LwfOrder selectLwfOrderByOrderId(Long orderId);

    public List<LwfOrder> selectLwfOrderList(LwfOrder lwfOrder);

    public int insertLwfOrder(LwfOrder lwfOrder);

    public int updateLwfOrder(LwfOrder lwfOrder);

    public int deleteLwfOrderByOrderIds(Long[] orderIds);

    public int deleteLwfOrderByOrderId(Long orderId);
}
