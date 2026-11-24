<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" @click="handleCreate" v-if="hasPermission('systemparamsconfig:add')">新增</a-button>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>

    <!--新增/编辑 - 弹窗-->
    <ScmModal @register="registerScmModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
//ts语法
import { ref, unref } from 'vue';
import { BasicTable, TableAction, ActionItem, EditRecordRow } from '/@/components/Table';
import ScmModal from './ScmModal.vue';
import { useListPage } from '/@/hooks/system/useListPage';
import { useModal } from '/@/components/Modal';
import { cloneDeep } from 'lodash-es';
import { columns } from './user.data';
import { getListPage, save, update, deleteItems } from './field.api';
import { usePermission } from '/@/hooks/web/usePermission';

const { hasPermission } = usePermission();

//新增/编辑弹窗
const [registerScmModal, { openModal: openModal }] = useModal();
// 列表页面公共参数、方法
const { tableContext } = useListPage({
  designScope: 'system-list',
  tableProps: {
    title: '系统配置',
    api: getListPage,
    columns: columns,
    size: 'small',
    showTableSetting: false,
    pagination: {
      pageSize: 15,
      showSizeChanger: true,
      showQuickJumper: true,
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
      slots: { customRender: 'action' },
    },
    useSearchForm: false,
    showIndexColumn: true,
  },
});

//注册table数据
const [registerTable, { reload, updateTableDataRecord }, { rowSelection, selectedRowKeys }] = tableContext;

/**
 * 新增事件
 */
function handleCreate() {
  openModal(true, {
    isUpdate: false,
    showFooter: true,
  });
}

/**
 * 列表操作栏
 */
function getTableAction(record: EditRecordRow): ActionItem[] {
  return [
    {
      label: '编辑',
            ifShow: () => hasPermission('systemparamsconfig:edit'),
      onClick: handleEdit.bind(null, record),
    },
    {
      label: '删除',
      ifShow: () => hasPermission('systemparamsconfig:del'),
      popConfirm: {
        title: '是否确认删除',
        placement: 'topRight',
        confirm: handleDelete.bind(null, record),
      },
      color: 'error',
    },
  ];
}
/**
 * 删除事件
 */
async function handleDelete(record: Recordable) {
  try {
    console.log(record, 'record----');
    await deleteItems({ idStr: String(record.id) }, reload);
    const index = unref(selectedRowKeys).findIndex((n) => n == record.id);
    if (index != -1) {
      selectedRowKeys.value.splice(index, 1);
    }
  } catch (error) {
    console.log('handleDelete', error);
  }
}
/**
 * 编辑事件
 */
async function handleEdit(record: Recordable) {
  openModal(true, {
    record: cloneDeep(record),
    isUpdate: true,
    showFooter: true,
  });
}
/**
 * 成功回调
 * @param {number} isUpdate 操作类型：0 无操作 1 新增/新增加编辑 2 编辑
 * @param {object} values 编辑的数据
 */
function handleSuccess({ isUpdate, values }) {
  console.log('弹窗回调', isUpdate, values);
  if (isUpdate == 2) {
    updateTableDataRecord(values.id, values);
  } else if (isUpdate == 1) {
    reload();
  }
}
</script>

<style scoped></style>
