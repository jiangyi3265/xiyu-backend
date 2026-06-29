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
import com.ruoyi.hotel.domain.LwfRoom;
import com.ruoyi.hotel.service.ILwfRoomService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 房型Controller
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/room")
public class LwfRoomController extends BaseController
{
    @Autowired
    private ILwfRoomService lwfRoomService;

    @PreAuthorize("@ss.hasPermi('hotel:room:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfRoom lwfRoom)
    {
        startPage();
        List<LwfRoom> list = lwfRoomService.selectLwfRoomList(lwfRoom);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:room:export')")
    @Log(title = "房型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LwfRoom lwfRoom)
    {
        List<LwfRoom> list = lwfRoomService.selectLwfRoomList(lwfRoom);
        ExcelUtil<LwfRoom> util = new ExcelUtil<LwfRoom>(LwfRoom.class);
        util.exportExcel(response, list, "房型数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:room:query')")
    @GetMapping(value = "/{roomId}")
    public AjaxResult getInfo(@PathVariable("roomId") Long roomId)
    {
        return success(lwfRoomService.selectLwfRoomByRoomId(roomId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:room:add')")
    @Log(title = "房型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LwfRoom lwfRoom)
    {
        lwfRoom.setCreateBy(getUsername());
        return toAjax(lwfRoomService.insertLwfRoom(lwfRoom));
    }

    @PreAuthorize("@ss.hasPermi('hotel:room:edit')")
    @Log(title = "房型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LwfRoom lwfRoom)
    {
        lwfRoom.setUpdateBy(getUsername());
        return toAjax(lwfRoomService.updateLwfRoom(lwfRoom));
    }

    @PreAuthorize("@ss.hasPermi('hotel:room:remove')")
    @Log(title = "房型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roomIds}")
    public AjaxResult remove(@PathVariable Long[] roomIds)
    {
        return toAjax(lwfRoomService.deleteLwfRoomByRoomIds(roomIds));
    }
}
