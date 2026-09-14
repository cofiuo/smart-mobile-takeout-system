<template>
  <div class="wrapper">
    <!-- 头部分 -->
    <Header title="商家列表" />
    <!-- 搜索栏 -->
    <div class="search">
      <div class="search-top" ref="fixedBox">
        <div class="search-box">
          <i class="search_icon" aria-hidden="true" />
          <input type="text" placeholder="搜索商家名称" />
        </div>
      </div>
    </div>

    <ul class="business">
      <!--: 循环迭代 商家列表部分 Begin -->
      <li v-for="item in businessList" :key="item.businessId" @click="toBusinessInfo(item.businessId)">
        <div class="business-img">
          <img :src="item.businessImg">
          <!-- 角标：显示是购物车中的商品数量 -->
          <div class="business-img-quantity" v-show="item.quantity">
            {{ item.quantity }}
          </div>
        </div>
        <div class="business-info">
          <h3>{{ item.businessName }}</h3>
          <p>&#165; {{ item.starPrice }} 起送 | &#165; {{ item.deliveryPrice }} 配送</p>
          <p>{{ item.businessExplain }}</p>
        </div>
      </li>
      <!--: 循环迭代 商家列表部分 End -->
    </ul>

    <!-- 该分类下商家数组businessList长度小于等于0。显示没有商家数据 -->
    <div style="text-align: center; margin-top:20vw;" v-if="businessList.length <= 0">该类别目前没有商家信息</div>
    <!-- 底部菜单部分 -->
    <Footer></Footer>

  </div>
</template>

<script setup>
import Footer from '../components/Footer.vue'
import Header from '../components/Header.vue'
import { ref } from "vue"
import { get } from '@/api';
import { useRouter, useRoute } from "vue-router"
import { getSessionStorage } from '@/common.js'
const router = useRouter();  //创建路由对象
const route = useRoute(); //获取路由参数

const businessList = ref([])

const account = getSessionStorage('account')

//传递商家id，跳转到商家详情页面
const toBusinessInfo = (businessId) => {
  router.push({ path: '/businessInfo', query: { businessId: businessId } })
}

//加载购物车数据
const loadCart = () => {
  let url = `/cart/listCartByAccountId/${account.accountId}`
  get(url).then(res => {
    if (res.data.code == 20000) {
      let cartArray = res.data.resultData;
      for (let i = 0; i < businessList.value.length; i++) {
        businessList.value[i].quantity = 0;
        for (let cart of cartArray) {
          if (businessList.value[i].businessId == cart.businessId) {
            businessList.value[i].quantity += cart.quantity;
          }
        }
      }
    }
  }).catch(e => { console.log(e) })
}


//通过分类id获取商家列表
const loadBusinessListByCategoryId = () => {
  let categoryId = route.query.categoryId;
  let url = `/business/listByCategoryId/${categoryId}`;
  get(url).then(res => {
    businessList.value = res.data.resultData

    if (account != null) {
      loadCart();
    }
  }).catch(e => { console.log(e) })
}

const init = () => {
  loadBusinessListByCategoryId();
}
init();
</script>

<style scoped>
/****************** 总容器 ******************/
.wrapper {
  width: 100%;
  height: 100%;
}

/****************** header部分 ******************/
/* .wrapper header {
  width: 100%;
  height: 12vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  color: #596164;

  font-size: 4.8vw;
  letter-spacing: 1vw;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;

  display: flex;
  justify-content: center;
  align-items: center;
} */

.wrapper .header {
  width: 100%;
  height: 14vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  display: flex;
  align-items: center;
  justify-content: space-between;

}

.wrapper .header .location-text {
  font-size: 4vw;
  font-weight: 600;
  color: #596164;
  margin: 0 1vw 0 3vw;
  display: flex;
  justify-content: left;
}

.wrapper .header p {
  font-size: 4.5vw;
  letter-spacing: 0.5vw;
  color: #596164;
}

.wrapper .header .location-text .location_icon {
  background-image: url(../assets/location.png);
  width: 6.6vw;
  height: 6.6vw;
  display: block;
  background-size: cover;
}

.wrapper .header .location-text .fa-angle-down {
  margin-left: 1.5vw;
}

/*搜索栏*/
.wrapper .search {
  width: 100%;
  height: 15vw;
  margin-top: -2vw;
  position: relative;
  z-index: 1000;
}

.wrapper .search .search-top {
  width: 100%;
  height: 15vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  display: flex;
  justify-content: center;
  align-items: center;
}

.wrapper .search .search-box {
  width: 90%;
  height: 9vw;
  background-color: #fff;
  border-radius: 4vw;
  border: 0.4vw solid #a1c4fd;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 3.5vw;
  color: #79859E;
  padding: 0 10vw;
  box-sizing: border-box;
}

.wrapper .search .search-box .search_icon {
  background-image: url(../assets/search.png);
  width: 6.6vw;
  height: 6.6vw;
  display: block;
  background-size: cover;
}

.wrapper .search .search-box input {
  width: 100%;
  height: 4.5vw;
  line-height: 4.5vw;
  font-size: 4vw;
  border: none;
  outline: none;
  margin-left: 2vw;
  color: #79859E;
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
  align-items: center;
}

.wrapper .business li .business-img {
  /*这里设置为相当定位，成为business-img-quantity元素的父元素*/
  position: relative;

}

.wrapper .business li .business-img img {
  width: 20vw;
  height: 20vw;
  border-radius: 2.5vw;
}

.wrapper .business li .business-img .business-img-quantity {
  width: 5vw;
  height: 5vw;
  background-color: red;
  color: #fff;
  font-size: 3.6vw;
  border-radius: 2.5vw;

  display: flex;
  justify-content: center;
  align-items: center;

  /*设置成绝对定位，不占文档流空间*/
  position: absolute;
  right: -1.5vw;
  top: -1.5vw;
}

.wrapper .business li .business-info {
  margin-left: 3vw;
}

.wrapper .business li .business-info h3 {
  font-size: 3.8vw;
  color: #555;
}

.wrapper .business li .business-info p {
  font-size: 3vw;
  color: #888;
  margin-top: 2vw;
}
</style>