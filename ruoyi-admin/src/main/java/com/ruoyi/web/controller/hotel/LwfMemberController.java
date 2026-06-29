package com.ruoyi.web.controller.hotel;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.hotel.domain.LwfMember;
import com.ruoyi.hotel.service.ILwfMemberService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 会员Controller
 *
 * @author liwangfu
 */
@RestController
@RequestMapping("/hotel/member")
public class LwfMemberController extends BaseController
{
    @Autowired
    private ILwfMemberService lwfMemberService;

    @PreAuthorize("@ss.hasPermi('hotel:member:list')")
    @GetMapping("/list")
    public TableDataInfo list(LwfMember lwfMember)
    {
        startPage();
        List<LwfMember> list = lwfMemberService.selectLwfMemberList(lwfMember);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hotel:member:export')")
    @Log(title = "会员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LwfMember lwfMember)
    {
        List<LwfMember> list = lwfMemberService.selectLwfMemberList(lwfMember);
        ExcelUtil<LwfMember> util = new ExcelUtil<LwfMember>(LwfMember.class);
        util.exportExcel(response, list, "会员数据");
    }

    @PreAuthorize("@ss.hasPermi('hotel:member:query')")
    @GetMapping(value = "/{memberId}")
    public AjaxResult getInfo(@PathVariable("memberId") Long memberId)
    {
        return success(lwfMemberService.selectLwfMemberByMemberId(memberId));
    }

    @PreAuthorize("@ss.hasPermi('hotel:member:add')")
    @Log(title = "会员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LwfMember lwfMember)
    {
        if (lwfMemberService.selectLwfMemberByPhone(lwfMember.getPhone()) != null)
        {
            return error("手机号已存在");
        }
        // 密码：填写则加密，否则给默认密码 123456
        if (StringUtils.isNotEmpty(lwfMember.getPassword()))
        {
            lwfMember.setPassword(SecurityUtils.encryptPassword(lwfMember.getPassword()));
        }
        else
        {
            lwfMember.setPassword(SecurityUtils.encryptPassword("123456"));
        }
        lwfMember.setCreateBy(getUsername());
        return toAjax(lwfMemberService.insertLwfMember(lwfMember));
    }

    @PreAuthorize("@ss.hasPermi('hotel:member:edit')")
    @Log(title = "会员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LwfMember lwfMember)
    {
        // 密码：填写则加密更新，留空则不修改
        if (StringUtils.isNotEmpty(lwfMember.getPassword()))
        {
            lwfMember.setPassword(SecurityUtils.encryptPassword(lwfMember.getPassword()));
        }
        else
        {
            lwfMember.setPassword(null);
        }
        lwfMember.setUpdateBy(getUsername());
        return toAjax(lwfMemberService.updateLwfMember(lwfMember));
    }

    @PreAuthorize("@ss.hasPermi('hotel:member:remove')")
    @Log(title = "会员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{memberIds}")
    public AjaxResult remove(@PathVariable Long[] memberIds)
    {
        return toAjax(lwfMemberService.deleteLwfMemberByMemberIds(memberIds));
    }
}
