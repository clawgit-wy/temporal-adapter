<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable" :rowSelection="rowSelection" @selectionChange="selectionChange" @editEnd="editEnd" @editCancel="editCancel">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="queryUpdate" v-if="hasAuth('members:employee:batchUpdateRemark')"
          >全量修改</a-button
        >
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="showModal" v-if="hasAuth('members:employee:batchUpdateRemark')">
          批量备注</a-button
        >
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls" v-if="hasAuth('members:employee:export')"> 导出</a-button>
        <a-button type="primary" preIcon="ant-design:sync-outlined" :loading="syncDropDownBoxLoading" @click="handleSyncDropDownBox">
          同步岗位等筛选项</a-button
        >
      </template>
    </BasicTable>
    <Modal @register="registerModal" @success="reload" :selectKeys="selectedRowKeys" :query="queryParams" :updateType="changeFlag" />
  </div>
</template>

<script lang="ts" name="members-employee" setup>
  import { ref } from 'vue';
  import { BasicTable } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import Modal from './Modal.vue';
  import { Api, list, updateRemark, saveDropDownBox } from './api';
  import { columns, searchFormSchema } from './data';
  import { useMessage } from '/@/hooks/web/useMessage';
  const { createMessage } = useMessage();
  const queryParams = ref<Record<string, string> | null>(null);
  const syncDropDownBoxLoading = ref(false);
  const changeFlag = ref(1); // 1 根据查询条件修改 2 根据选择项修改

  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'members-employee',
    tableProps: {
      title: '员工信息台账数据',
      api: list,
      columns: columns,
      showActionColumn: false,
      rowKey: 'id',
      rowSelection: {
        type: 'checkbox',
      },
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [
          ['date', ['startTime', 'endTime'], 'YYYY-MM-DD'],
        ],
      },
      showIndexColumn: true,
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
        if (params.empTypes && Array.isArray(params.empTypes)) {
          params.empTypes = params.empTypes.join(',');
        }
        if (params.jobTypeNames && Array.isArray(params.jobTypeNames)) {
          params.jobTypeNames = params.jobTypeNames.join(',');
        }
        return params;
      },
    },
    exportConfig: {
      url: Api.exportXls,
      name: '员工信息台账',
    },
  });

  // 注册 ListTable
  const [registerTable, { reload, getDataSource, getForm }, { rowSelection, selectedRowKeys }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  const { hasPermission } = usePermission();
  const queryUpdate = () => {
    const { getFieldsValue } = getForm();
    queryParams.value = getFieldsValue();
    changeFlag.value = 1;
    openModal(true, {
      title: '批量备注(根据查询条件修改)',
      showFooter: true,
      record: {},
    });
  };
  const selectionChange = ({ keys, rows }) => console.log(keys, rows);
  const editEnd = ({ record, key, value }) => {
    console.log(12346556565656, record, key, value);
    updateRemark({ ids: record.id, content: value });
  };
  const editCancel = () => {};

  /**
   * 备注表单
   */
  function showModal() {
    if (selectedRowKeys.value.length == 0) {
      return createMessage.warn('请选择数据');
    }
    changeFlag.value = 2;
    openModal(true, {
      title: '批量备注',
      showFooter: true,
      record: {},
    });
  }

  async function handleSyncDropDownBox() {
    try {
      syncDropDownBoxLoading.value = true;
      await saveDropDownBox();
      createMessage.success('同步成功');
    } finally {
      syncDropDownBoxLoading.value = false;
    }
  }

  function hasAuth(code) {
    return hasPermission(code);
  }
</script>

<style lang="less" scoped>
  @import 'index';
  .blacklistUpload {
    padding: 10px;
  }
</style>
