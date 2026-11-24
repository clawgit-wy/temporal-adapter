<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
      :rowSelection="rowSelection"
    >
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="dormancy()" v-if="hasAuth('patrol:tasksetting:disable')">批量休眠</a-button>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd" v-if="hasAuth('patrol:tasksetting:disable:add')">添加</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls" v-if="hasAuth('patrol:tasksetting:disable:export')">导出</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <listModal @register="registerModal" @success="reload" />
    <a-modal v-model:visible="visible" title="批量休眠" @ok="changeDormancy">
      <div style="margin: 20px; margin-right: 0;"><strong style="color: red;">*</strong><span>休眠日期：</span><a-range-picker valueFormat="YYYY-MM-DD" v-model:value="changeBormancyDate"/></div>
    </a-modal>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import listModal from './listModal.vue';
import {Api, list, changeStatus, modifyDormancy} from './list.api';
import { columns,searchFormSchema } from './list.data';
import { useModal } from '/@/components/Modal';
import { ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
const {createMessage} = useMessage()
const [registerModal, { openModal }] = useModal(); // 厂区弹出框

// 列表页面公共参数、方法
const { tableContext, onExportXls } = useListPage({
  designScope: 'checkCar-list',
  tableProps: {
    title: '任务设置',
    api: list,
    columns: columns,
    pagination: {
      pageSize: 15,
      showSizeChanger: false,
      showQuickJumper: false,
    },
    formConfig: {
      schemas: searchFormSchema,
      fieldMapToTime: [['date', ['beginDate', 'endDate'], 'YYYY-MM-DD']],
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
    name: '设备设施点检任务设置列表',
  },
});
  // 注册 ListTable
const [registerTable, { reload }, {rowSelection, selectedRowKeys}] = tableContext;
const { hasPermission } = usePermission();
 function hasAuth(code) {
    return hasPermission(code);
  }

function onEdit(record) {
  const factoryId = record.factoryId.split(',').map(item => Number(item))
  const factoryName = record.factoryName.split(',')
  const date = [record.beginDate, record.endDate]
  openModal(true, {
    title: '修改信息',
    isUpdate: true,
    record: {...record, factoryId, factoryName, date},
    showFooter: true,
  });
}

function onAdd() {
  openModal(true, {
    title: '添加任务',
    isUpdate: false,
    showFooter: true,
    record: {},
  });
}

// 批量休眠弹窗
const visible = ref(false)
// 批量休眠日期
const changeBormancyDate = ref([])
const dormancy = () => {
  if (selectedRowKeys.value.length == 0) {
    return createMessage.warning('请选择数据')
  }
  visible.value = true
}
const changeDormancy = async () => {
  if (changeBormancyDate.value.length == 0) {
    return createMessage.warning('请选择休眠日期')
  }
  const params = {
    ids: selectedRowKeys.value.join(),
    dormancyBegin: changeBormancyDate.value[0],
    dormancyEnd: changeBormancyDate.value[1],
  }
  await modifyDormancy(params)
  visible.value = false
  reload()
}
// 批量休眠
const onChangeStatus = async (ids:string[], status) => {
  await changeStatus({ids: ids.join(), status}, true)
  await reload()
}

/**
 * 操作栏
 */
function getTableAction(record): ActionItem[] {
  return [
    { label: '编辑', onClick: onEdit.bind(null, record), auth: 'patrol:tasksetting:edit' },
    { label: !record.status ? '禁用' : '启用', onClick: onChangeStatus.bind(null, [record.id], !record.status ? 1 : 0), auth: 'patrol:tasksetting:switch'},
  ];
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
