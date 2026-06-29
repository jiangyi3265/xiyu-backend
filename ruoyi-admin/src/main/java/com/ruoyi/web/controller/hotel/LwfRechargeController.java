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
import com.ruoyi.hotel.domain.LwfRecharge;
import com.ruoyi.hotel.service.ILwfRechargeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 充值套餐Controller
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/recharge")
public class LwfRechargeController extends BaseController
{
    @Autowired
    private ILwfRechargeService lwfRechargeService;

    @PreAuthorize("@ss.hasPermi('hotel:recharge:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfRecharge lwfRecharge)
    {
        startPage();
        List<LwfRecharge> list = lwfRechargeService.selectLwfRechargeList(lwfRecharge);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:recharge:export')")
    @Log(title = "充值套餐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LwfRecharge lwfRecharge)
    {
        List<LwfRecharge> list = lwfRechargeService.selectLwfRechargeList(lwfRecharge);
        ExcelUtil<LwfRecharge> util = new ExcelUtil<LwfRecharge>(LwfRecharge.class);
        util.exportExcel(response, list, "充值套餐数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:recharge:query')")
    @GetMapping(value = "/{rechargeId}")
    public AjaxResult getInfo(@PathVariable("rechargeId") Long rechargeId)
    {
        return success(lwfRechargeService.selectLwfRechargeByRechargeId(rechargeId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:recharge:add')")
    @Log(title = "充值套餐", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LwfRecharge lwfRecharge)
    {
        lwfRecharge.setCreateBy(getUsername());
        return toAjax(lwfRechargeService.insertLwfRecharge(lwfRecharge));
    }

    @PreAuthorize("@ss.hasPermi('hotel:recharge:edit')")
    @Log(title = "充值套餐", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LwfRecharge lwfRecharge)
    {
        lwfRecharge.setUpdateBy(getUsername());
        return toAjax(lwfRechargeService.updateLwfRecharge(lwfRecharge));
    }

    @PreAuthorize("@ss.hasPermi('hotel:recharge:remove')")
    @Log(title = "充值套餐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{rechargeIds}")
    public AjaxResult remove(@PathVariable Long[] rechargeIds)
    {
        return toAjax(lwfRechargeService.deleteLwfRechargeByRechargeIds(rechargeIds));
    }
}
