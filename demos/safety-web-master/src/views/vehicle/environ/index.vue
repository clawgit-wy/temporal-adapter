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
          v-if="hasAuth('VehicleManagement:environ:export')"
        >
          导出</a-button
        >
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="basic-table-demo" setup>
  import { searchFormSchema, columns } from './environ.data';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { list, dlt, exportXls } from './api';
  import { usePermission } from '/@/hooks/web/usePermission';
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
      name: '物流车环保信息表',
    },
  });
  //BasicTable绑定注册
  const [registerTable, { reload }] = tableContext;

  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    return [{ label: '删除', onClick: onDelete.bind(null, record), auth: 'VehicleManagement:environ:delete' }];
  }
  const { hasPermission } = usePermission();
  // 权限
  function hasAuth(code) {
    return hasPermission(code);
  }

  async function onDelete(record) {
    if (record) {
      await dlt({ id: String(record.id) }, record.fdInFactory, true);
      reload();
    }
  }
</script>
<style lang="less" scoped>
  ::v-deep(.jeecg-basic-table-action) {
    justify-content: center !important;
  }
</style>
