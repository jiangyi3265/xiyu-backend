package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfRoom;

/**
 * 房型Service接口
 *
 * @author liwangfu
 */
public interface ILwfRoomService
{
    public LwfRoom selectLwfRoomByRoomId(Long roomId);

    public List<LwfRoom> selectLwfRoomList(LwfRoom lwfRoom);

    public int insertLwfRoom(LwfRoom lwfRoom);

    public int updateLwfRoom(LwfRoom lwfRoom);

    public int deleteLwfRoomByRoomIds(Long[] roomIds);

    public int deleteLwfRoomByRoomId(Long roomId);
}
