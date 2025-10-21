<template>
  <div class="app-container">
    <!-- 页面标题与景区特色标签 -->
    <div class="page-header mb12">
      <h2 class="title">旅游景区推荐</h2>
      <div class="tag-group">
        <el-tag 
          v-for="(tag, idx) in sceneryTags" 
          :key="idx"
          :class="activeTag === tag.value ? 'active-tag' : ''"
          @click="handleTagClick(tag.value)"
        >
          {{ tag.label }}
        </el-tag>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="80px" class="mb8">
      <el-form-item label="推荐类型" prop="type">
        <el-select
          v-model="queryParams.type"
          placeholder="请选择推荐类型"
          clearable
          @change="handleQuery"
        >
          <el-option label="景点" value="scenery"></el-option>
          <el-option label="酒店" value="hotel"></el-option>
          <el-option label="路线" value="route"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="目的地" prop="destination">
        <el-input
          v-model="queryParams.destination"
          placeholder="请输入目的地"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="价格范围">
        <el-range-picker
          v-model="priceRange"
          range-separator="至"
          start-placeholder="最低价格"
          end-placeholder="最高价格"
          @change="handlePriceChange"
        ></el-range-picker>
      </el-form-item>
      <!-- 新增：地域筛选 -->
      <el-form-item label="地域">
        <el-select
          v-model="queryParams.region"
          placeholder="请选择地域"
          clearable
          @change="handleQuery"
        >
          <el-option label="华东" value="east"></el-option>
          <el-option label="华北" value="north"></el-option>
          <el-option label="西南" value="southwest"></el-option>
          <el-option label="华南" value="south"></el-option>
          <el-option label="西北" value="northwest"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 热门景区固定展示区 -->
    <div class="hot-scenery-section mb16">
      <h3 class="section-title">热门景区推荐</h3>
      <el-carousel :interval="3000" arrow="always" height="320px" class="hot-scenery-carousel">
        <el-carousel-item v-for="(scenery, idx) in hotSceneryList" :key="idx">
          <div class="carousel-item-content">
            <img :src="scenery.imageUrl" class="carousel-img">
            <div class="carousel-info">
              <h4 class="scenery-name">{{ scenery.name }}</h4>
              <div class="scenery-meta">
                <span class="location"><i class="el-icon-location"></i>{{ scenery.address }}</span>
                <span class="score"><i class="el-icon-star"></i>{{ scenery.rate }}分</span>
                <span class="region-tag">{{ scenery.regionText }}</span>
                <span class="price-tag">¥{{ scenery.price }}/{{ scenery.unit }}</span>
              </div>
              <el-button 
                type="primary" 
                size="mini"
                @click="handleDetail(scenery)"
              >
                查看详情
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 更多景区推荐列表 -->
    <div class="recommendation-section">
      <h3 class="section-title">更多景区推荐</h3>
      <el-row :gutter="20" class="mb8">
        <el-col :span="6" v-for="(item, index) in recommendationList" :key="index">
          <el-card :body-style="{ padding: '10px' }">
            <div slot="header" class="clearfix">
              <span>{{ item.name }}</span>
              <div class="float-right">
                <el-rate
                  v-model="item.rate"
                  disabled
                  size="mini"
                  class="mr2"
                ></el-rate>
                <span class="region-badge" :title="item.regionText">{{ item.regionShort }}</span>
              </div>
            </div>
            <div class="recommend-img">
              <img :src="item.imageUrl" class="image">
              <!-- 景区特色标签 -->
              <div class="scenery-badge">
                <el-badge v-for="(badge, bid) in item.badges" :key="bid" type="primary" size="mini">{{ badge }}</el-badge>
              </div>
            </div>
            <div class="recommend-info">
              <p class="desc">{{ item.description }}</p>
              <div class="price">
                <span class="money">¥{{ item.price }}</span>
                <span class="unit">{{ item.unit }}</span>
              </div>
              <el-button
                type="text"
                class="detail-btn"
                @click="handleDetail(item)"
              >查看详情</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 详情弹窗 -->
    <el-dialog :title="detailTitle" :visible.sync="openDetail" width="700px" append-to-body>
      <el-row :gutter="20">
        <el-col :span="10">
          <img :src="detailData.imageUrl" class="detail-img">
          <!-- 弹窗内景区图片预览 -->
          <div class="preview-img-list mt8">
            <img 
              v-for="(img, idx) in detailData.previewImages" 
              :key="idx"
              :src="img"
              class="preview-img"
              @click="detailData.imageUrl = img"
            >
          </div>
        </el-col>
        <el-col :span="14">
          <el-descriptions column="1" border>
            <el-descriptions-item label="名称">{{ detailData.name }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ getTypeName(detailData.type) }}</el-descriptions-item>
            <el-descriptions-item label="地域">{{ detailData.regionText }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ detailData.address }}</el-descriptions-item>
            <el-descriptions-item label="开放时间">{{ detailData.openTime || '09:00-18:00' }}</el-descriptions-item>
            <el-descriptions-item label="价格">{{ detailData.price }} {{ detailData.unit }}</el-descriptions-item>
            <el-descriptions-item label="评分">{{ detailData.rate }} 分</el-descriptions-item>
            <el-descriptions-item label="景区特色">
              <el-tag v-for="(tag, idx) in detailData.badges" :key="idx" size="mini">{{ tag }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="推荐理由">{{ detailData.reason }}</el-descriptions-item>
            <el-descriptions-item label="游玩贴士">{{ detailData.tips || '建议游玩时间2-3小时，可提前在线预约门票' }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openDetail = false">关闭</el-button>
        <el-button type="primary" @click="handleReserve(detailData)">立即预订</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRecommendation } from "@/api/system/recommendation";

export default {
  name: "Recommendation",
  data() {
    return {
      loading: true,
      recommendationList: [],
      total: 0,
      // 查询参数：新增地域筛选
      queryParams: {
        pageNum: 1,
        pageSize: 8,
        type: "scenery",
        destination: "",
        minPrice: null,
        maxPrice: null,
        tag: "",
        region: "" // 地域筛选参数
      },
      priceRange: [],
      openDetail: false,
      detailTitle: "",
      detailData: {},
      // 景区特色标签
      sceneryTags: [
        { label: "全部景区", value: "" },
        { label: "自然风光", value: "natural" },
        { label: "历史文化", value: "historical" },
        { label: "主题乐园", value: "theme" },
        { label: "亲子友好", value: "family" },
        { label: "网红打卡", value: "internet" }
      ],
      activeTag: "",
      // 新增5个景区，共8个热门景区静态数据
      hotSceneryList: [
        // 原有3个景区
        {
          id: 1001,
          name: "黄山风景区",
          type: "scenery",
          address: "安徽省黄山市黄山区",
          price: 190,
          unit: "人/次",
          rate: 4.9,
          region: "east", // 地域标识：华东
          regionText: "华东·安徽",
          regionShort: "皖", // 地域简称
          imageUrl: "https://picsum.photos/id/1036/1200/600",
          description: "世界文化与自然双重遗产，以奇松、怪石、云海、温泉“四绝”著称",
          badges: ["自然风光", "世界遗产", "5A景区"],
          openTime: "06:30-16:30（冬季07:00-16:00）",
          reason: "中国十大名山之一，四季景色各异，云海景观震撼，适合摄影与徒步",
          tips: "建议提前预订住宿，山区温差大需携带外套，推荐乘坐索道上山节省体力",
          previewImages: [
            "https://picsum.photos/id/1036/800/500",
            "https://picsum.photos/id/1039/800/500",
            "https://picsum.photos/id/1043/800/500"
          ]
        },
        {
          id: 1002,
          name: "故宫博物院",
          type: "scenery",
          address: "北京市东城区景山前街4号",
          price: 60,
          unit: "人/次（淡季）",
          rate: 4.8,
          region: "north", // 华北
          regionText: "华北·北京",
          regionShort: "京",
          imageUrl: "https://picsum.photos/id/1067/1200/600",
          description: "中国明清两代的皇家宫殿，世界上现存规模最大、保存最为完整的木质结构古建筑之一",
          badges: ["历史文化", "世界遗产", "5A景区"],
          openTime: "08:30-17:00（4月1日-10月31日），08:30-16:30（11月1日-3月31日）",
          reason: "感受中国古代皇家文化的精髓，建筑布局严谨对称，馆藏文物丰富",
          tips: "需提前7天在官网预约门票，周一闭馆（法定节假日除外），建议租用电子讲解器",
          previewImages: [
            "https://picsum.photos/id/1067/800/500",
            "https://picsum.photos/id/1068/800/500",
            "https://picsum.photos/id/1071/800/500"
          ]
        },
        {
          id: 1003,
          name: "上海迪士尼乐园",
          type: "scenery",
          address: "上海市浦东新区川沙新镇申迪北路753号",
          price: 435,
          unit: "人/次（平日）",
          rate: 4.7,
          region: "east", // 华东
          regionText: "华东·上海",
          regionShort: "沪",
          imageUrl: "https://picsum.photos/id/1058/1200/600",
          description: "中国内地首座迪士尼主题乐园，包含六大主题园区，拥有众多独特游乐项目和演出",
          badges: ["主题乐园", "亲子友好", "网红打卡"],
          openTime: "09:30-21:00（具体时间以官网为准）",
          reason: "适合全年龄段游玩，有经典迪士尼角色互动、花车巡游和夜光幻影秀，充满童话氛围",
          tips: "建议错峰出行，下载官方APP查看排队时间，热门项目可领取快速通行证",
          previewImages: [
            "https://picsum.photos/id/1058/800/500",
            "https://picsum.photos/id/1059/800/500",
            "https://picsum.photos/id/1060/800/500"
          ]
        },
        // 新增景区1：杭州西湖
        {
          id: 1004,
          name: "杭州西湖风景区",
          type: "scenery",
          address: "浙江省杭州市西湖区",
          price: 0,
          unit: "免费（部分景点收费）",
          rate: 4.8,
          region: "east", // 华东
          regionText: "华东·浙江",
          regionShort: "浙",
          imageUrl: "https://picsum.photos/id/1043/1200/600",
          description: "世界文化遗产，以“三面云山一面城”的独特格局闻名，有苏堤、白堤、断桥等经典景点",
          badges: ["自然风光", "世界遗产", "免费景区"],
          openTime: "全天开放（内部景点开放时间08:00-17:00）",
          reason: "湖光山色交相辉映，四季皆有特色，春季赏花、夏季观荷、秋季赏月、冬季赏雪",
          tips: "建议乘坐环湖电瓶车或骑行游览，雷峰塔、三潭印月等景点需单独购票，节假日人流量大",
          previewImages: [
            "https://picsum.photos/id/1043/800/500",
            "https://picsum.photos/id/1044/800/500",
            "https://picsum.photos/id/1045/800/500"
          ]
        },
        // 新增景区2：张家界国家森林公园
        {
          id: 1005,
          name: "张家界国家森林公园",
          type: "scenery",
          address: "湖南省张家界市武陵源区",
          price: 225,
          unit: "人/次（4天有效）",
          rate: 4.9,
          region: "south", // 华南
          regionText: "华南·湖南",
          regionShort: "湘",
          imageUrl: "https://picsum.photos/id/1063/1200/600",
          description: "电影《阿凡达》“哈利路亚山”取景地，以石英砂岩峰林地貌为主，拥有袁家界、杨家界等核心景区",
          badges: ["自然风光", "世界遗产", "5A景区"],
          openTime: "07:30-18:00（冬季08:00-17:30）",
          reason: "独一无二的峰林地貌，宛如仙境，百龙天梯、天门山索道等设施体验震撼，徒步路线丰富",
          tips: "景区面积大，建议规划2-3天行程，可选择住在景区内减少往返时间，穿舒适的徒步鞋",
          previewImages: [
            "https://picsum.photos/id/1063/800/500",
            "https://picsum.photos/id/1064/800/500",
            "https://picsum.photos/id/1065/800/500"
          ]
        },
        // 新增景区3：丽江古城
        {
          id: 1006,
          name: "丽江古城",
          type: "scenery",
          address: "云南省丽江市古城区",
          price: 0,
          unit: "免费（古城维护费50元，自愿缴纳）",
          rate: 4.7,
          region: "southwest", // 西南
          regionText: "西南·云南",
          regionShort: "滇",
          imageUrl: "https://picsum.photos/id/1059/1200/600",
          description: "世界文化遗产，拥有800多年历史，青石板路、小桥流水、纳西族民居，夜晚酒吧街热闹非凡",
          badges: ["历史文化", "世界遗产", "网红打卡"],
          openTime: "全天开放（古城内景点开放时间9:00-18:00）",
          reason: "体验纳西族文化与慢生活，白天逛古城、品特色美食，夜晚赏古城夜景、听民谣",
          tips: "古城内石板路较多，建议穿平底鞋，注意辨别古城内外的商铺价格，避免高价消费",
          previewImages: [
            "https://picsum.photos/id/1059/800/500",
            "https://picsum.photos/id/1060/800/500",
            "https://picsum.photos/id/1061/800/500"
          ]
        },
        // 新增景区4：西安秦始皇陵兵马俑
        {
          id: 1007,
          name: "秦始皇陵兵马俑博物馆",
          type: "scenery",
          address: "陕西省西安市临潼区秦陵北路",
          price: 120,
          unit: "人/次（旺季）",
          rate: 4.8,
          region: "northwest", // 西北
          regionText: "西北·陕西",
          regionShort: "陕",
          imageUrl: "https://picsum.photos/id/1071/1200/600",
          description: "世界第八大奇迹，是秦始皇陵的陪葬坑，出土的兵马俑数量众多、形态各异，展现了秦代军阵风貌",
          badges: ["历史文化", "世界遗产", "5A景区"],
          openTime: "08:30-18:00（4月1日-10月31日），08:30-17:30（11月1日-3月31日）",
          reason: "见证中国古代辉煌的军事与艺术成就，感受秦始皇陵的宏伟气势，适合历史文化爱好者",
          tips: "建议租用官方讲解器或跟随导游，景区内有免费摆渡车往返于兵马俑和丽山园，注意防晒",
          previewImages: [
            "https://picsum.photos/id/1071/800/500",
            "https://picsum.photos/id/1072/800/500",
            "https://picsum.photos/id/1073/800/500"
          ]
        },
        // 新增景区5：三亚亚龙湾
        {
          id: 1008,
          name: "三亚亚龙湾旅游度假区",
          type: "scenery",
          address: "海南省三亚市吉阳区亚龙湾路",
          price: 0,
          unit: "免费（沙滩免费，内部项目收费）",
          rate: 4.7,
          region: "south", // 华南
          regionText: "华南·海南",
          regionShort: "琼",
          imageUrl: "https://picsum.photos/id/1040/1200/600",
          description: "被誉为“天下第一湾”，拥有7公里长的月牙形海湾，沙滩洁白细腻，海水清澈见底，适合滨海度假",
          badges: ["自然风光", "滨海度假", "亲子友好"],
          openTime: "全天开放",
          reason: "热带滨海风光优美，可开展游泳、潜水、沙滩排球等多种水上项目，冬季气候温暖，适合避寒",
          tips: "沙滩紫外线强，需做好防晒措施，周边酒店众多，可选择海景房提升体验，餐饮价格较高",
          previewImages: [
            "https://picsum.photos/id/1040/800/500",
            "https://picsum.photos/id/1041/800/500",
            "https://picsum.photos/id/1042/800/500"
          ]
        }
      ]
    };
  },
  created() {
    this.getList();
    // 初始化：合并热门景区数据到推荐列表
    this.recommendationList = [...this.hotSceneryList, ...this.recommendationList];
    this.total = this.recommendationList.length;
  },
  methods: {
    /** 查询推荐列表：新增地域筛选逻辑 */
    getList() {
      this.loading = true;
      if (process.env.NODE_ENV === "development") {
        let filteredList = [...this.hotSceneryList];
        // 标签筛选
        if (this.queryParams.tag) {
          filteredList = filteredList.filter(item => 
            item.badges.some(badge => badge.includes(this.queryParams.tag))
          );
        }
        // 目的地筛选
        if (this.queryParams.destination) {
          filteredList = filteredList.filter(item => 
            item.address.includes(this.queryParams.destination)
          );
        }
        // 价格筛选
        if (this.queryParams.minPrice && this.queryParams.maxPrice) {
          filteredList = filteredList.filter(item => 
            // 处理免费景区（price=0）的价格筛选逻辑
            (item.price === 0 && this.queryParams.minPrice <= 0) || 
            (item.price >= this.queryParams.minPrice && item.price <= this.queryParams.maxPrice)
          );
        }
        // 新增：地域筛选
        if (this.queryParams.region) {
          filteredList = filteredList.filter(item => 
            item.region === this.queryParams.region
          );
        }
        this.recommendationList = filteredList;
        this.total = filteredList.length;
        this.loading = false;
      } else {
        listRecommendation(this.queryParams).then(response => {
          this.recommendationList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      }
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作：新增地域重置 */
    resetQuery() {
      this.priceRange = [];
      this.activeTag = "";
      this.queryParams.tag = "";
      this.queryParams.region = ""; // 重置地域筛选
      this.$refs.queryForm.resetFields();
      this.queryParams.minPrice = null;
      this.queryParams.maxPrice = null;
      this.queryParams.type = "scenery";
      this.handleQuery();
    },
    /** 价格范围变化 */
    handlePriceChange(val) {
      if (val) {
        this.queryParams.minPrice = val[0];
        this.queryParams.maxPrice = val[1];
      } else {
        this.queryParams.minPrice = null;
        this.queryParams.maxPrice = null;
      }
    },
    /** 查看详情 */
    handleDetail(item) {
      const fullItem = this.hotSceneryList.find(hs => hs.id === item.id) || item;
      this.detailData = {
        previewImages: fullItem.previewImages || [fullItem.imageUrl],
        openTime: fullItem.openTime || "09:00-18:00",
        tips: fullItem.tips || "建议提前规划游玩路线",
        regionText: fullItem.regionText || "",
        ...fullItem
      };
      this.detailTitle = `${this.getTypeName(fullItem.type)}详情`;
      this.openDetail = true;
    },
    /** 获取类型名称 */
    getTypeName(type) {
      const typeMap = {
        scenery: "景点",
        hotel: "酒店",
        route: "路线"
      };
      return typeMap[type] || type;
    },
    /** 立即预订 */
    handleReserve(item) {
      if (item.type === 'hotel') {
        this.$router.push({ path: '/hotel/reservation', query: { id: item.id } });
      } else if (item.type === 'scenery') {
        this.$router.push({ path: '/system/ticket_services/scenery', query: { id: item.id } });
      } else {
        this.$modal.msgSuccess("即将为您跳转到预订页面");
      }
      this.openDetail = false;
    },
    /** 景区标签筛选 */
    handleTagClick(tagValue) {
      this.activeTag = tagValue;
      this.queryParams.tag = tagValue;
      this.handleQuery();
    }
  }
};
</script>

<style scoped>
/* 新增：地域标签样式 */
.region-tag {
  background-color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}
.region-badge {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background-color: #e6f7ff;
  color: #1890ff;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
}

/* 原有样式保留 */
.page-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin: 0;
}
.tag-group {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.active-tag {
  background-color: #409eff;
  color: #fff;
}
.hot-scenery-section {
  margin-bottom: 24px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 4px solid #409eff;
}
.hot-scenery-carousel .el-carousel__item {
  height: 100%;
}
.carousel-item-content {
  position: relative;
  width: 100%;
  height: 100%;
}
.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}
.carousel-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: #fff;
  border-radius: 0 0 4px 4px;
}
.scenery-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px;
}
.scenery-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  margin-bottom: 12px;
  align-items: center;
  flex-wrap: wrap;
}
.price-tag {
  background-color: #f56c6c;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}
.scenery-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  gap: 4px;
}
.preview-img-list {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
.preview-img {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
}
.preview-img:hover {
  border-color: #409eff;
}
.app-container {
  padding: 16px;
}
.mb8 {
  margin-bottom: 8px;
}
.mb12 {
  margin-bottom: 12px;
}
.mb16 {
  margin-bottom: 16px;
}
.mr2 {
  margin-right: 2px;
}
.recommend-img {
  position: relative;
  height: 180px;
  overflow: hidden;
}
.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}
.image:hover {
  transform: scale(1.05);
}
.recommend-info {
  padding: 10px;
}
.desc {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  margin-bottom: 10px;
  height: 40px;
  font-size: 13px;
  color: #666;
}
.price {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.money {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}
.unit {
  font-size: 12px;
  color: #999;
  margin-left: 5px;
}
.detail-btn {
  width: 100%;
  color: #409eff;
  padding: 5px 0;
}
.detail-img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 4px;
}
.dialog-footer {
  text-align: right;
}
</style>