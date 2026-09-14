<template>

  <div class="wrapper">
    <header>
      <p>用户注册</p>
    </header>

    <el-form ref="RegisterForm" :model="account" :rules="rules" label-width="80px" class="form-box"
      @submit.prevent="register">
      <el-form-item label="账户" prop="accountId">
        <el-input v-model="account.accountId" @blur="checkAccountId()" placeholder="请输入注册账户(手机号)"
          :prefix-icon="User"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="account.password" placeholder="请输入注册密码" :prefix-icon="Lock"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input type="password" v-model="account.confirmPassword" placeholder="请再次输入注册密码"
          :prefix-icon="Lock"></el-input>
      </el-form-item>
      <el-form-item label="名称" prop="accountName">
        <el-input v-model="account.accountName" placeholder="请输入您的姓名" :prefix-icon="CircleCheck"></el-input>
      </el-form-item>
      <el-form-item label="性别" prop="accountSex">
        <el-radio-group v-model="account.accountSex">
          <el-radio value="1">男</el-radio>
          <el-radio value="0">女</el-radio>
        </el-radio-group>
      </el-form-item>

      <div class="button-login">
        <button type="button" @click="register">注 册</button>
      </div>
      <div class="button-register">
        <button type="button" @click="toLogin">登 录</button>
      </div>

    </el-form>


    <Footer />
  </div>
</template>


<script setup>
import { CircleCheck, Lock, User } from '@element-plus/icons-vue';
import { reactive, ref } from 'vue';
import Footer from '@/components/Footer.vue';
import { useRouter } from 'vue-router';
import { get, post } from '@/api/index.js'
import { ElMessage } from 'element-plus';

const router = useRouter();

// 新建变量
const account = reactive({
  accountId: '',
  password: '',
  password2: '',
  accountName: '',
  accountSex: '1',
  accountImg: '',
});



const RegisterForm = ref(null);

//登录跳转
const toLogin = () => {
  router.push('/login')
}


// 注册的方法
const register = () => {
  RegisterForm.value.validate((v) => {
    if (v) {
      let url = '/account/register';
      let param = {
        accountId: account.accountId,
        password: account.password,
        accountName: account.accountName,
        accountSex: account.accountSex,
        accountImg: account.accountImg,
      };
      post(url, param, true).then(res => {
        if (res.data.code == 20000) {
          ElMessage({
            message: "注册成功",
            type: 'success',
          });
        }

        window.location.hash = '#/login';

      })

    } else {
      return false;
    }
  });
};

//检测手机号是否已注册
const checkAccountId = () => {
  if (account.accountId != '') {
    let url = `/account/check/${account.accountId}`;
    get(url).then(res => {
      if (res.data.code == 20001) {
        ElMessage({
          message: res.data.message,
          type: 'error',
          showClose: true,
        });

        //清除用户手机号
        account.accountId = '';
      }
    }).catch(e => { console.log(e) })
  }

}


// 表单校验规则
const rules = reactive({
  accountId: [
    { required: true, message: '请输入注册账户(手机号)', trigger: 'blur' },
    { min: 11, max: 11, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入注册密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== account.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  accountName: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度需在2-10个字符之间', trigger: 'blur' }
  ]
});




</script>

<style scoped>
.wrapper header {
  width: 100%;
  height: 12vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  color: #596164;
  font-size: 5vw;
  position: fixed;
  left: 0;
  top: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.wrapper header p {
  letter-spacing: 2vw;
}

/* 注册表单部分样式代码 */
.wrapper .form-box {
  width: 100%;
  margin-top: 20vw;
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