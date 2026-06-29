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
import com.ruoyi.hotel.domain.LwfBanner;
import com.ruoyi.hotel.service.ILwfBannerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 横幅Controller
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/banner")
public class LwfBannerController extends BaseController
{
    @Autowired
    private ILwfBannerService lwfBannerService;

    @PreAuthorize("@ss.hasPermi('hotel:banner:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfBanner lwfBanner)
    {
        startPage();
        List<LwfBanner> list = lwfBannerService.selectLwfBannerList(lwfBanner);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:banner:export')")
    @Log(title = "横幅", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LwfBanner lwfBanner)
    {
        List<LwfBanner> list = lwfBannerService.selectLwfBannerList(lwfBanner);
        ExcelUtil<LwfBanner> util = new ExcelUtil<LwfBanner>(LwfBanner.class);
        util.exportExcel(response, list, "横幅数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:banner:query')")
    @GetMapping(value = "/{bannerId}")
    public AjaxResult getInfo(@PathVariable("bannerId") Long bannerId)
    {
        return success(lwfBannerService.selectLwfBannerByBannerId(bannerId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:banner:add')")
    @Log(title = "横幅", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LwfBanner lwfBanner)
    {
        lwfBanner.setCreateBy(getUsername());
        return toAjax(lwfBannerService.insertLwfBanner(lwfBanner));
    }

    @PreAuthorize("@ss.hasPermi('hotel:banner:edit')")
    @Log(title = "横幅", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LwfBanner lwfBanner)
    {
        lwfBanner.setUpdateBy(getUsername());
        return toAjax(lwfBannerService.updateLwfBanner(lwfBanner));
    }

    @PreAuthorize("@ss.hasPermi('hotel:banner:remove')")
    @Log(title = "横幅", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bannerIds}")
    public AjaxResult remove(@PathVariable Long[] bannerIds)
    {
        return toAjax(lwfBannerService.deleteLwfBannerByBannerIds(bannerIds));
    }
}
