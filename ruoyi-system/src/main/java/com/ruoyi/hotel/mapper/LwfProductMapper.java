package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfProduct;

/**
 * 产品Mapper接口
 *
 * @author liwangfu
 */
public interface LwfProductMapper
{
    public LwfProduct selectLwfProductByProductId(Long productId);

    public List<LwfProduct> selectLwfProductList(LwfProduct lwfProduct);

    public int insertLwfProduct(LwfProduct lwfProduct);

    public int updateLwfProduct(LwfProduct lwfProduct);

    public int deleteLwfProductByProductId(Long productId);

    public int deleteLwfProductByProductIds(Long[] productIds);
}
