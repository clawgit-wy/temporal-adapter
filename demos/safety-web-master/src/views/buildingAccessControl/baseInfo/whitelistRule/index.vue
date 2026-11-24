<template>
  <div>
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" @click="handleCreate">新增</a-button>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
            :actions="[
              {
                label: '删除',
                onClick: handleDelete.bind(null, record),
              },
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <WhitelistRuleModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { columns, searchFormSchema } from './whitelistRule.data';
  import { getWhitelistRuleList, deleteWhitelistRule } from './whitelistRule.api';
  import WhitelistRuleModal from './WhitelistRuleModal.vue';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createMessage, createConfirm } = useMessage();

  const [registerTable, { reload }] = useTable({
    title: '',
    api: getWhitelistRuleList,
    columns,
    formConfig: {
      labelWidth: 120,
      schemas: searchFormSchema,
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
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
    actionColumn: {
      width: 80,
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
    },
  });

  const [registerModal, { openModal }] = useModal();

  // 新增白名单规则
  function handleCreate() {
    openModal(true, {
      isUpdate: false,
    });
  }

  // 删除白名单规则
  async function handleDelete(record: Recordable) {
    createConfirm({
      iconType: 'warning',
      title: '提示',
      content: '是否确认删除该条规则？',
      onOk: async () => {
        await deleteWhitelistRule(record.id);
        createMessage.success('删除成功');
        reload();
      },
    });
  }

  // 新增或编辑成功后刷新表格
  function handleSuccess() {
    reload();
  }
</script>
