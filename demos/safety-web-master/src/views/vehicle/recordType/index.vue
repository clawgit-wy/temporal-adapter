<template>
  <div class="p-4">
    <!--定义表格-->
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" preIcon="ant-design:plus-outlined" class="custom-button" @click="onAdd" v-if="hasAuth('VehicleManagement:recordType:add')">新增</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <Modal @register="registerModal" @success="reload" />
  </div>
</template>
<script lang="ts" setup>
  import { columns } from './data';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import Modal from './Modal.vue';
  import {list, deleteDict} from './api'
  import { usePermission } from '/@/hooks/web/usePermission';
  const { hasPermission } = usePermission();
  function hasAuth(code) {
    return hasPermission(code);
  }
  // 列表页面公共参数、方法
  const { tableContext } = useListPage({
    tableProps: {
      title: '备案类型配置',
      api: list,
      columns: columns,
      useSearchForm: false,
      showIndexColumn: true,
      showActionColumn: false,
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
        params['supperCode'] = 'enter_per_type'
        params['supperId'] = 2
        return params;
      },
    },
  });
  //BasicTable绑定注册
  const [registerTable, { reload }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  // 导出
  // 编辑
  function onEdit(record) {
    openModal(true, {
      title: '修改备案类型',
      isUpdate: true,
      record,
      showFooter: true,
    });
  }
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    return [
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'VehicleManagement:recordType:edit' },
      { label: '删除', onClick: onDelete.bind(null, record), auth: 'VehicleManagement:recordType:delete' },
    ];
  }

  // 新增
  const onAdd = (): void => {
    openModal(true, {
      title: '新增备案类型',
      isUpdate: false,
      record: {},
      showFooter: true,
    });
  };

  async function onDelete(record) {
    if (record) {
      await deleteDict({ id: String(record.id) });
      await reload();
    }
  }
</script>
<style lang="less" scoped>
  ::v-deep(.ant-picker-range) {
    width: 100%;
  }

  ::v-deep(.ant-input-affix-wrapper) {
    border-color: #797979;
    border-radius: 4px;
  }
</style>
