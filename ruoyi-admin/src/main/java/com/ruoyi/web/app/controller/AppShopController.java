package com.ruoyi.web.app.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.hotel.domain.LwfBanner;
import com.ruoyi.hotel.domain.LwfBenefit;
import com.ruoyi.hotel.domain.LwfMall;
import com.ruoyi.hotel.domain.LwfProduct;
import com.ruoyi.hotel.domain.LwfRecharge;
import com.ruoyi.hotel.domain.LwfRoom;
import com.ruoyi.hotel.service.ILwfBannerService;
import com.ruoyi.hotel.service.ILwfBenefitService;
import com.ruoyi.hotel.service.ILwfMallService;
import com.ruoyi.hotel.service.ILwfProductService;
import com.ruoyi.hotel.service.ILwfRechargeService;
import com.ruoyi.hotel.service.ILwfRoomService;
import com.ruoyi.web.app.service.RoomStockService;

/**
 * C端公共数据（首页/房型/餐饮/储值/积分商城/会员权益）
 *
 * @author liwangfu
 */
@Anonymous
@RestController
@RequestMapping("/app")
public class AppShopController extends BaseController
{
    @Autowired
    private ILwfBannerService bannerService;
    @Autowired
    private ILwfRoomService roomService;
    @Autowired
    private ILwfProductService productService;
    @Autowired
    private ILwfRechargeService rechargeService;
    @Autowired
    private ILwfMallService mallService;
    @Autowired
    private ILwfBenefitService benefitService;
    @Autowired
    private RoomStockService roomStockService;

    /** 首页聚合 */
    @GetMapping("/home")
    public AjaxResult home()
    {
        AjaxResult ajax = success();
        ajax.put("hotel", hotelInfo());
        ajax.put("entries", entries());
        ajax.put("banners", activeBanners());
        ajax.put("promos", activeProducts("promo"));
        ajax.put("dishes", activeProducts("dish"));
        ajax.put("rooms", activeRooms());
        return ajax;
    }

    /** 房型列表 */
    @GetMapping("/room/list")
    public AjaxResult roomList()
    {
        return success(activeRooms());
    }

    /** 房型详情 */
    @GetMapping("/room/{roomId}")
    public AjaxResult roomDetail(@PathVariable Long roomId)
    {
        return success(roomService.selectLwfRoomByRoomId(roomId));
    }

    /** 房型某日期区间可售量（available=可订房数，stock=每日总量） */
    @GetMapping("/room/{roomId}/availability")
    public AjaxResult roomAvailability(@PathVariable Long roomId,
            @RequestParam(value = "checkIn", required = false) String checkIn,
            @RequestParam(value = "checkOut", required = false) String checkOut)
    {
        LwfRoom room = roomService.selectLwfRoomByRoomId(roomId);
        int dailyStock = (room != null && room.getStock() != null) ? room.getStock() : 0;
        int avail = roomStockService.availability(roomId, dailyStock, checkIn, checkOut);
        AjaxResult ajax = success();
        ajax.put("available", avail);
        ajax.put("stock", dailyStock);
        return ajax;
    }

    /** 商品列表（type=dish/promo） */
    @GetMapping("/product/list")
    public AjaxResult productList(@RequestParam(value = "type", required = false, defaultValue = "dish") String type)
    {
        if (!isUsefulParam(type))
        {
            type = "dish";
        }
        return success(activeProducts(type));
    }

    /** 商品详情 */
    @GetMapping("/product/{productId}")
    public AjaxResult productDetail(@PathVariable Long productId)
    {
        return success(productService.selectLwfProductByProductId(productId));
    }

    /** 储值套餐列表 */
    @GetMapping("/recharge/list")
    public AjaxResult rechargeList()
    {
        LwfRecharge q = new LwfRecharge();
        q.setStatus("0");
        return success(rechargeService.selectLwfRechargeList(q));
    }

    /** 积分商城 */
    @GetMapping("/mall/list")
    public AjaxResult mallList()
    {
        LwfMall q = new LwfMall();
        q.setStatus("0");
        AjaxResult ajax = success();
        ajax.put("tabs", new String[] { "房券", "餐饮其他", "月饼", "棋牌", "自助餐" });
        ajax.put("items", mallService.selectLwfMallList(q));
        return ajax;
    }

    /** 会员权益与等级 */
    @GetMapping("/benefit/list")
    public AjaxResult benefitList()
    {
        LwfBenefit q = new LwfBenefit();
        q.setStatus("0");
        AjaxResult ajax = success();
        ajax.put("levels", new String[] { "银卡会员", "金卡会员", "铂金会员", "钻石会员" });
        ajax.put("benefits", benefitService.selectLwfBenefitList(q));
        return ajax;
    }

    /* ============== 内部辅助 ============== */

    private List<LwfBanner> activeBanners()
    {
        LwfBanner q = new LwfBanner();
        q.setStatus("0");
        return bannerService.selectLwfBannerList(q);
    }

    private List<LwfRoom> activeRooms()
    {
        LwfRoom q = new LwfRoom();
        q.setStatus("0");
        return roomService.selectLwfRoomList(q);
    }

    private List<LwfProduct> activeProducts(String type)
    {
        LwfProduct q = new LwfProduct();
        q.setStatus("0");
        q.setType(type);
        return productService.selectLwfProductList(q);
    }

    private boolean isUsefulParam(String value)
    {
        return StringUtils.isNotEmpty(value)
                && !"undefined".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim());
    }

    private Map<String, Object> hotelInfo()
    {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", "平云山居");
        m.put("full", "平云山居");
        m.put("city", "广州");
        m.put("id", "304464073");
        return m;
    }

    private List<Map<String, Object>> entries()
    {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(entry("rooms", "客房预订", "bed", "/pages/rooms/rooms", false));
        list.add(entry("dining", "餐饮预订", "dish", "/pages/dining/dining", false));
        list.add(entry("recharge", "储值有礼", "card", "/pages/recharge/recharge", false));
        list.add(entry("member", "会员中心", "crown", "/pages/member/member", true));
        return list;
    }

    private Map<String, Object> entry(String key, String name, String icon, String url, boolean tab)
    {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("key", key);
        m.put("name", name);
        m.put("icon", icon);
        m.put("url", url);
        m.put("tab", tab);
        return m;
    }
}
