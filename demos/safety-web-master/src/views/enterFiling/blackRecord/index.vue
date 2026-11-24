<template>
  <!--定义表格-->
  <BasicTable @register="registerTable">
    <template #toolbar>
      <a-button type="primary" class="custom-button" @click="onExportXls()">导出</a-button>
      <a-button type="primary" class="custom-button" @click="enterApply()">受限人员入厂申请</a-button>
    </template>
    <!--操作栏-->
    <template #action="{ record }">
      <TableAction :actions="getTableAction(record)" />
    </template>
  </BasicTable>
</template>
<script lang="ts" setup>
  import { columns, formSchemas } from './data';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { list, deleteDict, Api } from './api';
  import { useMessage } from '/@/hooks/web/useMessage';
  const { createMessage } = useMessage();
  import { addFullParam } from '/@/utils/router/fullParam';
  import { usePermission } from '/@/hooks/web/usePermission';
  const { hasPermission } = usePermission();
  import { useRouter } from 'vue-router';
  const router = useRouter();
  function hasAuth(code) {
    return hasPermission(code);
  }
  // 列表页面公共参数、方法
  const { onExportXls, tableContext } = useListPage({
    tableProps: {
      title: '受限人员入厂备案记录',
      api: list,
      columns: columns,
      useSearchForm: true,
      formConfig: {
        schemas: formSchemas,
      },
      rowKey: 'id',
      rowSelection: {
        type: 'checkbox',
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
        params['supperCode'] = 'enter_per_type';
        params['supperId'] = 2;
        return params;
      },
    },
    exportConfig: {
      url: Api.export,
      name: '黑名单入厂备案记录',
    },
  });
  //BasicTable绑定注册
  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    // return [{ label: '详情', onClick: onDetail.bind(null, record), auth: 'EnterFiling:blackRecord:view' }];
    return [{ label: '详情', onClick: onDetail.bind(null, record) }];
  }
  // 详情
  function onDetail(record) {
    router.push(addFullParam({
      path: '/blackListEnterApply',
      query: {
        formInstanceId: record.processId,
        type: 'view',
      },
    }));
  }
  // 黑名单入厂申请
  function enterApply(record) {
    router.push(addFullParam({
      path: '/blackListEnterApply',
    }));
  }

  const selectionChange = ({ keys, rows }) => console.log(keys, rows);
</script>
<style lang="less" scoped>
  ::v-deep(.ant-picker-range) {
    width: 100%;
  }

  ::v-deep(.ant-input-affix-wrapper) {
    // border-color: #797979;
    border-radius: 4px;
  }
</style>
