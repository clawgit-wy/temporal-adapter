<template>
  <div class="list-container">
    <SelectAccessFormItem
      v-for="(item, index) in list"
      :key="item.deviceCode"
      :item="item"
      @remove="removeAction(item, index)"
      @selectedAccess="(doors) => selectedAccess(doors, item, index)"
      @detailLoaded="(detail) => updateItemDetail(detail, index)"
      ref="formRefs"
      :showRemove="!isView"
    />
    <div class="add-button" v-if="!isView">
      <a-button type="primary" @click="addAccess" :disabled="false" >增加</a-button>
    </div>
    <DoorAccessSelect v-model:value="showDoorSelect" placeholder="请选择门禁" @ok="selectedDoorAction" />
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import SelectAccessFormItem from './SelectAccessFormItem.vue';
  import DoorAccessSelect from '/@/components/DoorAccessSelect/index.vue';
  import { useAccessRequestForm } from './useAccessRequest';

  const { isView } = useAccessRequestForm();

  interface Item {
    key: string;
    title: string;
    [key: string]: any;
  }

  const showDoorSelect = ref(false);
  const formRefs = ref([]);

  // 验证所有表单
  const validateAll = async () => {
    try {
      const res = await Promise.all(formRefs.value.map((formRef: any) => formRef.validate()));

      console.log('历史结果', res);
      return res;
    } catch (error) {
      console.log('验证失败88888888', error);
      return false;
    }
  };

  // 定义事件
  const emit = defineEmits(['add', 'remove', 'selectedAccess', 'update:list', 'update']);
  // 属性定义
  const props = defineProps({
    list: {
      type: Array,
      default: () => [],
    },
  });
  // 增加
  const addAction = (item: any, index: number) => {
    if (isView.value) return; // 查看模式不允许操作
    console.log('增加', item, index);
    emit('add', item, index);
  };

  // 删除
  const removeAction = (item: any, index: number) => {
    debugger
    if (isView.value) return; // 查看模式不允许操作
    console.log('删除', item, index);
    emit('remove', item, index);
  };

  // 选择门禁
  const selectedAccess = (doors: any, item: any, index: number) => {
    if (isView.value) return; // 查看模式不允许操作
    console.log('选择门禁', item, index);
    emit('selectedAccess', doors, item, index);
  };

  // 新增门禁
  function addAccess() {
    console.log('新增门禁');
    showDoorSelect.value = true;
  }

  // 更新门禁项详情
  function updateItemDetail(detail: any, index: number) {
    console.log('更新门禁项详情', detail, index);
    emit('update', detail, index);
  }

  // 选中门禁
  function selectedDoorAction(doors) {
    doors = doors.map((door) => {
      return {
        deviceCode: door.key,
        deviceName: door.title,
      };
    });
    console.log('选中门禁', doors);
    // if (isView.value) return; // 查看模式不允许操作
    const updatedListTemp = doors.filter((door) => !props.list.some((item) => item.deviceCode === door.deviceCode));
    console.log('选中门禁--------------', updatedListTemp);
    const updatedList = [...props.list, ...updatedListTemp];

    emit('update:list', updatedList);
  }
  // 暴露验证方法
  defineExpose({
    validateAll,
  });
</script>

<style lang="less" scoped>
  .list-container {
    .add-button {
      display: flex;
      .ant-btn {
        flex: 1;
        color: rgba(0, 0, 0, 0.85);
        border-color: #d9d9d9;
        background: #fff;
        border-style: dashed;

        &[disabled] {
          color: rgba(0, 0, 0, 0.25);
          background-color: #f5f5f5;
          border-color: #d9d9d9;
          text-shadow: none;
          box-shadow: none;
          cursor: not-allowed;
        }
      }
    }
  }
</style>
