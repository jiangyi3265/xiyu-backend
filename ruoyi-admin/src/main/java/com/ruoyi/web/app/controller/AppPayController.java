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
 * 微信支付回调（匿名，由微信服务器调用）。验签通过后结算订单并应答。
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

    /** 支付结果通知（APIv2 XML） */
    @PostMapping(value = "/notify", produces = "application/xml;charset=UTF-8")
    public String notify(HttpServletRequest request)
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
            Map<String, String> data = wxPayService.parseNotify(sb.toString());
            if (!"SUCCESS".equals(data.get("return_code")) || !"SUCCESS".equals(data.get("result_code")))
            {
                return wxPayService.notifyFailXml("支付未成功");
            }
            if (!wxPayService.verifyNotifySign(data))
            {
                log.warn("微信支付回调验签失败 out_trade_no={}", data.get("out_trade_no"));
                return wxPayService.notifyFailXml("签名校验失败");
            }
            boolean ok = appBizService.markPaid(data.get("out_trade_no"));
            return ok ? wxPayService.notifySuccessXml() : wxPayService.notifyFailXml("订单处理失败");
        }
        catch (Exception e)
        {
            log.error("微信支付回调异常", e);
            return wxPayService.notifyFailXml("系统异常");
        }
    }
}
