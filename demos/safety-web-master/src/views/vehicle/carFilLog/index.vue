<template>
  <div class="p-4">
    <!--定义表格-->
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button
          type="primary"
          preIcon="ant-design:export-outlined"
          class="custom-button"
          v-if="hasAuth('VehicleManagement:carFilLog:issue')"
          @click="issue"
        >
          手动下发</a-button
        >
        <a-button
          type="primary"
          preIcon="ant-design:search-outlined"
          class="custom-button"
          v-if="hasAuth('VehicleManagement:carFilLog:export')"
          @click="onExportXls"
        >
          导出</a-button
        >
      </template>
      <template #issuanceResult="{ record }">
        <span v-if="record.issuanceResult == 'true'">成功</span>
        <span v-else style="color: red">失败</span>
      </template>
      <!--操作栏-->
      <!-- <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template> -->
    </BasicTable>
  </div>
</template>

<script lang="ts" name="basic-table-demo" setup>
  import { searchFormSchema, columns } from './car.data';
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { Api, list, updateIssuance } from './api';
  import { message } from 'ant-design-vue';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { ref } from 'vue';
  const { hasPermission } = usePermission();
  function hasAuth(code) {
    return hasPermission(code);
  }
  // 列表页面公共参数、方法
  const { tableContext, onExportXls } = useListPage({
    designScope: 'basic-table-demo-filter',
    tableProps: {
      title: '',
      api: list,
      columns: columns,
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['submissionTime', ['downStartTime', 'downEndTime'], 'YYYY-MM-DD']],
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
      url: Api.exportXls,
      name: '备案下发日志',
    },
  });
  const issue = async () => {
    await updateIssuance();
    message.success('操作成功');
    reload();
  };
  //BasicTable绑定注册
  const [registerTable, { reload }] = tableContext;
</script>
<style lang="less" scoped>
  ::v-deep(.ant-picker-range) {
    width: 100%;
  }
</style>
