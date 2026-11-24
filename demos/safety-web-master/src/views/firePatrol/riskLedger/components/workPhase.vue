<script setup lang="ts">
import {ActionItem, BasicTable, TableAction} from '/@/components/Table';
import {useListPage} from '/@/hooks/system/useListPage';
import {message} from "ant-design-vue";
import {BasicForm, useForm} from '/@/components/Form/index';

const {createConfirm} = useMessage();

import {
  columns, getWorkPhaseAddFormSchemas,
  searchFormSchema, workPhaseAddFormSchemas,
} from "/@/views/firePatrol/riskLedger/components/workPhase.data";
import {useMessage} from "/@/hooks/web/useMessage";
import {nextTick, ref} from "vue";
import {downloadByData} from "/@/utils/file/download";
import {
  pageList, workPhaseApi,
  workStepAdd, workStepBatchDelete, workStepDel, workStepDownload,
  workStepUpload
} from "/@/views/firePatrol/riskLedger/components/workPhaseApi";
import {useLoading} from "/@/components/Loading";
import dayjs from "dayjs";

// 列表页面公共参数、方法
const {tableContext, onImportXls, onExportXls} = useListPage({
  designScope: 'basic-table-demo',
  exportConfig: {
    url: workPhaseApi.exportXls,
    name: '作业环节' + dayjs().format('YYYYMMDD'),
  },
  importConfig: {
    url: workPhaseApi.importXls,
    success: () => {
      showMoreAdd.value = false
      reload()
    }
  },
  tableProps: {
    columns: columns,
    api: pageList,
    rowKey: "id",
    //定义rowSelection的类型，默认是checkbox多选，可以设置成radio单选
    rowSelection: {type: 'checkbox'},
    formConfig: {
      schemas: searchFormSchema,
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
    beforeFetch: (params) => {
      return params;
    },
  },
});

const showMoreAdd = ref(false)

const showAdd = ref(false)

const addTitle = ref('添加')

const [registerForm, {
  validate,
  clearValidate,
  getFieldsValue,
  resetFields,
  setFieldsValue,
  setProps
}] = useForm({
  //注册表单列
  schemas: workPhaseAddFormSchemas,
  //回车提交
  autoSubmitOnEnter: true,
  //不显示重置按钮
  showResetButton: false,
  //自定义提交按钮文本和图标
  submitButtonOptions: {text: '提交', preIcon: ''},
  //查询列占比 24代表一行 取值范围 0-24
  actionColOptions: {span: 24},
  labelWidth: 100,
  showActionButtonGroup: false
});

const wrapEl = ref<ElRef>(null);
const [openWrapLoading, closeWrapLoading] = useLoading({
  target: wrapEl,
  props: {
    tip: '加载中...',
    absolute: true,
  },
});


const [registerTable, {reload, clearSelectedRowKeys}, {rowSelection, selectedRows}] = tableContext;

function getTableAction(record): ActionItem[] {
  return [
    {
      label: '编辑',
      onClick: handleEdit.bind(null, record),
    },
    {
      label: '删除',
      onClick: handDel.bind(null, record),
    },
  ];
}

async function ondownLoadXls() {
  const res = await workStepDownload({});
  downloadByData(res, '批量导入作业环节模板.xlsx');
}

/*编辑按钮点击时间*/
function handleEdit(record) {
  openEdit(record)
}

/*删除按钮点击时间*/
function handDel(record) {
  delRequest(record)
}

/*批量删除按钮点击事件*/
function handleMoreDel() {
  if (selectedRows.value.length === 0) {
    message.error('请选择需要删除的数据')
    return
  }
  createConfirm({
    iconType: 'warning',
    title: '删除',
    content: '删除后将自动删除该作业环节下的所有检查标准配置及任务，请确认？',
    onOk: () => {
      openWrapLoading()
      workStepBatchDelete({
        ids: selectedRows.value.map((record) => record.id).join(','),
      }).then(async () => {
        selectedRows.value = []
        message.success('批量删除成功！')
        clearSelectedRowKeys()
        await reload()
      }).finally(() => {
        closeWrapLoading()
      })
    },
    onCancel: () => {

    },
  });

}

/*添加按钮点击事件*/
async function handAdd() {
  showAdd.value = true
  addTitle.value = '添加'
  nextTick(async () => {
    await setProps({
      schemas: getWorkPhaseAddFormSchemas(false)
    });
    await clearValidate()
    await resetFields()
  });
}

/*编辑按钮点击事件*/
async function openEdit(item) {
  showAdd.value = true
  addTitle.value = '编辑'
  nextTick(async () => {
    await setProps({
      schemas: getWorkPhaseAddFormSchemas(true)
    });
    await clearValidate()
    await resetFields()
    await setFieldsValue(item)
  });
}

function delRequest(item) {
  createConfirm({
    iconType: 'warning',
    title: '删除',
    content: '删除后将自动删除该作业环节下的所有检查标准配置及任务，请确认？',
    onOk: () => {
      openWrapLoading()
      workStepDel({id: item.id}).then(async () => {
        message.success('删除成功！')
        await reload()
      }).finally(() => {
        closeWrapLoading()
      })
    },
    onCancel: () => {

    },
  });
}

function handMoreAdd() {
  showMoreAdd.value = true
}

async function closeAdd() {
  await clearValidate()
  await resetFields()
  showAdd.value = false
}

async function handleSubmit() {
  await validate()
  let param = getFieldsValue()
  openWrapLoading()
  if (addTitle.value === '编辑') {
    workStepUpload(param).then(async () => {
      message.success('编辑成功！')
      await closeAdd()
      await reload()
    }).finally(() => {
      closeWrapLoading()
    })
  } else {
    workStepAdd(param).then(async () => {
      message.success('添加成功！')
      await closeAdd()
      await reload()
    }).finally(() => {
      closeWrapLoading()
    })
  }
}

function closeAddFrom() {
  showAdd.value = false
}

</script>

<template>
  <BasicTable @register="registerTable" :rowSelection="rowSelection">
    <template #tableTitle>
      <a-button type="primary" preIcon="ant-design:delete-outlined" @click="handleMoreDel">
        批量删除
      </a-button>
      <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handAdd">添加</a-button>
      <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handMoreAdd">批量添加
      </a-button>
      <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls">导出
      </a-button>
    </template>
    <!--操作栏-->
    <template #action="{ record }">
      <TableAction :actions="getTableAction(record)"/>
    </template>
  </BasicTable>
  <a-modal v-model:visible="showMoreAdd" title="批量添加" :footer="null">
    <div class="viewAdd">
      <div class="top">
        <span>添加作业环节：</span>
        <div class="upload">
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">
            上传
          </j-upload-button>
        </div>
        <span class="download" @click="ondownLoadXls">下载模板</span>
      </div>
      <span style="margin-left: 105px;margin-top: 10px">支持扩展名：.xlsx</span>
    </div>
  </a-modal>
  <a-modal v-model:visible="showAdd" :title="addTitle" :footer="null">
    <BasicForm @register="registerForm" style="margin: 20px">
      <template #formFooter>
        <div style="margin: 20px auto">
          <a-button type="primary" class="mr-2" @click="handleSubmit">提交</a-button>
          <a-button class="mr-2" @click="closeAddFrom"> 取消</a-button>
        </div>
      </template>
    </BasicForm>
  </a-modal>
</template>
<style scoped lang="less">
.viewAdd {
  display: flex;
  flex-direction: column;
  margin: 20px 30px;
  padding-bottom: 20px; /* 设置底部间距 */

  .top {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .upload {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;

    span {
      color: #333;
    }
  }

  .download {
    color: #0a8fe9;
  }
}
</style>
