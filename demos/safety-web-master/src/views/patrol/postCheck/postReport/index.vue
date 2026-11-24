<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
    >
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls" v-if="hasAuth('smartparking:checkcar:checkcarrole:add')">导出</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <listModal @register="registerModal" @success="reload" />
    
    <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
      <div class="blacklistUpload">
        <p>请按照模板规范填写，否则无法正确导入<a-button @click="ondownLoadXls">下载模板</a-button></p>
        <p>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">上传</j-upload-button>
        </p>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import listModal from './listModal.vue';
import {Api, list, remove, download } from './list.api';
import { columns,searchFormSchema } from './list.data';
import { useModal } from '/@/components/Modal';
import { ref } from 'vue';
import { downloadByData } from '/@/utils/file/download';
import { useMessage } from '/@/hooks/web/useMessage';
const { createMessage } = useMessage()
const [registerModal, { openModal }] = useModal(); // 厂区弹出框

// 列表页面公共参数、方法
const { tableContext, onImportXls, onExportXls } = useListPage({
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
      fieldMapToTime: [['date', ['startTime', 'endTime'], 'YYYY-MM-DD']],
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
    name: '黑名单管理列表',
  },
  importConfig: {
    url: Api.importXls,
    success: () => reload(),
  },
});
  // 注册 ListTable
const [registerTable, { reload }] = tableContext;
const { hasPermission } = usePermission();
 function hasAuth(code) {
    return hasPermission(code);
  }


function onEdit(record) {
  openModal(true, {
    title: '修改信息',
    isUpdate: true,
    record: record,
    showFooter: true,
  });
}

function onAdd() {
  openModal(true, {
    title: '添加人员',
    isUpdate: false,
    showFooter: true,
    record: {},
  });
}

async function onDelete(ids) {
  if (ids.length>0) {
    await remove({ ruleId: ids }, true);
    await reload();
  }else{
    createMessage.warning('请选择')
  }
}

/**
 * 操作栏
 */
function getTableAction(record): ActionItem[] {
  return [
    { label: '详情', onClick: onEdit.bind(null, record), auth: 'smartparking:checkcar:checkcarrole:edit' },
  ];
}
const openUpload = ref(false)
const handleOk = () => {
  openUpload.value = false
}
async function ondownLoadXls() {
  const res = await download({});
  downloadByData(res, '公出人员列表.xlsx');
}

const temporarilyVisible = ref(false)
const person = ref('')
const applyTime = ref([])
const personChange = (val) => {
  person.value = val
  console.log(selectedRowKeys);
  
  console.log(applyTime.value);
  
  console.log(val);
}
const change = (e) => {
  console.log(e.target);
  
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
