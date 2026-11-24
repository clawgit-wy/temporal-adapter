<template>
  <!-- <a-form-item> -->
  <BasicTable @register="registerTable" :columns="props.columns" :dataSource="dataSource">
    <template #tableTitle>
      <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd" v-if="!props.disabled">添加</a-button>
      <a-button type="primary" preIcon="ant-design:import-outlined" @click="showModal" v-if="!props.disabled"> 导入</a-button>
    </template>
    <!--操作栏-->
    <template #action="{ index, record }">
      <TableAction :actions="getTableAction(record, index)" v-if="!props.disabled"/>
    </template>
    <template #goodsCode="{index, record}">
      <div><a-input></a-input></div>
    </template>
    <template #goodsName="{index, record }">
      <div><a-input></a-input></div>
    </template>
    <template #goodsUnit="{ index, record }">
      <div><a-input></a-input></div>
    </template>
    <template #goodsNum="{ index, record }">
      <div><a-input-number></a-input-number></div>
    </template>
    <template #remark="{ index, record }">
      <div><a-input></a-input></div>
    </template>
  </BasicTable>
  <!-- </a-form-item> -->
  <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
    <div>
      <p class="pl-12">请按照模板规范填写，否则无法正确导入:<a-button :href="props.link" type="link">下载模板</a-button></p>
      <p class="p-12">
        <a-upload v-model:file-list="fileList" accept=".xls,.xlsx" :max-count="1" :customRequest="handleImport">
          <a-button>
            <upload-outlined></upload-outlined>
            上传
          </a-button>
        </a-upload>
      </p>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { PropType, watch, watchEffect, nextTick } from 'vue';
import {ref} from 'vue';
import { cloneDeep } from 'lodash-es';
import * as XLSX from 'xlsx/xlsx.mjs';
import { useMessage } from '/@/hooks/web/useMessage';
import { BasicTable, TableAction, useTable, BasicColumn, ActionItem, EditRecordRow, TableRowSelection } from '/@/components/Table';
const emit = defineEmits(['update:value']);
const $message = useMessage();

const props = defineProps({
  value: {
    type: Array<Record<string, any>>,
    default: () => [],
  },
  columns: {
    type: [Array] as PropType<BasicColumn[]>,
    default: () => [],
  },
  link: {
    type: String,
    default: 'http://ekp.yutong.com/ekp/sys/attachment/sys_att_main/sysAttMain.do?method=download&fdId=17c1123c6b19933e770d62d4878865c0',
  },
  disabled:{
    type:Boolean,
    default:false
  }
});
const currentEditKeyRef = ref('');
const dataSource = ref<Array<Record<string, any>>>([...props.value]);
console.log(props.value, 'props.value');
const [registerTable, { reload, getDataSource, setTableData, updateTableDataRecord, deleteSelectRowByKey, deleteTableDataRecord }] = useTable({
  rowKey: 'id',
  pagination: false,
  showSizeChanger:false,
  showTableSetting: false,
  showIndexColumn: true,
  useSearchForm: false,
  actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' },
  },
});

function getTableAction(record: EditRecordRow, index: number): ActionItem[] {
  if (!record.editable) {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record, index),
      },
      {
        label: '删除',
        icon: 'ic:outline-delete-outline',
        onClick: handleDelete.bind(null, record, index),
      },
    ];
  }
  return [
    {
      label: '保存',
      onClick: handleSave.bind(null, record),
    },
    {
      label: '取消',
      popConfirm: {
        title: '是否取消编辑',
        confirm: handleCancel.bind(null, record),
      },
    },
  ];
}
const openUpload = ref<boolean>(false);
const fileList = ref([]);
const exportData = ref<Array<Record<string, any>>>([]);
/**
 * 上传弹出框
 */
function showModal() {
  openUpload.value = true;
}

function handleOk() {
  openUpload.value = false;
  const tableData = getDataSource();
  setTableData([...tableData, ...exportData.value]);
  exportData.value = [];
  fileList.value = [];
}

const handleImport = async (file) => {
  const fileReader = new FileReader();
  fileReader.readAsBinaryString(file.file);
  fileReader.onload = (event) => {
    try {
      const data = event.target?.result;
      const workbook = XLSX.read(data, { type: 'binary' });
      const worksheet = workbook.Sheets[workbook.SheetNames[0]];
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
      jsonData.shift();
      const dataArr = jsonData.map((item) => {
        return props.columns.reduce((pre, cur, index) => {
          const { dataIndex } = cur;
          pre[dataIndex as string] = item[index];
          return pre;
        }, {});
      });
      exportData.value = dataArr;
    } catch {
      $message.createMessage.error('文件上传失败');
    }
  };
  fileList.value.map((i) => {
    i.status = 'done';
    return i;
  });

  return;
};

function onAdd() {
  const tableData = getDataSource();
  setTableData([...tableData, { editable: true }]);
}

async function handleSave(record: EditRecordRow) {
  const data = cloneDeep(record.editValueRefs);
  console.log(data, 'handleSave');
  // 保存之后提交编辑状态
  const pass = await record.onEdit?.(false, true);
  if (pass) {
    currentEditKeyRef.value = '';
  }
  // 获取表格数据，并且更新到表单中
  const tableData = getTableData();
  console.log(tableData, 'tableData');
  emit('update:value', JSON.stringify(tableData));
}

function handleEdit(record: EditRecordRow, index: number) {
  currentEditKeyRef.value = record.key;
  record.onEdit?.(true);
}

function handleCancel(record: EditRecordRow) {
  currentEditKeyRef.value = '';
  record.onEdit?.(false, false);
}
function handleDelete(record: Recordable, index: number) {
  const tableData = getTableData();
  tableData.splice(index, 1);
  setTableData(tableData);
  emit('update:value', JSON.stringify(tableData));
}
function getTableData() {
  const tableData = getDataSource();
  return tableData.map((item) => {
    if (item.editValueRefs) {
      return {
        ...item.editValueRefs,
      };
    }
    return item;
  });
}
</script>

<style>
</style>