<template>
  <div class="wrapper">
    <!-- header部分 -->
    <!-- <header>
      <p>新增送货地址</p>
    </header> -->
    <Header :title="daId ? '编辑送货地址' : '新增送货地址'"></Header>



    <div class="wrapper-content">
      <el-form ref="deliveryAddressForm" :model="deliveryAddress" label-width="80px" class="form-box" :rules="rules"
        @submit.prevent="addUserAddress">
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="deliveryAddress.contactName" placeholder="请输入联系人" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="deliveryAddress.contactSex">
            <el-radio value="1">男</el-radio>
            <el-radio value="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="contactTel">
          <el-input v-model="deliveryAddress.contactTel" placeholder="请输入联系电话" :prefix-icon="Phone" />
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input v-model="deliveryAddress.address" style="width:60vw" :rows="4" type="textarea"
            placeholder="请输入收货地址" />
        </el-form-item>
      </el-form>
      <div class="button-login">
        <button @click="addUserAddress()">保存</button>
      </div>
    </div>

    <!-- 底部菜单部分 -->
    <Footer></Footer>
  </div>
</template>

<script setup>
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import qs from "qs";
import { User, Location, Phone } from '@element-plus/icons-vue'
import { useRouter, useRoute } from "vue-router";
import { ref, inject, reactive } from "vue";
import { getSessionStorage, getLocalStorage } from "@/common.js";
import { get, post } from "@/api";
import { ElMessage } from 'element-plus'

const axios = inject("axios");
const router = useRouter();
const route = useRoute();

const user = getSessionStorage("user");
const businessId = route.query.businessId;
const daId = route.query.daId;


const deliveryAddress = reactive({
  daId: '',
  contactName: "",
  contactSex: '1',
  contactTel: "",
  address: "",
  accountId: ''
});


// 获得表单标签对象
const deliveryAddressForm = ref(null);

const account = getSessionStorage("account");

// 检验规则
const rules = reactive({
  contactName: [
    { required: true, message: '请输入联系人', trigger: 'blur' },
    { min: 2, max: 12, message: '用户名长度在2-12个字符之间', trigger: 'blur' }
  ],
  contactTel: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { min: 11, max: 11, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入收货地址', trigger: 'blur' },
    { min: 2, max: 12, message: '请输入正确的收货地址', trigger: 'blur' }
  ]

});

const addUserAddress = () => {
  deliveryAddressForm.value.validate((v) => {
    if (v) {
      let url = daId ? `/deliveryaddress/update` : `/deliveryaddress/save`;
      deliveryAddress.accountId = account.accountId;
      post(url, deliveryAddress, true).then(res => {
        if (res.data.code == 20000) {
          ElMessage({
            message: daId ? "配送地址编辑成功" : "配送地址添加成功",
            type: 'success',
          });
          router.push({ path: '/address', query: { businessId: businessId } })
        }
      }).catch(e => { console.log(e) })
    } else {
      return false;
    }
  })
};



// 加载地址详情
const loadAddressDetail = () => {
  if (!daId) return;
  const temp = getLocalStorage(account.accountId + 1);
  deliveryAddress.daId = temp.daId;
  deliveryAddress.contactName = temp.contactName;
  deliveryAddress.contactSex = temp.contactSex + '';
  deliveryAddress.contactTel = temp.contactTel;
  deliveryAddress.address = temp.address;
  deliveryAddress.accountId = temp.accountId;
};

const init = () => {
  loadAddressDetail();
};
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

/*************** header ***************/
.wrapper header {
  width: 100%;
  height: 12vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  color: #596164;
  display: flex;
  justify-content: space-around;
  align-items: center;
  letter-spacing: 1vw;
  font-size: 4.8vw;
  position: fixed;
  left: 0;
  top: 0;
  /*保证在最上层*/
  z-index: 1000;
}


/*************** （表单信息） ***************/
.wrapper .form-box {
  width: 100%;
  /* margin-top: 20vw; */
  padding: 5vw;
  box-sizing: border-box;

}

.wrapper .form-box .el-input {
  width: 90%;
}


.wrapper .button-login {
  width: 100%;
  box-sizing: border-box;
  padding: 4vw 4vw 0 4vw;
}


.wrapper .button-login button {
  width: 100%;
  height: 10vw;
  font-size: 3.8vw;
  background-color: #3cba92;
  border: none;
  border-radius: 1.5vw;
  color: #efefef;
  outline: none;
  letter-spacing: 2vw;
}

.wrapper .button-register {
  width: 100%;
  box-sizing: border-box;
  padding: 4vw;
}

.wrapper .button-register button {
  width: 100%;
  height: 10vw;
  font-size: 3.8vw;
  background-color: #fdfcfb;
  border: 0.3vw solid #e2d1c3;
  border-radius: 1.5vw;
  color: #5f72bd;
  outline: none;
  letter-spacing: 2vw;
}

/** 用户头像上传  **/
.upload {
  width: 42vw;
  height: 42vw;
  background-color: #efefef;
  border-radius: 4vw;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  border-radius: 3vw;
}
</style>