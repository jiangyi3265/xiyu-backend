package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfRoom;

/**
 * 房型Mapper接口
 *
 * @author liwangfu
 */
public interface LwfRoomMapper
{
    public LwfRoom selectLwfRoomByRoomId(Long roomId);

    public List<LwfRoom> selectLwfRoomList(LwfRoom lwfRoom);

    public int insertLwfRoom(LwfRoom lwfRoom);

    public int updateLwfRoom(LwfRoom lwfRoom);

    public int deleteLwfRoomByRoomId(Long roomId);

    public int deleteLwfRoomByRoomIds(Long[] roomIds);
}
