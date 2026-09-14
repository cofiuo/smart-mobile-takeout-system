<template>
  <div class="wrapper">
    <!-- 头部分 -->
    <Header title="我的收藏" />

    <!-- 收藏商家列表 -->
    <ul class="business">
      <!--: 循环迭代 收藏商家列表部分 Begin -->
      <li v-for="item in favoriteList" :key="item.favoriteId" @click="toBusinessInfo(item.business.businessId)">
        <div class="business-img">
          <img :src="item.business.businessImg">
        </div>
        <div class="business-info">
          <div class="business-info-top">
            <h3>{{ item.business.businessName }}</h3>
            <div class="favorite-icon" @click.stop="delFavorite(item)">
              <img src="../assets/light.png" alt="已收藏" style="width: 5vw; height: 5vw; cursor: pointer;" />
            </div>
          </div>
          <div class="business-info-mid">
            <div class="star">
              <!-- <span v-for="i in 5" :key="i" class="star-icon">
                <svg t="1736531000000" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4313" width="14" height="14"><path d="M908.1 353.1l-253.9-36.9L540.7 86.1c-3.1-6.3-8.2-11.4-14.5-14.5L369.8 53.9c-12.8-6.4-26.8-7.8-39.9-3.9L87.9 154.9c-12.9 4-23.6 14.6-27.6 27.6L35.9 369.8c-3.9 13.1-2.4 27.1 3.9 39.9l151.1 253.9-36.9 253.9c-4 12.9 2.4 26.8 14.5 32.8L369.8 968.1c6.4 3.2 13.3 4.9 20.3 4.9s13.9-1.6 20.3-4.9l253.9-151.1 253.9 36.9c12.9 4 26.8-2.4 32.8-14.5l120.4-320.3c4-12.9-2.4-26.8-14.5-32.8l-253.9-36.9z m-304.5 355.5l-119.8 71.8-71.8 119.8 23.5 61.6 143.3-85.9 143.3 85.9 23.5-61.6-71.8-119.8-119.8-71.8z m0-300l-119.8 71.8-71.8 119.8 23.5 61.6 143.3-85.9 143.3 85.9 23.5-61.6-71.8-119.8-119.8-71.8z" fill="#FFD700" p-id="4314"></path></svg>
              </span> -->
              <span class="star-score">5.0</span>
              <span class="month-sales">月售345单</span>
            </div>
          </div>
          <div class="business-info-bottom">
            <span class="price">¥{{ item.business.starPrice }}起送 | ¥{{ item.business.deliveryPrice }}配送</span>
            <!-- <span class="distance">{{ item.business.distance }}km | {{ item.business.deliveryTime }}分钟</span> -->
          </div>
          <div class="business-info-tag">
            <span class="tag" >{{ item.business.businessExplain }}</span>
          </div>
        </div>
      </li>
      <!--: 循环迭代 收藏商家列表部分 End -->
    </ul>

    <!-- 没有收藏商家时显示 -->
    <div style="text-align: center; margin-top:20vw;" v-if="favoriteList.length <= 0">您还没有收藏任何商家</div>
    <!-- 底部菜单部分 -->
    <Footer></Footer>

  </div>
</template>

<script setup>
import Footer from '../components/Footer.vue'
import Header from '../components/Header.vue'
import { ref, onMounted } from "vue"
import { get, post } from '@/api';
import { useRouter } from "vue-router"
import { getSessionStorage } from '@/common.js'
import { ElMessage } from 'element-plus'
const router = useRouter();  //创建路由对象

const favoriteList = ref([])

const account = getSessionStorage('account')

//跳转到商家详情页面
const toBusinessInfo = (businessId) => {
  router.push({ path: '/businessInfo', query: { businessId: businessId } })
}

//删除收藏
const delFavorite = (favoriteItem) => {
  if (!account) {
    router.push('/login')
    return
  }
  
  const params = {
    accountId: account.accountId,
    businessId: favoriteItem.business.businessId
  }
  
  post('/favorite/del', params, true).then(res => {
    if (res.data.code == 20000) {
      //重新加载收藏列表
      loadFavoriteList()
      ElMessage({
        message: '取消收藏成功',
        type: 'success'
      });
    }
  }).catch(e => { console.log(e) })
}

//加载收藏列表
const loadFavoriteList = () => {
  if (!account) {
    favoriteList.value = []
    return
  }
  
  let url = `/favorite/listFavoriteByAccountId/${account.accountId}`
  get(url).then(res => {
    if (res.data.code == 20000) {
      favoriteList.value = res.data.resultData
    }
  }).catch(e => { console.log(e) })
}

onMounted(() => {
  loadFavoriteList()
})
</script>

<style scoped>
/****************** 总容器 ******************/
.wrapper {
  width: 100%;
  height: 100%;
}

/****************** 商家列表部分 ******************/
.wrapper .business {
  width: 100%;
  margin-top: 0vw;
  margin-bottom: 14vw;
}

.wrapper .business li {
  width: 100%;
  box-sizing: border-box;
  padding: 2.5vw;
  border-bottom: solid 1px #ddd;
  user-select: none;
  cursor: pointer;
  
  display: flex;
  align-items: flex-start;
}

.wrapper .business li .business-img {
  position: relative;
  margin-right: 3vw;
}

.wrapper .business li .business-img img {
  width: 20vw;
  height: 20vw;
  border-radius: 2.5vw;
}

.wrapper .business li .business-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.wrapper .business li .business-info-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1vw;
}

.wrapper .business li .business-info h3 {
  font-size: 3.8vw;
  color: #555;
  margin: 0;
}

.wrapper .business li .business-info .favorite-icon {
  cursor: pointer;
}

.wrapper .business li .business-info-mid {
  margin-bottom: 1vw;
}

.wrapper .business li .business-info .star {
  display: flex;
  align-items: center;
}

.wrapper .business li .business-info .star-icon {
  margin-right: 0.5vw;
}

.wrapper .business li .business-info .star-score {
  font-size: 3vw;
  color: #555;
  margin-right: 2vw;
}

.wrapper .business li .business-info .month-sales {
  font-size: 3vw;
  color: #888;
}

.wrapper .business li .business-info-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1vw;
}

.wrapper .business li .business-info .price {
  font-size: 3vw;
  color: #888;
}

.wrapper .business li .business-info .distance {
  font-size: 3vw;
  color: #888;
}

.wrapper .business li .business-info-tag {
  display: flex;
  flex-wrap: wrap;
}

.wrapper .business li .business-info .tag {
  font-size: 2.5vw;
  color: #fff;
  background-color: #FF7F50;
  padding: 0.5vw 1.5vw;
  border-radius: 1vw;
  margin-right: 1vw;
  margin-bottom: 1vw;
}
</style>