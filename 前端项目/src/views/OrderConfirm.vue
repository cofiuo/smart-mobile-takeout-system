<template>
  <div class="wrapper">
    <!-- header部分 -->

    <Header title="确认订单"></Header>

    <div class="wrapper-content">
      <!-- 配送地址部分 -->
      <div class="order-info">
        <h5>订单配送至：</h5>
        <div class="order-info-address" @click="toAddress()">
          <p v-if="deliveryAddress">{{ deliveryAddress.address }}</p>
          <p v-else>请选择配送地址
            <!-- <p>上海市静安区静安寺街道常德路195号</p> -->
            <el-icon>
              <ArrowRight />
            </el-icon>
          </p>
        </div>
        <p v-if="deliveryAddress">
          {{ deliveryAddress.contactName }}
          {{ deliveryAddress.contactSex == 1 ? '先生' : '女士' }}
          <em>{{ deliveryAddress.contactTel }}</em>
        </p>
      </div>
      <div style="display: flex;margin-bottom: 2vw; border-bottom: solid 1px #ddd;margin-left: 3vw;">
        <img :src="business.businessImg" style="width: 10vw;height: 10vw;border-radius: 2vw;" />
        <h3>{{ business.businessName }}</h3>
      </div>

      <!-- 订单明细部分 -->
      <ul class="order-detailed">
        <li v-for="item in cartList" :key="item.cartId">
          <div class="order-detailed-left">
            <img :src="item.goods.goodsImg">
            <p>{{ item.goods.goodsName }} x {{ item.quantity }}.0</p>
          </div>
          <p>&#165; {{ new Decimal(item.goods.goodsPrice).mul(item.quantity).toNumber() }}</p>
        </li>

      </ul>
      <div class="order-deliveryfee">
        <p>配送费</p>
        <p>&#165; {{ business.deliveryPrice }}</p>
      </div>

      <!-- 合计部分 -->
      <div class="total">
        <div class="total-left">总价：
          &#165; {{ totalPrice }}
        </div>
        <div class="total-right" @click="toPayment()">
          去支付
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Header from "@/components/Header.vue";
import { ref, reactive, computed } from "vue"
import { useRouter, useRoute } from "vue-router"
import { get, post } from "@/api";
import { ElMessage } from 'element-plus'
import { getSessionStorage, getLocalStorage } from '@/common.js'
import Decimal from "decimal.js";

//创建路由对象
const router = useRouter();
const route = useRoute();

//从路由中获取商家的编号
const businessId = route.query.businessId;
const account = getSessionStorage('account');

const business = ref({});
const cartList = ref([]);

const totalPrice = ref(0)

const deliveryAddress = getLocalStorage(account.accountId);
const toAddress = () => {
  router.push({ path: '/address', query: { businessId: businessId } })
}

const toPayment = () => {
  if (!deliveryAddress) {
    ElMessage({
      message: '请选择配送地址信息',
      type: 'error',
    });
    return;
  }

  let url = `/orders/save`;
  let order = {
    accountId: account.accountId,
    businessId: businessId,
    daId: deliveryAddress.daId,
    orderTotal: totalPrice.value
  }

  post(url, order, true).then(res => {
    if (res.data.code == 20000) {
      let orderId = res.data.resultData;
      router.push({ path: '/payment', query: { businessId: businessId, orderId: orderId } })

    }
  }).catch(e => { console.log(e) })


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
    loadCartList();
  }).catch(e => { console.log(e) })
}

//使用账户id和商家id获取对应商家购物车信息
const loadCartList = () => {
  let url = `/cart/listCart/${account.accountId}/${businessId}`;
  get(url).then(res => {
    if (res.data.code === 20000) {
      cartList.value = res.data.resultData;
      let sum = 0;
      //获得购物车数据后计算总价
      cartList.value.forEach(cart => {
        sum = new Decimal(cart.goods.goodsPrice).mul(cart.quantity).add(sum).toNumber();
      })

      // 加配送费
      totalPrice.value = new Decimal(sum).add(business.value.deliveryPrice).toNumber();
    }
  }).catch(e => { console.log(e) })
}


const init = () => {
  loadBusinessById();
}
init();
</script>

<style scoped>
/****************** 总容器 ******************/
.wrapper {
  width: 100%;
  height: 100%;
  box-sizing: border-box;

}

.wrapper .wrapper-content {
  padding: 2vw;
  padding-bottom: 16vw;
}

/****************** header部分 ******************/
.wrapper header {
  width: 100%;
  height: 12vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  color: #596164;
  font-size: 4.8vw;
  letter-spacing: 2vw;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

/****************** 订单信息部分 ******************/
.wrapper .order-info {
  /*注意这里，不设置高，靠内容撑开。因为地址有可能折行*/
  width: 95%;
  margin: 4vw auto 10vw auto;
  background: linear-gradient(to right bottom, #d8edfd, #f6faff);
  box-sizing: border-box;
  color: #436283;
  border-radius: 2vw;
  padding: 4vw;
  margin-bottom: 5vw;
  padding: 4vw;
  box-sizing: border-box;
  box-shadow: 0.5vw 0.5vw 3vw rgba(0, 0, 0, 0.1);
}

.wrapper .order-info h5 {
  font-size: 3.6vw;
  font-weight: 600;
  color: #001747;
  margin-bottom: 4vw;
}

.wrapper .order-info .order-info-address {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;

  font-weight: 500;
  user-select: none;
  cursor: pointer;
  margin: 1vw 0;

}

.wrapper .order-info .order-info-address p {
  width: 90%;
  font-size: 4vw;
  text-align: left;
}

.wrapper .order-info .order-info-address i {
  font-size: 5vw;
}

.wrapper .order-info p {
  font-size: 3.5vw;
  margin-top: 2vw;
  text-align: right;
}

.wrapper h3 {
  box-sizing: border-box;
  padding: 3vw;
  font-size: 4vw;
  color: #66717d;
  border-bottom: solid 1px #ddd;
}

/****************** 订单明细部分 ******************/
.wrapper .business-avatar {
  border-radius: 15px;
}

.wrapper .order-detailed {
  width: 100%;
}

.wrapper .order-detailed li {
  width: 100%;
  height: 16vw;
  box-sizing: border-box;
  padding: 10vw 3vw;
  color: #666;

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .order-detailed li .order-detailed-left {
  display: flex;
  align-items: center;
}

.wrapper .order-detailed li .order-detailed-left img {
  width: 16vw;
  height: 16vw;
  border-radius: 2vw;
}

.wrapper .order-detailed li .order-detailed-left p {
  font-size: 3.5vw;
  margin-left: 3vw;
  color: #393939;
}

.wrapper .order-detailed li p {
  font-size: 3.5vw;
  color: #df5643;
  font-weight: 700;
}

.wrapper .order-deliveryfee {
  width: 100%;
  box-sizing: border-box;
  padding: 2vw 3vw;
  color: #466457;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 3.8vw;
  background-color: #e5e7da;
  border-radius: 0.5vw;
  margin-top: 4vw;
}

/****************** 订单合计部分 ******************/
.wrapper .total {
  width: 100%;
  height: 14vw;

  position: fixed;
  left: 0;
  bottom: 0;

  display: flex;
}

.wrapper .total .total-left {
  flex: 2;
  background-color: #66717d;
  color: #fff;
  font-size: 4.5vw;
  font-weight: 700;
  user-select: none;

  display: flex;
  justify-content: center;
  align-items: center;
}

.wrapper .total .total-right {
  flex: 1;
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
