package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfRoomMapper;
import com.ruoyi.hotel.domain.LwfRoom;
import com.ruoyi.hotel.service.ILwfRoomService;

/**
 * 房型Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfRoomServiceImpl implements ILwfRoomService
{
    @Autowired
    private LwfRoomMapper lwfRoomMapper;

    @Override
    public LwfRoom selectLwfRoomByRoomId(Long roomId)
    {
        return lwfRoomMapper.selectLwfRoomByRoomId(roomId);
    }

    @Override
    public List<LwfRoom> selectLwfRoomList(LwfRoom lwfRoom)
    {
        return lwfRoomMapper.selectLwfRoomList(lwfRoom);
    }

    @Override
    public int insertLwfRoom(LwfRoom lwfRoom)
    {
        return lwfRoomMapper.insertLwfRoom(lwfRoom);
    }

    @Override
    public int updateLwfRoom(LwfRoom lwfRoom)
    {
        return lwfRoomMapper.updateLwfRoom(lwfRoom);
    }

    @Override
    public int deleteLwfRoomByRoomIds(Long[] roomIds)
    {
        return lwfRoomMapper.deleteLwfRoomByRoomIds(roomIds);
    }

    @Override
    public int deleteLwfRoomByRoomId(Long roomId)
    {
        return lwfRoomMapper.deleteLwfRoomByRoomId(roomId);
    }
}
