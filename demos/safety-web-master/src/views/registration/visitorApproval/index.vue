<template>
  <div>
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" @click="handleApply">授权申请</a-button>
      </template>
      <template #toolbar>
        <a-button type="primary" preIcon="ant-design:plus-outlined" v-if="hasPermission('registration:visitorApproval:add')" @click="handleCreate">新增</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" v-if="hasPermission('registration:visitorApproval:export')" @click="handleExport">导出</a-button>
      </template>
      <!-- 授权流程列 - 查看 -->
      <template #bpmFlow="{ record }">
        <a-button v-if="record.bpmFlowId && record.authorizationApprovalId" type="link" @click="handleViewBpm(record)">查看</a-button>
      </template>
      <!-- 操作列 -->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <!-- 新增/编辑弹窗 -->
    <AddModal @register="registerAddModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="visitor-approval" setup>
import { BasicTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import { useModal } from '/@/components/Modal';
import { addFullParam } from '/@/utils/router/fullParam';
import { useRouter } from 'vue-router';
import { cloneDeep } from 'lodash-es';
import { columns, searchFormSchema, getTableAction as getActionConfig } from './config';
import { getListPage, Api } from './field.api';
import AddModal from './addModal.vue';

const { hasPermission } = usePermission();
const router = useRouter();

  // 注册弹窗
  const [registerAddModal, { openModal }] = useModal();

  const { onExportXls, tableContext } = useListPage({
    designScope: 'visitor-approval-list',
    tableProps: {
      title: '访客审批授权表',
      api: getListPage,
      columns: columns,
      size: 'small',
      rowKey: 'id',
      formConfig: {
        schemas: searchFormSchema,
        showAdvancedButton: false,
      },
      showTableSetting: false,
      pagination: {
        pageSize: 15,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      fetchSetting: {
        pageField: 'pageNum',
        sizeField: 'pageSize',
        listField: 'dataList',
        totalField: 'total',
      },
      api: getListPage,
      showIndexColumn: true,
      actionColumn: {
        width: 100,
        title: '操作',
        fixed: 'right',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
    },
    exportConfig: {
      url: Api.exportXls,
      name: '访客审批授权表',
    },
  });

  const [registerTable, { reload }] = tableContext;

  /** 操作列配置 */
  function getTableAction(record) {
    const actions = getActionConfig(hasPermission);
    actions.forEach((action) => {
      action.onClick = handleEdit.bind(null, record);
    });
    return actions;
  }

  /** 授权申请 */
  function handleApply() {
    router.push(addFullParam({ name: 'VisitorApprovalApply' }));
  }

  /** 新增 */
  function handleCreate() {
    openModal(true, { isUpdate: false });
  }

  /** 编辑 */
  function handleEdit(record) {
    openModal(true, { isUpdate: true, record: cloneDeep(record) });
  }

  /** 查看授权流程 */
  function handleViewBpm(record) {
    router.push(addFullParam({
      name: 'VisitorApprovalApply',
      query: {
        formInstanceId: record.authorizationApprovalId,
        processId: record.bpmFlowId,
      },
    }));
  }

  /** 导出 */
  function handleExport() {
    onExportXls();
  }

  /** 弹窗成功回调 */
  function handleSuccess() {
    reload();
  }
</script>
