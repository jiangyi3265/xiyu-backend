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
import com.ruoyi.hotel.domain.LwfBenefit;
import com.ruoyi.hotel.service.ILwfBenefitService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 会员权益Controller
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/benefit")
public class LwfBenefitController extends BaseController
{
    @Autowired
    private ILwfBenefitService lwfBenefitService;

    @PreAuthorize("@ss.hasPermi('hotel:benefit:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfBenefit lwfBenefit)
    {
        startPage();
        List<LwfBenefit> list = lwfBenefitService.selectLwfBenefitList(lwfBenefit);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:benefit:export')")
    @Log(title = "会员权益", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LwfBenefit lwfBenefit)
    {
        List<LwfBenefit> list = lwfBenefitService.selectLwfBenefitList(lwfBenefit);
        ExcelUtil<LwfBenefit> util = new ExcelUtil<LwfBenefit>(LwfBenefit.class);
        util.exportExcel(response, list, "会员权益数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:benefit:query')")
    @GetMapping(value = "/{benefitId}")
    public AjaxResult getInfo(@PathVariable("benefitId") Long benefitId)
    {
        return success(lwfBenefitService.selectLwfBenefitByBenefitId(benefitId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:benefit:add')")
    @Log(title = "会员权益", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LwfBenefit lwfBenefit)
    {
        lwfBenefit.setCreateBy(getUsername());
        return toAjax(lwfBenefitService.insertLwfBenefit(lwfBenefit));
    }

    @PreAuthorize("@ss.hasPermi('hotel:benefit:edit')")
    @Log(title = "会员权益", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LwfBenefit lwfBenefit)
    {
        lwfBenefit.setUpdateBy(getUsername());
        return toAjax(lwfBenefitService.updateLwfBenefit(lwfBenefit));
    }

    @PreAuthorize("@ss.hasPermi('hotel:benefit:remove')")
    @Log(title = "会员权益", businessType = BusinessType.DELETE)
    @DeleteMapping("/{benefitIds}")
    public AjaxResult remove(@PathVariable Long[] benefitIds)
    {
        return toAjax(lwfBenefitService.deleteLwfBenefitByBenefitIds(benefitIds));
    }
}
