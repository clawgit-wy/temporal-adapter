<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" v-if="hasAuth('systemd:formmanage:add')" @click="onAdd">添加</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" v-if="hasAuth('systemd:formmanage:save')" @click="save"> 保存</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
      <template #mustSet="{ record }">
        <a-checkbox v-model:checked="record.mustSet" />
      </template>
      <template #takeEffect="{ record }">
        <a-checkbox v-model:checked="record.takeEffect" />
      </template>
    </BasicTable>
    <FormmanageModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" name="device-binding" setup>
  // import { ref } from 'vue';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import FormmanageModal from './FormmanageModal.vue';
  import { list, deleteSingle, update } from './formmanage.api';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { columns } from './formmanage.data';
  // import dayjs from 'dayjs';
  import { useMessage } from '/@/hooks/web/useMessage';
  const { createMessage } = useMessage();
  const { hasPermission } = usePermission();

  // 列表页面公共参数、方法
  const { prefixCls, tableContext } = useListPage({
    designScope: 'device-binding',
    tableProps: {
      title: '表单列表数据',
      api: list,
      columns: columns,
      pagination: false,
      useSearchForm: false,
      showIndexColumn: true,
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
        console.log(params);
        return params;
      },
    },
  });

  // 注册 ListTable
  const [registerTable, { reload, getDataSource }] = tableContext;
  const [registerModal, { openModal }] = useModal();

  function onAdd() {
    openModal(true, {
      title: '添加表单',
      isUpdate: false,
      showFooter: true,
      record: {},
    });
  }
  async function save() {
    const data = getDataSource();
    const res = await update({ sheetSet: JSON.stringify(data) });
    createMessage.success('保存成功');
  }

  function onEdit(record) {
    openModal(true, {
      title: '修改表单',
      isUpdate: true,
      record: record,
      showFooter: true,
    });
  }

  async function onDelete(record) {
    if (record) {
      await deleteSingle({ id: String(record.id) }, true);
      await reload();
    }
  }
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    return [
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'systemd:formmanage:edit' },
      { label: '删除', onClick: onDelete.bind(null, record), auth: 'systemd:formmanage:delete' },
    ];
  }

  function hasAuth(code) {
    return hasPermission(code);
  }
</script>

<style lang="less">
  @import 'index';
</style>
