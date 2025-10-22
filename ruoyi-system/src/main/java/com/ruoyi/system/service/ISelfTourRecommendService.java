package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SelfTourRecommend;

/**
 * 旅游推荐服务接口
 * 
 * @author lzy
 * @date 2025-10-21
 */
public interface ISelfTourRecommendService 
{
    /**
     * 查询旅游推荐
     * 
     * @param id 旅游推荐主键
     * @return 旅游推荐
     */
    public SelfTourRecommend selectSelfTourRecommendById(Long id);

    /**
     * 查询旅游推荐列表
     * 
     * @param selfTourRecommend 旅游推荐
     * @return 旅游推荐集合
     */
    public List<SelfTourRecommend> selectSelfTourRecommendList(SelfTourRecommend selfTourRecommend);

    /**
     * 新增旅游推荐
     * 
     * @param selfTourRecommend 旅游推荐
     * @return 结果
     */
    public int insertSelfTourRecommend(SelfTourRecommend selfTourRecommend);

    /**
     * 修改旅游推荐
     * 
     * @param selfTourRecommend 旅游推荐
     * @return 结果
     */
    public int updateSelfTourRecommend(SelfTourRecommend selfTourRecommend);

    /**
     * 批量删除旅游推荐
     * 
     * @param ids 需要删除的旅游推荐主键集合
     * @return 结果
     */
    public int deleteSelfTourRecommendByIds(Long[] ids);

    /**
     * 删除旅游推荐信息
     * 
     * @param id 旅游推荐主键
     * @return 结果
     */
    public int deleteSelfTourRecommendById(Long id);
    
    /**
     * 基于用户偏好推荐景点
     * 
     * @param spotType 景点类型
     * @param suitableCrowd 适合人群
     * @return 推荐景点列表
     */
    public List<SelfTourRecommend> recommendByPreference(String spotType, String suitableCrowd);
    
    /**
     * 基于协同过滤算法推荐景点
     * 
     * @param userId 用户ID
     * @return 推荐景点列表
     */
    public List<SelfTourRecommend> recommendByCollaborativeFiltering(Long userId);
}