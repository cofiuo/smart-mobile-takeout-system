<template>

  <div class="header">
    <div class="location-text" @click="toBack()">
      <el-icon v-if="back">
        <ArrowLeft />
      </el-icon>
    </div>
    <p :style="back ? 'margin-right: 35px' : 'margin-right: 19px;'">{{ title }}</p>
    <div class="location-account"></div>
  </div>

</template>

<script setup>
import { defineProps } from 'vue'
import { useRouter } from 'vue-router'


const router = useRouter()

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  back: {
    type: Boolean,
    default: true
  }
})
const toBack = () => {
  if (props.back) {
    router.back();
  }
}

</script>

<style scoped>
/*头部*/
.header {
  width: 100%;
  height: 14vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  display: flex;
  align-items: center;
  justify-content: space-between;

}

.header .location-text {
  font-size: 4vw;
  font-weight: 600;
  color: #596164;
  margin: 0 1vw 0 3vw;
  display: flex;
  justify-content: left;
}

.header p {
  font-size: 4.5vw;
  letter-spacing: 0.5vw;
  color: #596164;
  /* margin-right: 52px;
  margin-right: 19px; */
}

.header .location-text .location_icon {
  background-image: url(../assets/location.png);
  width: 6.6vw;
  height: 6.6vw;
  display: block;
  background-size: cover;
}

.header .location-text .fa-angle-down {
  margin-left: 1.5vw;
}
</style>