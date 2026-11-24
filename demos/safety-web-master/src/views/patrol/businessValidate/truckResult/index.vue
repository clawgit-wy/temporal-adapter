<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
    >
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls" v-if="hasAuth('patrol:businessValidate:truckResult:export')">导出</a-button>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { BasicTable } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import {Api, list } from './list.api';
import { columns,searchFormSchema } from './list.data';
import { ref } from 'vue';
import day from 'dayjs'
import { useRoute } from 'vue-router';
const route = useRoute()

// 列表页面公共参数、方法
const { tableContext, onExportXls } = useListPage({
  designScope: 'checkCar-list',
  tableProps: {
    title: '滞留提醒通知列表',
    api: list,
    columns: columns,
    pagination: {
      pageSize: 15,
      showSizeChanger: false,
      showQuickJumper: false,
    },
    formConfig: {
      schemas: searchFormSchema,
      fieldMapToTime: [
        ['checkTime', ['checkTimeBegin', 'checkTimeEnd'], 'YYYY-MM-DD'],
      ],
    },
    showIndexColumn: true,
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
      if (route.query.id) {
        params['taskId'] = route.query.id
      }
      return params;
    },
    showActionColumn: false
  },
  exportConfig: {
    url: Api.exportXls,
    name: '货车环保标准抽查结果',
  },
});
  // 注册 ListTable
const [registerTable] = tableContext;
const { hasPermission } = usePermission();
function hasAuth(code) {
  return hasPermission(code);
}

</script>

<style lang="less" scoped>
@import 'index';

.blacklistUpload {
  padding: 10px;
}
.temp-box{
  padding: 20px;
  li {
    display: flex;
    align-items: center;
    margin-top: 20px;
    p{
      width: 100px;
      margin: 0;
    }
  }
}
</style>
