<template>
  <div class="systemd-noticemessage">
    <post v-show="category == '1'" :loading="loading" :category="category" @categoryChange="categoryChange" @apply="apply"></post>
    <personage v-show="category == '2'" :loading="loading" :category="category" @categoryChange="categoryChange" @apply="apply"></personage>
  </div>
</template>

<script lang="ts" name="checkcar-role" setup>
import { ref } from 'vue';
import post from './post.vue'
import personage from './personage.vue'
import { downAll } from './list.api';
import { Modal, message } from 'ant-design-vue';
const category = ref('1')
const categoryChange = (val) => {
  category.value = val
}
const loading = ref(false)
const apply = () => {
  Modal.confirm({
    title: '确认提醒',
    content: '确认执行？',
    onOk() {
      loading.value = true
      downAll()
      .then(res => {
        message.success('操作成功')
      })
      .catch(() => {
        message.error('操作失败')
      })
      .finally(() => {
        loading.value = false
      })
    },
  });
  
}
</script>

<style lang="less" scoped>

</style>
