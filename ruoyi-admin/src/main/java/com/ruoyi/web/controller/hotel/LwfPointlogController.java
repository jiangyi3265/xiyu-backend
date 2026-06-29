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
import com.ruoyi.hotel.domain.LwfPointlog;
import com.ruoyi.hotel.service.ILwfPointlogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 积分记录Controller
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/pointlog")
public class LwfPointlogController extends BaseController
{
    @Autowired
    private ILwfPointlogService lwfPointlogService;

    @PreAuthorize("@ss.hasPermi('hotel:pointlog:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfPointlog lwfPointlog)
    {
        startPage();
        List<LwfPointlog> list = lwfPointlogService.selectLwfPointlogList(lwfPointlog);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:pointlog:export')")
    @Log(title = "积分记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LwfPointlog lwfPointlog)
    {
        List<LwfPointlog> list = lwfPointlogService.selectLwfPointlogList(lwfPointlog);
        ExcelUtil<LwfPointlog> util = new ExcelUtil<LwfPointlog>(LwfPointlog.class);
        util.exportExcel(response, list, "积分记录数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:pointlog:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable("logId") Long logId)
    {
        return success(lwfPointlogService.selectLwfPointlogByLogId(logId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:pointlog:add')")
    @Log(title = "积分记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LwfPointlog lwfPointlog)
    {
        lwfPointlog.setCreateBy(getUsername());
        return toAjax(lwfPointlogService.insertLwfPointlog(lwfPointlog));
    }

    @PreAuthorize("@ss.hasPermi('hotel:pointlog:edit')")
    @Log(title = "积分记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LwfPointlog lwfPointlog)
    {
        lwfPointlog.setUpdateBy(getUsername());
        return toAjax(lwfPointlogService.updateLwfPointlog(lwfPointlog));
    }

    @PreAuthorize("@ss.hasPermi('hotel:pointlog:remove')")
    @Log(title = "积分记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(lwfPointlogService.deleteLwfPointlogByLogIds(logIds));
    }
}
