<template>
  <div class="door-select-input" :class="{ 'is-disabled': disabled }">
    <!-- 统一使用标签输入框 -->
    <div class="tag-input-wrapper" :class="{ disabled: disabled }" @click="!disabled && openDoorSelect()">
      <div class="text">{{ item.title || item.deviceName }}</div>
      <div class="tag-input-icon">
        <KeyOutlined />
      </div>
    </div>

    <!-- 门禁选择树组件 -->
    <DoorAccessSelect
      @ok="onSelectedChange"
      v-model:value="doorSelectVisible"
      placeholder="请选择门禁"
      :selected="doorList"
      :disabled="disabled"
    />
  </div>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import DoorAccessSelect from './index.vue';
  import { KeyOutlined } from '@ant-design/icons-vue';

  // 定义门禁项类型
  interface DoorItem {
    key?: string;
    title?: string;
    deviceName?: string;
    deviceCode?: string;
    [key: string]: any;
  }

  // DoorAccessSelect组件需要的door格式
  interface DoorSelectItem {
    key: string;
    title: string;
    [key: string]: any;
  }

  // 定义组件属性
  const props = defineProps({
    value: {
      type: String,
      default: () => '',
    },
    item: {
      type: Object as () => DoorItem,
      default: () => ({}),
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  });

  // 定义事件
  const emit = defineEmits(['confirm']);

  // 计算属性 - 确保门禁列表符合DoorAccessSelect组件要求的格式
  const doorList = computed<DoorSelectItem[]>(() => {
    const item = props.item as DoorItem;
    return [
      {
        ...item,
        key: item.key || item.deviceCode || '',
        title: item.title || item.deviceName || '',
      },
    ];
  });

  // 组件内部状态
  const doorSelectVisible = ref(false);

  // 打开门禁选择对话框
  const openDoorSelect = () => {
    if (props.disabled || doorSelectVisible.value) return; // 如果已经打开或禁用则不再处理
    doorSelectVisible.value = true;
  };

  const onSelectedChange = (values: any) => {
    if (props.disabled) return; // 禁用状态下不触发更新
    console.log('改变了', values);
    emit('confirm', values);
  };
</script>

<style lang="less" scoped>
  .door-select-input {
    width: 100%;

    &.is-disabled {
      color: rgba(0, 0, 0, 0.25);
      background-color: #f5f5f5;
      border-color: #d9d9d9;
      box-shadow: none;
      cursor: not-allowed;
      opacity: 1;
    }
  }

  .tag-input-wrapper {
    display: flex;
    align-items: center;
    min-height: 32px;
    padding: 0 11px;
    border: 1px solid #d9d9d9;
    border-radius: 2px;
    background-color: #fff;
    cursor: pointer;
    transition: all 0.3s;
    font-size: 13px;
    color: #333;
    .text {
      flex: 1;
    }
    &:hover {
      border-color: #40a9ff;
    }

    &.disabled {
      background-color: #f5f5f5;
      cursor: not-allowed;
      border-color: #d9d9d9;

      &:hover {
        border-color: #d9d9d9;
      }
    }
  }

  .tag-input-content {
    flex: 1;
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    min-height: 30px;
    padding: 4px 0;
  }

  .tag-input-icon {
    margin-left: 8px;
    color: rgba(0, 0, 0, 0.45);
  }

  .door-tag {
    margin-right: 4px;
    margin-bottom: 4px;
    background-color: #e6f7ff;
    border-color: #91d5ff;
    color: #1890ff;
    font-size: 12px;
    display: flex;
    align-items: center;
    height: 22px;
    line-height: 22px;
    border-radius: 2px;
    padding: 0 7px;

    &:hover {
      opacity: 0.85;
    }

    :deep(.ant-tag-close-icon) {
      color: #1890ff;
      font-size: 10px;
      margin-left: 5px;
    }
  }

  .placeholder {
    color: #bfbfbf;
    user-select: none;
    font-size: 14px;
    line-height: 32px;
  }
</style>
