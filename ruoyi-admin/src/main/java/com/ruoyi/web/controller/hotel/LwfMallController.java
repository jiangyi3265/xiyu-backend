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
import com.ruoyi.hotel.domain.LwfMall;
import com.ruoyi.hotel.service.ILwfMallService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 积分商城Controller
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/mall")
public class LwfMallController extends BaseController
{
    @Autowired
    private ILwfMallService lwfMallService;

    @PreAuthorize("@ss.hasPermi('hotel:mall:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfMall lwfMall)
    {
        startPage();
        List<LwfMall> list = lwfMallService.selectLwfMallList(lwfMall);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:mall:export')")
    @Log(title = "积分商城", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LwfMall lwfMall)
    {
        List<LwfMall> list = lwfMallService.selectLwfMallList(lwfMall);
        ExcelUtil<LwfMall> util = new ExcelUtil<LwfMall>(LwfMall.class);
        util.exportExcel(response, list, "积分商城数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:mall:query')")
    @GetMapping(value = "/{mallId}")
    public AjaxResult getInfo(@PathVariable("mallId") Long mallId)
    {
        return success(lwfMallService.selectLwfMallByMallId(mallId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:mall:add')")
    @Log(title = "积分商城", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LwfMall lwfMall)
    {
        lwfMall.setCreateBy(getUsername());
        return toAjax(lwfMallService.insertLwfMall(lwfMall));
    }

    @PreAuthorize("@ss.hasPermi('hotel:mall:edit')")
    @Log(title = "积分商城", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LwfMall lwfMall)
    {
        lwfMall.setUpdateBy(getUsername());
        return toAjax(lwfMallService.updateLwfMall(lwfMall));
    }

    @PreAuthorize("@ss.hasPermi('hotel:mall:remove')")
    @Log(title = "积分商城", businessType = BusinessType.DELETE)
    @DeleteMapping("/{mallIds}")
    public AjaxResult remove(@PathVariable Long[] mallIds)
    {
        return toAjax(lwfMallService.deleteLwfMallByMallIds(mallIds));
    }
}
