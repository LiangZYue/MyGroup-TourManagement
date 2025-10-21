package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SelfTourRecommend;

/**
 * 旅游推荐Mapper接口
 * 
 * @author lzy
 * @date 2025-10-21
 */
public interface SelfTourRecommendMapper 
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
     * 删除旅游推荐
     * 
     * @param id 旅游推荐主键
     * @return 结果
     */
    public int deleteSelfTourRecommendById(Long id);

    /**
     * 批量删除旅游推荐
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSelfTourRecommendByIds(Long[] ids);
    
    /**
     * 根据用户偏好获取推荐景点
     * 
     * @param spotType 景点类型
     * @param suitableCrowd 适合人群
     * @return 推荐景点列表
     */
    public List<SelfTourRecommend> selectRecommendByPreference(String spotType, String suitableCrowd);
}