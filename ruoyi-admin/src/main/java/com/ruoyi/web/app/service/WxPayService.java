package com.ruoyi.web.app.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 微信支付 APIv3：小程序 JSAPI 下单、调起支付参数签名、支付通知验签/解密、退款。
 */
@Service
public class WxPayService
{
    private static final Logger log = LoggerFactory.getLogger(WxPayService.class);

    private static final String JSAPI_URL = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    private static final String REFUND_URL = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";

    @Value("${wx.appid:}")
    private String appid;

    @Value("${wx.pay.mchId:}")
    private String mchId;

    @Value("${wx.pay.merchantSerialNo:}")
    private String merchantSerialNo;

    @Value("${wx.pay.privateKeyPath:}")
    private String privateKeyPath;

    @Value("${wx.pay.privateKey:}")
    private String privateKeyText;

    @Value("${wx.pay.apiV3Key:}")
    private String apiV3Key;

    @Value("${wx.pay.notifyUrl:}")
    private String notifyUrl;

    @Value("${wx.pay.platformCertPath:}")
    private String platformCertPath;

    /** 兼容微信支付公钥模式：配置公钥文件时同时配置 publicKeyId，用于匹配 Wechatpay-Serial。 */
    @Value("${wx.pay.platformPublicKeyPath:}")
    private String platformPublicKeyPath;

    @Value("${wx.pay.platformPublicKeyId:}")
    private String platformPublicKeyId;

    private volatile PrivateKey merchantPrivateKey;
    private volatile PublicKey platformPublicKey;
    private volatile String platformVerifierSerial;

    public boolean enabled()
    {
        return StringUtils.isNotEmpty(appid) && StringUtils.isNotEmpty(mchId)
                && StringUtils.isNotEmpty(merchantSerialNo) && hasMerchantPrivateKey()
                && StringUtils.isNotEmpty(apiV3Key) && StringUtils.isNotEmpty(notifyUrl)
                && hasPlatformVerifier();
    }

    public void ensureReady()
    {
        if (!enabled())
        {
            throw new ServiceException("微信支付未配置完整，请先配置商户号、商户API证书私钥、APIv3密钥、回调地址和微信支付平台证书/公钥");
        }
    }

    public boolean refundEnabled()
    {
        return enabled();
    }

    public Map<String, String> refund(String orderNo, int totalFen, int refundFen)
    {
        return refund(orderNo, "R" + orderNo, totalFen, refundFen);
    }

    public Map<String, String> refund(String orderNo, String outRefundNo, int totalFen, int refundFen)
    {
        ensureReady();
        Map<String, Object> amount = new LinkedHashMap<>();
        amount.put("refund", refundFen);
        amount.put("total", totalFen);
        amount.put("currency", "CNY");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("out_trade_no", orderNo);
        body.put("out_refund_no", outRefundNo);
        body.put("reason", "用户申请退款");
        body.put("amount", amount);

        JSONObject resp = postJson(REFUND_URL, "/v3/refund/domestic/refunds", JSON.toJSONString(body));
        Map<String, String> out = new LinkedHashMap<>();
        out.put("refund_id", resp.getString("refund_id"));
        out.put("out_refund_no", resp.getString("out_refund_no"));
        out.put("status", resp.getString("status"));
        return out;
    }

    /**
     * JSAPI 下单并返回小程序 uni.requestPayment/wx.requestPayment 参数。
     */
    public Map<String, String> jsapiPay(String orderNo, String body, BigDecimal payYuan, String openid, String clientIp)
    {
        ensureReady();
        if (StringUtils.isEmpty(openid))
        {
            throw new ServiceException("缺少 openid，请用微信登录后再支付");
        }
        int totalFee = yuanToFen(payYuan);
        if (totalFee <= 0)
        {
            throw new ServiceException("订单金额为 0，无需发起微信支付");
        }

        Map<String, Object> amount = new LinkedHashMap<>();
        amount.put("total", totalFee);
        amount.put("currency", "CNY");

        Map<String, Object> payer = new LinkedHashMap<>();
        payer.put("openid", openid);

        Map<String, Object> req = new LinkedHashMap<>();
        req.put("appid", appid);
        req.put("mchid", mchId);
        req.put("description", trimPayDescription(body));
        req.put("out_trade_no", orderNo);
        req.put("notify_url", notifyUrl);
        req.put("amount", amount);
        req.put("payer", payer);

        JSONObject resp = postJson(JSAPI_URL, "/v3/pay/transactions/jsapi", JSON.toJSONString(req));
        String prepayId = resp.getString("prepay_id");
        if (StringUtils.isEmpty(prepayId))
        {
            throw new ServiceException("微信下单未返回 prepay_id");
        }

        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = nonceStr();
        String pkg = "prepay_id=" + prepayId;
        String paySign = rsaSign(appid + "\n" + timeStamp + "\n" + nonceStr + "\n" + pkg + "\n");

        Map<String, String> out = new LinkedHashMap<>();
        out.put("timeStamp", timeStamp);
        out.put("nonceStr", nonceStr);
        out.put("package", pkg);
        out.put("signType", "RSA");
        out.put("paySign", paySign);
        return out;
    }

    public PayNotify parseAndVerifyNotify(String body, HttpServletRequest request)
    {
        ensureReady();
        verifyWechatpaySignature(body, request);
        JSONObject root = JSON.parseObject(body);
        JSONObject resource = root.getJSONObject("resource");
        if (resource == null)
        {
            throw new ServiceException("微信支付通知缺少 resource");
        }
        JSONObject plain = JSON.parseObject(decryptResource(resource));
        if (!appid.equals(plain.getString("appid")) || !mchId.equals(plain.getString("mchid")))
        {
            throw new ServiceException("微信支付通知商户信息不匹配");
        }
        if (!"SUCCESS".equals(plain.getString("trade_state")))
        {
            throw new ServiceException("微信支付未成功：" + plain.getString("trade_state"));
        }
        JSONObject amount = plain.getJSONObject("amount");
        Integer paidFen = amount == null ? null : amount.getInteger("payer_total");
        if (paidFen == null)
        {
            paidFen = amount == null ? null : amount.getInteger("total");
        }
        PayNotify notify = new PayNotify();
        notify.setOutTradeNo(plain.getString("out_trade_no"));
        notify.setTransactionId(plain.getString("transaction_id"));
        notify.setPaidFen(paidFen);
        return notify;
    }

    public Map<String, String> notifySuccess()
    {
        Map<String, String> r = new LinkedHashMap<>();
        r.put("code", "SUCCESS");
        r.put("message", "成功");
        return r;
    }

    public Map<String, String> notifyFail(String msg)
    {
        Map<String, String> r = new LinkedHashMap<>();
        r.put("code", "FAIL");
        r.put("message", msg);
        return r;
    }

    public int yuanToFen(BigDecimal yuan)
    {
        if (yuan == null)
        {
            return 0;
        }
        return yuan.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private JSONObject postJson(String url, String canonicalUrl, String body)
    {
        HttpURLConnection conn = null;
        try
        {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", authorization("POST", canonicalUrl, body));
            try (OutputStream os = conn.getOutputStream())
            {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
            String resp = readResponse(conn);
            int code = conn.getResponseCode();
            if (code < 200 || code >= 300)
            {
                JSONObject err = StringUtils.isEmpty(resp) ? new JSONObject() : JSON.parseObject(resp);
                String msg = err.getString("message");
                throw new ServiceException("微信支付请求失败：" + (StringUtils.isNotEmpty(msg) ? msg : resp));
            }
            return JSON.parseObject(resp);
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            log.error("微信支付请求异常", e);
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

    private String authorization(String method, String canonicalUrl, String body)
    {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = nonceStr();
        String message = method + "\n" + canonicalUrl + "\n" + timestamp + "\n" + nonce + "\n" + body + "\n";
        String signature = rsaSign(message);
        return "WECHATPAY2-SHA256-RSA2048 mchid=\"" + mchId + "\",nonce_str=\"" + nonce
                + "\",timestamp=\"" + timestamp + "\",serial_no=\"" + merchantSerialNo
                + "\",signature=\"" + signature + "\"";
    }

    private void verifyWechatpaySignature(String body, HttpServletRequest request)
    {
        String serial = request.getHeader("Wechatpay-Serial");
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String signature = request.getHeader("Wechatpay-Signature");
        if (StringUtils.isEmpty(serial) || StringUtils.isEmpty(timestamp)
                || StringUtils.isEmpty(nonce) || StringUtils.isEmpty(signature))
        {
            throw new ServiceException("微信支付通知缺少签名头");
        }
        if (!serial.equalsIgnoreCase(getPlatformVerifierSerial()))
        {
            throw new ServiceException("微信支付平台证书/公钥序列号不匹配");
        }
        long ts = Long.parseLong(timestamp);
        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - ts) > 300)
        {
            throw new ServiceException("微信支付通知时间戳超时");
        }
        String message = timestamp + "\n" + nonce + "\n" + body + "\n";
        try
        {
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(getPlatformPublicKey());
            verifier.update(message.getBytes(StandardCharsets.UTF_8));
            if (!verifier.verify(Base64.getDecoder().decode(signature)))
            {
                throw new ServiceException("微信支付通知签名校验失败");
            }
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("微信支付通知签名校验失败");
        }
    }

    private String decryptResource(JSONObject resource)
    {
        try
        {
            String algorithm = resource.getString("algorithm");
            if (!"AEAD_AES_256_GCM".equals(algorithm))
            {
                throw new ServiceException("不支持的微信支付通知加密算法：" + algorithm);
            }
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec key = new SecretKeySpec(apiV3Key.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec spec = new GCMParameterSpec(128, resource.getString("nonce").getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            String aad = resource.getString("associated_data");
            if (aad != null)
            {
                cipher.updateAAD(aad.getBytes(StandardCharsets.UTF_8));
            }
            byte[] plain = cipher.doFinal(Base64.getDecoder().decode(resource.getString("ciphertext")));
            return new String(plain, StandardCharsets.UTF_8);
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("微信支付通知解密失败");
        }
    }

    private String rsaSign(String message)
    {
        try
        {
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(getMerchantPrivateKey());
            signer.update(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signer.sign());
        }
        catch (Exception e)
        {
            throw new ServiceException("微信支付签名失败");
        }
    }

    private PrivateKey getMerchantPrivateKey()
    {
        if (merchantPrivateKey == null)
        {
            synchronized (this)
            {
                if (merchantPrivateKey == null)
                {
                    merchantPrivateKey = loadPrivateKey();
                }
            }
        }
        return merchantPrivateKey;
    }

    private PrivateKey loadPrivateKey()
    {
        try
        {
            String pem = StringUtils.isNotEmpty(privateKeyText) ? privateKeyText : readFile(privateKeyPath);
            pem = pem.replace("\\n", "\n")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(pem);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }
        catch (Exception e)
        {
            throw new ServiceException("读取微信支付商户私钥失败");
        }
    }

    private PublicKey getPlatformPublicKey()
    {
        if (platformPublicKey == null)
        {
            synchronized (this)
            {
                if (platformPublicKey == null)
                {
                    loadPlatformVerifier();
                }
            }
        }
        return platformPublicKey;
    }

    private String getPlatformVerifierSerial()
    {
        if (platformVerifierSerial == null)
        {
            synchronized (this)
            {
                if (platformVerifierSerial == null)
                {
                    loadPlatformVerifier();
                }
            }
        }
        return platformVerifierSerial;
    }

    private void loadPlatformVerifier()
    {
        try
        {
            if (StringUtils.isNotEmpty(platformCertPath))
            {
                try (InputStream in = new FileInputStream(platformCertPath))
                {
                    X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
                    platformPublicKey = cert.getPublicKey();
                    platformVerifierSerial = cert.getSerialNumber().toString(16).toUpperCase();
                    return;
                }
            }
            String pem = readFile(platformPublicKeyPath)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(pem);
            platformPublicKey = KeyFactory.getInstance("RSA").generatePublic(new java.security.spec.X509EncodedKeySpec(keyBytes));
            platformVerifierSerial = platformPublicKeyId;
        }
        catch (Exception e)
        {
            throw new ServiceException("读取微信支付平台证书/公钥失败");
        }
    }

    private boolean hasMerchantPrivateKey()
    {
        return StringUtils.isNotEmpty(privateKeyText) || StringUtils.isNotEmpty(privateKeyPath);
    }

    private boolean hasPlatformVerifier()
    {
        return StringUtils.isNotEmpty(platformCertPath)
                || (StringUtils.isNotEmpty(platformPublicKeyPath) && StringUtils.isNotEmpty(platformPublicKeyId));
    }

    private String trimPayDescription(String text)
    {
        String s = StringUtils.isEmpty(text) ? "平云山居订单" : text.trim();
        return s.length() > 42 ? s.substring(0, 42) : s;
    }

    private String nonceStr()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String readFile(String path) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    private String readResponse(HttpURLConnection conn) throws Exception
    {
        InputStream stream = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
        if (stream == null)
        {
            return "";
        }
        StringBuilder resp = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                resp.append(line);
            }
        }
        return resp.toString();
    }

    public static class PayNotify
    {
        private String outTradeNo;
        private String transactionId;
        private Integer paidFen;

        public String getOutTradeNo()
        {
            return outTradeNo;
        }

        public void setOutTradeNo(String outTradeNo)
        {
            this.outTradeNo = outTradeNo;
        }

        public String getTransactionId()
        {
            return transactionId;
        }

        public void setTransactionId(String transactionId)
        {
            this.transactionId = transactionId;
        }

        public Integer getPaidFen()
        {
            return paidFen;
        }

        public void setPaidFen(Integer paidFen)
        {
            this.paidFen = paidFen;
        }
    }
}
