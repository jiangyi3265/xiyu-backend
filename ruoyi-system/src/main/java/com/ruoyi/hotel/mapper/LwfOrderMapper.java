package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfOrder;

/**
 * 订单Mapper接口
 *
 * @author liwangfu
 */
public interface LwfOrderMapper
{
    public LwfOrder selectLwfOrderByOrderId(Long orderId);

    public List<LwfOrder> selectLwfOrderList(LwfOrder lwfOrder);

    public int insertLwfOrder(LwfOrder lwfOrder);

    public int updateLwfOrder(LwfOrder lwfOrder);

    public int deleteLwfOrderByOrderId(Long orderId);

    public int deleteLwfOrderByOrderIds(Long[] orderIds);
}
