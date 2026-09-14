<template>
  <div class="wrapper">
    <!-- header部分 -->
    <!-- <header>
      <p>在线客服</p>
    </header> -->
    <Header title="AI客服系统 - 订单查询" />

    <div class="customer-service">
      <el-container style="height: 100vh; border: 1px solid #eee;">
        <!-- <el-header style="text-align: center; font-size: 20px; line-height: 60px;">
          AI客服系统 - 订单查询
        </el-header> -->
        <el-main>
          <!-- 对话窗口 -->
          <div class="chat-container">
            <div class="chat-messages" ref="chatMessages">
              <!-- 系统欢迎语 -->
              <div class="message system-message">
                <div class="avatar">🤖</div>
                <div class="content">您好！我是智能客服，请问有什么可以帮助您的？</div>
              </div>

              <!-- 用户消息 -->
              <div v-for="(msg, index) in messageList" :key="index"
                :class="['message', msg.type === 'user' ? 'user-message' : 'system-message']">
                <div class="avatar">{{ msg.type === 'user' ? '👤' : '🤖' }}</div>
                <div class="content">{{ msg.content }}</div>
              </div>
            </div>

            <!-- 输入框 -->
            <div class="chat-input">
              <el-input v-model="inputMessage" placeholder="请输入您的问题（如：查询我的订单）" @keyup.enter="sendMessage"></el-input>
              <el-button type="success" @click="sendMessage" style="margin-left: 10px;">发送</el-button>
            </div>
          </div>
        </el-main>
      </el-container>
    </div>
    <Footer></Footer>
  </div>
</template>

<script setup>
import Header from "@/components/Header.vue"
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from "axios"
import Footer from "@/components/Footer.vue"

// 输入框内容
const inputMessage = ref('')
// 消息列表
const messageList = reactive([])
// 聊天窗口DOM
const chatMessages = ref(null)

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入内容后发送！')
    return
  }

  // 添加用户消息到列表
  const userMsg = {
    type: 'user',
    content: inputMessage.value.trim()
  }
  messageList.push(userMsg)

  // 清空输入框
  const tempMsg = inputMessage.value.trim()
  inputMessage.value = ''

  try {
    // 第一步：调用AI对话接口，获取回复   await 同步
    const chatRes = await axios.post('http://localhost:10002/ai/chat', {
      message: tempMsg
    })

    console.log(chatRes);

    const aiReply = chatRes.data
    // 添加AI回复到列表
    messageList.push({
      type: 'system',
      content: aiReply
    })


    // 滚动到最新消息
    scrollToBottom()
  } catch (error) {
    ElMessage.error('服务异常，请稍后重试！')
    messageList.push({
      type: 'system',
      content: '抱歉，系统暂时无法响应，请稍后再试。'
    })
  }
}

// 滚动到聊天窗口底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessages.value) {
      chatMessages.value.scrollTop = chatMessages.value.scrollHeight
    }
  })
}

// 解决Vue3 nextTick导入
const nextTick = (fn) => {
  Promise.resolve().then(fn)
}

onMounted(() => {
  // 初始化滚动到底部
  scrollToBottom()
})
</script>

<style scoped>
.wrapper {
  width: 100%;
  height: 100%;
  /* padding-top: 10vw; */
  box-sizing: border-box;
}

/****************** header部分 ******************/
.wrapper header {
  width: 100%;
  height: 12vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  color: #596164;
  letter-spacing: 2vw;
  font-size: 4.8vw;

  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;

  display: flex;
  justify-content: center;
  align-items: center;
}

.customer-service {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", "\5FAE\8F6F\96C5\9ED1", Arial, sans-serif;
}

.el-main {
  padding: 0;
}

.chat-container {
  width: 100%;
  margin: 0 auto;
  height: 88%;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  border: 1px solid #e2d1c3;
  border-radius: 1vw;
  padding: 20px;
  overflow-y: auto;
}

.message {
  display: flex;
  margin-bottom: 15px;
  max-width: 100%;

}

.user-message {
  flex-direction: row-reverse;
  margin-left: auto;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin: 0 10px;
}

.content {
  padding: 10px 15px;
  border-radius: 18px;
  background-color: #f0f0f0;
  line-height: 1.5;
}

.user-message .content {
  background-color: #409eff;
  color: white;
}

.chat-input {
  margin: 0;
  display: flex;
  align-items: center;
  padding: 2vw 4vw;


}

.chat-input .el-input {
  flex: 1;
  border: none;
  height: 10vw;
  line-height: 10vw;
  color: #596164
}

.chat-input .el-button {
  height: 10vw;
}
</style>