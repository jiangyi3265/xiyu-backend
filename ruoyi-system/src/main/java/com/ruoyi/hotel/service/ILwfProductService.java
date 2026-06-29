package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfProduct;

/**
 * 产品Service接口
 *
 * @author liwangfu
 */
public interface ILwfProductService
{
    public LwfProduct selectLwfProductByProductId(Long productId);

    public List<LwfProduct> selectLwfProductList(LwfProduct lwfProduct);

    public int insertLwfProduct(LwfProduct lwfProduct);

    public int updateLwfProduct(LwfProduct lwfProduct);

    public int deleteLwfProductByProductIds(Long[] productIds);

    public int deleteLwfProductByProductId(Long productId);
}
