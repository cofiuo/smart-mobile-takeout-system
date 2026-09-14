<template>
    <div class="wrapper">
        <!-- <header>
            <p>13711111111</p>
        </header> -->
        <Header title="用户登录" :back="false"/>

        <!-- 表单默认提交，阻止 -->
        <el-form ref="LoginFrom" :model="account" :rules="rules" label-width="80px" class="form-box"
            @submit.prevent="login">
            <el-form-item label="账户" prop="accountId">
                <el-input v-model="account.accountId" placeholder="请输入用户名" :prefix-icon="User"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input v-model="account.password" type="password" placeholder="请输入密码" show-password
                    :prefix-icon="Lock"></el-input>
            </el-form-item>


            <div class="button-login">
                <button type="button" @click="login">登 录</button>
            </div>
            <div class="button-register">
                <button type="button" @click="toRegister">注 册</button>
            </div>
        </el-form>

        <Footer />
    </div>
</template>


<script setup>
import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';
import { useRouter } from 'vue-router';
import { User, Lock } from '@element-plus/icons-vue';
import { reactive, ref } from 'vue';
import { post } from '@/api/index.js';
import { ElMessage } from 'element-plus';
import { setSessionStorage } from '@/common';


const router = useRouter();  //创建路由对象

// 获得表单标签对象
const LoginFrom = ref(null);

const account = reactive({  //账户对象
    accountId: '',
    password: ''
});

const toRegister = () => {
    router.push('/register')
}


// 检验规则
const rules = reactive({
    accountId: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 12, message: '用户名长度在3-12个字符之间', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
    ]
});


// 登录的方法
const login = () => {
    LoginFrom.value.validate((v) => {
        if (v) {
            post('/account/login', account, false).then(res => {
                if (res.data.code === 20000) {
                    //TODO:登录成功传递用户信息
                    setSessionStorage('account', res.data.resultData);
                    router.push('/');  // 登录成功跳转到首页
                    ElMessage({
                        message: '登录操作成功',
                        type: 'success',
                    });
                } else {
                    ElMessage({
                        message: res.data.message,
                        type: 'error',
                    });
                }
            }).catch(e => { console.log(e); });
        } else {
            return false;
        }
    });
};

</script>

<style scoped>
.wrapper {
    width: 100%;
    height: 100%;
    padding-bottom: 8vw;
    box-sizing: border-box;
}

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

/* APP标题部分样式 */

/* 登录部分样式代码 */
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
</style>