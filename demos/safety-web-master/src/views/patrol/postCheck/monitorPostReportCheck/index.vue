<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
    > 
      <template #flowNo="{text, record }">
        <a-button type="link" @click="goInfo({formInstanceId: record.id, processId: record.flowId, look: 1})">{{ text }}</a-button>
      </template>
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls" v-if="hasAuth('patrol:postcheck:monitorpostreportcheck:export')">导出</a-button>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { BasicTable } from '/@/components/Table';
import { useRouter } from 'vue-router'
import { useListPage } from '/@/hooks/system/useListPage';
import { addFullParam } from '/@/utils/router/fullParam';
import { usePermission } from '/@/hooks/web/usePermission';
import {Api, list } from './list.api';
import { columns,searchFormSchema } from './list.data';
const router = useRouter()

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
        ['checkTime', ['checkTimeStart', 'checkTimeEnd'], 'YYYY-MM-DD'],
        ['createTime', ['createTimeStart', 'createTimeEnd'], 'YYYY-MM-DD'],
        ['auditTime', ['auditTimeStart', 'auditTimeEnd'], 'YYYY-MM-DD'],
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
      // if (params.date) {
      //   params.startTime = dayjs(data.date).format("YYYY-MM-DD");
      //   params.endTime = dayjs
      // }
      return params;
    },
    showActionColumn: false,
    actionColumn: {
      width: 140,
      title: '操作',
      //是否锁定操作列取值 right ,left,false
      fixed: 'right',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  },
  exportConfig: {
    url: Api.exportXls,
    name: '监控岗位验证结果列表',
  },
});
  // 注册 ListTable
const [registerTable] = tableContext;
const { hasPermission } = usePermission();
 function hasAuth(code) {
    return hasPermission(code);
  }

const goInfo = (query) => {
  router.push(addFullParam({
    name: 'monitorPostApproval',
    query
  }))
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
      width: 160px;
      margin: 0;
    }
  }
}
</style>
