<template>
  <div class="comment-page">
    <!-- 顶部标题栏 -->
    <Header title="在线评论"></Header>

    <!-- 核心评论区 -->
    <div class="comment-content" :model="commentList">
      <!-- 满意度提问 -->
      <p class="question">这次的购物让您满意吗？</p>

      <!-- 商家信息卡片 -->
      <div class="shop-card">
        <!-- 商家头像/标识 -->
        <div class="shop-avatar">
          <img :src="businessInfo.businessImg" alt="小谷姐姐" class="avatar-img" /><!--:src="businessInfo.bussinessImg"-->
          <!-- 新店标签 -->
          <div class="new-tag" v-if="businessInfo.remarks">{{ businessInfo.remarks }}</div>
        </div>
        <!-- 商家名称 -->
        <h2 class="shop-name">{{ businessInfo.businessName }}</h2>
        <!-- 商家标签 -->
        <div class="shop-tag">{{ businessInfo.businessExplain }}</div>
        <!-- 星级评分 -->
        <div class="star-rating">
          <el-rate v-model="commentList.rate" allow-half />
        </div>
      </div>

      <!-- 评论输入框 -->
      <div class="input-area">
        <textarea placeholder="说点什么吧..." class="comment-input" v-model="commentList.commentContent"></textarea>
      </div>


      <!-- 提交按钮 -->
      <button class="submit-btn" @click="submitComment">
        <span class="icon">📝</span> 提交评论
      </button>
    </div>
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue';
import { ElMessage } from 'element-plus';
import { useRouter, useRoute } from "vue-router"
import { getSessionStorage } from '@/common.js';
import { reactive, ref } from 'vue';
import { get, post } from "@/api";

//创建路由对象
const router = useRouter();
const route = useRoute();
//从路由中接受商家id
const businessId = route.query.businessId;
//获得当前账户
const account = getSessionStorage('account');
//获得当前订单id
const orderId = route.query.orderId;
//评分分数和评论内容绑定
const commentList = reactive({
  rate: 0,
  commentContent: ''
})
//商家信息
const businessInfo = ref({})

//加载商家信息
const loadBusinessById = () => {
  let url = `/business/info/${businessId}`;
  get(url).then(res => {
    businessInfo.value = res.data.resultData;
  }).catch(e => { console.log(e); });

  console.log('商家信息：', businessInfo)
}

// 提交评论方法
const submitComment = () => {
  if (!commentList.commentContent.trim() || commentList.rate == 0) {
    ElMessage({
      message: '提交的评论或评分不能为空',
      type: 'warning',
    })
  }
  if (commentList.rate != 0 && commentList.commentContent != '') {
    let comment = {
      accountId: account.accountId,
      businessId: businessId,
      orderId: orderId,
      rate: commentList.rate,
      coText: commentList.commentContent
    }
    post('/comment/add', comment, true).then(res => {
      if (res.data.code == 20000) {
        ElMessage({
          message: '评论提交成功',
          type: 'success'
        })
        // 延迟返回主界面，让用户看到提交成功提示
        setTimeout(() => {
          router.push({ path: '/' });
        }, 1500);
      }
    }).catch(e => { console.log(e) })
  }

  console.log('提交的评论：', commentList.rate, commentList.commentContent)
}

const init = () => {
  loadBusinessById()
}
init()
</script>

<style scoped>
/* 全局适配 */
.comment-page {
  width: 100%;
  max-width: 750px;
  margin: 0 auto;
  background: #fff;
  min-height: 100vh;
  box-sizing: border-box;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.back-btn {
  background: transparent;
  border: none;
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  margin-right: 15px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  flex: 1;
  text-align: center;
  margin: 0;
}

/* 核心评论区 */
.comment-content {
  padding: 20px;
}

.question {
  font-size: 16px;
  color: #333;
  margin: 0 0 20px 0;
  text-align: center;
}

/* 商家卡片 */
.shop-card {
  text-align: center;
  margin-bottom: 25px;
}

.shop-avatar {
  position: relative;
  display: inline-block;
  margin-bottom: 10px;
}

.avatar-img {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  object-fit: cover;
}

.new-tag {
  position: absolute;
  top: 0;
  left: 0;
  background: #4caf50;
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 3px;
}

.shop-name {
  font-size: 16px;
  color: #333;
  margin: 5px 0;
}

.shop-tag {
  font-size: 12px;
  color: #666;
  background: #f5f5f5;
  padding: 3px 10px;
  border-radius: 12px;
  display: inline-block;
  margin-bottom: 10px;
}

.star-rating {
  color: #ccc;
  font-size: 20px;
  letter-spacing: 5px;
}

/* 输入框区域 */
.input-area {
  margin-bottom: 20px;
}

.comment-input {
  width: 100%;
  min-height: 120px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  box-sizing: border-box;
  font-size: 14px;
  resize: none;
  outline: none;
}

.comment-input::placeholder {
  color: #999;
}

/* 图片上传区域 */
.upload-area {
  margin-bottom: 30px;
}

.upload-text {
  font-size: 14px;
  color: #666;
  margin: 0 0 10px 0;
}

.upload-box {
  width: 80px;
  height: 80px;
  border: 1px dashed #e0e0e0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.plus-icon {
  font-size: 24px;
  color: #ccc;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  background: #f9a825;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 15px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.submit-btn:active {
  background: #f57f17;
}

.icon {
  font-size: 18px;
}
</style>