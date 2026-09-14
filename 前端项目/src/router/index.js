import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/categoryList',
    name: 'CategoryList',
    component: () => import('@/views/CategoryList.vue')
  },
  {
    path: '/businessList',
    name: 'BusinessList',
    component: () => import('@/views/BusinessList.vue')
  },
  {
    path: '/businessinfo',
    name: 'BusinessInfo',
    component: () => import('@/views/BusinessInfo.vue')
  },
  {
    path: '/my',
    name: 'My',
    component: () => import('@/views/My.vue')
  },
  {
    path: '/orderConfirm',
    name: 'OrderConfirm',
    component: () => import('@/views/OrderConfirm.vue')
  },
  {
    path: '/address',
    name: 'Address',
    component: () => import('@/views/Address.vue')
  },
  {
    path: '/addressinfo',
    name: 'AddressInfo',
    component: () => import('@/views/AddressInfo.vue')
  },
  {
    path: '/payment',
    name: 'Payment',
    component: () => import('@/views/Payment.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue')
  },
  {
    path: '/myComment',
    name: 'MyComment',
    component: () => import('@/views/MyComment.vue')
  },
  {
    path: '/businessComment',
    name: 'BusinessComment',
    component: () => import('@/views/BusinessComment.vue')
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@/views/Order.vue')
  },
  {
    path: '/comment',
    name: 'Comment',
    component: () => import('@/views/Comment.vue')
  },
  {
    path:'/myFavorite',
    name:'MyFavorite',
    component:()=>import('@/views/MyFavorite.vue')
  },
  {
    path:'/chat',
    name:'Chat',
    component:()=>import('@/views/Chat.vue')
  }

]
const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
