<template>
  <div class="wrapper">
    <!-- 使用Header组件 -->
    <Header title="商家详情" />
    <!-- 搜索栏 -->
    <div class="search">
      <div class="search-top" ref="fixedBox">
        <div class="search-box">
          <i class="search_icon" aria-hidden="true" />
          <input type="text" placeholder="搜索商品信息" />
        </div>
      </div>
    </div>

    <!-- 商家信息banner -->
    <div class="business-info-banner">
      <!-- 查看全部评论 -->
      <div class="top-right">
        <div class="AllComment" @click="toBusinessComment()">
          查看评论
          <i class="el-icon-d-arrow-right"></i>
        </div>
      </div>

      <div class="business-logo">
        <img :src="business.businessImg">
      </div>

      <div class=" business-info">
        <h1>{{ business.businessName }}</h1>
        <p style="color:#333; align">&#165; {{ business.starPrice }} 起送 &nbsp;|&nbsp; &#165; {{ business.deliveryPrice
        }}
          配送</p>
        <p>{{ business.businessExplain }}</p>
      </div>
    </div>


    <!-- 商品信息 -->
    <ul class="food">
      <!--TODO: 循环迭代显示 商家售卖 商品数组信息 Begin -->
      <li v-for="(item, index) in goodsList" :key="item.goodsId">

        <div class="food-left">
          <!-- 根据remark属性是否有值，动态显示 -->
          <div class="dish-img-feature" v-if="item.remarks && item.remarks.trim()">
            {{ item.remarks }}
          </div>
          <img :src="item.goodsImg">
          <div class="food-left-info">
            <h3>{{ item.goodsName }} </h3>
            <p>{{ item.goodsExplain }}</p>
            <p class="price">&#165; <em>{{ item.goodsPrice }}</em></p>
          </div>
        </div>
        <div class="food-right">
          <!-- disabled 标签失效(不可用)  -->
          <div @click="minus(index)" :class="{ disabled: item.quantity == 0 }">
            <!--减去餐品数量按钮：如果数量等于或者小于0，那么按钮不显示  v-show="item.quantity!=0"-->
            <el-icon>
              <RemoveFilled :style="item.quantity == 0 ? 'color:#ccc' : ''" />
            </el-icon>
          </div>
          <p>
            <span> {{ item.quantity }} </span>
            <!-- <span v-show="true">1</span> -->
          </p>
          <div @click="add(index)">
            <el-icon>
              <CirclePlusFilled />
            </el-icon>
          </div>
        </div>
        <div class="mask" v-if="item.statu !== 1">
          <span>抱歉, 该商品已下架</span>
        </div>
      </li>



      <!--TODO: 循环迭代显示 商家售卖 餐品信息 End -->
    </ul>

    <!--TODO: 购物车部分 Begin-->
    <div class="cart">
      <div class="cart-left">
        <!--购物车中没有商品，则显示灰色图标 -->
        <div class="cart-left-icon"
          :style="totalQuantity == 0 ? 'background-color:#cecece' : 'background-color:#fb8b06'">
          <el-icon>
            <ShoppingCartFull />
          </el-icon>
          <div class="cart-left-icon-quantity">
            {{ totalQuantity }}
          </div>
        </div>
        <div class="cart-left-info">
          <!-- 餐品总价 -->
          <p>&#165; {{ totalPrice }}</p>
          <p>另需配送费 {{ business.deliveryPrice }} 元</p>
        </div>
      </div>
      <div class="cart-right">
        <!-- 不够起送费显示内容  -->
        <div v-if="totalPrice >= business.starPrice" class="cart-right-item" @click="toOrderConfirm()">
          去结算
        </div>
        <div v-else class="cart-right-item" :style="'background-color:#CECECE'">
          &#165; {{ business.starPrice }} 起送
        </div>
      </div>
    </div>
    <!--TODO: 购物车部分End-->
  </div>
</template>

<script setup>
import Header from "@/components/Header.vue";
import { ref, computed } from "vue"
import { get, post } from "@/api";
import { useRouter, useRoute } from "vue-router"
import { ElMessage } from 'element-plus'
import { getSessionStorage } from '@/common.js'
import Decimal from "decimal.js";

//创建路由对象
const router = useRouter();
const route = useRoute();

// 商家详细信息对象
const business = ref({});

const businessId = route.query.businessId;

//good详细信息对象
const goodsList = ref([]);


const account = getSessionStorage('account');


const toBusinessComment = () => {
  router.push({ path: '/BusinessComment', query: { businessId: businessId } })
}

const toOrderConfirm = () => {
  router.push({
    path: '/orderConfirm',
    query: {
      businessId: businessId,
    }
  });
}

// 使用商家id获取数据
const loadBusinessById = () => {
  let url = `/business/info/${businessId}`;
  get(url).then(res => {
    if (res.data.code === 20000) {
      business.value = res.data.resultData;
    } else {
      ElMessage({
        message: res.data.message,
        type: 'error',
      });
    }
  }).catch(e => { console.log(e) })
}

//使用商家id获取货物信息
const loadGoodsByBusinessId = () => {
  let url = `/goods/listByBusinessId/${businessId}`;
  get(url).then(res => {
    let tmpeArray = res.data.resultData;
    for (let i = 0; i < tmpeArray.length; i++) {
      //更新购物车中数量
      if (tmpeArray[i].quantity == null) {
        tmpeArray[i].quantity = 0;
      }
    }
    goodsList.value = tmpeArray;

    //获取购物车信息
    if (account != null) {
      loadCart();
    }

  }).catch(e => { console.log(e) })
}

//使用账户id获取购物车信息
const loadCart = () => {
  let url = `/cart/listCart/${account.accountId}/${businessId}`;
  get(url).then(res => {
    if (res.data.code === 20000) {
      let cartList = res.data.resultData;

      for (let goods of goodsList.value) {
        goods.quantity = 0;

        for (let cartGoods of cartList) {
          if (goods.goodsId == cartGoods.goodsId) {
            goods.quantity = cartGoods.quantity;
          }
        }
      }
    }
  }).catch(e => { console.log(e) })
}

// 减少商品数量
const minus = (index) => {
  if (account == null) {
    router.push('/login');
    return;
  }

  if (goodsList.value[index].quantity > 1) {
    updateCart(index, -1)
  } else {
    let cart = {
      goodsId: goodsList.value[index].goodsId,
      businessId: businessId,
      accountId: account.accountId,
    }
    post('/cart/del', cart, true).then(res => {
      if (res.data.code === 20000) {
        //同步更新页面和数据库的数据
        goodsList.value[index].quantity = 0;
      }
    }).catch(e => { console.log(e) })

  }
}

// 添加商品数量
const add = (index) => {
  if (account == null) {
    router.push('/login');
    return;
  }
  if (goodsList.value[index].quantity == 0) {
    //调用服务器，录入购物车记录
    let cart = {
      goodsId: goodsList.value[index].goodsId,
      businessId: businessId,
      accountId: account.accountId,
      quantity: 1
    }
    post('/cart/add', cart, true).then(res => {
      if (res.data.code === 20000) {
        //同步更新页面和数据库的数据
        goodsList.value[index].quantity = 1;
      }

    }).catch(e => { console.log(e) })
  } else {
    //更新数量
    updateCart(index, 1)

  }
}

//调用服务器端，执行购物车记录更新
const updateCart = (index, num) => {
  let url = `/cart/update`;

  let cart = {
    goodsId: goodsList.value[index].goodsId,
    businessId: goodsList.value[index].businessId,
    accountId: account.accountId,
    quantity: goodsList.value[index].quantity + num
  }

  post(url, cart, true).then(res => {
    if (res.data.code === 20000) {
      //同步更新页面和数据库的数据
      goodsList.value[index].quantity += num;
    }
  }).catch(e => { console.log(e) })
}



// 计算购物车总数量
const totalQuantity = computed(() => {
  let c = 0;
  for (let goods of goodsList.value) {
    c += goods.quantity;
  }
  return c;
})

// 计算购物车总价
const totalPrice = computed(() => {
  let s = 0;
  for (let goods of goodsList.value) {
    s = new Decimal(goods.goodsPrice).mul(goods.quantity).add(s).toNumber();
  }
  return s;
})

const init = () => {
  loadBusinessById();
  loadGoodsByBusinessId();
}
init();

</script>

<style scoped>
.wrapper .hidd {
  visibility: hidden;
}

.wrapper .show {
  visibility: visible;
}

/****************** 总容器 ******************/
.wrapper {
  width: 100%;
  height: 100%;
  padding-bottom: 15vw;
}

/****************** header部分 ******************/
.wrapper header {
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
}

.wrapper .business-info-banner {

  padding: 0;
  margin: 0;
  margin-top: 3vw;
}

.wrapper .business-info-banner .top-right {
  float: right;
  margin-right: 4vw;
  margin-bottom: 6vw;
  border-radius: 2.5vw;
  padding: 1vw 2vw;
}

.wrapper .business-info-banner .AllComment {
  font-size: 3.5vw;
  color: #1c95ff;
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

/****************** 食品列表部分 ******************/
.wrapper .food {
  width: 100%;
  /*使用下外边距避开footer部分*/
  margin-bottom: 14vw;
  padding: 5vw;
  box-sizing: border-box;
}

.wrapper .food li {
  width: 100%;
  box-sizing: border-box;
  padding: 2.5vw;
  user-select: none;

  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
}

.wrapper .food li .food-left {
  display: flex;
  align-items: center;
}

.wrapper .food li .food-left .dish-img-feature {
  background-color: #ff812c;
  width: 6.8vw;
  height: 4.5vw;

  position: absolute;
  top: 3vw;
  left: -0.3vw;
  z-index: 50;

  color: #fff;
  font-size: 2.6vw;
  line-height: 4vw;
  text-align: center;
  border-bottom-right-radius: 1vw;
  border: 0.1vw solid #444;

  box-shadow: 0.3vw 0.3vw 0.3vw rgba(0, 0, 0, 0.5);
}

.wrapper .food li .food-left img {
  width: 19vw;
  height: 19vw;
  border-radius: 3vw;
}

.wrapper .food li .food-left .food-left-info {
  margin-left: 3vw;
}

.wrapper .food li .food-left .food-left-info .price {
  color: #df5643;

  font-weight: bold;
}

.wrapper .food li .food-left .food-left-info .price em {
  font-size: 3.6vw;
}

.wrapper .food li .food-left .food-left-info h3 {
  font-size: 3.8vw;
  color: #555;
}

.wrapper .food li .food-left .food-left-info p {
  font-size: 3vw;
  color: #888;
  margin-top: 2vw;
}

.wrapper .food li .food-right {
  width: 16vw;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .food li .food-right i {
  font-size: 5vw;
  color: #fb8b06;
  cursor: pointer;
}

.wrapper .food li .food-right p {
  padding: 0 2vw;
  font-size: 4.5vw;
  color: #333;
}

.wrapper .food li .food-right .fa-plus-circle {
  font-size: 5.5vw;
  color: #3cba92;
  cursor: pointer;
}

.wrapper .food li .mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 1.5vw;
  background-color: rgba(1, 53, 121, 0.2);
  z-index: 999;

  display: flex;
  justify-content: center;
  align-items: center;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.wrapper .food li .mask span {
  color: #fefefe;
  font-size: 4vw;
  font-weight: 800;
}

/****************** 购物车部分 ******************/
.wrapper .cart {
  width: 100%;
  height: 14vw;

  position: fixed;
  left: 0;
  bottom: 0;
  display: flex;
  /*保证工具栏显示在所有页面标签之上*/
  z-index: 10000;
}

.wrapper .disabled {
  cursor: not-allowed;
  pointer-events: none;
}

.wrapper .cart .cart-left {
  flex: 2;
  background-color: #66717d;
  display: flex;
}

.wrapper .cart .cart-left .cart-left-icon {
  width: 16vw;
  height: 16vw;
  box-sizing: border-box;
  border: solid 1.6vw #444;
  border-radius: 8vw;
  background-color: #df5643;
  font-size: 7vw;
  color: #fff;

  display: flex;
  justify-content: center;
  align-items: center;

  margin-top: -4vw;
  margin-left: 3vw;

  position: relative;
}

.wrapper .cart .cart-left .cart-left-icon-quantity {
  width: 5vw;
  height: 5vw;
  border-radius: 2.5vw;
  background-color: red;
  color: #fff;
  font-size: 3.6vw;

  display: flex;
  justify-content: center;
  align-items: center;

  position: absolute;
  right: -1.5vw;
  top: -1.5vw;
}

.wrapper .cart .cart-left .cart-left-info p:first-child {
  font-size: 4.5vw;
  color: #fff;
  margin-top: 1vw;
}

.wrapper .cart .cart-left .cart-left-info p:last-child {
  font-size: 2.8vw;
  color: #80b5e6;
}

.wrapper .cart .cart-right {
  flex: 1;
}

/*达到起送费时的样式*/
.wrapper .cart .cart-right .cart-right-item {
  width: 100%;
  height: 100%;
  background-color: #ff812c;
  color: #fff;
  font-size: 4.5vw;
  font-weight: 700;
  user-select: none;
  cursor: pointer;

  display: flex;
  justify-content: center;
  align-items: center;
}
</style>