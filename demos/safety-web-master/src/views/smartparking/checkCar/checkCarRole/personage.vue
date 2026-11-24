<template>
  <div class="systemd-noticemessage">
    <BasicTable title="提示信息"  @register="registerTable">
      <template #tableTitle>
        <a-radio-group :value="props.category" @change="categoryChange">
          <a-radio-button value="1">岗位</a-radio-button>
          <a-radio-button value="2">个人</a-radio-button>
        </a-radio-group>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd" v-if="hasAuth('smartparking:checkcar:checkcarrole:add')">添加</a-button>
        <a-button type="primary" :loading="props.loading" preIcon="ant-design:caret-right-outlined" @click="execute" v-if="hasAuth('smartparking:checkcar:checkcarrole:add')">执行</a-button>
        <!-- <a-button type="primary" preIcon="ant-design:import-outlined" @click="openUpload = true" v-if="hasAuth('smartparking:checkcar:checkcarrole:add')">批量导入</a-button> -->
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <listModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" name="checkcar-role" setup>
import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import listModal from './perListModal.vue';
import { perList as list, perDelete as remove } from './list.api';
import { personColumns as columns,perSearchFormSchema as searchFormSchema } from './list.data';
import { useModal } from '/@/components/Modal';
const [registerModal, { openModal }] = useModal(); // 厂区弹出框
const props = defineProps({
  category: {
    type: String,
    default: '1'
  },
  loading: {
    type: Boolean,
    default: false
  }
})
// 列表页面公共参数、方法
const { tableContext } = useListPage({
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
async function onDelete(record) {
  if (record) {
    await remove({ id: String(record.id) }, true);
    await reload();
  }
}

/**
 * 操作栏
 */
function getTableAction(record): ActionItem[] {
  return [
    { label: '编辑', onClick: onEdit.bind(null, record), auth: 'smartparking:checkcar:checkcarrole:edit' },
    { label: '删除', onClick: onDelete.bind(null, record), auth: 'smartparking:checkcar:checkcarrole:delete'},
  ];
}
const emits = defineEmits(['categoryChange', 'apply'])
const categoryChange = (e) => {
  emits('categoryChange', e.target.value)
}
const execute = () => {
  emits('apply')
}
</script>

<style lang="less" scoped>
@import 'index';

.blacklistUpload {
  padding: 10px;
}
</style>
