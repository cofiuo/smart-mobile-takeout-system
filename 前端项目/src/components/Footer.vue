<template>
    <ul class="footer">
        <li >
            <i class="icon icon-home" @click="toHome"/>
            <p>首页</p>
        </li>
        <li>
            <i class="icon icon-cart" @click="toCart()"/>
            <p>购物车</p>
        </li>
        <li>
            <i class="icon icon-order" @click="toOrder()"/>
            <p>订单</p>
        </li>
        <li>
            <i class="icon icon-me" @click="toMy" />
            <p>我的</p>
        </li>
    </ul>
</template>

<script setup>
import { useRouter } from 'vue-router';
import {getSessionStorage} from '../common.js';


const router = useRouter();

const account = getSessionStorage('account');

const toHome = () => {
    router.push('/');
};

const toOrder = () => {
    router.push('/order');
};

const toCart = () => {
    router.push('/cart');
};

const toMy = () => {
    if(account) {
        router.push('/my');
    } else {
        router.push('/login');
    }
};

</script>

<style scoped>
.icon {
    width: 8vw;
    height: 8vw;
    display: block;
    background-size: cover;
}

.icon-me {
    background-image: url(../assets/me.png);
}

.icon-order {
    background-image: url(../assets/order.png);
}

.icon-home {
    background-image: url(../assets/home.png);
}

.icon-cart {
    background-image: url(../assets/cart.png);
}

/*/////底部导航栏/////*/
.wrapper .footer {
    width: 100%;
    height: 14vw;
    border-top: solid 1px #ddd;
    background-color: #fff;
    position: fixed;
    left: 0;
    bottom: -0.1vw;
    display: flex;
    justify-content: space-around;
    align-items: center;
}

.wrapper .footer li {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: #79859E;
    user-select: none;
    cursor: pointer;
}

.wrapper .footer li p {
    font-size: 2.8vw;
}

.wrapper .footer li i {
    font-size: 5vw;
}
</style>