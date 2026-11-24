<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="handleExport"> 导出 </a-button>
      </template>
      <!--来源列插槽-->
      <template #source="{ text, record }">
        <span
          style="color: blue; cursor: pointer"
          @click="navigateToProcessDetail(record.processId, record.bpmFlowId, 'personAccessDetail', record.roleType)"
          >{{ text }}</span
        >
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="buildingAccessControl-personAccessDetail" setup>
  import { ref, onMounted, h } from 'vue';
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import * as apiModule from './personAccessDetail.api';
  import { columns, searchFormSchema } from './personAccessDetail.data';
  import { useProcessNavigation } from '/@/views/buildingAccessControl/hooks/useProcessNavigation';
  const { navigateToProcessDetail } = useProcessNavigation();

  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'buildingAccessControl-personAccessDetail',
    tableProps: {
      title: '人员门禁授权明细',
      api: apiModule.listPersonAccessDetail,
      columns,
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['timeRange', ['beginTime', 'endTime'], 'YYYY-MM-DD']],
      },
      showIndexColumn: true,
      ellipsis: true,
      showActionColumn: false,

      fetchSetting: {
        pageField: 'pageNum',
        sizeField: 'pageSize',
        listField: 'dataList',
        totalField: 'total',
      },
    },
    exportConfig: {
      url: apiModule.Api.exportXls,
      name: '人员门禁授权明细',
    },
  });

  // 注册 ListTable
  const [registerTable, { getForm }] = tableContext;

  // 处理导出事件
  function handleExport() {
    onExportXls();
  }

  onMounted(() => {
    const defaultCode = getHashQuery('creator');
    getForm().setFieldsValue({ creator: defaultCode });
  });
  function getHashQuery(key: string): string | null {
    const hash = window.location.hash; // "#/personAccessDetail?code=123456"
    const queryStr = hash.split('?')[1]; // "code=123456"
    if (!queryStr) return null;
    const params = new URLSearchParams(queryStr);
    return params.get(key);
  }
</script>

<style lang="less" scoped>
  @import '/@/views/buildingAccessControl/styles/common.less';

  .process-link {
    .link-style();
  }
</style>
