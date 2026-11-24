<script setup lang="ts">
import {ActionItem, BasicTable, TableAction} from '/@/components/Table';
import {useListPage} from '/@/hooks/system/useListPage';

import {
  taskDetailsColumns,
  taskDetailsSearchFormSchema,
} from "/@/views/firePatrol/taskDetails/taskDetails.data";
import DetailsModal from "/@/views/firePatrol/taskDetails/components/detailsModal.vue";
import {ref} from "vue";
import {taskDetailsApi, taskInfoPageList} from "/@/views/firePatrol/taskDetails/taskDetailsApi";
import dayjs from "dayjs";
import {useModal} from "/@/components/Modal";

// 列表页面公共参数、方法
const {tableContext, onExportXls} = useListPage({
  designScope: 'basic-table-check-standard',
  exportConfig: {
    url: taskDetailsApi.taskDetailsExportXls,
    name: '任务详情' + dayjs().format('YYYYMMDD'),
  },
  tableProps: {
    api: taskInfoPageList,
    columns: taskDetailsColumns,
    rowKey: "id",
    //定义rowSelection的类型，默认是checkbox多选，可以设置成radio单选
    rowSelection: {type: 'checkbox'},
    formConfig: {
      schemas: taskDetailsSearchFormSchema,
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
    actionColumn: {
      width: 140,
      title: '详情',
      //是否锁定操作列取值 right ,left,false
      fixed: 'right',
      dataIndex: 'action',
      slots: {customRender: 'action'},
    },
  },
});

const [registerModal, {openModal}] = useModal();


const [registerTable, {reload}] = tableContext;

function getTableAction(record): ActionItem[] {
  return [
    {
      label: '查看',
      onClick: handleDetails.bind(null, record),
    }
  ];
}

/*详情按钮点击事件*/
function handleDetails(record) {
  openModal(true, {
    title: '任务详情',
    isUpdate: false,
    record: record,
    showFooter: false,
  });
}

</script>

<template>
  <BasicTable @register="registerTable">
    <template #tableTitle>
      <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls">导出
      </a-button>
    </template>
    <!--操作栏-->
    <template #action="{ record }">
      <TableAction :actions="getTableAction(record)"/>
    </template>
    <template #inspectionResult="{ text }">
      <span :style="(text === '正常' || !text)?'color:#222':'color:red'">
        {{ (text && text.length > 0) ? text : '/' }}
      </span>
    </template>
    <template #rectificationResult="{ text }">
      <span :style="(text === '正常' || !text)?'color:#222':'color:red'">
        {{ (text && text.length > 0) ? text : '/' }}
      </span>
    </template>
    <template #taskStatus="{ text }">
      <span :style="text === '未完成'?'color:red':'color:green'">
        {{ text }}
      </span>
    </template>
  </BasicTable>
  <details-modal @register="registerModal"></details-modal>

  <!--  <a-modal v-model:visible="showDetails" title="详情" width="1000px">-->
<!--    <template #footer>-->
<!--      <div style="text-align: center;">-->
<!--        <a-button type="primary" @click="showDetails=false">关闭</a-button>-->
<!--      </div>-->
<!--    </template>-->

<!--    <details-modal @register="registerModal"></details-modal>-->
<!--  </a-modal>-->
</template>

<style scoped lang="less">

</style>
