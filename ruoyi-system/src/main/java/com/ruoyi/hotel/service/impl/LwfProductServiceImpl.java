package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfProductMapper;
import com.ruoyi.hotel.domain.LwfProduct;
import com.ruoyi.hotel.service.ILwfProductService;

/**
 * 产品Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfProductServiceImpl implements ILwfProductService
{
    @Autowired
    private LwfProductMapper lwfProductMapper;

    @Override
    public LwfProduct selectLwfProductByProductId(Long productId)
    {
        return lwfProductMapper.selectLwfProductByProductId(productId);
    }

    @Override
    public List<LwfProduct> selectLwfProductList(LwfProduct lwfProduct)
    {
        return lwfProductMapper.selectLwfProductList(lwfProduct);
    }

    @Override
    public int insertLwfProduct(LwfProduct lwfProduct)
    {
        return lwfProductMapper.insertLwfProduct(lwfProduct);
    }

    @Override
    public int updateLwfProduct(LwfProduct lwfProduct)
    {
        return lwfProductMapper.updateLwfProduct(lwfProduct);
    }

    @Override
    public int deleteLwfProductByProductIds(Long[] productIds)
    {
        return lwfProductMapper.deleteLwfProductByProductIds(productIds);
    }

    @Override
    public int deleteLwfProductByProductId(Long productId)
    {
        return lwfProductMapper.deleteLwfProductByProductId(productId);
    }
}
