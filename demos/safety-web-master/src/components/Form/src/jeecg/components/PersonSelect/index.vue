<template>
  <div>
    <PersonSelectInput
      :personList="value"
      :placeholder="'请选择'"
      :multiple="multiple"
      :needEip="true"
      :disabled="disabled"
      @change="peopleConfirm"
      @confirm="peopleConfirm"
    />
  </div>
</template>
<script lang="ts" setup>
  import { defineProps, computed, ref, defineEmits } from 'vue';

  import PersonSelectInput from '/@/components/PersonSelectTree/PersonSelectInput.vue';
  const props = defineProps({
    disabled: {
      type: Boolean,
      default: false,
    },
    value: {
      type: Array,
      default: () => [],
    },
    multiple: {
      type: Boolean,
      default: false,
    },
    placeholder: {
      type: String,
      default: '请选择',
    },
  });

  // 定义事件
  const emit = defineEmits(['update:value', 'change']);

  // 弹出框点击确定
  function peopleConfirm(list) {
    emit('update:value', list);
    emit('change', list);
  }
</script>
<style lang="less" scoped>
  .export-excel-emp-tips {
    font-size: 14px;
    line-height: 1.6;
    border: 1px solid #ff4d4f;
    border-radius: 4px;
    padding: 10px;
    background-color: #fff1f0;
    color: #1890ff;
    margin-bottom: 10px;
  }
</style>
