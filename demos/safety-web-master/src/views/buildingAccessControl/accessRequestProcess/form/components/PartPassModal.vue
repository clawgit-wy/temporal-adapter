<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="新增门禁审批"
    width="800px"
    :height="900"
    :canFullscreen="false"
    okText="确定"
    cancelText="取消"
    @ok="handOk"
  >
    <!-- 简化的上下布局 -->
    <div class="simple-layout">
      <!-- 门禁名称输入区域 -->
      <div class="input-group">
        <label class="field-label">门禁名称：</label>
        <a-input
          placeholder="请输入门禁名称"
          v-model:value="deviceName"
          class="field-input"
          @pressEnter="handSearch"
        />
      </div>
      <div class="input-group" style="margin-left: 10px">
        <label class="field-label">授权范围：</label>
        <a-input
          placeholder="请输入门禁名称"
          v-model:value="empowerAreaName"
          class="field-input"
          @pressEnter="handSearch"
        />
      </div>

      <!-- 按钮区域 -->
      <div class="button-group">
        <a-button type="primary" @click="handSearch">查询</a-button>
        <a-button style="margin-left: 8px" @click="handReset">重置</a-button>
      </div>
    </div>
    <span style="margin-left: 5px;color: red;font-weight: 800">请选择审批不通过的门禁</span>

    <BasicTable @register="registerTable">
    </BasicTable>
  </BasicModal>
</template>

<script lang="ts" setup>
import {ref, unref} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {BasicTable, useTable} from '/@/components/Table';
import {useMessage} from "/@/hooks/web/useMessage";
import {
  partPassProcess
} from "/@/views/buildingAccessControl/accessRequestProcess/form/accessRequestForm.api";

const emit = defineEmits(['register', 'success', 'update:deviceList']);

const otherParam = ref({})

const doorList = ref([]);
const deviceName = ref('')
const empowerAreaName = ref('')

const columns = [
  {
    title: '类型',
    dataIndex: 'empowerType',
    customRender: ({text}) => text && text.length > 0 && text === '1' ? '按人员' : '按组织',
    width: 80,
  },
  {
    title: '授权范围',
    dataIndex: 'empowerAreaName',
  },
  {
    title: '授权门禁',
    dataIndex: 'deviceName',
  },

];


const [registerTable, {setTableData, getSelectRows, clearSelectedRowKeys}] = useTable({
  columns,
  showTableSetting: false,
  bordered: true,
  showIndexColumn: false,
  canResize: true,
  pagination: true,
  rowKey: 'id',
  showActionColumn: true,
  rowSelection: {type: 'checkbox'},
});
const {createMessage} = useMessage();

const [registerModal] = useModalInner((data) => {
  doorList.value = data?.doorList || [];
  otherParam.value = data?.param || {};
  deviceName.value = ''
  clearSelectedRowKeys()
  setTableData(doorList.value);
});

async function handOk() {
  console.log('handleOk', getSelectRows());
  let allData = doorList.value;
  let secArr = getSelectRows();
  if (secArr.length === 0) {
    createMessage.error('请选择门禁');
    return
  }
  let notSelectedData = allData.filter(item =>
    !secArr.some(selectedItem => selectedItem.id === item.id)
  );

  let param = {
    ...otherParam.value,
    passIdStr: notSelectedData.map(item => item.id).join(','),
    failIdStr: secArr.map(item => item.id).join(','),
  }
  console.log('allData', allData)
  console.log(param)
  await partPassProcess(param)
  emit('success')
}

function handSearch() {
  let resultArr = doorList.value;
  if (deviceName.value.length > 0) {
    resultArr = doorList.value.filter(item => item.deviceName.indexOf(deviceName.value) > -1);
  }

  if (empowerAreaName.value.length > 0) {
    console.log('resultArr', resultArr)
    console.log('empower', empowerAreaName.value)
    resultArr = resultArr.filter(item => item.empowerAreaName.indexOf(empowerAreaName.value) > -1);
  }

  setTableData(resultArr);
}

const filters = {
  deviceName: deviceName.value,
  empowerAreaName: empowerAreaName.value
};


function filterDoorList(doorList, filters) {
  return doorList.filter(item => {
    return (filters.deviceName ? item.deviceName.includes(filters.deviceName) : true) &&
      (filters.empowerAreaName ? item.empowerAreaName.includes(filters.empowerAreaName) : true);
  });
}

function handReset() {
  deviceName.value = ''
  empowerAreaName.value = ''
  setTableData(doorList.value);
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

.simple-layout {
  display: flex;
  margin: 0 5px;
}

// 输入区域样式
.input-group {
  display: flex;
  align-items: center;
  margin-bottom: 16px; // 与按钮区域保持距离
}

// 标签样式
.field-label {
  text-align: left;
  padding-right: 12px;
  font-weight: 500;
}

// 输入框样式
.field-input {
  flex: 1; // 输入框占满剩余空间
  min-width: 200px;
}

// 按钮区域样式
.button-group {
  margin-left: auto;
}
</style>
