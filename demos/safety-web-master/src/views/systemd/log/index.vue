<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
      </template>
      <!--操作栏-->
    </BasicTable>
  </div>
</template>

<script lang="ts" setup name="systemd-log">
  // import { unref, computed, toRaw, ref } from 'vue';
  // import { Modal, Button } from 'ant-design-vue';
  import { BasicTable } from '/@/components/Table';
  // import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { Api, list } from './log.api';
  import { columns, searchFormSchema } from './log.data';

  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'systemd-log',
    tableProps: {
      title: '日志管理列表数据',
      api: list,
      columns: columns,
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['date', ['startTime', 'endTime'], 'YYYY-MM-DD']],
      },
      showTableSetting: false,
      showActionColumn: false,
      showIndexColumn: true,
      ellipsis: false,
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
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
    },
    exportConfig: {
      url: Api.exportXls,
      name: '日志管理列表',
    },
  });
  // 注册 ListTable
  const [registerTable] = tableContext;
</script>

<style lang="less">
  @import 'index';
</style>
