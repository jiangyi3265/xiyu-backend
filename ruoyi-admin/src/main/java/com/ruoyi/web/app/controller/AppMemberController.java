package com.ruoyi.web.app.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.hotel.domain.LwfMember;
import com.ruoyi.hotel.domain.LwfMemberCoupon;
import com.ruoyi.hotel.domain.LwfOrder;
import com.ruoyi.hotel.domain.LwfPointlog;
import com.ruoyi.hotel.service.ILwfMemberCouponService;
import com.ruoyi.hotel.service.ILwfMemberService;
import com.ruoyi.hotel.service.ILwfOrderService;
import com.ruoyi.hotel.service.ILwfPointlogService;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.web.app.domain.AppOrderReq;
import com.ruoyi.web.app.security.AppUserContext;
import com.ruoyi.web.app.service.AppBizService;
import com.ruoyi.web.app.service.WxPayService;

/**
 * C端会员中心（需会员登录，鉴权由 AppAuthInterceptor 负责）
 *
 * @author liwangfu
 */
@Anonymous
@RestController
@RequestMapping("/app/member")
public class AppMemberController extends BaseController
{
    @Autowired
    private ILwfMemberService memberService;
    @Autowired
    private ILwfOrderService orderService;
    @Autowired
    private ILwfPointlogService pointlogService;
    @Autowired
    private ILwfMemberCouponService memberCouponService;
    @Autowired
    private AppBizService appBizService;
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private WxPayService wxPayService;

    /** 会员信息 + 可用券数 */
    @GetMapping("/info")
    public AjaxResult info()
    {
        Long memberId = AppUserContext.getMemberId();
        LwfMember member = memberService.selectLwfMemberByMemberId(memberId);
        LwfMemberCoupon cq = new LwfMemberCoupon();
        cq.setMemberId(memberId);
        cq.setStatus("0");
        int couponCount = memberCouponService.selectLwfMemberCouponList(cq).size();
        AjaxResult ajax = success(member);
        ajax.put("couponCount", couponCount);
        return ajax;
    }

    /** 修改资料 */
    @PutMapping("/profile")
    public AjaxResult profile(@RequestBody Map<String, String> body)
    {
        LwfMember up = new LwfMember();
        up.setMemberId(AppUserContext.getMemberId());
        if (body.containsKey("name"))
        {
            up.setName(body.get("name"));
        }
        if (body.containsKey("avatar"))
        {
            up.setAvatar(body.get("avatar"));
        }
        if (body.containsKey("address"))
        {
            up.setAddress(body.get("address"));
        }
        return toAjax(memberService.updateLwfMember(up));
    }

    @PostMapping("/phone")
    public AjaxResult bindPhone(@RequestBody Map<String, String> body)
    {
        LwfMember member = appBizService.bindWxPhone(AppUserContext.getMemberId(), body.get("code"));
        AjaxResult ajax = success();
        ajax.put("member", member);
        return ajax;
    }

    /** 上传头像：保存文件并把可访问 URL 写回会员，返回 url */
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("file") MultipartFile file) throws Exception
    {
        if (file == null || file.isEmpty())
        {
            return error("请选择头像图片");
        }
        String fileName = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);
        String url = serverConfig.getUrl() + fileName;
        LwfMember up = new LwfMember();
        up.setMemberId(AppUserContext.getMemberId());
        up.setAvatar(url);
        memberService.updateLwfMember(up);
        AjaxResult ajax = success();
        ajax.put("url", url);
        return ajax;
    }

    /** 我的订单 */
    @GetMapping("/order/list")
    public AjaxResult orderList(@RequestParam(required = false) String status,
            @RequestParam(required = false) String kw)
    {
        LwfOrder q = new LwfOrder();
        q.setMemberId(AppUserContext.getMemberId());
        if (isUsefulParam(status) && !"all".equals(status))
        {
            q.setStatus(status);
        }
        if (isUsefulParam(kw))
        {
            q.setTitle(kw);
        }
        return success(orderService.selectLwfOrderList(q));
    }

    private boolean isUsefulParam(String value)
    {
        return StringUtils.isNotEmpty(value)
                && !"undefined".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim());
    }

    /** 下单并发起真实微信支付；未配置商户参数时不做模拟支付，直接报错。 */
    @PostMapping("/order")
    public AjaxResult createOrder(@RequestBody AppOrderReq req, HttpServletRequest request)
    {
        Long memberId = AppUserContext.getMemberId();
        LwfOrder order = appBizService.createOrder(memberId, req);
        return buildPayResult(memberId, order, request);
    }

    /** 对已存在的待支付订单再次发起支付（订单列表/详情"去支付"） */
    @PutMapping("/order/{orderId}/prepay")
    public AjaxResult prepayOrder(@PathVariable Long orderId, HttpServletRequest request)
    {
        Long memberId = AppUserContext.getMemberId();
        LwfOrder order = appBizService.getMemberPayOrder(memberId, orderId);
        return buildPayResult(memberId, order, request);
    }

    /** 统一构造支付结果：needPay + 小程序唤起参数 */
    private AjaxResult buildPayResult(Long memberId, LwfOrder order, HttpServletRequest request)
    {
        AjaxResult ajax = success();
        ajax.put("orderId", order.getOrderId());
        ajax.put("orderNo", order.getOrderNo());
        if ("pay".equals(order.getStatus()))
        {
            LwfMember m = memberService.selectLwfMemberByMemberId(memberId);
            Map<String, String> pay = wxPayService.jsapiPay(order.getOrderNo(), order.getTitle(),
                    order.getPayAmount(), m.getOpenid(), IpUtils.getIpAddr(request));
            ajax.put("needPay", true);
            ajax.put("pay", pay);
        }
        else
        {
            ajax.put("needPay", false);
        }
        return ajax;
    }

    /** 删除订单 */
    @DeleteMapping("/order/{orderId}")
    public AjaxResult deleteOrder(@PathVariable Long orderId)
    {
        return toAjax(appBizService.deleteOrder(AppUserContext.getMemberId(), orderId));
    }

    /** 取消订单 */
    @PutMapping("/order/{orderId}/cancel")
    public AjaxResult cancelOrder(@PathVariable Long orderId)
    {
        return toAjax(appBizService.cancelOrder(AppUserContext.getMemberId(), orderId));
    }

    /** 禁止模拟支付：真实订单只能由微信支付回调结算。 */
    @PutMapping("/order/{orderId}/pay")
    public AjaxResult payOrder(@PathVariable Long orderId)
    {
        return toAjax(appBizService.payOrder(AppUserContext.getMemberId(), orderId));
    }

    /** 核销使用订单 */
    @PutMapping("/order/{orderId}/use")
    public AjaxResult useOrder(@PathVariable Long orderId)
    {
        return toAjax(appBizService.useOrder(AppUserContext.getMemberId(), orderId));
    }

    /** 申请退款 */
    @PutMapping("/order/{orderId}/refund")
    public AjaxResult refundOrder(@PathVariable Long orderId)
    {
        return toAjax(appBizService.refundOrder(AppUserContext.getMemberId(), orderId));
    }

    /** 储值余额可退金额 */
    @GetMapping("/recharge/refund/info")
    public AjaxResult rechargeRefundInfo()
    {
        AjaxResult ajax = success();
        ajax.put("maxAmount", appBizService.maxRechargeRefundAmount(AppUserContext.getMemberId()));
        ajax.put("policy", "仅退剩余可退实付本金，赠送金额、赠券、赠送权益不可提现；提交后由后台审核。");
        return ajax;
    }

    /** 申请储值余额退款 */
    @PostMapping("/recharge/refund")
    public AjaxResult rechargeRefund(@RequestBody Map<String, Object> body)
    {
        BigDecimal amount = body.get("amount") == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(body.get("amount")));
        String reason = body.get("reason") == null ? "" : String.valueOf(body.get("reason"));
        return toAjax(appBizService.requestRechargeRefund(AppUserContext.getMemberId(), amount, reason));
    }

    /** 提交订单评价 */
    @PostMapping("/review")
    public AjaxResult review(@RequestBody Map<String, Object> body)
    {
        Long orderId = Long.valueOf(String.valueOf(body.get("orderId")));
        String content = body.get("content") == null ? "" : String.valueOf(body.get("content"));
        Integer rating = body.get("rating") == null ? 5 : Integer.valueOf(String.valueOf(body.get("rating")));
        return toAjax(appBizService.submitReview(AppUserContext.getMemberId(), orderId, content, rating));
    }

    /** 储值充值并发起真实微信支付；支付成功回调后入账。 */
    @PostMapping("/recharge")
    public AjaxResult recharge(@RequestBody Map<String, Object> body, HttpServletRequest request)
    {
        Long memberId = AppUserContext.getMemberId();
        Long rechargeId = Long.valueOf(String.valueOf(body.get("rechargeId")));
        Integer qty = body.get("qty") == null ? 1 : Integer.valueOf(String.valueOf(body.get("qty")));
        LwfOrder order = appBizService.recharge(memberId, rechargeId, qty);
        AjaxResult ajax = buildPayResult(memberId, order, request);
        if (!Boolean.TRUE.equals(ajax.get("needPay")))
        {
            ajax.put("member", memberService.selectLwfMemberByMemberId(memberId));
        }
        return ajax;
    }

    /** 积分兑换 */
    @PostMapping("/mall/redeem")
    public AjaxResult redeem(@RequestBody Map<String, Object> body)
    {
        Long mallId = Long.valueOf(String.valueOf(body.get("mallId")));
        return success(appBizService.redeemMall(AppUserContext.getMemberId(), mallId));
    }

    /** 积分明细 + 累计收支 */
    @GetMapping("/point/list")
    public AjaxResult pointList(@RequestParam(required = false) String type)
    {
        Long memberId = AppUserContext.getMemberId();
        LwfPointlog q = new LwfPointlog();
        q.setMemberId(memberId);
        List<LwfPointlog> all = pointlogService.selectLwfPointlogList(q);
        int income = all.stream().filter(l -> "in".equals(l.getType())).mapToInt(LwfPointlog::getPoints).sum();
        int outcome = all.stream().filter(l -> "out".equals(l.getType())).mapToInt(LwfPointlog::getPoints).sum();
        List<LwfPointlog> list = all;
        if (isUsefulParam(type) && !"all".equals(type))
        {
            list = all.stream().filter(l -> type.equals(l.getType())).collect(Collectors.toList());
        }
        AjaxResult ajax = success(list);
        ajax.put("income", income);
        ajax.put("outcome", outcome);
        return ajax;
    }

    /** 我的优惠券（status: 0待用 1已用 2失效） */
    @GetMapping("/coupon/list")
    public AjaxResult couponList(@RequestParam(required = false) String status)
    {
        LwfMemberCoupon q = new LwfMemberCoupon();
        q.setMemberId(AppUserContext.getMemberId());
        if (isUsefulParam(status))
        {
            q.setStatus(status);
        }
        return success(memberCouponService.selectLwfMemberCouponList(q));
    }
}
