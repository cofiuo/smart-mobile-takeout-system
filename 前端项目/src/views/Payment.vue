<template>
  <div class="wrapper">
    <!-- header部分 -->
    <Header title="在线支付" />
    <!-- 订单信息部分 -->
    <h3>订单信息：{{ orderId }}</h3>
    <div class="order-info">
      <p>
        {{ businessInfo.businessName }}

        <el-icon v-if="!isShowDetailet" @click="detailetShow">
          <CaretBottom />
        </el-icon>
        <el-icon v-else @click="detailetShow">
          <CaretTop />
        </el-icon>
      </p>
      <p>&#165; {{ totalPrcie }}</p>
    </div>

    <ul class="order-detailet" v-show="isShowDetailet">
      <li>
        <img :src="businessInfo.businessImg" alt="商家图片">
      </li>
      <!-- 订单明细部分 -->
      <li v-for="item in cartList" :key="item.cartId">
        <img :src="item.goods.goodsImg">
        <p>{{ item.goods.goodsName }} x {{ item.quantity }}</p>
        <p>&#165; {{ new Decimal(item.goods.goodsPrice).mul(item.quantity).toNumber() }}</p>
      </li>
      <li>
        <p>配送费</p>
        <p>&#165; {{ businessInfo.deliveryPrice }}</p>
      </li>
    </ul>

    <!-- 支付方式部分 -->
    <ul class="payment-type" v-if="statu == 0 || statu == null ">
      <li @click="alipay = true">
        <img src="../assets/alipay.png">
        <el-icon v-show="alipay" :size="26" style="color:#3cba92">
          <CircleCheckFilled />
        </el-icon>
      </li>
      <li @click="alipay = false">
        <img src="../assets/wechat.png">
        <el-icon v-show="!alipay" :size="26" style="color:#3cba92">
          <CircleCheckFilled />
        </el-icon>
      </li>
    </ul>
    <div class="payment-button" v-if="statu == 0 || statu == null ">
      <button @click="cofirmPayment">确认支付</button>
    </div>

    <!-- 底部菜单部分 -->
    <Footer></Footer>
  </div>
</template>

<script setup>
import Footer from '../components/Footer.vue'
import { ref, reactive, computed, onMounted, onUnmounted } from "vue"
import { useRouter, useRoute } from "vue-router"
import { get, post } from "@/api";
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue';
import { getSessionStorage, getLocalStorage } from '@/common.js'
import Decimal from "decimal.js";

//创建路由对象
const router = useRouter();
const route = useRoute();

const isShowDetailet = ref(false);
const alipay = ref(true);
//从路由中获取商家的编号和订单号
const businessId = route.query.businessId;
const orderId = route.query.orderId;
const statu = route.query.orderStatu

const businessInfo = ref({})
const account = getSessionStorage('account');
const cartList = ref([]);
const totalPrcie = ref(0)

const detailetShow = () => {
  isShowDetailet.value = !isShowDetailet.value;
}

const loadBusiness = () => {
  let url = `/business/info/${businessId}`
  get(url).then(res => {
    businessInfo.value = res.data.resultData;
    loadCartList()
  }).catch(e => { console.log(e); });
}

//加载购物车数据
const loadCartList = () => {
  let url = `/ordersdetailet/listByOrderId/${orderId}`
  get(url).then(res => {
    if (res.data.code == 20000) {
      cartList.value = res.data.resultData;
      let sum = 0
      cartList.value.forEach(cart => {
        sum = new Decimal(cart.goods.goodsPrice).mul(cart.quantity).add(sum).toNumber()
      })
      totalPrcie.value = new Decimal(sum).add(businessInfo.value.deliveryPrice).toNumber()
    }
  }).catch(e => { console.log(e); });
}

// 确认支付方法
const cofirmPayment = () => {
  let url = `/orders/update`
  let orders = {
    orderId: orderId,
    accountId: account.accountId,
    businessId: businessId
  }
  post(url, orders, true).then(res => {
    if (res.data.code == 20000) {
      ElMessage({
        message: '支付成功',
        type: 'success'
      })
      // 延迟返回主界面，让用户看到支付成功提示
      setTimeout(() => {
        router.push({ path: '/' });
      }, 1500);
    }
  }).catch(e => { console.log(e); });
}


const init = () => {
  loadBusiness()
  console.log(cartList)
  console.log(statu)
}
init();

onMounted(() => {
  //项目购物车 一但提交，生成订单，就不能再返回上一页 订单组件见面。
  history.pushState(null, null, document.URL);
  //pushState 事件能够监控到history对象变化
  window.onpopstate = () => {
    router.push({ path: '/' });
  }
});

// 销毁
onUnmounted(() => {
  //当前组件销毁的钩子函数
  window.onpopstate = null;
});
</script>

<style scoped>
/****************** 总容器 ******************/
.wrapper {
  width: 100%;
  height: 100%;
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

/****************** 订单信息部分 ******************/
.wrapper h3 {
  margin-top: 12vw;
  box-sizing: border-box;
  padding: 4vw 4vw 0;

  font-size: 4vw;
  font-weight: 300;
  color: #999;
}

.wrapper .order-info {
  box-sizing: border-box;
  padding: 4vw;
  font-size: 4vw;
  color: #666;

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .order-info p:last-child {
  color: orangered;
}

/****************** 订单明细部分 ******************/
.wrapper .order-detailet {
  width: 100%;
}

.wrapper .order-detailet li {
  width: 100%;
  box-sizing: border-box;
  padding: 1vw 4vw;

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .order-detailet li img {
  width: 16vw;
  height: 16vw;
  border-radius: 5px;
}



.wrapper .order-detailet li p {
  font-size: 3.8vw;
  color: #79859E;
}

/****************** 支付方式部分 ******************/
.wrapper .payment-type {
  width: 100%;
  margin-top: 15vw;
  border-top: 0.1vw solid #cecece;
  padding-top: 4vw;
}

.wrapper .payment-type li {

  width: 100%;
  box-sizing: border-box;
  padding: 2vw 4vw;

  display: flex;
  justify-content: space-between;
  align-items: center;

}

.wrapper .payment-type li img {
  width: 26vw;
  height: 7vw;

}

.wrapper .payment-type li .fa-check-circle {
  font-size: 5vw;
  color: #3cba92;
}

.wrapper .payment-button {
  width: 100%;
  box-sizing: border-box;
  padding: 4vw;
}

.wrapper .payment-button .el-icon-success {
  color: #3cba92;
}

.wrapper .payment-button button {
  width: 100%;
  height: 10vw;
  border: none;
  /*去掉外轮廓线*/
  outline: none;
  border-radius: 4px;
  background-color: #3cba92;
  color: #fff;
}
</style>