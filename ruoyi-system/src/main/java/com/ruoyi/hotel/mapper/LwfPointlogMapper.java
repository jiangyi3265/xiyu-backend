package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfPointlog;

/**
 * 积分记录Mapper接口
 *
 * @author liwangfu
 */
public interface LwfPointlogMapper
{
    public LwfPointlog selectLwfPointlogByLogId(Long logId);

    public List<LwfPointlog> selectLwfPointlogList(LwfPointlog lwfPointlog);

    public int insertLwfPointlog(LwfPointlog lwfPointlog);

    public int updateLwfPointlog(LwfPointlog lwfPointlog);

    public int deleteLwfPointlogByLogId(Long logId);

    public int deleteLwfPointlogByLogIds(Long[] logIds);
}
