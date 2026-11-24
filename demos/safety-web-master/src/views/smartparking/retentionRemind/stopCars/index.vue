<template>
  <div :class="prefixCls">
    <BasicTable title="提示信息"  @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onExportXls" v-if="hasAuth('smartparking:retentionremind:stopcars:export')">导出</a-button>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="systemd-noticemessage" setup>
import { BasicTable } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import { Api, list } from './list.api';
import { columns,searchFormSchema } from './list.data';

// 列表页面公共参数、方法
const { prefixCls, onExportXls, tableContext } = useListPage({
  designScope: 'retention-list',
  tableProps: {
    title: '滞留提醒通知列表',
    api: list,
    columns: columns,
    pagination: {
      pageSize: 15,
      showSizeChanger: false,
      showQuickJumper: false,
    },
    showActionColumn: false,
    formConfig: {
      schemas: searchFormSchema,
      fieldMapToTime: [['inTime', ['beginDate', 'endDate'], 'YYYY-MM-DD']],
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
      // if (params.date) {
      //   params.startTime = dayjs(data.date).format("YYYY-MM-DD");
      //   params.endTime = dayjs
      // }
      return params;
    },
  },
  exportConfig: {
    url: Api.export,
    name: '厂内车辆信息'
  }
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
</style>
