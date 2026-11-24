<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
      :rowSelection="rowSelection"
    >
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd" v-if="hasAuth('facilities:facilitiesmanager:standardconfig:add')">添加</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onDelete(selectedRowKeys)" v-if="hasAuth('facilities:facilitiesmanager:standardconfig:deletes')">批量删除</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="openUpload = true" v-if="hasAuth('facilities:facilitiesmanager:standardconfig:impord')">批量导入</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls" v-if="hasAuth('facilities:facilitiesmanager:standardconfig:expord')">导出</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
      <template #normalImgRequire="{ record }">
        <a-checkbox :checked="record.normalImgRequire" @change="(e) => requireChange({normalImgRequire: e.target.checked, id: record.id})">必填</a-checkbox>
      </template>
      <template #exceptionImgRequire="{ record }">
        <a-checkbox :checked="record.exceptionImgRequire" @change="(e) => requireChange({exceptionImgRequire: e.target.checked, id: record.id})">必填</a-checkbox>
      </template>
      <template #reformImgRequire="{ record }">
        <a-checkbox :checked="record.reformImgRequire" @change="(e) => requireChange({reformImgRequire: e.target.checked, id: record.id})">必填</a-checkbox>
      </template>
      <template #validateImgRequire="{ record }">
        <a-checkbox :checked="record.validateImgRequire" @change="(e) => requireChange({validateImgRequire: e.target.checked, id: record.id})">必填</a-checkbox>
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
import {Api, list, remove, download, updateRequire, fieldTree } from './list.api';
import { columns,searchFormSchema } from './list.data';
import { useModal } from '/@/components/Modal';
import { ref } from 'vue';
import { downloadByData } from '/@/utils/file/download';
import { useMessage } from '/@/hooks/web/useMessage';
const { createMessage } = useMessage()
const [registerModal, { openModal }] = useModal(); // 厂区弹出框

// 列表页面公共参数、方法
const { tableContext, onImportXls, onExportXls } = useListPage({
  designScope: 'standard-list',
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
    name: '设备巡检标准配置列表',
  },
  importConfig: {
    url: Api.importXls,
    success: () => reload(),
  },
});
  // 注册 ListTable
const [registerTable, { reload, getForm }, {rowSelection, selectedRowKeys}] = tableContext;
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
  if (Array.isArray(ids) && ids.length == 0) {
    return createMessage.warning('请选择')
  }
  await remove(ids, true);
  await reload();
}

/**
 * 操作栏
 */
function getTableAction(record): ActionItem[] {
  return [
    { label: '编辑', onClick: onEdit.bind(null, record), auth: 'facilities:facilitiesmanager:standardconfig:edit' },
    { label: '删除', onClick: onDelete.bind(null, record.id), auth: 'facilities:facilitiesmanager:standardconfig:delete'},
  ];
}
const openUpload = ref(false)
const handleOk = () => {
  openUpload.value = false
}
async function ondownLoadXls() {
  const res = await download({});
  downloadByData(res, '设备巡检标准配置导入模板.xlsx');
}

const requireChange = async (params) => {
  await updateRequire(params)
  reload()
}
fieldTree({fieldType: 'spotTeam', topFlag: false}).then(res => {
  console.log(res, 'spotTeam');
  const options = res.map(item => ({
    label: item.fieldName,
    options: item.regionList.map(child => ({label: child.fieldName, value: child.fieldName}))
  }))
  const {updateSchema} = getForm()
  updateSchema([
    {
      field: 'teamName',
      componentProps: {
        options
      }
    }
  ])
  
})
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
