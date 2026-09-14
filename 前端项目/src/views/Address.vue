<template>
  <div class="wrapper">

    <!-- header部分 -->
    <!-- <header>
        <p>地址管理</p>
      </header> -->
    <Header title="地址管理"></Header>

    <div class="wrapper-content">
      <!-- 地址列表部分 -->
      <ul class="addresslist">
        <li v-for="item in addresslist" :key="item.daId">
          <div class="addresslist-left" @click="setDefaultAddress(item)">
            <h3><em>{{ item.contactName }}</em> {{ item.contactSex == 1 ? "先生" : "女士" }}
              <i>{{ item.contactTel }}</i>
            </h3>
            <p>{{ item.address }}</p>
          </div>
          <div class="addresslist-right">
            <!-- <i class="fa fa-edit" @click="editUserAddress(index)"></i> -->
            <el-icon @click="toEditAddress(item)">
              <Edit />
            </el-icon>
            <el-icon @click="removeAddress(item.daId)">
              <Delete />
            </el-icon>
          </div>
        </li>

        <template v-if="addresslist.length == 0">
          <p class="none">还没有添加配送信息</p>
        </template>

      </ul>

      <!-- 新增地址部分 -->
      <div class="addbtn" @click="toAddressInfo()">
        <el-icon><Select /></el-icon>
        <p>新增收货地址</p>
      </div>
    </div>

    <!-- 底部菜单部分 -->
    <Footer></Footer>
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { ref, reactive, computed } from "vue"
import { useRouter, useRoute } from "vue-router"
import { get, post } from "@/api";
import { ElMessageBox, ElMessage } from 'element-plus'
import { getSessionStorage, setLocalStorage, removeLocalStorage } from '@/common.js'

//创建路由对象
const router = useRouter();
const route = useRoute();
//从路由中获取商家的编号
const businessId = route.query.businessId;
const account = getSessionStorage('account');


const addresslist = ref([]);


//跳转到编辑页面
const toEditAddress = (da) => {
  setLocalStorage(account.accountId + 1, da)
  router.push({ path: '/addressInfo', query: { daId: da, businessId: businessId } })
}


const toAddressInfo = () => {
  router.push({ path: '/addressInfo', query: { businessId: businessId } })
}

// 获取用户配送地址
const loadDeliveryAddress = () => {
  let url = `/deliveryaddress/listByAccountId/${account.accountId}`
  get(url).then(res => {
    if (res.data.code == 20000) {
      addresslist.value = res.data.resultData;
    }
  }).catch(e => { console.log(e) })
}

// 删除配送地址
const removeAddress = (daId) => {
  ElMessageBox.confirm(
    '确认要删除配送地址吗?',
    '提示',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    let url = `/deliveryaddress/del/${daId}`
    post(url).then(res => {
      if (res.data.code == 20000) {
        //如果删除地址是默认地址，需同时删除localstorage的地址
        let defaultAddress = getSessionStorage(account.accountId);
        if (defaultAddress && defaultAddress.daId == daId) {
          removeLocalStorage(account.accountId)
        }
        ElMessage({
          type: 'success',
          message: '配送信息删除成功',
        })
        loadDeliveryAddress();
      }
    }).catch(e => { console.log(e) })
  }).catch((e) => { console.log(e) })
}

//设置默认配送地址的方法
const setDefaultAddress = (da) => {
  setLocalStorage(account.accountId, da)
  router.push({ path: '/orderConfirm', query: { businessId: businessId } })
}

const init = () => {
  loadDeliveryAddress();
}
init();
</script>

<style scoped>
/*************** 总容器 ***************/
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

/*************** addresslist ***************/
.wrapper .addresslist {
  width: 100%;
  margin-top: 3vw;
}

.wrapper .addresslist .none {
  text-align: center;
  color: #c9c9c9;
}

.wrapper .addresslist li {
  width: 100%;
  box-sizing: border-box;
  box-shadow: 0.5vw 0.5vw 3vw rgba(0, 0, 0, 0.1);
  padding: 3vw;
  display: flex;
  background-color: #fff;
  border-radius: 1.6vw;
  margin: 2vw auto;
}

.wrapper .addresslist li .addresslist-left {
  flex: 5;
  /*左边这块区域是可以点击的*/
  user-select: none;
  cursor: pointer;
}

.wrapper .addresslist li .addresslist-left h3 {
  font-size: 3.8vw;
  font-weight: 300;
  color: #79859E;
  margin: 0.5vw;
}

.wrapper .addresslist li .addresslist-left h3 em {
  color: #3cba92;
  font-style: normal;
  font-weight: 400;
}

.wrapper .addresslist li .addresslist-left p {
  font-size: 3.4vw;
  color: #35624a;
  margin: 0.5vw;
}

.wrapper .addresslist li .addresslist-right {
  flex: 1;
  font-size: 4.6vw;
  color: #79859E;
  cursor: pointer;
  padding-left: 4vw;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.wrapper .addresslist li .addresslist-right i {
  display: block;
  width: 6.4vw;
  height: 6.4vw;
  background-position: center;
  background-size: cover;
  user-select: none;
  cursor: pointer;
  margin-left: 1.5vw;

}


.wrapper .addresslist li .addresslist-right .edit_icon {
  background-image: url(../assets/edit.png);
}


.wrapper .addresslist li .addresslist-right .remove_icon {
  background-image: url(../assets/remove.png);
}

.wrapper .addresslist li .addresslist-right .el-icon-delete {
  color: #ff0066;
}

/*************** 新增地址部分 ***************/
.wrapper .addbtn {
  width: 100%;
  height: 14vw;
  border-top: solid 1px #fff;
  margin-top: 4vw;

  display: flex;
  justify-content: center;
  align-items: center;

  font-size: 4vw;

  user-select: none;
  cursor: pointer;

  background-color: #fdfcfb;
  border: 0.3vw solid #e2d1c3;
  border-radius: 1.5vw;
  color: #5f72bd;
}

.wrapper .addbtn p {
  margin-left: 2vw;
}

.wrapper .addbtn .fa-plus-circle {
  color: #2c3035;
}
</style>