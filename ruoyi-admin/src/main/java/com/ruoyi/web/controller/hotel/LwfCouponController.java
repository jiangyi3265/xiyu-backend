package com.ruoyi.web.controller.hotel;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.hotel.domain.LwfCoupon;
import com.ruoyi.hotel.service.ILwfCouponService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 优惠券Controller
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/coupon")
public class LwfCouponController extends BaseController
{
    @Autowired
    private ILwfCouponService lwfCouponService;

    @PreAuthorize("@ss.hasPermi('hotel:coupon:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfCoupon lwfCoupon)
    {
        startPage();
        List<LwfCoupon> list = lwfCouponService.selectLwfCouponList(lwfCoupon);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:coupon:export')")
    @Log(title = "优惠券", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LwfCoupon lwfCoupon)
    {
        List<LwfCoupon> list = lwfCouponService.selectLwfCouponList(lwfCoupon);
        ExcelUtil<LwfCoupon> util = new ExcelUtil<LwfCoupon>(LwfCoupon.class);
        util.exportExcel(response, list, "优惠券数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:coupon:query')")
    @GetMapping(value = "/{couponId}")
    public AjaxResult getInfo(@PathVariable("couponId") Long couponId)
    {
        return success(lwfCouponService.selectLwfCouponByCouponId(couponId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:coupon:add')")
    @Log(title = "优惠券", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LwfCoupon lwfCoupon)
    {
        lwfCoupon.setCreateBy(getUsername());
        return toAjax(lwfCouponService.insertLwfCoupon(lwfCoupon));
    }

    @PreAuthorize("@ss.hasPermi('hotel:coupon:edit')")
    @Log(title = "优惠券", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LwfCoupon lwfCoupon)
    {
        lwfCoupon.setUpdateBy(getUsername());
        return toAjax(lwfCouponService.updateLwfCoupon(lwfCoupon));
    }

    @PreAuthorize("@ss.hasPermi('hotel:coupon:remove')")
    @Log(title = "优惠券", businessType = BusinessType.DELETE)
    @DeleteMapping("/{couponIds}")
    public AjaxResult remove(@PathVariable Long[] couponIds)
    {
        return toAjax(lwfCouponService.deleteLwfCouponByCouponIds(couponIds));
    }
}
