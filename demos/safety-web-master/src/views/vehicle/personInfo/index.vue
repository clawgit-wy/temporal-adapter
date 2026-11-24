<template>
  <div class="p-4">
    <!--定义表格-->
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button
          type="primary"
          preIcon="ant-design:export-outlined"
          class="custom-button"
          @click="onExportXlsx"
          v-if="hasAuth('VehicleManagement:personInfo:export')"
        >
          导出名单</a-button
        >
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <Modal @register="registerModal" @success="reload"></Modal>
  </div>
</template>

<script lang="ts" name="basic-table-demo" setup>
  import { usePermission } from '/@/hooks/web/usePermission';
  import Modal from './Modal.vue';
  import { searchFormSchema, columns } from './data';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { list, dlt, exportXls } from './api';
  import { useModal } from '/@/components/Modal';

  // 列表页面公共参数、方法
  const { tableContext, onExportXlsx } = useListPage({
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
        // if (params.date) {
        //   params.startTime = dayjs(data.date).format("YYYY-MM-DD");
        //   params.endTime = dayjs
        // }
        return params;
      },
    },
    exportConfig: {
      url: exportXls,
      name: '已登记人员信息表',
    },
  });
  //BasicTable绑定注册
  const [registerTable, { reload }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  const { hasPermission } = usePermission();

  function onEdit(record) {
    openModal(true, {
      title: '编辑',
      isUpdate: true,
      record: record,
      showFooter: true,
    });
  }
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    return [
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'VehicleManagement:personInfo:edit' },
      { label: '删除', onClick: onDelete.bind(null, record), auth: 'VehicleManagement:personInfo:delete' },
    ];
  }

  async function onDelete(record) {
    console.log('删除======');
    if (record) {
      // await dlt({ id: String(record.id) }, record.fdInFactory, true);
      // await reload();
      openModal(true, {
        title: '删除',
        isUpdate: true,
        record: record,
        showFooter: true,
      });
    }
  }
  // 权限
  function hasAuth(code) {
    return hasPermission(code);
  }
</script>
<style lang="less" scoped>
  ::v-deep(.ant-picker-range) {
    width: 100%;
  }
  ::v-deep(.jeecg-basic-table-action) {
    justify-content: center !important;
  }
  ::v-deep(.jeecg-basic-table-action .ant-divider, .jeecg-basic-table-action .ant-divider-vertical) {
    margin: 0px -5px !important;
  }
</style>
