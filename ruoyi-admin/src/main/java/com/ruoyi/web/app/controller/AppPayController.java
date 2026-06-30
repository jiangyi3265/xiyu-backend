package com.ruoyi.web.app.controller;

import java.io.BufferedReader;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.web.app.service.AppBizService;
import com.ruoyi.web.app.service.WxPayService;

/**
 * 微信支付 APIv3 回调（匿名，由微信服务器调用）。验签、解密、核对金额后结算订单并应答。
 *
 * @author liwangfu
 */
@Anonymous
@RestController
@RequestMapping("/app/pay")
public class AppPayController
{
    private static final Logger log = LoggerFactory.getLogger(AppPayController.class);

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private AppBizService appBizService;

    /** 支付结果通知（APIv3 JSON） */
    @PostMapping(value = "/notify", produces = "application/json;charset=UTF-8")
    public Map<String, String> notify(HttpServletRequest request)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = request.getReader())
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                }
            }
            WxPayService.PayNotify data = wxPayService.parseAndVerifyNotify(sb.toString(), request);
            boolean ok = appBizService.markPaid(data.getOutTradeNo(), data.getPaidFen(), data.getTransactionId());
            return ok ? wxPayService.notifySuccess() : wxPayService.notifyFail("订单处理失败");
        }
        catch (Exception e)
        {
            log.error("微信支付回调异常", e);
            return wxPayService.notifyFail("系统异常");
        }
    }
}
