package com.ruoyi.web.app.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 微信支付（小程序 JSAPI，APIv2）
 *
 * 设计：未配置商户号/密钥时 {@link #enabled()} 返回 false，业务层走演示模式（不触发真实支付）；
 *      配置后自动启用：统一下单换 prepay_id、生成小程序 wx.requestPayment 参数、校验支付回调签名。
 * 全程仅用 JDK（MD5 + HttpURLConnection），无第三方依赖。商户密钥后期通过环境变量 WX_* 注入即可。
 *
 * @author liwangfu
 */
@Service
public class WxPayService
{
    private static final Logger log = LoggerFactory.getLogger(WxPayService.class);

    /** 统一下单接口（APIv2） */
    private static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /** 退款接口（APIv2，需商户证书双向认证） */
    private static final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    @Value("${wx.appid:}")
    private String appid;

    @Value("${wx.pay.mchId:}")
    private String mchId;

    @Value("${wx.pay.apiKey:}")
    private String apiKey;

    @Value("${wx.pay.notifyUrl:}")
    private String notifyUrl;

    @Value("${wx.pay.certPath:}")
    private String certPath;

    /** 是否已配置（appid + 商户号 + API密钥 + 回调地址 齐全才算启用真实支付） */
    public boolean enabled()
    {
        return StringUtils.isNotEmpty(appid) && StringUtils.isNotEmpty(mchId)
                && StringUtils.isNotEmpty(apiKey) && StringUtils.isNotEmpty(notifyUrl);
    }

    /** 是否可发起微信退款（在已启用支付基础上，还需配置商户证书 p12 路径） */
    public boolean refundEnabled()
    {
        return enabled() && StringUtils.isNotEmpty(certPath);
    }

    /**
     * 微信退款（APIv2，全额或部分）。需商户证书。
     *
     * @param orderNo   原商户订单号
     * @param totalFen  原订单总额（分）
     * @param refundFen 退款额（分）
     */
    public Map<String, String> refund(String orderNo, int totalFen, int refundFen)
    {
        if (!refundEnabled())
        {
            throw new ServiceException("微信退款未配置（缺少商户证书 WX_CERT_PATH）");
        }
        SortedMap<String, String> params = new TreeMap<>();
        params.put("appid", appid);
        params.put("mch_id", mchId);
        params.put("nonce_str", nonceStr());
        params.put("out_trade_no", orderNo);
        params.put("out_refund_no", "R" + orderNo);
        params.put("total_fee", String.valueOf(totalFen));
        params.put("refund_fee", String.valueOf(refundFen));
        params.put("sign", sign(params));

        String resp = postXmlWithCert(REFUND_URL, toXml(params));
        Map<String, String> result = parseXml(resp);
        if (!"SUCCESS".equals(result.get("return_code")) || !"SUCCESS".equals(result.get("result_code")))
        {
            String msg = result.get("err_code_des") != null ? result.get("err_code_des") : result.get("return_msg");
            throw new ServiceException("微信退款失败：" + msg);
        }
        return result;
    }

    /**
     * 统一下单并返回小程序 wx.requestPayment 所需参数。
     *
     * @param orderNo  商户订单号（out_trade_no）
     * @param body     商品描述
     * @param payYuan  支付金额（元）
     * @param openid   支付用户 openid
     * @param clientIp 终端IP
     * @return {timeStamp,nonceStr,package,signType,paySign}
     */
    public Map<String, String> jsapiPay(String orderNo, String body, BigDecimal payYuan, String openid, String clientIp)
    {
        if (!enabled())
        {
            throw new ServiceException("微信支付未配置");
        }
        if (StringUtils.isEmpty(openid))
        {
            throw new ServiceException("缺少 openid，请用微信登录后再支付");
        }
        int totalFee = payYuan.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue();
        if (totalFee <= 0)
        {
            totalFee = 1; // 微信最小支付金额 1 分（0 元订单一般不应走支付，这里兜底）
        }

        // 1. 组装统一下单参数并签名
        SortedMap<String, String> params = new TreeMap<>();
        params.put("appid", appid);
        params.put("mch_id", mchId);
        params.put("nonce_str", nonceStr());
        params.put("body", body);
        params.put("out_trade_no", orderNo);
        params.put("total_fee", String.valueOf(totalFee));
        params.put("spbill_create_ip", StringUtils.isEmpty(clientIp) ? "127.0.0.1" : clientIp);
        params.put("notify_url", notifyUrl);
        params.put("trade_type", "JSAPI");
        params.put("openid", openid);
        params.put("sign", sign(params));

        // 2. 调用统一下单
        String resp = postXml(UNIFIED_ORDER_URL, toXml(params));
        Map<String, String> result = parseXml(resp);
        if (!"SUCCESS".equals(result.get("return_code")))
        {
            throw new ServiceException("微信下单失败：" + result.get("return_msg"));
        }
        if (!"SUCCESS".equals(result.get("result_code")))
        {
            throw new ServiceException("微信下单失败：" + result.get("err_code_des"));
        }
        String prepayId = result.get("prepay_id");
        if (StringUtils.isEmpty(prepayId))
        {
            throw new ServiceException("微信下单未返回 prepay_id");
        }

        // 3. 生成小程序唤起支付参数（再次签名）
        SortedMap<String, String> pay = new TreeMap<>();
        pay.put("appId", appid);
        pay.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        pay.put("nonceStr", nonceStr());
        pay.put("package", "prepay_id=" + prepayId);
        pay.put("signType", "MD5");
        String paySign = sign(pay);

        // 返回给前端（appId 前端不需要，wx.requestPayment 只用下面 5 项）
        SortedMap<String, String> out = new TreeMap<>();
        out.put("timeStamp", pay.get("timeStamp"));
        out.put("nonceStr", pay.get("nonceStr"));
        out.put("package", pay.get("package"));
        out.put("signType", "MD5");
        out.put("paySign", paySign);
        return out;
    }

    /** 解析支付回调 XML 为 Map */
    public Map<String, String> parseNotify(String xmlBody)
    {
        return parseXml(xmlBody);
    }

    /** 校验回调签名是否合法 */
    public boolean verifyNotifySign(Map<String, String> params)
    {
        if (params == null || StringUtils.isEmpty(params.get("sign")))
        {
            return false;
        }
        SortedMap<String, String> sorted = new TreeMap<>(params);
        String received = sorted.remove("sign");
        return received.equals(sign(sorted));
    }

    /** 回调成功应答 XML */
    public String notifySuccessXml()
    {
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    /** 回调失败应答 XML（微信会重试） */
    public String notifyFailXml(String msg)
    {
        return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[" + msg + "]]></return_msg></xml>";
    }

    /* ===================== 内部工具 ===================== */

    /** APIv2 MD5 签名：参数升序拼接 + &key=密钥，MD5 后转大写 */
    private String sign(SortedMap<String, String> params)
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : params.entrySet())
        {
            if ("sign".equals(e.getKey()) || StringUtils.isEmpty(e.getValue()))
            {
                continue;
            }
            sb.append(e.getKey()).append('=').append(e.getValue()).append('&');
        }
        sb.append("key=").append(apiKey);
        return md5(sb.toString()).toUpperCase();
    }

    private String md5(String text)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest)
            {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1)
                {
                    hex.append('0');
                }
                hex.append(h);
            }
            return hex.toString();
        }
        catch (Exception e)
        {
            throw new ServiceException("MD5 签名失败");
        }
    }

    private String nonceStr()
    {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < 32; i++)
        {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String toXml(Map<String, String> params)
    {
        StringBuilder sb = new StringBuilder("<xml>");
        for (Map.Entry<String, String> e : params.entrySet())
        {
            sb.append('<').append(e.getKey()).append("><![CDATA[").append(e.getValue())
              .append("]]></").append(e.getKey()).append('>');
        }
        return sb.append("</xml>").toString();
    }

    private Map<String, String> parseXml(String xml)
    {
        Map<String, String> map = new TreeMap<>();
        if (StringUtils.isEmpty(xml))
        {
            return map;
        }
        // 只匹配叶子标签：<tag><![CDATA[val]]></tag> 或 <tag>val</tag>
        // （用 [^<] 保证不把外层 <xml> 包裹整体吞掉）
        Matcher m = Pattern.compile("<(\\w+)>(?:<!\\[CDATA\\[(.*?)\\]\\]>|([^<]*))</\\1>", Pattern.DOTALL).matcher(xml);
        while (m.find())
        {
            map.put(m.group(1), m.group(2) != null ? m.group(2) : m.group(3));
        }
        return map;
    }

    /** 带商户证书(p12)的双向 TLS POST，用于退款等 secapi 接口；证书密码默认为商户号 */
    private String postXmlWithCert(String url, String xmlBody)
    {
        HttpsURLConnection conn = null;
        try (FileInputStream certStream = new FileInputStream(certPath))
        {
            char[] pwd = mchId.toCharArray();
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(certStream, pwd);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, pwd);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), null, new SecureRandom());

            conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setSSLSocketFactory(ctx.getSocketFactory());
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            try (OutputStream os = conn.getOutputStream())
            {
                os.write(xmlBody.getBytes(StandardCharsets.UTF_8));
            }
            StringBuilder resp = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    resp.append(line);
                }
            }
            return resp.toString();
        }
        catch (Exception e)
        {
            log.error("微信退款(带证书)请求失败: {}", e.getMessage());
            throw new ServiceException("微信退款请求失败：" + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }
        }
    }

    private String postXml(String url, String xmlBody)
    {
        HttpURLConnection conn = null;
        try
        {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            try (OutputStream os = conn.getOutputStream())
            {
                os.write(xmlBody.getBytes(StandardCharsets.UTF_8));
            }
            StringBuilder resp = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    resp.append(line);
                }
            }
            return resp.toString();
        }
        catch (Exception e)
        {
            log.error("微信支付请求失败: {}", e.getMessage());
            throw new ServiceException("微信支付请求失败，请稍后重试");
        }
        finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }
        }
    }
}
