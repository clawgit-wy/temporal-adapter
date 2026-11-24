<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
    >
      <template #tableTitle v-if="route.name !== 'epiboly-look'">
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd" v-if="showBtn">入厂权限调整</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls" v-if="showBtn">导出</a-button>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <ListModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import {Api, list, Looklist} from './list.api';
import { columns,searchFormSchema } from './list.data';
import {useRouter, useRoute} from 'vue-router'
import { message } from 'ant-design-vue';
import { addFullParam } from '/@/utils/router/fullParam';
import ListModal from './ListModal.vue';
import { useModal } from '/@/components/Modal';
import { ref } from 'vue';
const route = useRoute()
const [registerModal, { openModal }] = useModal();
const listFn = route.name === 'epiboly-look' ? Looklist : list
const showBtn = ref(false)
let tableColumns = [];
function getColumns() {
  if (route.name === 'epiboly-look') {
    return columns.filter(item=> {
      return item.dataIndex !== 'status'
    })
  }
  return columns;
} 
tableColumns = getColumns()
// 列表页面公共参数、方法
const { tableContext, onExportXls } = useListPage({
  designScope: 'checkCar-list',
  tableProps: {
    title: '外协入厂权限查询',
    immediate: route.name === 'epiboly-look' ? false : true,
    api: listFn,
    columns: tableColumns,
    pagination: {
      pageSize: 15,
      showSizeChanger: false,
      showQuickJumper: false,
    },
    formConfig: {
      schemas: searchFormSchema,
      fieldMapToTime: [['updateTime', ['updateTimeStart', 'updateTimeEnd'], 'YYYY-MM-DD']],
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
    beforeFetch(data) {
      const searchValues = Object.values(data).filter(item => item)
      console.log(searchValues, Object.values(data));
      if (searchValues.length <= 4 && route.name == 'epiboly-look') {
        message.warning('请输入查询条件');
        throw new Error('123')
      }
    },
    afterFetch(t) {
      if (!showBtn.value) {
        if (t.length > 0) {
          showBtn.value = true
        }
      }
    }
  },
  exportConfig: {
    url: Api.exportXls,
    name: '外协外包人员管理列表',
  },
});
  // 注册 ListTable
const [registerTable, { reload }] = tableContext;
const { hasPermission } = usePermission();
function getTableAction(record): ActionItem[] {
  return [
    { label: '编辑', onClick: onEdit.bind(null, record), auth: 'SecurityService:outAndIn:epiboly:edit' },
  ];
}

function onEdit(record) {
  openModal(true, {
    title: '编辑',
    isUpdate: true,
    record: record,
    showFooter: true,
  });
}
function hasAuth(code) {
  return hasPermission(code);
}


const router = useRouter()
function onAdd() {
  router.push(addFullParam({
    name: 'initiate'
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
