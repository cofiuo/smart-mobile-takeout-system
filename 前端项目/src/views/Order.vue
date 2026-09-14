<template>
  <div class="wrapper">
    <Header title="我的订单" />

    <!-- 状态筛选导航 -->
    <div class="status-nav">
      <div v-for="status in statusList" :key="status.value" class="status-item"
        :class="{ active: currentStatus === status.value }" @click="changeStatus(status.value)">
        {{ status.label }}
      </div>
    </div>

    <!-- 搜索框 -->
    <div class="search-box">
      <input type="text" placeholder="请输入您想要的商家名" v-model="searchKeyword" @input="handleSearchInput" />
      <i class="search-icon" @click="onSearch">🔍</i>
      <i class="clear-icon" v-if="searchKeyword" @click="clearSearch">
        ✕
      </i>
    </div>

    <!-- 订单列表 -->
    <div v-if="filteredOrders.length === 0" class="empty">
      暂无订单
    </div>

    <ul class="order-list" v-else>
      <li v-for="o in filteredOrders" :key="o.orderId" class="order-item">
        <div @click="handleOrderClick(o)">
          <!-- 商家名称和订单状态 -->
          <div class="order-header">
            <div class="shop-name">
              <span class="shop-icon">🏪</span>
              {{ o.business ? o.business.businessName : ('商家ID ' + o.businessId) }}
            </div>
            <div class="order-status" :class="'status-' + o.statu">
              {{ stateText(o.statu) }}
            </div>
          </div>

          <!-- 交易金额和日期 -->
          <div class="order-info">
            <div class="order-amount">
              <span class="label">交易金额:</span>
              <span class="value">¥ {{ Number(o.orderTotal || 0).toFixed(2) }}</span>
            </div>
            <div class="order-date">
              <span class="label">交易日期:</span>
              <span class="value">{{ formatTime(o.created) }}</span>
            </div>
          </div>
        </div>

        <!-- 订单底部：去评价按钮，只有已支付订单才显示 -->
        <div class="order-footer" v-if="o.statu === 1">
          <button class="evaluate-btn" @click="toComment(o.orderId, o.businessId)" v-if="!o.isCommented">
            去评价 &gt;
          </button>
          <div class="evaluated-text" v-else>
            已评价
          </div>
        </div>
      </li>
    </ul>

    <Footer />
  </div>
</template>


<script setup>
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/api/index.js'
import { getSessionStorage } from '@/common.js'

const router = useRouter()
const orders = ref([])
const searchKeyword = ref('')
const currentStatus = ref(-1) // -1表示全部订单

// 状态列表
const statusList = [
  { label: '全部订单', value: -1 },
  { label: '已支付', value: 1 },
  { label: '待支付', value: 0 }
]

const stateText = (s) => {
  const n = Number(s)
  if (n === 0) return '待支付'
  if (n === 1) return '已支付'
  if (n === 2) return '已完成'
  if (n === 3) return '已取消'
  return '未知'
}

const formatTime = (t) => {
  if (!t) return ''
  // 兼容后端返回 LocalDateTime 字符串
  return String(t).replace('T', ' ').slice(0, 19)
}

// 计算属性：根据状态和搜索关键词过滤订单
const filteredOrders = computed(() => {
  let result = [...orders.value]

  // 根据状态筛选
  if (currentStatus.value !== -1) {
    result = result.filter(order => order.statu === currentStatus.value)
  }

  // 根据搜索关键词筛选
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(order => {
      const businessName = order.business ? order.business.businessName.toLowerCase() : ''
      return businessName.includes(keyword)
    })
  }

  return result
})

// 加载订单
const loadOrders = async () => {
  const account = getSessionStorage('account')
  if (!account) {
    router.replace('/login')
    return
  }

  let url = `/orders/listByAccountId/${account.accountId}`
  if (currentStatus.value !== -1) {
    url = `/orders/listByAccountIdAndStatu/${account.accountId}/${currentStatus.value}`
  }

  const r = await get(url)
  if (r.data.code === 20000) {
    orders.value = Array.isArray(r.data.resultData) ? r.data.resultData : []
  } else {
    orders.value = []
  }
}

// 切换订单状态
const changeStatus = (status) => {
  currentStatus.value = status
  loadOrders()
}

// 搜索功能
const onSearch = () => {
  // 搜索逻辑已在计算属性中处理，无需重新请求API
  // 这里可以添加额外的搜索逻辑，比如防抖处理等
}

// 监听搜索框输入变化，实现即时搜索
const handleSearchInput = (event) => {
  searchKeyword.value = event.target.value;
  onSearch();
}

// 清空搜索
const clearSearch = () => {
  searchKeyword.value = '';
  onSearch();
}

// 订单点击事件
const handleOrderClick = (order) => {
  router.push({
    path: '/payment',
    query: {
      orderId: order.orderId,
      businessId: order.businessId,
      orderStatu: order.statu
    }
  })
}



// 去评价
const toComment = (orderId, businessId) => {
  router.push({ path: '/comment', query: { orderId, businessId } })

}

onMounted(loadOrders)
</script>

<style scoped>
/* 页面容器 */
.wrapper {
  min-height: 100vh;
  background-color: #fafafa;
  padding-bottom: 18vw;
  /* 给 Footer 留空间 */
  box-sizing: border-box;
}

/* 顶部标题 */
.header {
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  padding: 4vw 4vw 3vw;
  box-sizing: border-box;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
}

.header h3 {
  margin: 0;
  font-size: 4.6vw;
  font-weight: 800;
  color: #2e3135;
  letter-spacing: 0.5px;
  text-align: center;
}

/* 状态导航 */
.status-nav {
  display: flex;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  margin: 2vw;
  border-radius: 1vw;
  padding: 1vw;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-item {
  flex: 1;
  text-align: center;
  padding: 2vw;
  font-size: 3.5vw;
  font-weight: 600;
  color: #596164;
  border-radius: 0.8vw;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: rgba(255, 255, 255, 0.5);
  margin: 0 0.5vw;
}

.status-item.active {
  background-color: rgba(255, 255, 255, 0.9);
  color: #596164;
  font-weight: 700;
}

/* 搜索框 */
.search-box {
  position: relative;
  margin: 2vw;
  background-color: #fff;
  border-radius: 2vw;
  padding: 2vw;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  background: linear-gradient(to right, #fff1eb, #ace0f9);
}

.search-box input {
  width: 100%;
  padding: 2vw 6vw 2vw 2vw;
  font-size: 3.5vw;
  border: none;
  outline: none;
  border-radius: 2vw;
  background-color: rgba(255, 255, 255, 0.8);
  box-sizing: border-box;
  color: #596164;
}

.search-box .search-icon {
  position: absolute;
  right: 4vw;
  top: 50%;
  transform: translateY(-50%);
  font-size: 3.5vw;
  color: #999;
  cursor: pointer;
}

.search-box .clear-icon {
  position: absolute;
  right: 10vw;
  top: 50%;
  transform: translateY(-50%);
  font-size: 3.5vw;
  color: #999;
  cursor: pointer;
  background-color: #f5f5f5;
  width: 5vw;
  height: 5vw;
  border-radius: 50%;
  text-align: center;
  line-height: 5vw;
}

/* 空态 */
.empty {
  margin: 14vw 4vw 0;
  padding: 10vw 5vw;
  border-radius: 3vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  box-shadow: 0 1.2vw 4vw rgba(15, 23, 42, 0.06);
  color: #596164;
  text-align: center;
  font-size: 3.6vw;
  font-weight: 600;
}

/* 列表 */
.order-list {
  list-style: none;
  padding: 2vw;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 2vw;
}

/* 订单卡片 */
.order-item {
  background: #fff;
  border-radius: 1.5vw;
  padding: 3vw;
  box-sizing: border-box;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(172, 224, 249, 0.5);
}

/* 订单头部：商家名称和状态 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2vw;
}

.shop-name {
  display: flex;
  align-items: center;
  font-size: 3.8vw;
  font-weight: 700;
  color: #333;
}

.shop-icon {
  margin-right: 1vw;
  font-size: 4vw;
}

.order-status {
  font-size: 3vw;
  font-weight: 700;
  padding: 1vw 2vw;
  border-radius: 0.8vw;
  color: #fff;
}

/* 状态样式 */
.status-0 {
  background-color: #ff9800;
}

.status-1 {
  background-color: #4caf50;
}

.status-2 {
  background-color: #2196f3;
}

.status-3 {
  background-color: #f44336;
}

/* 订单信息：交易金额和日期 */
.order-info {
  margin-bottom: 2vw;
}

.order-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5vw;
  font-size: 3.5vw;
}

.order-amount .label {
  color: #666;
}

.order-amount .value {
  font-weight: 700;
  color: #f44336;
}

.order-date {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 3vw;
  color: #999;
}

/* 订单底部：去评价按钮 */
.order-footer {
  display: flex;
  justify-content: flex-end;
}

.evaluate-btn {
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  color: #596164;
  border: none;
  padding: 1.5vw 4vw;
  font-size: 3.2vw;
  font-weight: 600;
  border-radius: 1vw;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.evaluate-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 已评价文本样式 */
.evaluated-text {
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  color: #596164;
  border: none;
  padding: 1.5vw 4vw;
  font-size: 3.2vw;
  font-weight: 600;
  border-radius: 1vw;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: inline-block;
  text-align: center;
}
</style>