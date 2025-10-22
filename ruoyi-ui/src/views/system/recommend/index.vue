<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="景点名称" prop="spotName">
        <el-input
          v-model="queryParams.spotName"
          placeholder="请输入景点名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="景点类型" prop="spotType">
        <el-select v-model="queryParams.spotType" placeholder="请选择景点类型" clearable>
          <el-option
            v-for="dict in dict.type.spot_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="适合人群" prop="suitableCrowd">
        <el-select v-model="queryParams.suitableCrowd" placeholder="请选择适合人群" clearable>
          <el-option
            v-for="dict in dict.type.suitable_crowd"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:recommend:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:recommend:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:recommend:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:recommend:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="景点列表" name="list">
        <el-table v-loading="loading" :data="recommendList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="ID" align="center" prop="id" />
          <el-table-column label="景点名称" align="center" prop="spotName" />
          <el-table-column label="景点位置" align="center" prop="spotLocation" />
          <el-table-column label="推荐指数" align="center" prop="recommendIndex">
            <template slot-scope="scope">
              <el-rate
                v-model="scope.row.recommendIndex"
                :max="5"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                disabled
                :score-template="scope.row.recommendIndex"
              />
            </template>
          </el-table-column>
          <el-table-column label="景点类型" align="center" prop="spotType">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.spot_type" :value="scope.row.spotType"/>
            </template>
          </el-table-column>
          <el-table-column label="适合人群" align="center" prop="suitableCrowd">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.suitable_crowd" :value="scope.row.suitableCrowd"/>
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" prop="status">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['system:recommend:edit']"
              >修改</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['system:recommend:remove']"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-tab-pane>
      
      <el-tab-pane label="个性化推荐" name="recommend">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="景点类型">
            <el-select v-model="preferenceParams.spotType" placeholder="请选择景点类型">
              <el-option
                v-for="dict in dict.type.spot_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="适合人群">
            <el-select v-model="preferenceParams.suitableCrowd" placeholder="请选择适合人群">
              <el-option
                v-for="dict in dict.type.suitable_crowd"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getRecommendByPreference">获取推荐</el-button>
          </el-form-item>
        </el-form>
        
        <el-row :gutter="20" class="recommend-cards">
          <el-col :span="8" v-for="(item, index) in recommendedSpots" :key="index">
            <el-card :body-style="{ padding: '0px' }" class="recommend-card">
              <img :src="item.imageUrl || 'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png'" class="image">
              <div style="padding: 14px;">
                <span>{{ item.spotName }}</span>
                <div class="bottom clearfix">
                  <el-rate
                    v-model="item.recommendIndex"
                    :max="5"
                    :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                    disabled
                    :score-template="item.recommendIndex"
                  />
                  <el-button type="text" class="button" @click="showSpotDetail(item)">查看详情</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
      
      <el-tab-pane label="协同过滤推荐" name="collaborative">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="用户ID">
            <el-input v-model="userId" placeholder="请输入用户ID"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getRecommendByCollaborative">获取推荐</el-button>
          </el-form-item>
        </el-form>
        
        <el-row :gutter="20" class="recommend-cards">
          <el-col :span="8" v-for="(item, index) in collaborativeSpots" :key="index">
            <el-card :body-style="{ padding: '0px' }" class="recommend-card">
              <img :src="item.imageUrl || 'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png'" class="image">
              <div style="padding: 14px;">
                <span>{{ item.spotName }}</span>
                <div class="bottom clearfix">
                  <el-rate
                    v-model="item.recommendIndex"
                    :max="5"
                    :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                    disabled
                    :score-template="item.recommendIndex"
                  />
                  <el-button type="text" class="button" @click="showSpotDetail(item)">查看详情</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加或修改旅游推荐对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="景点名称" prop="spotName">
          <el-input v-model="form.spotName" placeholder="请输入景点名称" />
        </el-form-item>
        <el-form-item label="景点描述" prop="spotDescription">
          <el-input v-model="form.spotDescription" type="textarea" placeholder="请输入景点描述" />
        </el-form-item>
        <el-form-item label="景点位置" prop="spotLocation">
          <el-input v-model="form.spotLocation" placeholder="请输入景点位置" />
        </el-form-item>
        <el-form-item label="推荐指数" prop="recommendIndex">
          <el-slider v-model="form.recommendIndex" :max="100"></el-slider>
        </el-form-item>
        <el-form-item label="景点类型" prop="spotType">
          <el-select v-model="form.spotType" placeholder="请选择景点类型">
            <el-option
              v-for="dict in dict.type.spot_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="适合人群" prop="suitableCrowd">
          <el-select v-model="form.suitableCrowd" placeholder="请选择适合人群">
            <el-option
              v-for="dict in dict.type.suitable_crowd"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="最佳时间" prop="bestVisitTime">
          <el-input v-model="form.bestVisitTime" placeholder="请输入最佳游览时间" />
        </el-form-item>
        <el-form-item label="景点图片" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入景点图片URL" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    
    <!-- 景点详情对话框 -->
    <el-dialog title="景点详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>{{ detailForm.spotName }}</span>
        </div>
        <div class="text item">
          <el-image
            style="width: 100%; max-height: 300px"
            :src="detailForm.imageUrl || 'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png'"
            fit="cover"></el-image>
          <el-descriptions class="margin-top" title="景点信息" :column="1" border>
            <el-descriptions-item label="景点位置">{{ detailForm.spotLocation }}</el-descriptions-item>
            <el-descriptions-item label="景点类型">{{ detailForm.spotType }}</el-descriptions-item>
            <el-descriptions-item label="适合人群">{{ detailForm.suitableCrowd }}</el-descriptions-item>
            <el-descriptions-item label="最佳游览时间">{{ detailForm.bestVisitTime }}</el-descriptions-item>
            <el-descriptions-item label="推荐指数">
              <el-rate
                v-model="detailForm.recommendIndex"
                :max="5"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                disabled
                :score-template="detailForm.recommendIndex"
              />
            </el-descriptions-item>
            <el-descriptions-item label="景点描述">{{ detailForm.spotDescription }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>
    </el-dialog>
  </div>
</template>

<script>
import { listRecommend, getRecommend, delRecommend, addRecommend, updateRecommend } from "@/api/system/recommend";

export default {
  name: "Recommend",
  dicts: ['sys_normal_disable', 'spot_type', 'suitable_crowd'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 旅游推荐表格数据
      recommendList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 当前激活的标签页
      activeTab: 'list',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        spotName: null,
        spotType: null,
        suitableCrowd: null,
        status: null
      },
      // 表单参数
      form: {},
      // 详情表单
      detailForm: {},
      // 表单校验
      rules: {
        spotName: [
          { required: true, message: "景点名称不能为空", trigger: "blur" }
        ],
        spotType: [
          { required: true, message: "景点类型不能为空", trigger: "change" }
        ]
      },
      // 偏好推荐参数
      preferenceParams: {
        spotType: null,
        suitableCrowd: null
      },
      // 推荐景点列表
      recommendedSpots: [],
      // 协同过滤推荐景点列表
      collaborativeSpots: [],
      // 用户ID
      userId: 1
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询旅游推荐列表 */
    getList() {
      this.loading = true;
      listRecommend(this.queryParams).then(response => {
        this.recommendList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        spotName: null,
        spotDescription: null,
        spotLocation: null,
        recommendIndex: 50,
        spotType: null,
        suitableCrowd: null,
        bestVisitTime: null,
        imageUrl: null,
        status: "0",
        remark: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加旅游推荐";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getRecommend(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改旅游推荐";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRecommend(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addRecommend(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除旅游推荐编号为"' + ids + '"的数据项？').then(function() {
        return delRecommend(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/recommend/export', {
        ...this.queryParams
      }, `recommend_${new Date().getTime()}.xlsx`)
    },
    /** 标签页切换 */
    handleTabClick(tab) {
      this.activeTab = tab.name;
    },
    /** 基于偏好获取推荐 */
    getRecommendByPreference() {
      this.$modal.loading("正在获取推荐...");
      this.$http.get('/system/recommend/preference', {
        params: this.preferenceParams
      }).then(res => {
        this.recommendedSpots = res.data;
        // 将推荐指数转换为1-5的评分
        this.recommendedSpots.forEach(item => {
          item.recommendIndex = Math.round(item.recommendIndex / 20);
        });
        this.$modal.closeLoading();
      }).catch(() => {
        this.$modal.closeLoading();
      });
    },
    /** 基于协同过滤获取推荐 */
    getRecommendByCollaborative() {
      if (!this.userId) {
        this.$modal.msgError("请输入用户ID");
        return;
      }
      this.$modal.loading("正在获取推荐...");
      this.$http.get(`/system/recommend/collaborative/${this.userId}`).then(res => {
        this.collaborativeSpots = res.data;
        // 将推荐指数转换为1-5的评分
        this.collaborativeSpots.forEach(item => {
          item.recommendIndex = Math.round(item.recommendIndex / 20);
        });
        this.$modal.closeLoading();
      }).catch(() => {
        this.$modal.closeLoading();
      });
    },
    /** 显示景点详情 */
    showSpotDetail(spot) {
      this.detailForm = JSON.parse(JSON.stringify(spot));
      this.detailOpen = true;
    }
  }
};
</script>

<style scoped>
.recommend-cards {
  margin-top: 20px;
}
.recommend-card {
  margin-bottom: 20px;
}
.image {
  width: 100%;
  height: 200px;
  display: block;
  object-fit: cover;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}
.bottom {
  margin-top: 13px;
  line-height: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.button {
  padding: 0;
  float: right;
}
</style>