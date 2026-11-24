<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls()" v-if="hasPermission('materialdetail:export')"
          >导出</a-button
        >
      </template>
      <template #flowNo="{ text, record }">
        <span style="color: blue; cursor: pointer" @click="goInfo(record)">{{ text }}</span>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>

    <!--新增/编辑 - 弹窗-->
    <!-- <ScmModal @register="registerScmModal" @success="handleSuccess" /> -->
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
  //ts语法
  import { ref, unref } from 'vue';
  import { BasicTable, TableAction, ActionItem, EditRecordRow } from '/@/components/Table';
  // import ScmModal from './ScmModal.vue';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useDrawer } from '/@/components/Drawer';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { cloneDeep } from 'lodash-es';
  import { columns, searchFormSchema } from './config';
  import { Api, getListPage } from './field.api';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { addFullParam } from '/@/utils/router/fullParam';
  import { useRouter } from 'vue-router';
  const router = useRouter();

  const { hasPermission } = usePermission();

  // 消息
  const { createMessage } = useMessage();

  //新增/编辑弹窗
  const [registerScmModal, { openModal: openModal }] = useModal();

  //字典配置drawer
  const [registerDrawer, { openDrawer }] = useDrawer();

  // 列表页面公共参数、方法
  const { onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'subsystem-list',
    tableProps: {
      title: '物资明细列表',
      api: getListPage,
      columns: columns,
      size: 'small',
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
      actionColumn: {
        ifShow: false,
        width: 120,
        fixed: 'right',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
      showIndexColumn: true,
      // beforeFetch: (params) => {
      //   let _params = Object.assign({}, { column: 'createTime', order: 'desc' }, params);
      //   console.log('params', _params);
      //   return _params;
      // },
    },
    exportConfig: {
      url: Api.exportXls,
      name: '物资出门明细列表',
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
   * 批量删除事件
   */
  async function batchHandleDelete() {
    console.log('selectedRowKeys', selectedRowKeys.value.join(','));
    await batchDeleteUser({ ids: selectedRowKeys.value }, () => {
      selectedRowKeys.value = [];
      reload();
    });
  }

  /**
   * 列表操作栏
   */
  function getTableAction(record: EditRecordRow): ActionItem[] {
    return [
      {
        label: '编辑',
        ifShow: () => hasPermission('materialdetail:edit'),
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '删除',
        ifShow: () => hasPermission('materialdetail:del'),
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
   * 删除事件
   */
  async function handleDelete(record: Recordable) {
    try {
      await deleteUser({ id: record.id }, reload);
      const index = unref(selectedRowKeys).findIndex((n) => n == record.id);
      if (index != -1) {
        selectedRowKeys.value.splice(index, 1);
      }
    } catch (error) {
      console.log('handleDelete', error);
    }
  }
  /**
   * 字典配置
   */
  function handleItem(record) {
    openDrawer(true, {
      id: record.id,
      code: record.code,
      name: record.name,
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

  // 跳转流程详情页面
  const goInfo = (record) => {
    console.log(record);
      if (record.flowUrl) {
        const hasFullParam = window.location.hash.includes('__full__=true');
      if (hasFullParam) {
        window.open(record.flowUrl+'&__full__=true', '_blank')
      } else {
        window.open(record.flowUrl, '_blank')
      }
    } else {
      router.push(addFullParam({
        name: 'goodsexitreview',
        query: {
          formInstanceId: record.processId,
        },
      }));
    }
  };
</script>

<style scoped></style>
