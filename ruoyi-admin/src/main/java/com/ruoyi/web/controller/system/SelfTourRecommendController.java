package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SelfTourRecommend;
import com.ruoyi.system.service.ISelfTourRecommendService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 旅游推荐Controller
 * 
 * @author lzy
 * @date 2025-10-21
 */
@RestController
@RequestMapping("/system/recommend")
public class SelfTourRecommendController extends BaseController
{
    @Autowired
    private ISelfTourRecommendService selfTourRecommendService;

    /**
     * 查询旅游推荐列表
     */
    @PreAuthorize("@ss.hasPermi('system:recommend:list')")
    @GetMapping("/list")
    public TableDataInfo list(SelfTourRecommend selfTourRecommend)
    {
        startPage();
        List<SelfTourRecommend> list = selfTourRecommendService.selectSelfTourRecommendList(selfTourRecommend);
        return getDataTable(list);
    }

    /**
     * 导出旅游推荐列表
     */
    @PreAuthorize("@ss.hasPermi('system:recommend:export')")
    @Log(title = "旅游推荐", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SelfTourRecommend selfTourRecommend)
    {
        List<SelfTourRecommend> list = selfTourRecommendService.selectSelfTourRecommendList(selfTourRecommend);
        ExcelUtil<SelfTourRecommend> util = new ExcelUtil<SelfTourRecommend>(SelfTourRecommend.class);
        return util.exportExcel(list, "旅游推荐数据");
    }

    /**
     * 获取旅游推荐详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:recommend:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(selfTourRecommendService.selectSelfTourRecommendById(id));
    }

    /**
     * 新增旅游推荐
     */
    @PreAuthorize("@ss.hasPermi('system:recommend:add')")
    @Log(title = "旅游推荐", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SelfTourRecommend selfTourRecommend)
    {
        return toAjax(selfTourRecommendService.insertSelfTourRecommend(selfTourRecommend));
    }

    /**
     * 修改旅游推荐
     */
    @PreAuthorize("@ss.hasPermi('system:recommend:edit')")
    @Log(title = "旅游推荐", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SelfTourRecommend selfTourRecommend)
    {
        return toAjax(selfTourRecommendService.updateSelfTourRecommend(selfTourRecommend));
    }

    /**
     * 删除旅游推荐
     */
    @PreAuthorize("@ss.hasPermi('system:recommend:remove')")
    @Log(title = "旅游推荐", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(selfTourRecommendService.deleteSelfTourRecommendByIds(ids));
    }
    
    /**
     * 基于用户偏好推荐景点
     */
    @GetMapping("/preference")
    public AjaxResult recommendByPreference(String spotType, String suitableCrowd)
    {
        List<SelfTourRecommend> list = selfTourRecommendService.recommendByPreference(spotType, suitableCrowd);
        return success(list);
    }
    
    /**
     * 基于协同过滤算法推荐景点
     */
    @GetMapping("/collaborative/{userId}")
    public AjaxResult recommendByCollaborative(@PathVariable("userId") Long userId)
    {
        List<SelfTourRecommend> list = selfTourRecommendService.recommendByCollaborativeFiltering(userId);
        return success(list);
    }
}