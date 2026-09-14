<template>
  <div class="wrapper">
    <!-- 头部分 -->
    <Header title="商家评论" :back="true" />

    <div class="business-logo">
            <img :src="businessInfo.businessImg">
        </div>

        <div class="business-info">
            <h1>{{businessInfo.businessName}}</h1>
            <p style="color:#333; align">&#165;  {{businessInfo.starPrice}} 起送  &nbsp;|&nbsp;  &#165; {{businessInfo.deliveryPrice}} 配送</p>
            <p>{{businessInfo.businessExplain}}</p>
    </div>

    <!-- 评论筛选导航 -->
    <div class="comment-filter">
      <div 
        v-for="filter in filterOptions" 
        :key="filter.value" 
        class="filter-item" 
        :class="{ active: currentFilter === filter.value }"
        @click="changeFilter(filter.value)"
      >
        {{ filter.label }}
      </div>
    </div>
    
    <!-- 评论列表 -->
    <div class="comment-container">
      <div v-if="commentList.length === 0" class="no-comment">
        <p>暂无评论~</p>

      </div>
      
      <div class="comment-item" v-for="comment in commentList" :key="comment.coId">
        <!-- 用户信息 -->
        <div class="account-info">
          <img :src="comment.account.accountImg" class="account-img" />
          <div class="account-name">{{ comment.account.accountName }}</div>
        </div>
        
        <!-- 评论内容 -->
        <div class="comment-content"  >
          <!-- 评分 -->
          <div class="comment-rate">
            <div class="stars"> 
              <el-rate v-model="comment.rate" disabled allow-half />
            </div>
            <span class="rate-text">{{ comment.rate }}</span>
          </div>
          
          <!-- 评论文字 -->
          <div class="comment-text">{{ comment.coText }}</div>
          
          <!-- 评论图片 -->
          <div v-if="comment.coImg" class="comment-img">
            <img :src="comment.coImg" />
          </div>
          
          <!-- 评论时间 -->
          <div class="comment-time">{{ formatTime(comment.created) }}</div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';
import { ref, onMounted } from 'vue';
import { getSessionStorage } from '@/common.js';
import { get } from '@/api/index.js';
import { ElMessage } from 'element-plus';
import {useRouter,useRoute} from "vue-router"

//创建路由对象
const router = useRouter();
const route = useRoute();
const account = getSessionStorage('account');
const allComments = ref([]); // 存储所有评论
const commentList = ref([]); // 显示的评论列表
const businessId = route.query.businessId;
const businessInfo = ref({})

// 筛选选项
const filterOptions = [
  { label: '全部评论', value: 'all' },
  { label: '好评', value: 'good' },
  { label: '差评', value: 'bad' }
];

// 当前筛选状态
const currentFilter = ref('all');

//根据id查询商家信息
const loadBusinessById=()=>{
  let url = `/business/info/${businessId}`;
  get(url).then(res=>{
        businessInfo.value = res.data.resultData;
      }).catch(e=>{ console.log(e); });
}
// 获取评论列表数据
const loadComments = () => {
  if (!account) {
    ElMessage({
      message: '请先登录',
      type: 'warning'
    });
    return;
  }
  
  // 根据businessId获取评论列表
  get(`/comment/listByBusinessId/${businessId}`).then(res => {
    if (res.data.code === 20000) {
      allComments.value = res.data.resultData;
      filterComments(); // 筛选评论
    } else {
      ElMessage({
        message: res.data.message,
        type: 'error'
      });
    }
  }).catch(err => {
    console.error('获取评论列表失败:', err);
    ElMessage({
      message: '获取评论列表失败',
      type: 'error'
    });
  });
};

// 筛选评论函数
const filterComments = () => {
  switch (currentFilter.value) {
    case 'good':
      // 好评：3分及以上
      commentList.value = allComments.value.filter(comment => comment.rate >= 3);
      break;
    case 'bad':
      // 差评：3分以下
      commentList.value = allComments.value.filter(comment => comment.rate < 3);
      break;
    default:
      // 全部评论
      commentList.value = allComments.value;
      break;
  }
};

// 切换筛选条件
const changeFilter = (filter) => {
  currentFilter.value = filter;
  filterComments();
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};

// 初始化
onMounted(() => {
  loadBusinessById()
  loadComments();
});
</script>

<style scoped>
.wrapper {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 12vw;
}

/* 评论筛选导航 */
.comment-filter {
  display: flex;
  background-color: #fff;
  margin: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.filter-item {
  flex: 1;
  padding: 12px 0;
  text-align: center;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-item:hover {
  background-color: #f5f5f5;
}

.filter-item.active {
  background-color: #fff1eb;
  color: #596164;
  font-weight: 600;
}

/* 评论容器 */
.comment-container {
  padding: 10px;
}

/* 无评论提示 */
.no-comment {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #999;
  font-size: 16px;
  background-color: #fff;
  border-radius: 8px;
  margin: 10px;
}

/* 评论项 */
.comment-item {
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 10px;
  padding: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 用户信息 */
.account-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.account-img {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  margin-right: 10px;
  object-fit: cover;
}

.account-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

/* 评论内容 */
.comment-content {
  padding-left: 50px;
}

/* 评分 */
.comment-rate {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.stars {
  display: flex;
  margin-right: 8px;
}

.stars .el-icon-star-on {
  color: #e6e6e6;
  font-size: 18px;
  margin-right: 4px;
}

.stars .el-icon-star-on.active {
  color: #fcba26;
}

.stars .el-icon-star-on.half {
  color: #fcba26;
  position: relative;
  overflow: hidden;
}

.stars .el-icon-star-on.half::after {
  content: '\e78b';
  position: absolute;
  left: 0;
  top: 0;
  color: #e6e6e6;
  width: 50%;
  overflow: hidden;
}

.rate-text {
  color: #fcba26;
  font-size: 14px;
  font-weight: 500;
}

/* 评论文字 */
.comment-text {
  color: #333;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 10px;
  word-break: break-word;
}

/* 评论图片 */
.comment-img {
  margin-bottom: 10px;
}

.comment-img img {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  object-fit: cover;
  border: 1px solid #eee;
}

/* 评论时间 */
.comment-time {
  color: #999;
  font-size: 12px;
}

/****************** 商家logo部分 ******************/
    .wrapper .business-logo {  
      width: 100%;
      height: 35vw;
      /*使用上外边距避开header部分*/
  
      display: flex;
      justify-content: center;
      align-items: center;
      
    }
    .wrapper .business-logo img {
      width: 30vw;
      height: 30vw;
      border-radius: 5vw;
      border: 2vw solid #efefef;
    }
  /****************** 商家信息部分 ******************/
    .wrapper .business-info {
      width: 100%;
      height: 20vw;
  
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }
    .wrapper .business-info h1 {
      font-size: 4vw;
      margin-top: 2vw;
    }
    .wrapper .business-info p {
      font-size: 3vw;
      color: #999;
      margin-top: 1vw;
      align-items: left;
    }
    .wrapper .disabledf{
      cursor:not-allowed;/*禁止鼠标样式 */
      pointer-events:none;
    }
</style>