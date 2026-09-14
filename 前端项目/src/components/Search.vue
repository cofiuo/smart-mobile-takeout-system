<template>
  <!-- 搜索栏 -->
  <div class="search">
    <div class="search-top" ref="fixedBox">
      <div class="search-box">
        <i class="search_icon" aria-hidden="true" />
        <input type="text" placeholder="placeholder" :value="modelValue" @input="onSearchInput" />
      </div>
    </div>
  </div>
</template>

<script setup>


const props = defineProps({
  placeholder: {
    type: String,
    default: '搜索'
  },
  modelValue: {
    type: String,
    default: ''
  },
  list: {
    type: Array,
    default: () => []
  },
})

</script>

<style scoped>
.search {
  width: 100%;
  height: 15vw;
  margin-top: -2vw;
  position: relative;
  z-index: 1000;
}

.search.fixed {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  z-index: 1000;
}

.search .search-top {
  width: 100%;
  height: 15vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  display: flex;
  justify-content: center;
  align-items: center;
}

.search .search-box {
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

.search .search-box .search_icon {
  background-image: url('../assets/search.png');
  width: 6.6vw;
  height: 6.6vw;
  display: block;
  background-size: cover;
}

.search .search-box input {
  width: 100%;
  height: 4.5vw;
  line-height: 4.5vw;
  font-size: 4vw;
  border: none;
  outline: none;
  margin-left: 2vw;
  color: #79859E;
  background: transparent;
}
</style>