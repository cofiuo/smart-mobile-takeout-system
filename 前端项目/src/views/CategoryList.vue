<template>
    <div class="wrapper">
        <!-- 头部分 -->
        <Header title="全部分类列表" />
        <!-- 搜索栏 -->
        <div class="search">
            <div class="search-top" ref="fixedBox">
                <div class="search-box">
                    <i class="search_icon" aria-hidden="true" />
                    <input type="text" placeholder="搜索商家分类" :value="searchKeyword" @input="onSearchInput" />
                </div>
            </div>
        </div>
        <ul class="category-ul">
            <!-- 循环显示全部分类信息 -->
            <li v-for="item in categoryList" :key="item.categoryId" @click="toBusinessList(item.categoryId)">
                <img :src="item.categoryCover" />
                <p>{{ item.categoryName }}</p>
            </li>
        </ul>

        <Footer></Footer>
    </div>
</template>

<script setup>
import Footer from '@/components/Footer.vue'
import Header from '@/components/Header.vue';
// 导入依赖
import { ref, reactive, onMounted } from 'vue';
import { get, post } from "@/api/index.js"
import { useRouter } from "vue-router"

const router = useRouter();  //创建路由对象
//搜索关键词
const searchKeyword = ref('');
//保存原始数据
const originalCategoryList = ref([]);


//传递分类编号，跳转到商家列表页面
const toBusinessList = (categoryId) => {
    router.push({ path: '/businessList', query: { categoryId: categoryId } })
}


const toBack = () => {
    router.back();
}

const categoryList = ref([]);
//加载商家分类数据
const loadCategory = () => {
    get('/category/list').then(res => {
        if (res.data.code == 20000) {
            //总共11条 分类数据，页面显示10条
            categoryList.value = res.data.resultData;
            originalCategoryList.value = [...res.data.resultData];
        } else {
            ElMessage({
                message: res.data.message,
                type: 'error',
            });
        }
    }).catch(e => { console.log(e) });
}

//搜索功能
const search = () => {
    if (!searchKeyword.value.trim()) {
        // 如果搜索关键词为空，显示全部分类
        categoryList.value = [...originalCategoryList.value];
    } else {
        // 根据关键词过滤分类数据
        categoryList.value = originalCategoryList.value.filter(item =>
            item.categoryName.toLowerCase().includes(searchKeyword.value.toLowerCase())
        );
    }
}

// 监听搜索框输入变化，实现即时搜索
const onSearchInput = (event) => {
    searchKeyword.value = event.target.value;
    search();
}

//初始化页面的方法
const init = () => {
    loadCategory();
}
init();
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

/*商品分类*/
.wrapper .category-ul {
    margin-top: 3vw;
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    padding: 0 5vw;
    align-items: center;
    box-sizing: border-box;
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
</style>
