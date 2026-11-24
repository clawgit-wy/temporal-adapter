<template>
  <div class="org-auth-config">
    <div class="table-header mb-4">
      <a-button type="primary" @click="handleAdd">新增</a-button>
    </div>
    <BasicTable @register="registerTable">
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <OrgAuthModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup name="OrgAuthConfig">
  import { BasicTable, TableAction, ActionItem } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { orgAuthColumns as columns } from './authConfig.data';
  import { getAuthPageList, authDelete } from './authConfig.api';
  import OrgAuthModal from './orgAuth.modal.vue';

  const { createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();

  const { tableContext } = useListPage({
    designScope: 'org-auth-config',
    tableProps: {
      title: '访客授权配置（按组织）',
      api: getAuthPageList,
      columns,
      size: 'small',
      rowKey: 'id',
      showTableSetting: false,
      useSearchForm: false,
      showIndexColumn: true,
      fetchSetting: {
        pageField: 'pageNum',
        sizeField: 'pageSize',
        listField: 'dataList',
        totalField: 'total',
      },
      beforeFetch: (params) => {
        params.configType = 0;
        return params;
      },
      actionColumn: {
        width: 120,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
    },
  });

  const [registerTable, { reload, updateTableDataRecord }] = tableContext;

  function getTableAction(record): ActionItem[] {
    const actions: ActionItem[] = [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
    ];
    
    // 当 empOrgCodePath === 0 && configType === 0 时，不显示删除按钮
    if (!(Number(record.empOrgCodePath) === 0 && Number(record.configType) === 0)) {
      actions.push({
        label: '删除',
        color: 'error',
        popConfirm: {
          title: '是否确认删除？',
          confirm: handleDelete.bind(null, record),
        },
      });
    }
    
    return actions;
  }

  function handleAdd() {
    openModal(true, { isUpdate: false });
  }

  function handleEdit(record) {
    console.log('[OrgAuthConfig] handleEdit record:', JSON.parse(JSON.stringify(record)));
    openModal(true, { record: { ...record }, isUpdate: true });
  }

  async function handleDelete(record) {
    try {
      await authDelete(record.id);
      createMessage.success('删除成功');
      reload();
    } catch (error) {
      console.error('删除失败', error);
    }
  }

  function handleSuccess() {
    reload();
  }
</script>

<style lang="less" scoped>
.org-auth-config {
  .ant-table-wrapper {
    max-width: 900px;
  }
  .table-header {
    display: flex;
    justify-content: flex-end;
  }
}
</style>
