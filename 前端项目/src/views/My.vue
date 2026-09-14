<template>
    <div class="wrapper">
        <Header title="我的" />

        <div class="account-info">
            <el-avatar class="user-avatar" :src="account.accountImg" shape="square" :size="73" />

            <!-- 信息内容 -->
            <div class="user-info-content">
                <div class="user-nickname">{{ account.accountName }}</div>
                <div class="user-detail">
                    <span class="user-phone">{{ account.accountId }} </span>
                    <span class="user-sex">{{ account.accountSex == 1 ? '男士' : '女士' }}</span>
                </div>
            </div>
        </div>

        <!-- 推荐 -->
        <div class="recommend-title">
            <div class="title-line">————</div>
            <span class="title-text">为您推荐</span>
            <div class="title-line">————</div>
        </div>

        <div class="recommend-content">
            <!-- --推荐商家-- -->
            <ul class="business" v-if="randomBusiness" @click="toBusinessInfo(randomBusiness.businessId)">
                <li>
                    <!-- 添加div，设置相对定位 -->
                    <div style="position: relative; z-index:0">
                        <img :src="randomBusiness.businessImg" />
                        <!-- 角标代码 从BusinessList.vue复制 -->
                        <div class="business-img-quantity" v-show="false">
                            <!-- {{ item. }} -->
                        </div>
                    </div>
                    <div class="business-info">
                        <div class="business-info-h">
                            <h3>{{ randomBusiness.businessName }}</h3>
                            <!-- 收藏商家 图标 -->
                            <div class="business-info-like" @click.stop="toggleFavorite(randomBusiness)">
                                <img v-if="randomBusiness.isFavorite" src="../assets/light.png" class="heart_icon"
                                    alt="已收藏" />
                                <img v-else src="../assets/dark.png" class="heart_icon" alt="未收藏" />
                            </div>
                        </div>
                        <div class="business-info-star">
                            <div class="business-info-star-left ">
                                <i class="el-icon-star-on fa-star"></i>
                                <i class="el-icon-star-on fa-star"></i>
                                <i class="el-icon-star-on fa-star"></i>
                                <i class="el-icon-star-on fa-star"></i>
                                <i class="el-icon-star-on fa-star"></i>
                                <p>5.0 月售345单</p>
                            </div>
                            <div class="business-info-star-right">
                                商家专送
                            </div>
                        </div>
                        <div class="business-info-delivery">
                            <p>&#165; {{ randomBusiness.starPrice }} 起送 | &#165; {{ randomBusiness.deliveryPrice }} 配送
                            </p>
                            <!-- <p>3.0km | 30分钟</p> -->
                        </div>
                        <div class="business-info-explain">
                            <div> {{ randomBusiness.businessExplain }} </div>
                        </div>
                    </div>
                </li>
            </ul>
        </div>

        <div class="function-container">
            <ul class="function-list">
                <li class="function-item" @click="toChat()">
                    <span class="item-text"><el-icon class="item-img" size="40">
                            <ChatDotSquare />
                        </el-icon>客服联系</span>
                    <el-icon>
                        <ArrowRight />
                    </el-icon>
                </li>
                <hr>
                <li class="function-item" @click="toMyComment()">
                    <span class="item-text"><el-icon class="item-img" size="40">
                            <ChatLineRound />
                        </el-icon>我的评论</span>
                    <el-icon>
                        <ArrowRight />
                    </el-icon>
                </li>
                <hr>
                <li class="function-item" @click="toMyFavorite()">
                    <span class="item-text"><el-icon class="item-img" size="40">
                            <StarFilled />
                        </el-icon>店铺关注</span>
                    <el-icon>
                        <ArrowRight />
                    </el-icon>
                </li>
                <hr>
                <li class="function-item" @click="toAddressPage()">
                    <span class="item-text"><el-icon class="item-img" size="40">
                            <Edit />
                        </el-icon>地址管理</span>
                    <el-icon>
                        <ArrowRight />
                    </el-icon>
                </li>
                <hr>
                <li class="function-item" @click="showUpdatePasswordDialog = true">
                    <span class="item-text"><el-icon class="item-img" size="40">
                            <Edit />
                        </el-icon>修改密码</span>
                    <el-icon>
                        <ArrowRight />
                    </el-icon>
                </li>
                <hr>
                <li class="function-item" @click="showUpdateNameDialog = true">
                    <span class="item-text"><el-icon class="item-img" size="40">
                            <Edit />
                        </el-icon>修改昵称</span>
                    <el-icon>
                        <ArrowRight />
                    </el-icon>
                </li>
                <hr>
                <li class="function-item" @click="showUpdateSexDialog = true">
                    <span class="item-text"><el-icon class="item-img" size="40">
                            <UserFilled />
                        </el-icon>修改性别</span>
                    <el-icon>
                        <ArrowRight />
                    </el-icon>
                </li>

            </ul>
        </div>

        <!-- 修改密码对话框 -->
        <el-dialog v-model="showUpdatePasswordDialog" title="修改密码" width="80%" center>
            <el-form :model="passwordForm" label-position="top" :rules="passwordRules" ref="passwordFormRef">
                <el-form-item label="原密码" prop="oldPassword">
                    <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入原密码" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                    <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showUpdatePasswordDialog = false">取消</el-button>
                    <el-button type="primary" @click="updatePassword">确认修改</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 修改昵称对话框 -->
        <el-dialog v-model="showUpdateNameDialog" title="修改昵称" width="80%" center>
            <el-form :model="nameForm" label-position="top" :rules="nameRules" ref="nameFormRef">
                <el-form-item label="新昵称" prop="newName">
                    <el-input v-model="nameForm.newName" placeholder="请输入新昵称" maxlength="20" show-word-limit />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showUpdateNameDialog = false">取消</el-button>
                    <el-button type="primary" @click="updateName">确认修改</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 修改性别对话框 -->
        <el-dialog v-model="showUpdateSexDialog" title="修改性别" width="80%" center>
            <el-radio-group v-model="sexForm.newSex" class="sex-radio-group">
                <el-radio :label="1">男士</el-radio>
                <el-radio :label="0">女士</el-radio>
            </el-radio-group>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showUpdateSexDialog = false">取消</el-button>
                    <el-button type="primary" @click="updateSex">确认修改</el-button>
                </span>
            </template>
        </el-dialog>


        <!-- 注销账号和退出登录 -->

        <div class="logout-container">
            <button class="logout-btn cancel-account" @click="cancelAccount()">注销账号</button>
            <button class="logout-btn exit-login" @click="logout()">退出登录</button>
        </div>






        <Footer />
    </div>
</template>




<script setup>
import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';
import { getSessionStorage, setSessionStorage, removeLocalStorage, removeSessionStorage } from '@/common.js';
import { ref, reactive } from 'vue';
import { get, post } from '@/api/index.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter, useRoute } from "vue-router"


const router = useRouter();  //创建路由对象

const account = reactive(getSessionStorage('account'));

const businessList = ref([]);
const randomBusiness = ref(null);

// 对话框显示状态
const showUpdatePasswordDialog = ref(false);
const showUpdateNameDialog = ref(false);
const showUpdateSexDialog = ref(false);

// 表单引用
const passwordFormRef = ref(null);
const nameFormRef = ref(null);

// 修改密码表单数据
const passwordForm = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
});

// 修改昵称表单数据
const nameForm = reactive({
    newName: account.accountName
});

// 修改性别表单数据
const sexForm = reactive({
    newSex: account.accountSex
});

// 修改密码表单验证规则
const passwordRules = reactive({
    oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
    ],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请再次输入新密码', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                if (value !== passwordForm.newPassword) {
                    callback(new Error('两次输入密码不一致'));
                } else {
                    callback();
                }
            },
            trigger: 'blur'
        }
    ]
});

// 修改昵称表单验证规则
const nameRules = reactive({
    newName: [
        { required: true, message: '请输入新昵称', trigger: 'blur' },
        { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
    ]
});

//传递商家id，跳转到商家详情页面
const toBusinessInfo = (businessId) => {
    router.push({ path: '/businessInfo', query: { businessId: businessId } })
}



const toChat = () => {
    router.push({ path: '/chat' })
}

const toMyComment = () => {
    router.push({ path: '/myComment', query: { accountId: account.accountId } })
}



const logout = () => {
    //删除本地存储和会话存储数据
    removeLocalStorage(account.accountId + 1)
    removeSessionStorage('account');
    ElMessage({
        message: "退出登录成功",
        type: 'success',
    })
    router.push('/login');
};

const cancelAccount = () => {
    ElMessageBox.confirm(
        '注销账号后所有数据将无法恢复，您确定要注销吗？',
        '注销确认',
        {
            confirmButtonText: '确认注销',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(() => {
        let url = '/account/cancel';
        let param = { accountId: account.accountId };
        post(url, param, true).then(res => {
            if (res.data.code == 20000) {
                ElMessage({
                    message: res.data.message,
                    type: 'success'
                });
                // 清除存储并跳转登录页
                removeLocalStorage(account.accountId + 1)
                removeSessionStorage('account');
                router.push('/login');
            } else {
                ElMessage({
                    message: res.data.message,
                    type: 'error'
                });
            }
        }).catch(err => { console.log(err); });
    }).catch(err => { console.log(err); });
}

// 获取商家列表数据
const loadBusiness = () => {
    get('/business/list').then(res => {
        if (res.data.code === 20000) {
            businessList.value = res.data.resultData
            selectRandomBusiness();
        } else {
            ElMessage({
                message: res.data.message,
                type: 'error',
            })
        }
    }).catch(err => { console.log(err); });
}

const toMyFavorite = () => {
    // 跳转至favorite页面，携带businessId
    router.push({
        path: '/myFavorite'
    });
};

const toAddressPage = () => {
    // 跳转至address页面，携带businessId
    router.push({
        path: '/address',
        query: { businessId: randomBusiness.value?.businessId }
    });
};

// 生成随机数函数
const getRandomInt = (max) => {
    return Math.floor(Math.random() * max);
};

// 随机选择一个商家
const selectRandomBusiness = () => {
    if (businessList.value && businessList.value.length > 0) {
        const randomIndex = getRandomInt(businessList.value.length);
        randomBusiness.value = businessList.value[randomIndex];
    }
    // 重新加载收藏状态
    if (account) {
        loadFavoriteStatus();
    }
};

// 加载商家收藏状态
const loadFavoriteStatus = () => {
    if (!account) {
        return
    }

    businessList.value.forEach(business => {
        let url = `/favorite/checkFavorite/${account.accountId}/${business.businessId}`
        get(url).then(res => {
            if (res.data.code == 20000) {
                business.isFavorite = res.data.resultData
            }
        }).catch(e => { console.log(e) })
    })
}

// 收藏/取消收藏
const toggleFavorite = (businessItem) => {
    if (!account) {
        router.push('/login')
        return
    }

    if (businessItem.isFavorite) {
        //取消收藏
        const params = {
            accountId: account.accountId,
            businessId: businessItem.businessId
        }

        post('/favorite/del', params, true).then(res => {
            if (res.data.code == 20000) {
                businessItem.isFavorite = false
                ElMessage({
                    message: '取消收藏成功',
                    type: 'success'
                });
            }
        }).catch(e => { console.log(e) })
    } else {
        //添加收藏
        const params = {
            accountId: account.accountId,
            businessId: businessItem.businessId
        }

        post('/favorite/add', params, true).then(res => {
            if (res.data.code == 20000) {
                businessItem.isFavorite = true
                ElMessage({
                    message: '收藏成功',
                    type: 'success'
                });
            }
        }).catch(e => { console.log(e) })
    }
}

// 修改密码
const updatePassword = async () => {
    if (!passwordFormRef.value) return;
    await passwordFormRef.value.validate(async (valid) => {
        if (valid) {
            // 表单验证通过，发送请求
            const params = {
                accountId: account.accountId,
                password: passwordForm.oldPassword,
                newPassword: passwordForm.newPassword
            };

            try {
                const res = await post('/account/updatePassword', params, true);
                if (res.data.code == 20000) {
                    ElMessage({
                        message: '密码修改成功',
                        type: 'success'
                    });
                    showUpdatePasswordDialog.value = false;
                    // 重置表单
                    passwordForm.oldPassword = '';
                    passwordForm.newPassword = '';
                    passwordForm.confirmPassword = '';
                } else {
                    ElMessage({
                        message: res.data.message,
                        type: 'error'
                    });
                }
            } catch (err) {
                console.log(err);
                ElMessage({
                    message: '密码修改失败，请重试',
                    type: 'error'
                });
            }
        }
    });
};

// 修改昵称
const updateName = async () => {
    if (!nameFormRef.value) return;
    await nameFormRef.value.validate(async (valid) => {
        if (valid) {
            // 表单验证通过，发送请求
            const params = {
                accountId: account.accountId,
                accountName: nameForm.newName
            };

            try {
                const res = await post('/account/updateInfo', params, true);
                if (res.data.code == 20000) {
                    // 更新本地用户信息
                    account.accountName = res.data.resultData.accountName;
                    setSessionStorage('account', account);

                    ElMessage({
                        message: '昵称修改成功',
                        type: 'success'
                    });
                    showUpdateNameDialog.value = false;
                } else {
                    ElMessage({
                        message: res.data.message,
                        type: 'error'
                    });
                }
            } catch (err) {
                console.log(err);
                ElMessage({
                    message: '昵称修改失败，请重试',
                    type: 'error'
                });
            }
        }
    });
};

// 修改性别
const updateSex = async () => {
    // 发送请求
    const params = {
        accountId: account.accountId,
        accountSex: sexForm.newSex
    };

    try {
        const res = await post('/account/updateInfo', params, true);
        if (res.data.code == 20000) {
            // 更新本地用户信息
            account.accountSex = res.data.resultData.accountSex;
            setSessionStorage('account', account);

            ElMessage({
                message: '性别修改成功',
                type: 'success'
            });
            showUpdateSexDialog.value = false;
        } else {
            ElMessage({
                message: res.data.message,
                type: 'error'
            });
        }
    } catch (err) {
        console.log(err);
        ElMessage({
            message: '性别修改失败，请重试',
            type: 'error'
        });
    }
};

const init = () => {
    loadBusiness();
}
init();

</script>

<style scoped>
.wrapper {
    width: 100%;
    height: 100%;
    padding-bottom: 12vw;
    box-sizing: border-box;
}

.wrapper .account-info {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background-color: #fff;
    border-radius: 8px;
    margin: 10px;
    gap: 12px;
    border: groove;
    margin-top: 25px;
    height: 75px;
}


.wrapper .account-info .user-avatar {
    border-radius: 5px;
}

.wrapper .account-info .user-info-content {
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin-left: 12px;
}

.wrapper .account-info .user-nickname {
    font-size: 16px;
    font-weight: 500;
    color: #333;
    margin-bottom: 7px;
}

.wrapper .account-info .user-detail {
    font-size: 16px;
    color: #666;
    display: flex;
    gap: 12px;
    align-items: center;
}

.wrapper .account-info .user-detail .user-phone {
    font-size: 16px;
}

.wrapper .account-info .user-detail .user-sex {
    font-size: 16px;
}


.wrapper .recommend-title {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 8px 0;
}

.wrapper .recommend-title .title-line {
    font-size: 18px;
    font-weight: 800;
    color: #d4af37;
}

.wrapper .recommend-title .title-text {
    font-size: 18px;
    font-weight: 800;
    color: #d4af37;
}

/* 商家列表样式 */
.wrapper .recommend-content {
    margin: 10px;
    padding: 10px;
    padding: 8px 12px;
    background-color: #fff;
    border-radius: 8px;
    border: groove;
}


.wrapper .business li .business-img-quantity {
    width: 5vw;
    height: 5vw;
    background-color: red;
    color: #fff;
    font-size: 3.6vw;
    border-radius: 2.5vw;
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    right: -1.5vw;
    top: -1.5vw;
}

.wrapper .business li {
    padding: 4px 0;
    display: flex;
    align-items: center;
    border-bottom: none;
}

.wrapper .business li img {
    width: 18vw;
    height: 18vw;
    border-radius: 2vw;
    position: relative;
    z-index: 0;
}

.wrapper .business li .business-info {
    width: 100%;
    box-sizing: border-box;
    padding: 0 3vw;
}

.wrapper .business li .business-info .business-info-h {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.wrapper .business li .business-info .business-info-h h3 {
    font-size: 4vw;
    color: #696969;
}

.wrapper .business li .business-info .business-info-h .business-info-like {
    width: 4.6vw;
    height: 3.4vw;
    color: #FF3D8D;
    font-size: 4vw;
    margin-right: 4vw;
    display: flex;
    justify-content: center;
    align-items: center;
}

.wrapper .business li .business-info .business-info-h .business-info-like .heart_icon {
    /* background-image: url(../assets/heart.png); */
    width: 5vw;
    height: 5vw;
    display: block;
    background-size: cover;
    background-position: center;
}

.wrapper .business li .business-info .business-info-star {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 3.1vw;
}

.wrapper .business li .business-info .business-info-star .business-info-star-left {
    display: flex;
    align-items: center;
}

.wrapper .business li .business-info .business-info-star .business-info-star-left .fa-star {
    color: #fcba26;
    margin-right: 0.5vw;
}

.wrapper .business li .business-info .business-info-star .business-info-star-left p {
    color: #666;
    margin-left: 1vw;
}

.wrapper .business li .business-info .business-info-star .business-info-star-right {
    background-color: #016cf4;
    color: #f9f9f9;
    font-size: 2.8vw;
    border-radius: 0.4vw;
    padding: 0 0.8vw;
    margin-bottom: 3px;
}

.wrapper .business li .business-info .business-info-delivery {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #666;
    font-size: 3.1vw;
    margin-bottom: 2vw;
}

.wrapper .business li .business-info .business-info-explain {
    display: flex;
    justify-content: left;
    align-items: center;
}

.wrapper .business li .business-info .business-info-explain div {
    border: 0.1vw solid #e2d1c3;
    font-size: 3vw;
    color: #666;
    border-radius: 1vw;
    padding: 0 2vw;
    background-color: #fdfcfb;
}


.wrapper .function-container {
    margin: 30px 10px 10px 10px;
    padding: 10px;
    background-color: #fff;
    border-radius: 8px;
    border: groove;
}

.wrapper .function-container .function-item {
    display: flex;
    justify-content: space-between;
    height: 11vw;
    align-items: center;
    margin: 0 13px 0 13px;
    color: #7b869e;
}

.wrapper .function-container .function-item .item-text {
    display: flex;
    align-items: center;
}

.wrapper .function-container .function-item .item-img {
    width: 7vw;
    margin-right: 4px;
}


/* 新增：注销账号和退出登录样式 */
.wrapper .logout-container {
    display: flex;
    justify-content: space-between;
    margin: 25px 27px;
    padding: 10px;
    background-color: #fff;
}

.wrapper .logout-btn {
    width: 45%;
    height: 45px;
    font-size: 20px;
    border-radius: 3vw;
    cursor: pointer;
    transition: all 0.3s ease;
    border: groove;
}

.wrapper .cancel-account {
    background-color: #999999;
    color: #fff;
}

.wrapper .exit-login {
    color: #596164;
    background-color: #92dffb;
}

/* 新增：修改性别对话框样式 */
.sex-radio-group {
    display: flex;
    justify-content: center;
    gap: 30px;
    margin: 20px 0;
}

.sex-radio-group .el-radio {
    font-size: 16px;
}
</style>