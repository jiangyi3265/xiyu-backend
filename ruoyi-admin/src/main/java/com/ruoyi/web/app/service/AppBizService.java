package com.ruoyi.web.app.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.hotel.domain.LwfMall;
import com.ruoyi.hotel.domain.LwfMember;
import com.ruoyi.hotel.domain.LwfMemberCoupon;
import com.ruoyi.hotel.domain.LwfOrder;
import com.ruoyi.hotel.domain.LwfPointlog;
import com.ruoyi.hotel.domain.LwfProduct;
import com.ruoyi.hotel.domain.LwfRecharge;
import com.ruoyi.hotel.domain.LwfReview;
import com.ruoyi.hotel.domain.LwfRoom;
import com.ruoyi.hotel.service.ILwfMallService;
import com.ruoyi.hotel.service.ILwfReviewService;
import com.ruoyi.hotel.service.ILwfMemberCouponService;
import com.ruoyi.hotel.service.ILwfMemberService;
import com.ruoyi.hotel.service.ILwfOrderService;
import com.ruoyi.hotel.service.ILwfPointlogService;
import com.ruoyi.hotel.service.ILwfProductService;
import com.ruoyi.hotel.service.ILwfRechargeService;
import com.ruoyi.hotel.service.ILwfRoomService;
import com.ruoyi.web.app.domain.AppOrderReq;

/**
 * C端业务编排（下单/储值/积分兑换/注册），统一事务处理
 *
 * @author liwangfu
 */
@Service
public class AppBizService
{
    private volatile String wxAccessToken;
    private volatile long wxAccessTokenExpireAt;

    @Autowired
    private ILwfMemberService memberService;
    @Autowired
    private ILwfOrderService orderService;
    @Autowired
    private ILwfPointlogService pointlogService;
    @Autowired
    private ILwfMemberCouponService memberCouponService;
    @Autowired
    private ILwfRechargeService rechargeService;
    @Autowired
    private ILwfMallService mallService;
    @Autowired
    private RoomStockService roomStockService;
    @Autowired
    private ILwfRoomService roomService;
    @Autowired
    private ILwfProductService productService;
    @Autowired
    private ILwfReviewService reviewService;
    @Autowired
    private WxPayService wxPayService;

    /** 微信小程序 appid（application.yml: wx.appid，缺省为空=演示模式） */
    @Value("${wx.appid:}")
    private String wxAppid;

    /** 微信小程序 secret（application.yml: wx.secret，缺省为空=演示模式） */
    @Value("${wx.secret:}")
    private String wxSecret;

    /**
     * 微信一键登录：用 code 换 openid，按 openid 查会员，没有则自动注册。
     * 已配置 wx.appid/wx.secret 时走真实 jscode2session；否则演示兜底（开发者工具无 appid 也能登录）。
     */
    @Transactional(rollbackFor = Exception.class)
    public LwfMember wxLogin(String code, String nickName, String avatarUrl)
    {
        String openid = resolveOpenid(code);
        LwfMember q = new LwfMember();
        q.setOpenid(openid);
        List<LwfMember> exist = memberService.selectLwfMemberList(q);
        if (!exist.isEmpty())
        {
            LwfMember m = exist.get(0);
            // 同步最新微信昵称/头像
            if (StringUtils.isNotEmpty(nickName) || StringUtils.isNotEmpty(avatarUrl))
            {
                LwfMember up = new LwfMember();
                up.setMemberId(m.getMemberId());
                if (StringUtils.isNotEmpty(nickName)) up.setName(nickName);
                if (StringUtils.isNotEmpty(avatarUrl)) up.setAvatar(avatarUrl);
                memberService.updateLwfMember(up);
            }
            return memberService.selectLwfMemberByMemberId(m.getMemberId());
        }
        // 首次微信登录：自动注册，送 500 积分
        LwfMember member = new LwfMember();
        member.setOpenid(openid);
        member.setPhone("wx_" + openid.substring(Math.max(0, openid.length() - 10)));
        member.setName(StringUtils.isNotEmpty(nickName) ? nickName : "微信用户");
        member.setAvatar(avatarUrl);
        member.setLevel("银卡会员");
        member.setPoints(500);
        member.setBalance(BigDecimal.ZERO);
        member.setStatus("0");
        member.setCreateBy("wx");
        memberService.insertLwfMember(member);
        addPointLog(member.getMemberId(), "in", "微信登录注册礼", 500);
        return member;
    }

    /** 用 code 换取 openid；真实模式调微信接口，演示模式返回固定 openid */
    private String resolveOpenid(String code)
    {
        if (StringUtils.isEmpty(code))
        {
            throw new ServiceException("缺少微信登录 code");
        }
        if (StringUtils.isEmpty(wxAppid) || StringUtils.isEmpty(wxSecret))
        {
            // 演示兜底：固定 openid，开发者工具/未配置 appid 时统一登录到同一微信演示会员
            return "wx_demo_openid";
        }
        try
        {
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + URLEncoder.encode(wxAppid, "UTF-8")
                    + "&secret=" + URLEncoder.encode(wxSecret, "UTF-8")
                    + "&js_code=" + URLEncoder.encode(code, "UTF-8") + "&grant_type=authorization_code";
            String resp = HttpUtils.sendGet(url);
            String openid = extractJson(resp, "openid");
            if (StringUtils.isNotEmpty(openid))
            {
                return openid;
            }
            String errmsg = extractJson(resp, "errmsg");
            if (StringUtils.isNotEmpty(errmsg))
            {
                throw new ServiceException("微信登录失败：" + errmsg);
            }
            throw new ServiceException("微信登录失败：微信接口未返回 openid");
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("微信登录失败，请稍后重试");
        }
    }

    /** 极简 JSON 取值（避免额外引入解析库；仅用于解析微信返回） */
    private String extractJson(String json, String key)
    {
        if (json == null) return null;
        String flag = "\"" + key + "\"";
        int i = json.indexOf(flag);
        if (i < 0) return null;
        int c = json.indexOf(':', i);
        int q1 = json.indexOf('"', c + 1);
        int q2 = json.indexOf('"', q1 + 1);
        if (q1 < 0 || q2 < 0) return null;
        return json.substring(q1 + 1, q2);
    }

    @Transactional(rollbackFor = Exception.class)
    public LwfMember bindWxPhone(Long memberId, String code)
    {
        if (StringUtils.isEmpty(code))
        {
            throw new ServiceException("缺少微信手机号授权 code");
        }
        if (StringUtils.isEmpty(wxAppid) || StringUtils.isEmpty(wxSecret))
        {
            throw new ServiceException("微信小程序未配置，无法获取手机号");
        }

        String accessToken = getWxAccessToken();
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken;
        String resp = HttpUtils.sendPost(url, "{\"code\":\"" + jsonEscape(code) + "\"}", "application/json;charset=UTF-8");
        String phone = extractJson(resp, "phoneNumber");
        if (StringUtils.isEmpty(phone))
        {
            String errmsg = extractJson(resp, "errmsg");
            throw new ServiceException(StringUtils.isNotEmpty(errmsg) ? ("获取微信手机号失败：" + errmsg) : "获取微信手机号失败");
        }

        LwfMember exists = memberService.selectLwfMemberByPhone(phone);
        if (exists != null && !memberId.equals(exists.getMemberId()))
        {
            throw new ServiceException("该手机号已绑定其他账号");
        }

        LwfMember up = new LwfMember();
        up.setMemberId(memberId);
        up.setPhone(phone);
        memberService.updateLwfMember(up);
        return memberService.selectLwfMemberByMemberId(memberId);
    }

    private synchronized String getWxAccessToken()
    {
        long now = System.currentTimeMillis();
        if (StringUtils.isNotEmpty(wxAccessToken) && now < wxAccessTokenExpireAt)
        {
            return wxAccessToken;
        }
        try
        {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                    + URLEncoder.encode(wxAppid, "UTF-8") + "&secret=" + URLEncoder.encode(wxSecret, "UTF-8");
            String resp = HttpUtils.sendGet(url);
            String token = extractJson(resp, "access_token");
            if (StringUtils.isEmpty(token))
            {
                String errmsg = extractJson(resp, "errmsg");
                throw new ServiceException(StringUtils.isNotEmpty(errmsg) ? ("获取微信 access_token 失败：" + errmsg) : "获取微信 access_token 失败");
            }
            String expiresIn = extractNumber(resp, "expires_in");
            long ttl = 7000L;
            try
            {
                ttl = Long.parseLong(expiresIn);
            }
            catch (Exception ignored)
            {
            }
            wxAccessToken = token;
            wxAccessTokenExpireAt = now + Math.max(60L, ttl - 300L) * 1000L;
            return wxAccessToken;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("获取微信 access_token 失败，请稍后重试");
        }
    }

    private String jsonEscape(String s)
    {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String extractNumber(String json, String key)
    {
        if (json == null) return null;
        String flag = "\"" + key + "\"";
        int i = json.indexOf(flag);
        if (i < 0) return null;
        int c = json.indexOf(':', i);
        if (c < 0) return null;
        int start = c + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
        return end > start ? json.substring(start, end) : null;
    }

    /** 注册：送500积分注册礼 */
    @Transactional(rollbackFor = Exception.class)
    public LwfMember register(String phone, String password, String name)
    {
        if (memberService.selectLwfMemberByPhone(phone) != null)
        {
            throw new ServiceException("该手机号已注册");
        }
        LwfMember member = new LwfMember();
        member.setPhone(phone);
        member.setPassword(SecurityUtils.encryptPassword(password));
        member.setName(StringUtils.isEmpty(name) ? ("会员" + phone.substring(Math.max(0, phone.length() - 4))) : name);
        member.setLevel("银卡会员");
        member.setPoints(500);
        member.setBalance(BigDecimal.ZERO);
        member.setStatus("0");
        member.setCreateBy("app");
        memberService.insertLwfMember(member);
        addPointLog(member.getMemberId(), "in", "会员注册礼", 500);
        return member;
    }

    /** 下单：处理优惠券、积分抵扣、积分获取 */
    @Transactional(rollbackFor = Exception.class)
    public LwfOrder createOrder(Long memberId, AppOrderReq req)
    {
        LwfMember member = memberService.selectLwfMemberByMemberId(memberId);
        if (member == null)
        {
            throw new ServiceException("会员不存在");
        }
        // 后端核价：单价/金额一律以服务端价格为准，忽略前端传入的 unitPrice/amount，杜绝改价下单
        int reqQty = (req.getQty() == null || req.getQty() < 1) ? 1 : req.getQty();
        boolean isRoom = "room".equals(req.getKind()) && req.getRoomId() != null;
        BigDecimal unit;
        int qty;
        if (isRoom)
        {
            // 客房：按房型表价格，间夜数以入离日期为准（防止改 qty 少付）
            LwfRoom room = roomService.selectLwfRoomByRoomId(req.getRoomId());
            if (room == null || !"0".equals(room.getStatus()))
            {
                throw new ServiceException("房型不存在或已停用");
            }
            unit = room.getPrice();
            int nights = nightsBetween(req.getCheckIn(), req.getCheckOut());
            qty = nights > 0 ? nights : reqQty;
        }
        else if (req.getProductId() != null)
        {
            // 餐饮/周年庆套票：按产品表价格
            LwfProduct product = productService.selectLwfProductByProductId(req.getProductId());
            if (product == null || !"0".equals(product.getStatus()))
            {
                throw new ServiceException("商品不存在或已下架");
            }
            unit = product.getPrice();
            qty = reqQty;
        }
        else
        {
            // 缺少 roomId/productId 无法核价：拒绝下单，杜绝前端自定义价格
            throw new ServiceException("下单参数有误，请重新选择商品");
        }
        if (unit == null)
        {
            unit = BigDecimal.ZERO;
        }
        BigDecimal amount = unit.multiply(new BigDecimal(qty));

        // 优惠券抵扣
        BigDecimal couponCut = BigDecimal.ZERO;
        Long couponMcId = null;
        if (req.getCouponMcId() != null)
        {
            LwfMemberCoupon mc = memberCouponService.selectLwfMemberCouponByMcId(req.getCouponMcId());
            if (mc == null || !memberId.equals(mc.getMemberId()) || !"0".equals(mc.getStatus()))
            {
                throw new ServiceException("优惠券不可用");
            }
            if (mc.getCondAmount() != null && amount.compareTo(mc.getCondAmount()) < 0)
            {
                throw new ServiceException("未达到优惠券使用门槛");
            }
            couponCut = mc.getAmount();
            couponMcId = mc.getMcId();
        }

        // 积分抵扣：100积分=1元，最多抵20元
        int pointCutYuan = 0;
        if (Boolean.TRUE.equals(req.getUsePoints()))
        {
            pointCutYuan = Math.min(20, member.getPoints() / 100);
        }
        int usedPoints = pointCutYuan * 100;

        BigDecimal payable = amount.subtract(couponCut).subtract(new BigDecimal(pointCutYuan));
        if (payable.compareTo(BigDecimal.ZERO) < 0)
        {
            payable = BigDecimal.ZERO;
        }

        boolean needWechatPay = payable.compareTo(BigDecimal.ZERO) > 0;
        if (needWechatPay)
        {
            wxPayService.ensureReady();
        }

        // 客房订单：按日期防超卖，锁定每一晚库存；订满则在建单前抛错
        if (isRoom)
        {
            roomStockService.reserve(req.getRoomId(), req.getCheckIn(), req.getCheckOut(), 1);
        }

        // 创建订单
        LwfOrder order = new LwfOrder();
        order.setOrderNo(genOrderNo());
        order.setMemberId(memberId);
        order.setMemberName(member.getName());
        order.setShop("平云山居");
        order.setKind(req.getKind());
        order.setScene(req.getScene());
        order.setStatus(needWechatPay ? "pay" : "use");
        order.setTitle(req.getTitle());
        order.setUnitPrice(unit);
        order.setQty(qty);
        order.setAmount(amount);
        order.setPayAmount(payable);
        order.setCouponMcId(couponMcId);
        order.setUsedPoints(usedPoints);
        if (isRoom)
        {
            order.setRefId(req.getRoomId());
            order.setCheckIn(req.getCheckIn());
            order.setCheckOut(req.getCheckOut());
        }
        order.setRemark(req.getDesc());
        order.setCreateBy("app");
        orderService.insertLwfOrder(order);

        // 0 元订单无需微信支付，直接结算；有实付金额的订单必须等待微信支付回调。
        if (!needWechatPay)
        {
            settlePurchase(member, order);
        }
        return order;
    }

    /** 结算一笔消费订单：核销券 + 扣抵扣积分 + 加消费积分 */
    private void settlePurchase(LwfMember member, LwfOrder order)
    {
        int usedPoints = order.getUsedPoints() == null ? 0 : order.getUsedPoints();
        int earned = order.getPayAmount() == null ? 0 : order.getPayAmount().setScale(0, RoundingMode.HALF_UP).intValue();
        LwfMember up = new LwfMember();
        up.setMemberId(member.getMemberId());
        up.setPoints(member.getPoints() - usedPoints + earned);
        memberService.updateLwfMember(up);
        if (usedPoints > 0)
        {
            addPointLog(member.getMemberId(), "out", "积分抵扣 · " + order.getTitle(), usedPoints);
        }
        if (earned > 0)
        {
            addPointLog(member.getMemberId(), "in", "消费 · " + order.getTitle(), earned);
        }
        if (order.getCouponMcId() != null)
        {
            LwfMemberCoupon mcUp = new LwfMemberCoupon();
            mcUp.setMcId(order.getCouponMcId());
            mcUp.setStatus("1");
            mcUp.setUsedTime(new Date());
            memberCouponService.updateLwfMemberCoupon(mcUp);
        }
    }

    /** 储值充值：创建待支付单，支付成功回调后入账。 */
    @Transactional(rollbackFor = Exception.class)
    public LwfOrder recharge(Long memberId, Long rechargeId, Integer qtyParam)
    {
        LwfMember member = memberService.selectLwfMemberByMemberId(memberId);
        if (member == null)
        {
            throw new ServiceException("会员不存在");
        }
        LwfRecharge r = rechargeService.selectLwfRechargeByRechargeId(rechargeId);
        if (r == null || !"0".equals(r.getStatus()))
        {
            throw new ServiceException("充值套餐不存在");
        }
        int qty = qtyParam == null || qtyParam < 1 ? 1 : qtyParam;
        BigDecimal pay = r.getAmount().multiply(new BigDecimal(qty));
        wxPayService.ensureReady();

        LwfOrder order = new LwfOrder();
        order.setOrderNo(genOrderNo());
        order.setMemberId(memberId);
        order.setMemberName(member.getName());
        order.setShop("平云山居");
        order.setKind("recharge");
        order.setScene("night");
        order.setStatus("pay");
        order.setTitle("储值充值 ¥" + pay.stripTrailingZeros().toPlainString() + " ×" + qty);
        order.setUnitPrice(r.getAmount());
        order.setQty(qty);
        order.setAmount(pay);
        order.setPayAmount(pay);
        order.setRefId(rechargeId);
        order.setCreateBy("app");
        orderService.insertLwfOrder(order);

        return order;
    }

    /** 储值入账：余额=充值+赠送，送积分，赠券入账 */
    private void applyRecharge(LwfMember member, LwfRecharge r, int qty)
    {
        BigDecimal qtyBd = new BigDecimal(qty);
        BigDecimal pay = r.getAmount().multiply(qtyBd);
        BigDecimal give = (r.getGive() == null ? BigDecimal.ZERO : r.getGive()).multiply(qtyBd);
        BigDecimal addBalance = pay.add(give);
        int addPoints = pay.setScale(0, RoundingMode.HALF_UP).intValue();

        LwfMember up = new LwfMember();
        up.setMemberId(member.getMemberId());
        up.setBalance((member.getBalance() == null ? BigDecimal.ZERO : member.getBalance()).add(addBalance));
        up.setPoints(member.getPoints() + addPoints);
        memberService.updateLwfMember(up);

        addPointLog(member.getMemberId(), "in", "储值赠送积分", addPoints);

        if (r.getCoupon() != null && r.getCoupon().compareTo(BigDecimal.ZERO) > 0)
        {
            LwfMemberCoupon mc = new LwfMemberCoupon();
            mc.setMemberId(member.getMemberId());
            mc.setTitle("储值专享赠券");
            mc.setCat(2);
            mc.setAmount(r.getCoupon().multiply(qtyBd));
            mc.setCondAmount(BigDecimal.ZERO);
            mc.setDescr("储值有礼赠送代金券");
            mc.setValidTime(plusOneYear());
            mc.setStatus("0");
            mc.setCreateBy("app");
            memberCouponService.insertLwfMemberCoupon(mc);
        }
    }

    /** 积分兑换商城商品：扣积分，发券 */
    @Transactional(rollbackFor = Exception.class)
    public LwfMember redeemMall(Long memberId, Long mallId)
    {
        LwfMember member = memberService.selectLwfMemberByMemberId(memberId);
        if (member == null)
        {
            throw new ServiceException("会员不存在");
        }
        LwfMall m = mallService.selectLwfMallByMallId(mallId);
        if (m == null || !"0".equals(m.getStatus()))
        {
            throw new ServiceException("兑换商品不存在");
        }
        if (member.getPoints() < m.getPoints())
        {
            throw new ServiceException("积分不足，还差 " + (m.getPoints() - member.getPoints()) + " 积分");
        }
        LwfMember up = new LwfMember();
        up.setMemberId(memberId);
        up.setPoints(member.getPoints() - m.getPoints());
        memberService.updateLwfMember(up);

        addPointLog(memberId, "out", "积分兑换 · " + m.getTitle(), m.getPoints());

        // 兑换得券
        LwfMemberCoupon mc = new LwfMemberCoupon();
        mc.setMemberId(memberId);
        mc.setTitle(m.getTitle());
        mc.setCat(2);
        mc.setAmount(BigDecimal.ZERO);
        mc.setCondAmount(BigDecimal.ZERO);
        mc.setDescr("积分兑换礼遇券");
        mc.setValidTime(plusOneYear());
        mc.setStatus("0");
        mc.setCreateBy("app");
        memberCouponService.insertLwfMemberCoupon(mc);

        return memberService.selectLwfMemberByMemberId(memberId);
    }

    /** 取消订单 */
    @Transactional(rollbackFor = Exception.class)
    public int cancelOrder(Long memberId, Long orderId)
    {
        LwfOrder order = orderService.selectLwfOrderByOrderId(orderId);
        if (order == null || !memberId.equals(order.getMemberId()))
        {
            throw new ServiceException("订单不存在");
        }
        releaseRoomStock(order);
        LwfOrder up = new LwfOrder();
        up.setOrderId(orderId);
        up.setStatus("cancel");
        return orderService.updateLwfOrder(up);
    }

    /** 客房订单释放库存（取消/退款/超时关单时调用，非客房或无日期则无操作） */
    private void releaseRoomStock(LwfOrder order)
    {
        if ("room".equals(order.getKind()) && order.getRefId() != null)
        {
            roomStockService.release(order.getRefId(), order.getCheckIn(), order.getCheckOut(), 1);
        }
    }

    /** 支付订单必须通过微信支付完成，禁止手动/模拟改为已支付。 */
    @Transactional(rollbackFor = Exception.class)
    public int payOrder(Long memberId, Long orderId)
    {
        getMemberOrder(memberId, orderId);
        throw new ServiceException("请通过微信支付完成订单");
    }

    /** 支付成功结算入口：按订单类型入账并转待使用，幂等。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean markPaid(String orderNo)
    {
        return markPaid(orderNo, null, null);
    }

    /** 支付成功结算入口：微信回调验签解密后调用，严格校验实付金额。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean markPaid(String orderNo, Integer paidFen, String transactionId)
    {
        LwfOrder q = new LwfOrder();
        q.setOrderNo(orderNo);
        LwfOrder order = orderService.selectLwfOrderList(q).stream()
                .filter(o -> orderNo.equals(o.getOrderNo())).findFirst().orElse(null);
        if (order == null)
        {
            return false;
        }
        int expectedFen = wxPayService.yuanToFen(order.getPayAmount());
        if (paidFen != null && paidFen.intValue() != expectedFen)
        {
            throw new ServiceException("微信支付金额与订单金额不一致");
        }
        if (!"pay".equals(order.getStatus()))
        {
            return true; // 已结算，幂等
        }
        LwfMember member = memberService.selectLwfMemberByMemberId(order.getMemberId());
        if (member == null)
        {
            return false;
        }
        if ("recharge".equals(order.getKind()))
        {
            LwfRecharge r = rechargeService.selectLwfRechargeByRechargeId(order.getRefId());
            if (r != null)
            {
                applyRecharge(member, r, order.getQty() == null ? 1 : order.getQty());
            }
        }
        else
        {
            settlePurchase(member, order);
        }
        LwfOrder up = new LwfOrder();
        up.setOrderId(order.getOrderId());
        up.setStatus("use");
        up.setUpdateBy("pay");
        if (StringUtils.isNotEmpty(transactionId))
        {
            up.setRemark((StringUtils.isEmpty(order.getRemark()) ? "" : order.getRemark() + "\n") + "微信支付单号：" + transactionId);
        }
        orderService.updateLwfOrder(up);
        return true;
    }

    /** 取会员的待支付订单（再次发起支付用），非本人/非待支付抛错 */
    public LwfOrder getMemberPayOrder(Long memberId, Long orderId)
    {
        LwfOrder order = getMemberOrder(memberId, orderId);
        if (!"pay".equals(order.getStatus()))
        {
            throw new ServiceException("订单当前状态不可支付");
        }
        return order;
    }

    /** 核销使用订单 */
    @Transactional(rollbackFor = Exception.class)
    public int useOrder(Long memberId, Long orderId)
    {
        LwfOrder order = getMemberOrder(memberId, orderId);
        if (!"use".equals(order.getStatus()))
        {
            throw new ServiceException("当前订单状态不可核销");
        }
        LwfOrder up = new LwfOrder();
        up.setOrderId(orderId);
        up.setStatus("done");
        up.setUpdateBy("app");
        return orderService.updateLwfOrder(up);
    }

    /** 订单退款：回退积分/券，已支付的发起微信退款，转退款态 */
    @Transactional(rollbackFor = Exception.class)
    public int refundOrder(Long memberId, Long orderId)
    {
        LwfOrder order = getMemberOrder(memberId, orderId);
        if (!"use".equals(order.getStatus()) && !"pay".equals(order.getStatus()) && !"confirm".equals(order.getStatus()))
        {
            throw new ServiceException("当前订单状态不可退款");
        }
        if ("recharge".equals(order.getKind()))
        {
            throw new ServiceException("储值订单不支持自助退款，请联系前台处理");
        }
        // 已结算（已支付）的订单：先退微信钱（已配证书时），再回退积分/券
        boolean settled = "use".equals(order.getStatus());
        if (settled)
        {
            if (wxPayService.refundEnabled())
            {
                int totalFen = order.getPayAmount() == null ? 0
                        : order.getPayAmount().multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue();
                if (totalFen > 0)
                {
                    wxPayService.refund(order.getOrderNo(), totalFen, totalFen);
                }
            }
            LwfMember member = memberService.selectLwfMemberByMemberId(memberId);
            reverseSettle(member, order);
        }
        releaseRoomStock(order);
        LwfOrder up = new LwfOrder();
        up.setOrderId(orderId);
        up.setStatus("refund");
        up.setUpdateBy("app");
        return orderService.updateLwfOrder(up);
    }

    /** 回退一笔已结算订单：券还原为待用、积分回退（加回抵扣、扣回消费，下限 0） */
    private void reverseSettle(LwfMember member, LwfOrder order)
    {
        int usedPoints = order.getUsedPoints() == null ? 0 : order.getUsedPoints();
        int earned = order.getPayAmount() == null ? 0 : order.getPayAmount().setScale(0, RoundingMode.HALF_UP).intValue();
        int newPoints = member.getPoints() + usedPoints - earned;
        if (newPoints < 0)
        {
            newPoints = 0;
        }
        LwfMember up = new LwfMember();
        up.setMemberId(member.getMemberId());
        up.setPoints(newPoints);
        memberService.updateLwfMember(up);
        if (usedPoints > 0)
        {
            addPointLog(member.getMemberId(), "in", "退款返还抵扣积分 · " + order.getTitle(), usedPoints);
        }
        if (earned > 0)
        {
            addPointLog(member.getMemberId(), "out", "退款扣回消费积分 · " + order.getTitle(), earned);
        }
        if (order.getCouponMcId() != null)
        {
            LwfMemberCoupon mcUp = new LwfMemberCoupon();
            mcUp.setMcId(order.getCouponMcId());
            mcUp.setStatus("0");
            memberCouponService.updateLwfMemberCoupon(mcUp);
        }
    }

    /** 删除订单 */
    public int deleteOrder(Long memberId, Long orderId)
    {
        getMemberOrder(memberId, orderId);
        return orderService.deleteLwfOrderByOrderId(orderId);
    }

    /** 提交订单评价：校验归属+已完成+未评价，写评价并标记订单已评价 */
    @Transactional(rollbackFor = Exception.class)
    public int submitReview(Long memberId, Long orderId, String content, Integer rating)
    {
        LwfOrder order = getMemberOrder(memberId, orderId);
        if (!"done".equals(order.getStatus()))
        {
            throw new ServiceException("仅已完成订单可评价");
        }
        if ("1".equals(order.getReviewed()))
        {
            throw new ServiceException("该订单已评价");
        }
        LwfMember member = memberService.selectLwfMemberByMemberId(memberId);
        LwfReview review = new LwfReview();
        review.setOrderId(orderId);
        review.setMemberId(memberId);
        review.setMemberName(member != null ? member.getName() : "");
        review.setOrderTitle(order.getTitle());
        review.setRating(rating == null ? 5 : Math.max(1, Math.min(5, rating)));
        review.setContent(content);
        review.setCreateBy("app");
        reviewService.insertLwfReview(review);

        LwfOrder up = new LwfOrder();
        up.setOrderId(orderId);
        up.setReviewed("1");
        up.setUpdateBy("app");
        orderService.updateLwfOrder(up);
        return 1;
    }

    private LwfOrder getMemberOrder(Long memberId, Long orderId)
    {
        LwfOrder order = orderService.selectLwfOrderByOrderId(orderId);
        if (order == null || !memberId.equals(order.getMemberId()))
        {
            throw new ServiceException("订单不存在");
        }
        return order;
    }

    private void addPointLog(Long memberId, String type, String title, int points)
    {
        LwfPointlog log = new LwfPointlog();
        log.setMemberId(memberId);
        log.setType(type);
        log.setTitle(title);
        log.setPoints(points);
        log.setCreateBy("app");
        pointlogService.insertLwfPointlog(log);
    }

    /** 计算 [checkIn, checkOut) 的间夜数；日期为空/非法/非正时返回 0，交由调用方兜底 */
    private int nightsBetween(String checkIn, String checkOut)
    {
        if (StringUtils.isEmpty(checkIn) || StringUtils.isEmpty(checkOut))
        {
            return 0;
        }
        try
        {
            long days = ChronoUnit.DAYS.between(LocalDate.parse(checkIn.trim()), LocalDate.parse(checkOut.trim()));
            return days > 0 ? (int) days : 0;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private String genOrderNo()
    {
        return "LWF" + System.currentTimeMillis() + String.format("%02d", (int) (System.nanoTime() % 100));
    }

    private Date plusOneYear()
    {
        return new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
    }
}
