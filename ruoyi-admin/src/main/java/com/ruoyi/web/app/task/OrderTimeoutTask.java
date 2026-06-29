package com.ruoyi.web.app.task;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ruoyi.hotel.domain.LwfOrder;
import com.ruoyi.hotel.service.ILwfOrderService;
import com.ruoyi.web.app.service.AppBizService;
import com.ruoyi.web.app.service.WxPayService;

/**
 * 订单超时自动关闭：每 5 分钟扫描，关闭超过 30 分钟仍未支付（pay）的订单。
 * 仅在已启用微信支付时生效——演示模式下订单不会停留在待支付，故跳过，避免误动演示数据。
 *
 * @author liwangfu
 */
@Component
public class OrderTimeoutTask
{
    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutTask.class);

    /** 未支付超时阈值（分钟），可用环境变量 ORDER_UNPAID_TIMEOUT_MINUTES 覆盖，默认 30 */
    @Value("${order.unpaid-timeout-minutes:30}")
    private long timeoutMinutes;

    @Autowired
    private ILwfOrderService orderService;
    @Autowired
    private AppBizService appBizService;
    @Autowired
    private WxPayService wxPayService;

    /** 扫描频率（毫秒），可用环境变量 ORDER_TIMEOUT_CHECK_RATE_MS 覆盖，默认 5 分钟 */
    @Scheduled(fixedRateString = "${order.timeout-check-rate-ms:300000}")
    public void closeUnpaidOrders()
    {
        if (!wxPayService.enabled())
        {
            return;
        }
        LwfOrder q = new LwfOrder();
        q.setStatus("pay");
        List<LwfOrder> list = orderService.selectLwfOrderList(q);
        long now = System.currentTimeMillis();
        int closed = 0;
        for (LwfOrder o : list)
        {
            Date ct = o.getCreateTime();
            if (ct == null)
            {
                continue;
            }
            if (now - ct.getTime() > timeoutMinutes * 60 * 1000)
            {
                // 复用取消逻辑：客房订单会一并释放库存
                appBizService.cancelOrder(o.getMemberId(), o.getOrderId());
                closed++;
            }
        }
        if (closed > 0)
        {
            log.info("订单超时自动关闭 {} 单", closed);
        }
    }
}
