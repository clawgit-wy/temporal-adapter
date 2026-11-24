<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="公共区域门禁"
    width="800px"
    :canFullscreen="false"
    :showOkBtn="false"
    cancelText="关闭"
  >
    <BasicTable @register="registerTable">
      <template #action="{ record, index }">
        <TableAction :actions="getTableAction(record, index)" />
      </template>
    </BasicTable>
  </BasicModal>
</template>

<script lang="ts" setup>
import { ref, unref } from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
import { ActionItem,BasicTable, useTable, TableAction } from '/@/components/Table';
import { useMessage } from '/@/hooks/web/useMessage';
const { createConfirm } = useMessage();
const emit = defineEmits(['register', 'success', 'update:deviceList']);

const deviceList = ref([]);
const isView = ref(false);
const currentRecord = ref(null);

const columns = [
  {
    title: '门禁名称',
    dataIndex: 'deviceName',
    width: 300,
  },
 
];

const [registerTable, { setTableData, getDataSource }] = useTable({
  columns,
  showTableSetting: false,
  bordered: true,
  showIndexColumn: true,
  canResize: true,
  pagination: false,
  rowKey: 'deviceCode',
  showActionColumn: true,
  actionColumn: {
    width: 120,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' },
  },
});

const [registerModal, { setModalProps }] = useModalInner((data) => {
  isView.value = data?.isView || false;
  deviceList.value = data?.deviceDOList || [];
  currentRecord.value = data?.record || null;  
  setTableData(deviceList.value);
});

function confirmRemoveDevice(record, index) {
  const deviceName = record?.deviceName || '';
  createConfirm({
    iconType: 'warning',
    title: '提示',
    content: '确定删除公共区域门禁?',
    okText: '确定',
    onOk: async () => {
      handleRemoveDevice(index);
    },
  });
}

function handleRemoveDevice(index) {
  const newList = [...deviceList.value];
  newList.splice(index, 1);
  deviceList.value = newList;
  setTableData(newList);
  
  // 更新父组件中的数据
  if (currentRecord.value) {
    currentRecord.value.deviceDOList = newList;
    emit('success', { record: currentRecord.value, deviceList: newList });
  }
}
  /**
   * 操作栏
   */
   function getTableAction(record, index): ActionItem[] {
    if(isView.value){
      return [];
    }
    const actions: ActionItem[] = [
      { 
        label: '删除', 
        color: 'error',
        onClick: () => confirmRemoveDevice(record, index) 
      }
    ];
    return actions;
  }
// 导出注册方法供外部使用
defineExpose({
  registerModal,
});
</script>

<style lang="less" scoped>
.public-device-container {
  padding: 16px;
  
  .tags-container {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .empty-text {
    text-align: center;
    color: #999;
    padding: 20px 0;
  }
}
</style> 
