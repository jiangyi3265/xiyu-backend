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
import com.ruoyi.hotel.domain.LwfProduct;
import com.ruoyi.hotel.service.ILwfProductService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 产品Controller
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/product")
public class LwfProductController extends BaseController
{
    @Autowired
    private ILwfProductService lwfProductService;

    @PreAuthorize("@ss.hasPermi('hotel:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfProduct lwfProduct)
    {
        startPage();
        List<LwfProduct> list = lwfProductService.selectLwfProductList(lwfProduct);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:product:export')")
    @Log(title = "产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LwfProduct lwfProduct)
    {
        List<LwfProduct> list = lwfProductService.selectLwfProductList(lwfProduct);
        ExcelUtil<LwfProduct> util = new ExcelUtil<LwfProduct>(LwfProduct.class);
        util.exportExcel(response, list, "产品数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:product:query')")
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable("productId") Long productId)
    {
        return success(lwfProductService.selectLwfProductByProductId(productId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:product:add')")
    @Log(title = "产品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LwfProduct lwfProduct)
    {
        lwfProduct.setCreateBy(getUsername());
        return toAjax(lwfProductService.insertLwfProduct(lwfProduct));
    }

    @PreAuthorize("@ss.hasPermi('hotel:product:edit')")
    @Log(title = "产品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LwfProduct lwfProduct)
    {
        lwfProduct.setUpdateBy(getUsername());
        return toAjax(lwfProductService.updateLwfProduct(lwfProduct));
    }

    @PreAuthorize("@ss.hasPermi('hotel:product:remove')")
    @Log(title = "产品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        return toAjax(lwfProductService.deleteLwfProductByProductIds(productIds));
    }
}
