package com.ruoyi.web.controller.hotel;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.hotel.domain.LwfOrder;
import com.ruoyi.hotel.service.ILwfOrderService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 订单Controller（订单由C端会员创建，后台仅查看/改状态/删除）
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/order")
public class LwfOrderController extends BaseController
{
    @Autowired
    private ILwfOrderService lwfOrderService;

    @PreAuthorize("@ss.hasPermi('hotel:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfOrder lwfOrder)
    {
        startPage();
        List<LwfOrder> list = lwfOrderService.selectLwfOrderList(lwfOrder);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:order:export')")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LwfOrder lwfOrder)
    {
        List<LwfOrder> list = lwfOrderService.selectLwfOrderList(lwfOrder);
        ExcelUtil<LwfOrder> util = new ExcelUtil<LwfOrder>(LwfOrder.class);
        util.exportExcel(response, list, "订单数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(lwfOrderService.selectLwfOrderByOrderId(orderId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:order:edit')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LwfOrder lwfOrder)
    {
        lwfOrder.setUpdateBy(getUsername());
        return toAjax(lwfOrderService.updateLwfOrder(lwfOrder));
    }

    @PreAuthorize("@ss.hasPermi('hotel:order:remove')")
    @Log(title = "订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(lwfOrderService.deleteLwfOrderByOrderIds(orderIds));
    }
}
