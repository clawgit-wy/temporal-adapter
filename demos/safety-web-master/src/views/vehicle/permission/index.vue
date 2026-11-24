<template>
  <div class="p-4">
    <!--定义表格-->
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button
          type="primary"
          preIcon="ant-design:plus-outlined"
          class="custom-button"
          @click="onAdd"
          v-if="hasAuth('VehicleManagement:permission:add')"
        >
          新增</a-button
        >
      </template>
      <template #tableTitle>
        <div style="padding-left: 20px">
          <a-tabs v-model:activeKey="fdConfigType" @change="tableChange">
            <a-tab-pane key="1" tab="个人"></a-tab-pane>
            <a-tab-pane key="2" tab="组织"></a-tab-pane>
          </a-tabs>
        </div>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <Modal @register="registerModal" @success="reload"></Modal>
    <DeptModal @register="deptRegisterModal" @success="reload"></DeptModal>
    <a-modal destroyOnClose v-model:visible="visible" title="组织架构选择"  width="1000px" height="600px" @ok="handleOk">
      <DeptTransfer  @change="deptChange" :defaultList="selectedKeys"></DeptTransfer>
    </a-modal>
  </div>
</template>

<script lang="ts" name="basic-table-demo" setup>
  import { searchFormSchema, columns, deptColumns } from './data';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import Modal from './Modal.vue';
  import DeptModal from './ModalDept.vue';
  import { list, dlt, updatePowerOrg } from './api';
  import { usePermission } from '/@/hooks/web/usePermission';
  import DeptTransfer from './DeptTransfer.vue'
  import {  ref } from 'vue';
import { message } from 'ant-design-vue';
  const fdConfigType = ref('1')
  // 列表页面公共参数、方法
  const { tableContext } = useListPage({
    designScope: 'basic-table-demo-filter',
    tableProps: {
      title: '',
      api: list,
      columns: columns,
      formConfig: {
        schemas: searchFormSchema,
      },
      showIndexColumn: true,
      showActionColumn: false,
      ellipsis: true,
      fetchSetting: {
        // 传给后台的当前页字段
        pageField: 'pageNum',
        // 传给后台的每页显示多少条的字段
        sizeField: 'pageSize',
        // 接口返回表格数据的字段
        listField: 'dataList',
        // 接口返回表格总数的字段
        totalField: 'total',
      },
      beforeFetch: (params) => {
        params['fdConfigType'] = fdConfigType.value
        // if (params.date) {
        //   params.startTime = dayjs(data.date).format("YYYY-MM-DD");
        //   params.endTime = dayjs
        // }
        return params;
      },
    },
    exportConfig: {
      url: '',
      name: '历史备案信息表',
    },
    importConfig: {
      url: '',
      success: () => reload(),
    },
  });
  //BasicTable绑定注册
  const [registerTable, { reload, setColumns }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  const [deptRegisterModal, { openModal: openDeptModal }] = useModal();

  const tableChange = (key) => {
    fdConfigType.value = key;
    if (key == 1) {
      setColumns(columns);
    } else {
      setColumns(deptColumns);
    }
    reload()
  };

  
  // 添加
  function onAdd() {
    console.log('add', fdConfigType.value);
    
    if(fdConfigType.value == '1') {
      openModal(true, {
        title: '新增',
        isUpdate: false,
        record: {},
        showFooter: true,
      });
    }else{
      openDeptModal(true, {
        title: '新增',
        isUpdate: false,
        record: {},
        showFooter: true,
      });
    }
  }
  // 删除
  async function onDelete(record) {
    console.log(record);
    if (record) {
      await dlt({ id: String(record.id) }, true);
      await reload();
    }
  }
  // 编辑
  function onEdit(record) {
    openModal(true, {
      title: '编辑',
      isUpdate: true,
      record: record,
      showFooter: true,
    });
  }
  function getTableAction(record): ActionItem[] {
    return [
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'VehicleManagement:permission:edit', ifShow: ()=>fdConfigType.value == '1'  },
      { label: '设置', onClick: setDept.bind(null, record), auth: 'VehicleManagement:permission:delete', ifShow: ()=>fdConfigType.value == '2' },
      { label: '删除', color: 'error', onClick: onDelete.bind(null, record), auth: 'VehicleManagement:permission:delete'},
    ];
  }
    // 穿梭框
  const visible = ref(false)
  let selectedKeys = ref('')
  const deptChange = (selectedKey) => {
    selectedKeys.value = selectedKey
  }

  const setDeptId = ref('')
  const setDept = (record) => {
    setDeptId.value = record.id
    if(record.fdPowerOrgCode) {
      selectedKeys.value = record.fdPowerOrgCode
    }
    visible.value = true
  } 
  const handleOk = () => {
    updatePowerOrg({
      id: setDeptId.value,
      powerOrgCode: selectedKeys.value
    }).then(() => {
      reload()
      message.success('操作成功')
      visible.value = false
    })
  }

  const { hasPermission } = usePermission();
  // 权限
  function hasAuth(code) {
    return hasPermission(code);
  }
</script>
<style lang="less" scoped>
  //   .custom-button {
  //     border-radius: 4px !important;
  //   }
  ::v-deep(.jeecg-basic-table-action .ant-divider, .jeecg-basic-table-action .ant-divider-vertical) {
    margin: 0px -5px !important;
  }

  ::v-deep(.ant-picker-range) {
    width: 100%;
  }
  ::v-deep(.jeecg-basic-table-action) {
    justify-content: center !important;
  }
  .tb-til {
    flex-wrap: nowrap;
    .ant-col {
      border: 1px solid #797979;
      padding-top: 10px;
      padding-bottom: 10px;
    }
  }
</style>
