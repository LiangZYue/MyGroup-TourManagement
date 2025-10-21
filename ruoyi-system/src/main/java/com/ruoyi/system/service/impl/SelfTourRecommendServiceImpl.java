package com.ruoyi.system.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SelfTourRecommendMapper;
import com.ruoyi.system.domain.SelfTourRecommend;
import com.ruoyi.system.service.ISelfTourRecommendService;

/**
 * 旅游推荐服务实现类
 * 
 * @author lzy
 * @date 2025-10-21
 */
@Service
public class SelfTourRecommendServiceImpl implements ISelfTourRecommendService 
{
    @Autowired
    private SelfTourRecommendMapper selfTourRecommendMapper;

    /**
     * 查询旅游推荐
     * 
     * @param id 旅游推荐主键
     * @return 旅游推荐
     */
    @Override
    public SelfTourRecommend selectSelfTourRecommendById(Long id)
    {
        return selfTourRecommendMapper.selectSelfTourRecommendById(id);
    }

    /**
     * 查询旅游推荐列表
     * 
     * @param selfTourRecommend 旅游推荐
     * @return 旅游推荐
     */
    @Override
    public List<SelfTourRecommend> selectSelfTourRecommendList(SelfTourRecommend selfTourRecommend)
    {
        return selfTourRecommendMapper.selectSelfTourRecommendList(selfTourRecommend);
    }

    /**
     * 新增旅游推荐
     * 
     * @param selfTourRecommend 旅游推荐
     * @return 结果
     */
    @Override
    public int insertSelfTourRecommend(SelfTourRecommend selfTourRecommend)
    {
        return selfTourRecommendMapper.insertSelfTourRecommend(selfTourRecommend);
    }

    /**
     * 修改旅游推荐
     * 
     * @param selfTourRecommend 旅游推荐
     * @return 结果
     */
    @Override
    public int updateSelfTourRecommend(SelfTourRecommend selfTourRecommend)
    {
        return selfTourRecommendMapper.updateSelfTourRecommend(selfTourRecommend);
    }

    /**
     * 批量删除旅游推荐
     * 
     * @param ids 需要删除的旅游推荐主键
     * @return 结果
     */
    @Override
    public int deleteSelfTourRecommendByIds(Long[] ids)
    {
        return selfTourRecommendMapper.deleteSelfTourRecommendByIds(ids);
    }

    /**
     * 删除旅游推荐信息
     * 
     * @param id 旅游推荐主键
     * @return 结果
     */
    @Override
    public int deleteSelfTourRecommendById(Long id)
    {
        return selfTourRecommendMapper.deleteSelfTourRecommendById(id);
    }
    
    /**
     * 基于用户偏好推荐景点
     * 
     * @param spotType 景点类型
     * @param suitableCrowd 适合人群
     * @return 推荐景点列表
     */
    @Override
    public List<SelfTourRecommend> recommendByPreference(String spotType, String suitableCrowd)
    {
        return selfTourRecommendMapper.selectRecommendByPreference(spotType, suitableCrowd);
    }
    
    /**
     * 基于协同过滤算法推荐景点
     * 
     * @param userId 用户ID
     * @return 推荐景点列表
     */
    @Override
    public List<SelfTourRecommend> recommendByCollaborativeFiltering(Long userId)
    {
        // 1. 获取所有景点
        SelfTourRecommend query = new SelfTourRecommend();
        query.setStatus("0"); // 只查询正常状态的景点
        List<SelfTourRecommend> allSpots = selfTourRecommendMapper.selectSelfTourRecommendList(query);
        
        if (allSpots.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 2. 模拟用户-景点评分矩阵（实际项目中应从数据库获取）
        Map<Long, Map<Long, Double>> userRatings = simulateUserRatings(userId, allSpots);
        
        // 3. 计算用户相似度
        Map<Long, Double> userSimilarities = calculateUserSimilarity(userId, userRatings);
        
        // 4. 基于相似用户的偏好进行推荐
        List<SelfTourRecommend> recommendations = generateRecommendations(userId, userRatings, userSimilarities, allSpots);
        
        // 5. 按推荐指数排序并返回前10个
        return recommendations.stream()
                .sorted(Comparator.comparing(SelfTourRecommend::getRecommendIndex).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
    
    /**
     * 模拟用户-景点评分矩阵
     * 实际项目中应从数据库获取真实数据
     */
    private Map<Long, Map<Long, Double>> simulateUserRatings(Long userId, List<SelfTourRecommend> allSpots) {
        Map<Long, Map<Long, Double>> userRatings = new HashMap<>();
        
        // 模拟5个用户的评分数据
        for (long uid = 1; uid <= 5; uid++) {
            Map<Long, Double> ratings = new HashMap<>();
            
            // 为每个用户随机生成对部分景点的评分
            for (SelfTourRecommend spot : allSpots) {
                // 模拟70%的景点有评分
                if (Math.random() < 0.7) {
                    // 评分范围1-5
                    double rating = 1 + Math.random() * 4;
                    ratings.put(spot.getId(), rating);
                }
            }
            
            userRatings.put(uid, ratings);
        }
        
        // 如果传入的userId不在模拟数据中，添加一个空的评分集
        if (!userRatings.containsKey(userId)) {
            userRatings.put(userId, new HashMap<>());
        }
        
        return userRatings;
    }
    
    /**
     * 计算用户相似度（使用余弦相似度）
     */
    private Map<Long, Double> calculateUserSimilarity(Long userId, Map<Long, Map<Long, Double>> userRatings) {
        Map<Long, Double> similarities = new HashMap<>();
        Map<Long, Double> userRating = userRatings.get(userId);
        
        for (Map.Entry<Long, Map<Long, Double>> entry : userRatings.entrySet()) {
            Long otherUserId = entry.getKey();
            
            // 跳过自己
            if (otherUserId.equals(userId)) {
                continue;
            }
            
            Map<Long, Double> otherRating = entry.getValue();
            
            // 找出两个用户共同评价过的景点
            Set<Long> commonItems = new HashSet<>(userRating.keySet());
            commonItems.retainAll(otherRating.keySet());
            
            // 如果没有共同评价的景点，相似度为0
            if (commonItems.isEmpty()) {
                similarities.put(otherUserId, 0.0);
                continue;
            }
            
            // 计算余弦相似度
            double dotProduct = 0.0;
            double normA = 0.0;
            double normB = 0.0;
            
            for (Long itemId : commonItems) {
                dotProduct += userRating.get(itemId) * otherRating.get(itemId);
                normA += Math.pow(userRating.get(itemId), 2);
                normB += Math.pow(otherRating.get(itemId), 2);
            }
            
            double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
            similarities.put(otherUserId, similarity);
        }
        
        return similarities;
    }
    
    /**
     * 基于相似用户的偏好生成推荐
     */
    private List<SelfTourRecommend> generateRecommendations(Long userId, 
                                                          Map<Long, Map<Long, Double>> userRatings,
                                                          Map<Long, Double> userSimilarities,
                                                          List<SelfTourRecommend> allSpots) {
        Map<Long, Double> userRating = userRatings.get(userId);
        Map<Long, Double> predictedRatings = new HashMap<>();
        
        // 对于用户未评价的每个景点，预测评分
        for (SelfTourRecommend spot : allSpots) {
            // 跳过用户已评价的景点
            if (userRating.containsKey(spot.getId())) {
                continue;
            }
            
            double weightedSum = 0.0;
            double similaritySum = 0.0;
            
            // 基于其他用户的评分和相似度计算预测评分
            for (Map.Entry<Long, Double> entry : userSimilarities.entrySet()) {
                Long otherUserId = entry.getKey();
                Double similarity = entry.getValue();
                
                // 如果相似度太低，忽略
                if (similarity < 0.1) {
                    continue;
                }
                
                Map<Long, Double> otherRating = userRatings.get(otherUserId);
                
                // 如果其他用户评价过这个景点
                if (otherRating.containsKey(spot.getId())) {
                    weightedSum += similarity * otherRating.get(spot.getId());
                    similaritySum += similarity;
                }
            }
            
            // 计算加权平均评分
            if (similaritySum > 0) {
                double predictedRating = weightedSum / similaritySum;
                predictedRatings.put(spot.getId(), predictedRating);
            }
        }
        
        // 根据预测评分筛选推荐景点
        return allSpots.stream()
                .filter(spot -> predictedRatings.containsKey(spot.getId()))
                .peek(spot -> {
                    // 将预测评分转换为推荐指数（1-100）
                    int recommendIndex = (int) (predictedRatings.get(spot.getId()) * 20);
                    spot.setRecommendIndex(recommendIndex);
                })
                .collect(Collectors.toList());
    }
}