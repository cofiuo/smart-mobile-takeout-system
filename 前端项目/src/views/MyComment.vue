<template>
  <div class="wrapper">
    <!-- 头部分 -->
    <Header title="我的评论" :back="true" />

    <!-- 评论列表 -->
    <div class="comment-container">
      <div v-if="commentList.length === 0" class="no-comment">
        <p>暂无评论，快去下单体验吧~</p>
      </div>

      <div class="comment-item" v-for="comment in commentList" :key="comment.coId">
        <!-- 商家信息 -->
        <div class="business-info">
          <img :src="comment.business.businessImg" class="business-img" />
          <div class="business-name">{{ comment.business.businessName }}</div>
        </div>

        <!-- 评论内容 -->
        <div class="comment-content">
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

const account = getSessionStorage('account');
const commentList = ref([]);

// 获取评论列表数据
const loadComments = () => {
  if (!account) {
    ElMessage({
      message: '请先登录',
      type: 'warning'
    });
    return;
  }

  // 根据accountId获取评论列表
  get(`/comment/listByAccountId/${account.accountId}`).then(res => {
    if (res.data.code === 20000) {
      commentList.value = res.data.resultData;
      console.log(commentList);
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

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};

// 初始化
onMounted(() => {
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

/* 商家信息 */
.business-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.business-img {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  margin-right: 10px;
  object-fit: cover;
}

.business-name {
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
</style>