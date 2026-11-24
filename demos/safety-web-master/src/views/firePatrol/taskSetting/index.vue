<script setup lang="ts">
import {ActionItem, BasicTable, TableAction} from '/@/components/Table';
import {useListPage} from '/@/hooks/system/useListPage';
import {message} from "ant-design-vue";
import {BasicForm, useForm} from '/@/components/Form/index';

const {createConfirm} = useMessage();

import {
  taskSettingColumns,
  taskSettingSearchFormSchema,
  taskSettingAddFormSchemas
} from "/@/views/firePatrol/taskSetting/taskSetting.data";
import {useMessage} from "/@/hooks/web/useMessage";
import {nextTick, ref} from "vue";
import {downloadByData} from "/@/utils/file/download";
import CustomTaskObj from "/@/views/firePatrol/taskSetting/components/CustomTaskObj.vue";
import {useLoading} from "/@/components/Loading";
import {
  taskSetAdd, taskSetBatchDelete, taskSetDel, taskSetDownload,
  taskSetPageList, taskSettingApi, taskSetUpdateStatus,
  taskSetUpload
} from "/@/views/firePatrol/taskSetting/taskSettingApi";
import {useModal} from '/@/components/Modal';
import SubDimensionModal from "/@/views/firePatrol/taskSetting/components/subDimensionModal.vue";
import JUploadButton from "/@/components/Button/src/JUploadButton.vue";

// 列表页面公共参数、方法
const {tableContext, onImportXls} = useListPage({
  designScope: 'basic-table-check-standard',
  importConfig: {
    url: taskSettingApi.taskSetImport,
    success: () => {
      showMoreAdd.value = false
      reload()
    }
  },
  tableProps: {
    api: taskSetPageList,
    columns: taskSettingColumns,
    rowKey: "id",
    //定义rowSelection的类型，默认是checkbox多选，可以设置成radio单选
    rowSelection: {type: 'checkbox'},
    formConfig: {
      schemas: taskSettingSearchFormSchema,
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
      width: 180,
      title: '操作',
      //是否锁定操作列取值 right ,left,false
      fixed: 'right',
      dataIndex: 'action',
      slots: {customRender: 'action'},
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
  validateFields
}] = useForm({
  //注册表单列
  schemas: taskSettingAddFormSchemas,
  //回车提交
  autoSubmitOnEnter: false,
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

const taskObjRef = ref(null)


const [registerTable, {reload, clearSelectedRowKeys}, {
  rowSelection,
  selectedRows,
  selectedRowKeys
}] = tableContext;

function getTableAction(record): ActionItem[] {
  return [
    {
      label: '编辑',
      onClick: handleEdit.bind(null, record),
    },
    {
      label: record.status.toString() === '0' ? '暂停' : '开启',
      onClick: handOpenPause.bind(null, record),
    },
    {
      label: '删除',
      onClick: handDel.bind(null, record),
    }
  ]
}

async function ondownLoadXls() {
  const res = await taskSetDownload({});
  downloadByData(res, '任务批量导入模版.xlsx');
}

/*编辑按钮点击时间*/
function handleEdit(record) {
  openEdit(record)
}

/*删除按钮点击时间*/
function handDel(record) {
  delRequest(record)
}

/*暂停按钮点击时间*/
function handOpenPause(record) {
  /*修改状态*/
  openWrapLoading()
  taskSetUpdateStatus({
    id: record.id,
  }).then(async () => {
    selectedRows.value = []
    message.success('操作成功！')
    await reload()
  }).finally(() => {
    closeWrapLoading()
  })
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
    content: '删除后下个周期将不再下发任务，请确认？',
    onOk: () => {
      openWrapLoading()
      taskSetBatchDelete({
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

const [registerModal, {openModal}] = useModal();

/*添加按钮点击事件*/
async function handAdd() {
  showAdd.value = true
  addTitle.value = '添加'
  nextTick(async () => {
    await clearValidate()
    await resetFields()
    if (taskObjRef.value) {
      taskObjRef.value.handleOpenAdd()
    }
  })

}

/*编辑按钮点击事件*/
async function openEdit(item) {
  showAdd.value = true
  addTitle.value = '编辑'
  nextTick(async () => {
    await clearValidate()
    await resetFields()
    item.date = `${item.taskBeginDate},${item.taskEndDate}`
    let editItem = {
      ...item,
      taskObject: Number(item.taskObjectType) === 1 ? 'taskObject' : ''
    }
    await setFieldsValue(editItem)
    if (taskObjRef.value) {
      taskObjRef.value.resetTaskObj(item.taskObjectType, item.empNo, item.empName)
    }
  });
}

function delRequest(item) {
  createConfirm({
    iconType: 'warning',
    title: '删除',
    content: '删除后下个周期将不再下发任务，请确认？',
    onOk: () => {
      openWrapLoading()
      taskSetDel({id: item.id}).then(async () => {
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

async function handleSubmit() {
  await validate()
  let param = getFieldsValue()
  openWrapLoading()
  if (addTitle.value === '编辑') {
    taskSetUpload(param).then(async () => {
      message.success('编辑成功！')
      await closeAdd()
      await reload()
    }).finally(() => {
      closeWrapLoading()
    })
  } else {
    taskSetAdd(param).then(async () => {
      message.success('添加成功！')
      await closeAdd()
      await reload()
    }).finally(() => {
      closeWrapLoading()
    })
  }
}

async function closeAdd() {
  await clearValidate()
  await resetFields()
  showAdd.value = false
}


function closeAddFrom() {
  showAdd.value = false
}

function openDetails(record) {
  openModal(true, {
    title: '任务详情',
    isUpdate: false,
    record: record,
    showFooter: false,
  });
}

async function taskObjectChange(e) {
  let taskObject = e.data && e.data.length > 0 ? 'taskObject' : undefined
  await setFieldsValue({
    taskObjectType: e.taskObjectType,
    taskObject: taskObject
  })
  if (Number(e.taskObjectType) === 0 || taskObject) {
    nextTick(async () => {
      await clearValidate('taskObject')
    })
  }

  if (Number(e.taskObjectType) === 1 && !taskObject) {
    setTimeout(async () => {
      await validate(['taskObject'])
    }, 50)
  }

  let realName = ''
  let empNo = ''
  if (Number(e.taskObjectType) === 1 && e.data) {
    realName = e.data.map(item => item.realName).join(',')
    empNo = e.data.map(item => item.value).join(',')
  }
  await setFieldsValue({
    empNo: empNo,
    empName: realName,
  })
}
</script>

<template>
  <BasicTable @register="registerTable" :rowSelection="rowSelection">
    <template #tableTitle>
      <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handAdd">添加</a-button>
      <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handMoreAdd">批量添加
      </a-button>
      <a-button type="primary" preIcon="ant-design:delete-outlined" @click="handleMoreDel">
        批量删除
      </a-button>
    </template>
    <!--操作栏-->
    <template #action="{ record }">
      <TableAction :actions="getTableAction(record)"/>
    </template>
    <template #inspectionSubDimensionNum="{ text,record }">
      <span style="color: #1b90ff; cursor: pointer;" @click="openDetails(record)">
        {{ text }}
      </span>
    </template>
  </BasicTable>
  <a-modal v-model:visible="showMoreAdd" title="批量添加">
    <div class="viewAdd">
      <div class="top">
        <span>添加任务设置：</span>
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
      <template #taskObject="{ model }">
        <custom-task-obj ref="taskObjRef" :work-dept-code="model.workDeptCode"
                         :risk-level="model.riskLevel" @change="taskObjectChange"></custom-task-obj>
      </template>
      <template #formFooter>
        <div style="margin: 20px auto">
          <a-button type="primary" class="mr-2" @click="handleSubmit">提交</a-button>
          <a-button class="mr-2" @click="closeAddFrom"> 取消</a-button>
        </div>
      </template>
    </BasicForm>
  </a-modal>
  <sub-dimension-modal @register="registerModal"></sub-dimension-modal>
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
