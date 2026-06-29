package com.ruoyi.web.controller.hotel;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.hotel.domain.LwfReview;
import com.ruoyi.hotel.service.ILwfReviewService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 订单评价 后台管理（只读 + 删除，评价由会员产生不支持新增/修改）
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/review")
public class LwfReviewController extends BaseController
{
    @Autowired
    private ILwfReviewService lwfReviewService;

    @PreAuthorize("@ss.hasPermi('hotel:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfReview lwfReview)
    {
        startPage();
        List<LwfReview> list = lwfReviewService.selectLwfReviewList(lwfReview);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:review:export')")
    @Log(title = "订单评价", businessType = BusinessType.EXPORT)
    @org.springframework.web.bind.annotation.PostMapping("/export")
    public void export(HttpServletResponse response, LwfReview lwfReview)
    {
        List<LwfReview> list = lwfReviewService.selectLwfReviewList(lwfReview);
        ExcelUtil<LwfReview> util = new ExcelUtil<LwfReview>(LwfReview.class);
        util.exportExcel(response, list, "订单评价数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:review:query')")
    @GetMapping(value = "/{reviewId}")
    public AjaxResult getInfo(@PathVariable("reviewId") Long reviewId)
    {
        return success(lwfReviewService.selectLwfReviewByReviewId(reviewId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:review:remove')")
    @Log(title = "订单评价", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reviewIds}")
    public AjaxResult remove(@PathVariable Long[] reviewIds)
    {
        return toAjax(lwfReviewService.deleteLwfReviewByReviewIds(reviewIds));
    }
}
