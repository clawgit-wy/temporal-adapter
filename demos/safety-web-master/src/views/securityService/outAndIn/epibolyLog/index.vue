<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
    >
      <template #action="{ record }">
        <template v-if="record.processStatus === '31'">
          /
        </template>
        <template v-else>
          <TableAction :actions="getTableAction(record)" />
        </template>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import {list} from './list.api';
import { columns,searchFormSchema } from './list.data';
import { useRoute} from 'vue-router'
import { addFullParam } from '/@/utils/router/fullParam';
const route = useRoute()
// 列表页面公共参数、方法
const { tableContext } = useListPage({
  designScope: 'checkCar-list',
  tableProps: {
    title: '外协入厂权限查询',
    immediate: route.name === 'epiboly-look' ? false : true,
    api: list,
    columns: columns,
    pagination: {
      pageSize: 15,
      showSizeChanger: false,
      showQuickJumper: false,
    },
    formConfig: {
      schemas: searchFormSchema,
      fieldMapToTime: [['submissionTime', ['submissionStartTime', 'submissionEndTime'], 'YYYY-MM-DD']],
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
    actionColumn: {
      width: 140,
      title: '操作',
      //是否锁定操作列取值 right ,left,false
      fixed: 'right',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  },
});
  // 注册 ListTable
const [registerTable] = tableContext;
function getTableAction(record): ActionItem[] {
  return [
    { label: '详情', onClick: goInfo.bind(null, record) },
  ];
}

function goInfo(record) {
  if (record.bpmFlowUrl) {
    const hasFullParam = window.location.hash.includes('__full__=true');
    if (hasFullParam) {
      window.open(record.bpmUrl+'&__full__=true', '_blank')
    } else {
      window.open(record.bpmUrl, '_blank')
    }
  }
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
:deep(.jeecg-basic-table) {
  &>.ant-form-horizontal{
    &>.ant-row{
      &>.ant-col:first-child{
        .ant-form-item-label{
          flex: auto;
          max-width: 30%;
        }
      }
    }
  }
}
</style>
