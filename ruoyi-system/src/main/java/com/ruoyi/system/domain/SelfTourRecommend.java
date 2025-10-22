package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 旅游推荐对象 self_tour_recommend
 * 
 * @author lzy
 * @date 2025-10-21
 */
public class SelfTourRecommend extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 推荐ID */
    private Long id;

    /** 景点名称 */
    @Excel(name = "景点名称")
    private String spotName;

    /** 景点描述 */
    @Excel(name = "景点描述")
    private String spotDescription;

    /** 景点位置 */
    @Excel(name = "景点位置")
    private String spotLocation;

    /** 推荐指数 */
    @Excel(name = "推荐指数")
    private Integer recommendIndex;

    /** 景点类型 */
    @Excel(name = "景点类型")
    private String spotType;

    /** 适合人群 */
    @Excel(name = "适合人群")
    private String suitableCrowd;

    /** 最佳游览时间 */
    @Excel(name = "最佳游览时间")
    private String bestVisitTime;

    /** 景点图片URL */
    @Excel(name = "景点图片URL")
    private String imageUrl;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSpotName(String spotName) 
    {
        this.spotName = spotName;
    }

    public String getSpotName() 
    {
        return spotName;
    }
    public void setSpotDescription(String spotDescription) 
    {
        this.spotDescription = spotDescription;
    }

    public String getSpotDescription() 
    {
        return spotDescription;
    }
    public void setSpotLocation(String spotLocation) 
    {
        this.spotLocation = spotLocation;
    }

    public String getSpotLocation() 
    {
        return spotLocation;
    }
    public void setRecommendIndex(Integer recommendIndex) 
    {
        this.recommendIndex = recommendIndex;
    }

    public Integer getRecommendIndex() 
    {
        return recommendIndex;
    }
    public void setSpotType(String spotType) 
    {
        this.spotType = spotType;
    }

    public String getSpotType() 
    {
        return spotType;
    }
    public void setSuitableCrowd(String suitableCrowd) 
    {
        this.suitableCrowd = suitableCrowd;
    }

    public String getSuitableCrowd() 
    {
        return suitableCrowd;
    }
    public void setBestVisitTime(String bestVisitTime) 
    {
        this.bestVisitTime = bestVisitTime;
    }

    public String getBestVisitTime() 
    {
        return bestVisitTime;
    }
    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("spotName", getSpotName())
            .append("spotDescription", getSpotDescription())
            .append("spotLocation", getSpotLocation())
            .append("recommendIndex", getRecommendIndex())
            .append("spotType", getSpotType())
            .append("suitableCrowd", getSuitableCrowd())
            .append("bestVisitTime", getBestVisitTime())
            .append("imageUrl", getImageUrl())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}