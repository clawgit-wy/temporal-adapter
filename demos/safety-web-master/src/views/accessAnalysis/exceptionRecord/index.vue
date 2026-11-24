<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="handleExport">导出</a-button>
      </template>
      <template #facePhoto="{ record }">
        <img
          v-if="record.facePhoto || record.snapUrl || record.snap_url"
          class="capture-face-img"
          :src="record.facePhoto || record.snapUrl || record.snap_url"
          alt="抓拍人脸"
        />
        <span v-else>无</span>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'AccessAnalysisExceptionRecord',
  };
</script>

<script lang="ts" setup>
  import { onMounted } from 'vue';
  import dayjs from 'dayjs';
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { downloadByData } from '/@/utils/file/download';
  import { getAreaCascaderProps, getAreaCodes, loadAreaOptions } from '../common/area';
  import { exportXls, list } from './api';
  import { columns, searchFormSchema } from './data';

  const { prefixCls, tableContext } = useListPage({
    designScope: 'access-analysis-exception-record',
    tableProps: {
      title: '异常进出记录',
      api: list,
      columns,
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['eventTime', ['beginEventTime', 'endEventTime'], 'YYYY-MM-DD HH:mm:ss']],
      },
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      showIndexColumn: true,
      showActionColumn: false,
      ellipsis: true,
      fetchSetting: {
        pageField: 'pageNum',
        sizeField: 'pageSize',
        listField: 'dataList',
        totalField: 'total',
      },
      beforeFetch: normalizeParams,
    },
  });

  const [registerTable, { getForm }] = tableContext;

  onMounted(async () => {
    await loadAreaOptions();
    await getForm().updateSchema({
      field: 'areaCode',
      componentProps: getAreaCascaderProps(),
    });
  });

  async function handleExport() {
    const values = await getForm().validate();
    const res = await exportXls(normalizeParams({ pageNum: 1, pageSize: 999999, ...values }));
    downloadByData(res, '异常进出记录.xlsx');
  }

  function normalizeParams(params) {
    const { areaCode, eventTime, ...rest } = params || {};
    const { factoryCode, buildCode } = getAreaCodes(areaCode);
    return {
      ...rest,
      factoryCode,
      buildCode,
      beginEventTime: rest.beginEventTime || formatTime((eventTime || [])[0]),
      endEventTime: rest.endEventTime || formatTime((eventTime || [])[1]),
    };
  }

  function formatTime(value) {
    return value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : undefined;
  }
</script>

<style lang="less" scoped>
  @import 'index';
</style>
