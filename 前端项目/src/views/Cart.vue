<template>
    <div class="wrapper">

        <Header title="购物车" />

        <!-- 搜索栏 -->
        <div class="search">
            <div class="search-top">
                <div class="search-box">
                    <i class="search_icon" aria-hidden="true" />
                    <input type="text" placeholder="搜索商品名称" @input="onSearchInput" />
                </div>
            </div>
        </div>

        <!-- 空购物车提示 -->
        <div class="empty-cart" v-if="!hasCartItems">
            <el-icon size="80">
                <ShoppingCart />
            </el-icon>
            <p>购物车空空如也~</p>
            <button @click="toHome()">去逛逛</button>
        </div>

        <!-- 商家-商品核心模块 -->
        <template class="cart-content" v-for="(merchant, index) in businessList" :key="merchant.businessId">
            <div class="merchant-card" v-if="merchant.quantity > 0">
                <!-- 商家头部 -->
                <div class="merchant-header">
                    <input type="checkbox" class="check-box" :checked="selectedBusinessId === merchant.businessId"
                        @change="handleMerchantSelect(merchant.businessId)" />
                    <span class="merchant-icon">🛒</span>
                    <span class="merchant-name">{{ merchant.businessName }}</span>
                </div>

                <!-- 商品列表 -->
                <div class="goods-list">
                    <div class="goods-item" v-for="(cart, cIndex) in cartList[index]" :key="cart.cartId">

                        <!-- <input type="checkbox" class="check-box"/> -->
                        <img class="goods-img" :src="cart.goods.goodsImg" />
                        <div class="goods-info">
                            <div class="goods-name">{{ cart.goods.goodsName }}</div>
                            <div class="goods-price">{{ cart.goods.goodsPrice }}元</div>
                        </div>
                        <div class="goods-count">
                            <!-- disabled 标签失效(不可用)  -->
                            <div @click="minus(index, cIndex)" :class="{ disabled: cart.quantity == 0 }">
                                <!--减去餐品数量按钮：如果数量等于或者小于0，那么按钮不显示  v-show="item.quantity!=0"-->
                                <el-icon>
                                    <RemoveFilled :style="cart.quantity == 0 ? 'color:#ccc' : ''" />
                                </el-icon>
                            </div>
                            <p>
                                <span> {{ cart.quantity }} </span>
                                <!-- <span v-show="true">1</span> -->
                            </p>
                            <div @click="add(index, cIndex)">
                                <el-icon>
                                    <CirclePlusFilled />
                                </el-icon>
                            </div>
                        </div>
                    </div>

                </div>

                <!-- 商家价格栏 -->
                <div class="merchant-price-bar">
                    <div class="send-price">起送 {{ merchant.starPrice }} 元</div>
                    <div class="current-price">当前 {{ calcMerchantTotal(index) }} 元</div>
                </div>
            </div>
        </template>

        <!-- 底部结算栏 -->
        <div class="checkout-bar" v-if="hasCartItems">
            <div class="checkout-left">
                <div class="total-section">
                    <button class="delete-btn" @click="handleDelete()" :disabled="!selectedBusinessId">
                        删除
                    </button>
                    <div class="total-price">
                        <span class="total-label">合计:</span>
                        <span class="total-value">¥ {{ totalPrice }}</span>
                    </div>
                </div>
                <div class="delivery-info">
                    <span>含配送费 ¥ {{ deliveryFee }}</span>
                </div>
            </div>
            <button class="checkout-btn" @click="handleCheckout()" :disabled="!selectedBusinessId">
                去结算
            </button>
        </div>

        <Footer />
    </div>
</template>

<script setup>

import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { getSessionStorage } from '@/common.js'
import { ref, computed } from 'vue'
import { get, post } from "@/api";
import Decimal from "decimal.js";
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRouter, useRoute } from "vue-router"


const router = useRouter();  //创建路由对象
const route = useRoute(); //获取路由参数


const account = getSessionStorage('account')

const businessList = ref([])
const cartList = ref([]);
const selectedBusinessId = ref('');

// 搜索关键词
const searchKeyword = ref('');
// 保存原始数据
const originalBusinessList = ref([]);
const originalCartList = ref([]);

const toHome = () => {
    router.push('/');
}



// 计算属性：判断购物车是否有商品
const hasCartItems = computed(() => {
    // 遍历所有商家，只要有商家的商品数量大于0，就表示购物车有商品
    return businessList.value.some(merchant => merchant.quantity > 0);
});

// 计算属性：商品总价
const goodsTotalPrice = computed(() => {
    let total = 0;
    if (selectedBusinessId.value) {
        const merchantIndex = businessList.value.findIndex(
            merchant => merchant.businessId === selectedBusinessId.value
        );
        if (merchantIndex > -1) {
            total = calcMerchantTotal(merchantIndex);
        }
    }
    return total;
});

// 计算属性：配送费
const deliveryFee = computed(() => {
    if (selectedBusinessId.value) {
        const merchant = businessList.value.find(
            merchant => merchant.businessId === selectedBusinessId.value
        );
        return merchant ? merchant.deliveryPrice : 0;
    }
    return 0;
});

// 计算属性：总价
const totalPrice = computed(() => {
    return new Decimal(goodsTotalPrice.value).add(deliveryFee.value).toNumber();
});


// 处理商家选择
const handleMerchantSelect = (businessId) => {
    // 如果点击的是已选中的商家，取消选中；否则选中当前商家
    if (selectedBusinessId.value === businessId) {
        selectedBusinessId.value = '';
    } else {
        selectedBusinessId.value = businessId;
    }
};


// 加载购物车
const loadCartList = () => {
    cartList.value = new Array(businessList.value.length).fill([]);
    for (let i = 0; i < businessList.value.length; i++) {
        let businessId = businessList.value[i].businessId;
        const currentIndex = i;
        let url = `/cart/listCart/${account.accountId}/${businessId}`;
        get(url).then(res => {
            if (res.data.code === 20000) {
                cartList.value[currentIndex] = res.data.resultData;
            }
        }).catch(e => { console.log(e) })
    }
}

// 加载商家列表
const loadBusiness = () => {
    get('/business/list').then(res => {
        if (res.data.code === 20000) {
            businessList.value = res.data.resultData;
            originalBusinessList.value = [...res.data.resultData];
            if (account != null) {
                loadCart();
            }
        } else {
            console.log('商家列表加载失败：', res.data.message)
        }
    }).catch(err => { console.log(err); });
}

// 加载购物车并关联商家
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
            loadCartList();
            // 在购物车数量更新后，保存完整的原始数据
            setTimeout(() => {
                originalBusinessList.value = [...businessList.value];
                originalCartList.value = JSON.parse(JSON.stringify(cartList.value));
            }, 100);
        }
    }).catch(e => { console.log(e) })
}

// 计算单个商家的总价
const calcMerchantTotal = (cIndex) => {
    let sum = 0;
    if (cartList.value[cIndex] && cartList.value[cIndex].length > 0) {
        cartList.value[cIndex].forEach(cart => {
            sum = new Decimal(cart.goods.goodsPrice).mul(cart.quantity).add(sum).toNumber();
        })
    }
    return sum;
}

// 减少商品数量
const minus = (index, cIndex) => {
    const currentItem = cartList.value[index][cIndex];

    if (currentItem.quantity > 1) {
        updateCart(index, cIndex, -1)
    } else {
        ElMessageBox.confirm(
            '购物车商品已剩1件,是否删除该商品?',
            '提示',
            {
                confirmButtonText: '确定删除',
                cancelButtonText: '取消',
                type: 'warning',
            }
        ).then(() => {
            let cart = {
                goodsId: currentItem.goods.goodsId,
                businessId: currentItem.businessId,
                accountId: account.accountId,
            }
            post('/cart/del', cart, true).then(res => {
                if (res.data.code === 20000) {
                    cartList.value[index].splice(cIndex, 1);
                    // 更新商家的总商品数量
                    updateMerchantQuantity(index);
                    // 如果该商家没有商品了，调用重新加载或直接更新
                    handleEmptyMerchant(index);
                }
            }).catch(e => { console.log(e) })
        }).catch((e) => { console.log(e) })

    }
}

// 添加商品数量
const add = (index, cIndex) => {
    // 注意：这里需要检查索引是否存在，因为删除后索引可能会变化
    if (!cartList.value[index] || !cartList.value[index][cIndex]) {
        console.log('商品不存在，可能需要重新加载购物车');
        loadCart();
        return;
    }
    const currentItem = cartList.value[index][cIndex];
    if (currentItem.quantity == 0) {
        //调用服务器，录入购物车记录
        let cart = {
            goodsId: currentItem.goods.goodsId,
            businessId: currentItem.businessId,
            accountId: account.accountId,
            quantity: 1
        }
        post('/cart/add', cart, true).then(res => {
            if (res.data.code === 20000) {
                //同步更新页面和数据库的数据
                currentItem.quantity = 1;
                updateMerchantQuantity(index);
            }

        }).catch(e => { console.log(e) })
    } else {
        //更新数量
        updateCart(index, cIndex, 1)

    }
}

//调用服务器端，执行购物车记录更新
const updateCart = (index, cIndex, num) => {
    let url = `/cart/update`;

    let cart = {
        goodsId: cartList.value[index][cIndex].goods.goodsId,
        businessId: cartList.value[index][cIndex].businessId,
        accountId: account.accountId,
        quantity: cartList.value[index][cIndex].quantity + num
    }

    post(url, cart, true).then(res => {
        if (res.data.code === 20000) {
            //同步更新页面和数据库的数据
            cartList.value[index][cIndex].quantity += num;
            // 更新商家的总商品数量
            updateMerchantQuantity(index);
        }
    }).catch(e => { console.log(e) })
}

// 更新商家商品总数
const updateMerchantQuantity = (index) => {
    if (!businessList.value[index]) return;

    let total = 0;
    cartList.value[index].forEach(item => {
        total += item.quantity;
    });
    businessList.value[index].quantity = total;
}

// 处理空商家的情况
const handleEmptyMerchant = (index) => {
    if (cartList.value[index].length === 0) {
        loadCart();
    }
}

// 搜索功能
const search = () => {
    if (!searchKeyword.value.trim()) {
        // 如果搜索关键词为空，重新加载完整数据
        loadBusiness();
        return;
    }

    // 确保有原始数据可供过滤
    if (originalBusinessList.value.length === 0 || originalCartList.value.length === 0) {
        return;
    }

    const keyword = searchKeyword.value.toLowerCase();

    // 复制原始数据进行筛选
    const filteredBusinessList = [...originalBusinessList.value];
    const filteredCartList = JSON.parse(JSON.stringify(originalCartList.value));

    // 遍历所有商家
    for (let i = 0; i < filteredBusinessList.length; i++) {
        const business = filteredBusinessList[i];
        const hasMatchingGoods = filteredCartList[i] && filteredCartList[i].some(cart =>
            cart.goods.goodsName.toLowerCase().includes(keyword)
        );

        const isMatchingBusiness = business.businessName.toLowerCase().includes(keyword);

        if (isMatchingBusiness || hasMatchingGoods) {
            // 如果商家名称匹配或有商品匹配，显示该商家的所有商品
            // 保持原始的商品数量
            let total = 0;
            if (filteredCartList[i] && filteredCartList[i].length > 0) {
                filteredCartList[i].forEach(item => {
                    total += item.quantity;
                });
            }
            business.quantity = total;
        } else {
            // 如果商家和商品都不匹配，清空该商家的商品
            if (filteredCartList[i]) {
                filteredCartList[i] = [];
            }
            business.quantity = 0;
        }
    }

    // 更新显示数据
    businessList.value = filteredBusinessList;
    cartList.value = filteredCartList;
}

// 监听搜索框输入变化，实现即时搜索
const onSearchInput = (event) => {
    searchKeyword.value = event.target.value;
    search();
}

// 处理删除操作
const handleDelete = () => {
    if (!selectedBusinessId.value) {
        ElMessage({
            message: '请选择一个商家进行删除',
            type: 'warning',
        });
        return;
    }

    ElMessageBox.confirm(
        '确定要删除该商家的所有购物车商品吗？',
        '删除确认',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {
        // 调用删除接口
        const url = `/cart/delByBusiness/${account.accountId}/${selectedBusinessId.value}`;
        post(url, {}, true).then(res => {
            if (res.data.code === 20000) {
                ElMessage({
                    message: '删除成功',
                    type: 'success',
                });
                // 重新加载购物车数据
                loadCart();
                // 清空选中状态
                selectedBusinessId.value = '';
            }
        }).catch(e => {
            console.log(e);
            ElMessage({
                message: '删除失败',
                type: 'error',
            });
        });
    }).catch(() => {
        // 取消删除
        ElMessage({
            message: '已取消删除',
            type: 'info',
        });
    });
};

// 处理结算 - 跳转到订单确认页面
const handleCheckout = () => {
    if (!selectedBusinessId.value) {
        ElMessage({
            message: '请选择一个商家进行结算',
            type: 'warning',
        });
        return;
    }

    // 获取选中的商家信息
    const merchantIndex = businessList.value.findIndex(
        merchant => merchant.businessId === selectedBusinessId.value
    );

    if (merchantIndex === -1) {
        ElMessage({
            message: '商家信息不存在',
            type: 'error',
        });
        return;
    }

    const merchant = businessList.value[merchantIndex];
    const cartItems = cartList.value[merchantIndex];

    if (!cartItems || cartItems.length === 0) {
        ElMessage({
            message: '该商家购物车为空',
            type: 'warning',
        });
        return;
    }

    // 检查是否满足起送价
    const goodsTotal = goodsTotalPrice.value;
    if (goodsTotal < merchant.starPrice) {
        ElMessage({
            message: `未达到起送价，还需¥${(merchant.starPrice - goodsTotal).toFixed(2)}`,
            type: 'warning',
        });
        return;
    }

    // 跳转到订单确认页面，传递商家ID
    router.push(
        {
            path: '/orderConfirm', query: { businessId: selectedBusinessId.value }
        });
};

const init = () => {
    loadBusiness();
    // console.log(cartList)
    // console.log(businessList)
}
init()

</script>

<style scoped>
.wrapper {
    width: 100%;
    height: 100%;
    padding-bottom: 30vw;
    box-sizing: border-box;
}

.wrapper .disabled {
    cursor: not-allowed;
    pointer-events: none;
}

.wrapper .goods-count {
    width: 16vw;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.wrapper .goods-count i {
    font-size: 5vw;
    color: #fb8b06;
    cursor: pointer;
}

.wrapper .goods-count p {
    padding: 0 2vw;
    font-size: 4.5vw;
    color: #333;
}

.cart-container {
    width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
    background-color: #ffffff;
    box-sizing: border-box;
    margin: 0;
    padding: 0;
}


/* 商家商品区域样式 */
.cart-content {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
}

.merchant-card {
    border: 2px groove;
    border-radius: 8px;
    overflow: hidden;
    margin: 10px;
}

.merchant-header {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    background-color: #f3fbfe;
    height: 40px;
}

.check-box {
    width: 18px;
    height: 18px;
    margin-right: 8px;
    cursor: pointer;
}

.merchant-icon {
    margin-right: 8px;
    font-size: 16px;
}

.merchant-name {
    font-size: 16px;
    font-weight: 500;
    color: #495657;
}

/* 商品列表样式 */
.goods-list {
    padding: 0;
    margin: 0;
}

.goods-item {
    display: flex;
    align-items: center;
    padding: 12px 15px;
    border-bottom: 1px solid #f0f0f0;
}

.goods-img {
    width: 60px;
    height: 60px;
    border-radius: 4px;
    margin: 0 10px;
    background-color: #f5f5f5;
    /* 图片占位背景 */
}

.goods-info {
    flex: 1;
}

.goods-name {
    font-size: 15px;
    margin-bottom: 5px;
    color: #333333;
    font-weight: 530;
}

.goods-price {
    font-size: 14px;
    color: #ff6600;
}


.count-btn {
    width: 24px;
    height: 24px;
    border: 1px solid #cccccc;
    border-radius: 50%;
    background-color: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 12px;
    padding: 0;
}

.count-num {
    margin: 0 8px;
    font-size: 14px;
}

/* 商家价格栏样式 */
.merchant-price-bar {
    display: flex;
    justify-content: space-between;
    padding: 8px 15px;
    background: linear-gradient(to right, #fff1eb, #ace0f9);
    color: #333333;
    font-size: 16px;
    height: 30px;
    align-items: center;
}

.send-price,
.current-price {
    font-weight: 500;
}

/* 结算栏样式 */
.checkout-bar {
    position: fixed;
    bottom: 14vw;
    left: 0;
    width: 100%;
    height: 15vw;
    background: linear-gradient(to top, #ffffff, #b5e5fa);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 4vw;
    box-sizing: border-box;
    border-top: 1px solid #eee;
    z-index: 1000;
    /* border: 2px groove; */
    border-radius: 8px;
}

.checkout-left {
    display: flex;
    flex-direction: column;
    flex: 1;
}

.total-section {
    display: flex;
    align-items: center;
    margin-bottom: 1vw;
}

.delete-btn {
    background-color: #ff4d4f;
    color: #fff;
    border: none;
    border-radius: 2vw;
    padding: 1vw 3vw;
    font-size: 3.5vw;
    font-weight: bold;
    cursor: pointer;
    margin-right: 4vw;
}

.delete-btn:active {
    background-color: #cf1322;
}

.delete-btn:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.total-price {
    font-size: 4vw;
    color: #333;
    display: flex;
    align-items: center;
}

.total-label {
    margin-right: 2vw;
}

.total-value {
    color: #ff0000;
    font-weight: bold;
    font-size: 4.5vw;
}

.delivery-info {
    font-size: 3vw;
    color: #999;
    margin-top: 1vw;
    margin-left: 18.5vw;
    /* 与合计价格左对齐，形成同一列 */
}

.checkout-btn {
    background-color: #30aa25;
    color: #fff;
    border: none;
    border-radius: 3vw;
    padding: 2vw 6vw;
    font-size: 4vw;
    font-weight: bold;
    cursor: pointer;
}

.checkout-btn:active {
    background-color: #e55a00;
}

.checkout-btn:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

/* 空购物车样式 */
.empty-cart {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 60vh;
    color: #999;
}

.empty-cart .el-icon {
    color: #ccc;
    margin-bottom: 20px;
}

.empty-cart p {
    font-size: 16px;
    margin-bottom: 20px;
}

.empty-cart button {
    background-color: #ff6600;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 20px;
    font-size: 14px;
    cursor: pointer;
}

/* 搜索栏样式 */
.wrapper .search {
    width: 100%;
    height: 15vw;
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

/* 底部导航栏样式 */
.cart-tabbar {
    display: flex;
    justify-content: space-around;
    padding: 8px 0;
    border-top: 1px solid #eeeeee;
    background-color: #ffffff;
}

.tab-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    font-size: 12px;
    color: #666666;
}

.tab-item.active {
    color: #ff6600;
}

.tab-icon {
    font-size: 18px;
    margin-bottom: 2px;
}

.tab-text {
    margin-top: 2px;
}
</style>
