<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
    >
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls" v-if="hasAuth('exitpass:abnormal:export')"> 导出</a-button>
      </template>
      <template #subject="{ text, record }">
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
    <EditModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { BasicTable, TableImg, TableAction, ActionItem } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import {Api, list} from './list.api';
import { columns,searchFormSchema } from './list.data';
import { addFullParam } from '/@/utils/router/fullParam';
import { useRoute, useRouter } from 'vue-router';
import EditModal from './editModal.vue'
import { useModal } from '/@/components/Modal';
const router = useRouter()

// 列表页面公共参数、方法
const { tableContext,onExportXls } = useListPage({
  designScope: 'checkCar-list',
  tableProps: {
    title: '',
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
        ['eventCreatedTime', ['submissionStartTime', 'submissionEndTime'], 'YYYY-MM-DD'],
      ],
    },
    actionColumn: {
      width: 180,
      title: '操作',
      //是否锁定操作列取值 right ,left,false
      fixed: 'right',
      dataIndex: 'action',
      slots: { customRender: 'action' },
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
  },
  exportConfig: {
    url: Api.exportXls,
    name: '出门证异常明细',
  },
});
  // 注册 ListTable
const [registerTable, { reload }] = tableContext;
const { hasPermission } = usePermission();
function hasAuth(code) {
  return hasPermission(code);
}

const goInfo = (record) => {
  if (record.bpmFlowUrl) {
    const hasFullParam = window.location.hash.includes('__full__=true');
    if (hasFullParam) {
      window.open(record.bpmFlowUrl+'&__full__=true', '_blank')
    } else {
      window.open(record.bpmFlowUrl, '_blank')
    }
  }
}


const eventPost = (record) => {
  router.push(addFullParam({
    name: 'abnormalCreate',
    query: {
      subject: record.subject,
      relatedProcessId: record.relatedProcessId,
      flowNo: record.flowNo,
      id: record.id
    }
  }))
}
const edit = (record) => {
  openModal(true, {
      title: '编辑',
      isUpdate: true,
      showFooter: true,
      record,
    });
}
function getTableAction(record): ActionItem[] {
  return [
    { label: '编辑', onClick: edit.bind(null, record), auth: 'exitpass:abnormal:edit' },
    { label: '事件上报', onClick: eventPost.bind(null, record), auth: 'exitpass:abnormal:event' },
  ];
}
const [registerModal, { openModal }] = useModal();

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
