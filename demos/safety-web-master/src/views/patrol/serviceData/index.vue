<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
    >
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls" v-if="hasAuth('patrol:servicedata:export')">导出</a-button>
      </template>
      <template #flowNo="{ text, record }">
        <span style="color: blue; cursor: pointer;" @click="goInfo(record)">{{text}}</span>
      </template>
      <template #problemImage="{ text }">
        <TableImg v-if="text" :size="60" :simpleShow="true" :imgList="text.split(',')" />
      </template>
      <template #dealImage="{ text }">
        <TableImg v-if="text" :size="60" :simpleShow="true" :imgList="text.split(',')" />
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <a-modal v-model:visible="visible" title="详情" width="1000px" :footer="null">
      <div style="padding: 10px 20px">
        <a-table :dataSource="dataSource" :columns="historycolumns" size="small" :pagination="false">
          <template #bodyCell="{ column, text }">
            <template v-if="column.dataIndex === 'dealImage'">
              <Image
                v-for="item in text.split(',')"
                :key="item"
                :width="40"
                :src="item"
              />
            </template>
          </template>
        </a-table>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { BasicTable, TableImg, TableAction, ActionItem } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import {Api, list} from './list.api';
import { columns,searchFormSchema } from './list.data';
import { useRoute, useRouter } from 'vue-router';
import { getDealHistoryList } from '../approval/field.api';
import {Image} from 'ant-design-vue'
import { ref } from 'vue';
import { addFullParam } from '/@/utils/router/fullParam';
const route = useRoute()
const router = useRouter()

// 列表页面公共参数、方法
const { tableContext, onExportXls } = useListPage({
  designScope: 'checkCar-list',
  tableProps: {
    title: '巡查日志',
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
        ['occur', ['occurBegin', 'occurEnd'], 'YYYY-MM-DD'],
        ['create', ['createBegin', 'createEnd'], 'YYYY-MM-DD'],
        ['planDate', ['planDealBegin', 'planDealEnd'], 'YYYY-MM-DD'],
        ['deal', ['dealFinishBegin', 'dealFinishEnd'], 'YYYY-MM-DD'],
      ],
    },
    showIndexColumn: true,
    // showActionColumn: false,
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
  },
  exportConfig: {
    url: Api.exportXls,
    name: '设备巡检日志',
  },
});
  // 注册 ListTable
const [registerTable] = tableContext;
const { hasPermission } = usePermission();
function hasAuth(code) {
  return hasPermission(code);
}
const goInfo = (record) => {
  console.log(record);
  if (record.planName == '出门证违规上报') {
    const hasFullParam = window.location.hash.includes('__full__=true');
    if (hasFullParam) {
      window.open(record.goodsOutFlowUrl+'&__full__=true', '_blank')
    } else {
      window.open(record.goodsOutFlowUrl, '_blank')
    }
  } else if (record.flowUrl) {
    const hasFullParam = window.location.hash.includes('__full__=true');
    if (hasFullParam) {
      window.open(record.flowUrl+'&__full__=true', '_blank')
    } else {
      window.open(record.flowUrl, '_blank')
    }
  }else{
    router.push(addFullParam({
      name: 'patrolApproval',
      query: {
        formInstanceId: record.id,
      }
    }))
  }
}

const visible = ref(false)
const showInfo = (record) => {
  if (record.planName == '出门证违规上报') {
    const hasFullParam = window.location.hash.includes('__full__=true');
    if (hasFullParam) {
      window.open(record.flowUrl+'&__full__=true', '_blank')
    } else {
      window.open(record.flowUrl, '_blank')
    }
    return
  }
  getHistory(record.id)
}
function getTableAction(record): ActionItem[] {
  return [
    { label: '详情', onClick: showInfo.bind(null, record) },
  ];
}
const historycolumns = [
  { title: '处理人', dataIndex: 'dealPerson', key: 'dealPerson', },
  { title: '处理人员工号', dataIndex: 'dealPersonNo', key: 'dealPersonNo', },
  { title: '问题描述', dataIndex: 'dealRecord', key: 'dealRecord', },
  { title: '处理日期', dataIndex: 'dealTime', key: 'dealTime', },
  { title: '处理图片', dataIndex: 'dealImage', key: 'dealImage', },
  { title: '处理结果', dataIndex: 'isFinish', key: 'isFinish', customRender: ({text}) => !text ? '未通过' : '通过' },
  { title: '情况描述', dataIndex: 'resultRemark', key: 'resultRemark', },
  { title: '评价人', dataIndex: 'resultPerson', key: 'resultPerson', },
]
const dataSource = ref([])
const getHistory = async (id) => {
  visible.value = true
  const res = await getDealHistoryList({processId: id})
  dataSource.value = res
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
