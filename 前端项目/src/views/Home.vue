<template>
    <div class="wrapper">
        <!-- 头部分 -->
        <div class="header">
            <div class="location-text">
                <i class="location_icon"></i>
                <span v-if="isLogin()">{{ account.accountName }}</span>

            </div>
            <!-- 用户登录之后显示头像 -->
            <div class="location-account" v-if="isLogin()">
                <img :src="account.accountImg" v-if="account.accountImg !== ''" />
            </div>
        </div>
        <!-- 搜索栏 -->
        <div class="search">
            <div class="search-top" ref="fixedBox">
                <div class="search-box">
                    <i class="search_icon" aria-hidden="true" />
                    <input type="text" placeholder="搜索商家名称、商品名称" @input="onSearchInput" />
                </div>
            </div>
        </div>

        <!-- 商品的分类信息 动态数据 
            {{ 插值表达式 }}只能将数据显示在标签中 <h1>{{变量名}}</h1> 
             插值表达式无法将数据  赋给 标签的属性。
             使用v-bind 指令完成标签属性的绑定（赋值）
             v-bind:src="item.categoryCover"  v-bind后属性"item.categoryCover"不再是字符串，而是表达式
             v-bind:src="item.categoryCover" 简写 :src="item.categoryCover"
             :key="不重复数据"  作用优化v-for循环显示渲染数据的效率
        -->
        <!-- 商品的分类信息 -->
        <ul class="category-ul" v-if="categoryList && categoryList.length">
            <span @click="toCategoryList">...</span>
            <li v-for="item in categoryList" :key="item.categoryId" @click="toBusinessList(item.categoryId)">
                <img :src="item.categoryCover" />
                <p>{{ item.categoryName }}</p>
            </li>
        </ul>



        <!-- 横幅广告 -->
        <div class="banner">
            <h3>广东技术师范大学</h3>
            <p>在线商城 齐全吃的香</p>
            <a>立即下单 &gt;</a>
        </div>

        <!-- 超级会员 -->
        <div class="supermember">
            <div class="left">
                <img src="../assets/super_member.png" />
                <h3>超级会员</h3>
                <p>&#8226; 每月享超级权益</p>
            </div>
            <div class="right">
                立即开通 &gt;
            </div>
        </div>

        <!--推荐商家-->
        <div class="show-business">
            <div class="show-business-line"></div>
            <p>推荐商家</p>
            <div class="show-business-line"></div>
        </div>

        <!-- 推荐方式 -->
        <ul class="showtype">
            <li>综合排序<i class="fa fa-caret-down" aria-hidden="true" /></li>
            <li>距离最近</li>
            <li>销量最高</li>
            <li>筛选<i class="fa fa-filter" aria-hidden="true" /></li>
        </ul>

        <!-- 商家列表信息 
            v-for="(元素,下标) in 数组"" 
        -->

        <ul class="business" v-if="businessList && businessList.length">
            <li v-for="item in businessList" :key="item.businessId" @click="toBusinessInfo(item.businessId)">
                <!-- 添加div，设置相对定位 -->
                <div style="position: relative; z-index:0">
                    <img :src="item.businessImg" />
                    <!-- 角标代码 从BusinessList.vue复制 -->
                    <div class="business-img-quantity" v-show="item.quantity">
                        {{ item.quantity }}
                    </div>
                </div>
                <div class="business-info">
                    <div class="business-info-h">
                        <h3>{{ item.businessName }}</h3>
                        <!-- 收藏商家 图标 -->
                        <div class="business-info-like" @click.stop="toggleFavorite(item)">
                            <img v-if="item.isFavorite" src="../assets/light.png" class="heart_icon" alt="已收藏" />
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
                        <p>&#165; {{ item.starPrice }} 起送 | &#165; {{ item.deliveryPrice }} 配送</p>
                        <!-- <p>3.0km | 30分钟</p> -->
                    </div>
                    <div class="business-info-explain">
                        <div> {{ item.businessExplain }} </div>
                    </div>
                </div>
            </li>
        </ul>



        <!-- vue3步骤2：使用组件 -->
        <Footer></Footer>
    </div>
</template>

<script setup>
//vue3步骤1： 导入公共组件 (子组件)
import Footer from '@/components/Footer.vue'
import { ref, onMounted, onUnmounted } from "vue"
import { useRouter } from "vue-router"
import { getSessionStorage } from '@/common';
import { get, post } from '@/api/index.js';
import { ElMessage } from 'element-plus'

const router = useRouter();  //创建路由对象
const categoryList = ref([]); //商品分类数据
const businessList = ref([]); //商家列表数据

//页面加载，读取sessionStorage中的用户数据
const account = getSessionStorage('account');

//搜索关键词
const searchKeyword = ref('');
//保存原始数据
const originalBusinessList = ref([]);

//传递分类编号，跳转到商家列表页面
const toBusinessList = (categoryId) => {
    router.push({ path: '/businessList', query: { categoryId: categoryId } })
}


//判断是否登录
const isLogin = () => {
    if (getSessionStorage('account') == null) {
        return false;
    } else {
        return true;
    }
}

//跳转分类列表页面
const toCategoryList = () => {
    router.push('/categoryList')
}

//获取商品分类数据
const loadCategories = () => {
    get('/category/list').then(res => {
        if (res.data.code === 20000) {
            let categoryListTemp = res.data.resultData;
            for (let i = 0; i < 10; i++) {
                categoryList.value.push(categoryListTemp[i]);
            }
        } else {
            ElMessage({
                message: res.data.message,
                type: 'error',
            })
        }
    })
        .catch(err => {
            console.log(err);
        });
}

//传递商家id，跳转到商家详情页面
const toBusinessInfo = (businessId) => {
    router.push({ path: '/businessInfo', query: { businessId: businessId } })
}


//获取商家列表数据
const loadBusiness = () => {
    get('/business/list').then(res => {
        if (res.data.code === 20000) {
            businessList.value = res.data.resultData
            originalBusinessList.value = [...res.data.resultData];
        } else {
            ElMessage({
                message: res.data.message,
                type: 'error',
            })
        }
        if (account != null) {
            loadCart();
            loadFavoriteStatus();
        }
    }).catch(err => { console.log(err); });
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

//搜索功能
const search = () => {
    if (!searchKeyword.value.trim()) {
        // 如果搜索关键词为空，显示全部分类
        businessList.value = [...originalBusinessList.value];
    } else {
        // 根据关键词过滤分类数据
        businessList.value = originalBusinessList.value.filter(item =>
            item.businessName.toLowerCase().includes(searchKeyword.value.toLowerCase())
            || item.businessExplain.toLowerCase().includes(searchKeyword.value.toLowerCase())
        );

    }
    // 重新加载收藏状态
    if (account) {
        loadFavoriteStatus();
    }
}

// 监听搜索框输入变化，实现即时搜索
const onSearchInput = (event) => {
    searchKeyword.value = event.target.value;
    search();
}


//加载商家收藏状态
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

//收藏/取消收藏
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


//初始化页面
const init = () => {
    //加载商品分类数据
    loadCategories();
    //加载商家列表数据
    loadBusiness();
}

init();


/* ---头部固定搜索--- */
const fixedBox = ref(null);
onMounted(() => {
    document.onscroll = () => {
        //获取滚动条位置
        let s1 = document.documentElement.scrollTop;
        let s2 = document.body.scrollTop;
        let scroll = s1 == 0 ? s2 : s1;
        //获取视口宽度
        let width = document.documentElement.clientWidth;

        //获取顶部固定块
        let search = fixedBox.value;

        //判断滚动条超过视口宽度的12%时，搜索块变固定定位
        if (scroll > width * 0.12) {
            search.style.position = "fixed";
            search.style.left = "0";
            search.style.top = "0";
        } else {
            search.style.position = "static";
        }
    };
});



// 销毁
onUnmounted(() => {
    //当切换到其他组件时，就不需要document滚动条事件，所以将此事件去掉
    document.onscroll = null;
});

</script>

<style scoped>
.wrapper {
    width: 100%;
    height: 100%;
}

/*头部*/
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

.wrapper .header .location-account img {
    width: 9vw;
    height: 9vw;
    border-radius: 5vw;
    margin: 2vw 3vw 0 0;
    border: 0.5vw solid #fff;
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


/*商品分类*/
.wrapper .category-ul {
    width: 100%;
    height: 44vw;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
    align-content: center;
    padding: 4.4vw;
    box-sizing: border-box;
    position: relative;
}

.wrapper .category-ul span {
    position: absolute;
    right: 1.5vw;
    top: -2vw;
    background-color: #ffde09;
    color: #fb8b06;
    border: 0.3vw solid #444;
    border-radius: 1vw;
    padding: 0.5vw 2vw;
    font-weight: 800;

    box-shadow: 0.2vw 0.2vw 0.2vw rgba(0, 0, 0, 0.5);
    z-index: 9999;
    height: 5vw;
}

.wrapper .category-ul li {
    width: 18vw;
    height: 18vw;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    user-select: none;
    cursor: pointer;
}

.wrapper .category-ul li img {
    width: 13vw;
    height: 11.3vw;
}

.wrapper .category-ul li p {
    font-size: 3.2vw;
    color: #79859E;
}

/*横幅广告*/
.wrapper .banner {
    width: 95%;
    margin: 0 auto;
    height: 29vw;
    background-image: url(../assets/index_banner.png);
    background-repeat: no-repeat;
    background-size: cover;
    box-sizing: border-box;
    padding: 3vw 6vw;
}

.wrapper .banner h3 {
    font-size: 4.2vw;
    margin-bottom: 1.2vw;
}

.wrapper .banner p {
    font-size: 3.4vw;
    color: #79859E;
    margin-bottom: 2.4vw;
}

.wrapper .banner a {
    font-size: 3.6vw;
    color: #FF3D8D;
    font-weight: 600;
    cursor: pointer;
}

/*超级会员*/
.wrapper .supermember {
    width: 95%;
    margin: 0 auto;
    height: 11.5vw;
    background: linear-gradient(to right bottom, #e2d1c3, #fdfcfb);
    margin-top: 1.3vw;
    border-radius: 1vw;
    color: #644f1b;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.wrapper .supermember .left {
    display: flex;
    align-items: center;
    margin-left: 4vw;
    user-select: none;
}

.wrapper .supermember .left img {
    width: 6vw;
    height: 6vw;
    margin-right: 2vw;
}

.wrapper .supermember .left h3 {
    font-size: 4vw;
    margin-right: 2vw;
}

.wrapper .supermember .left p {
    font-size: 3vw;
}

.wrapper .supermember .right {
    font-size: 3.4vw;
    margin-right: 4vw;
}

/*推荐商家*/
.wrapper .show-business {
    width: 100%;
    height: 14vw;
    display: flex;
    justify-content: center;
    align-items: center;
}

.wrapper .show-business .show-business-line {
    width: 8vw;
    height: 0.3vw;
    background-color: #e2d1c3;
}

.wrapper .show-business p {
    font-size: 4vw;
    margin: 0 4vw;
    color: #79859E;
}


/*推荐方式*/
.wrapper .showtype {
    width: 100%;
    height: 5vw;
    display: flex;
    justify-content: space-around;
    align-items: center;
    margin-bottom: 5vw;
    color: #79859E
}

.wrapper .showtype li {
    font-size: 3.5vw;
    color: #79859E;
}

/* 商家列表 */
.wrapper .business {
    width: 100%;
    margin-bottom: 14vw;
    padding-bottom: 15vw;

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

    /*设置成绝对定位，不占文档流空间*/
    position: absolute;
    right: -1.5vw;
    top: -1.5vw;
}

.wrapper .business li {
    width: 100%;
    box-sizing: border-box;
    padding: 2.5vw;
    user-select: none;
    border-bottom: 0.3vw solid #e2d1c3;
    display: flex;

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
    width: 5vw;
    height: 5vw;
    cursor: pointer;
    display: block;
    object-fit: contain;
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
    margin-top: 3px;
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
</style>