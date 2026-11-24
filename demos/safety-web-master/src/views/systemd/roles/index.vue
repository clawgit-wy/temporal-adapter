<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" v-if="hasAuth('systemd:roles:add')" @click="onAdd">添加</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" v-if="hasAuth('systemd:roles:export')" @click="onExportXls"> 导出</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" v-if="hasAuth('systemd:roles:import')" @click="showModal"> 导入</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <RolesModal @register="registerModal" @success="reload" />
    <TreeModal @register="registerTreeModal" @success="reload" />
    <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
      <p>请按照模板规范填写，否则无法正确导入<a-button>下载模板</a-button></p>
      <p>
        <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">上传</j-upload-button>
      </p>
    </a-modal>
  </div>
</template>

<script lang="ts" name="device-binding" setup>
  import { ref } from 'vue';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import RolesModal from './RolesModal.vue';
  import TreeModal from './TreeModal.vue';
  import { Api, list, getTree, detail } from './roles.api';
  import { columns, searchFormSchema } from './roles.data';
  // import { unref } from 'vue';
  // import dayjs from 'dayjs';
  // import { useMessage } from '/@/hooks/web/useMessage';
  // const { createMessage } = useMessage();
  const openUpload = ref<boolean>(false);
  let zTreeRoot = [];
  const { hasPermission } = usePermission();

  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'systemd-roles',
    tableProps: {
      title: '角色管理',
      api: list,
      columns: columns,
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
      },
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
    exportConfig: {
      url: Api.exportXls,
      name: '角色管理列表',
    },
    importConfig: {
      url: Api.importXls,
      success: () => reload(),
    },
  });

  // 注册 ListTable
  const [registerTable, { reload }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  const [registerTreeModal, treeModal] = useModal();
  function onAdd() {

    openModal(true, {
      title: '添加角色',
      isUpdate: false,
      showFooter: true,
      record: {},
      zTreeRoot: zTreeRoot,
    });
  }

  // 显示tree组件
  async function onAddTree(record) {
    const res = await detail({ id: record.id });
    const data = {
      ...res,
      permIds: res.permIds ? res.permIds.join(',') : '',
    };
    treeModal.openModal(true, {
      title: '选择菜单',
      showFooter: true,
      record: data,
      permIds: res.permIds ? res.permIds.map((item) => String(item)) : [],
      type: 'permIds',
      zTreeRoot: zTreeRoot,
    });
  }

  async function onEdit(record) {
    const res = await detail({ id: record.id });
    const data = {
      ...res,
      permIds: res.permIds ? res.permIds.join(',') : '',
    };
    openModal(true, {
      title: '修改角色名称',
      isUpdate: true,
      record: data,
      showFooter: true,
      zTreeRoot: zTreeRoot,
    });
  }

  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    return [
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'systemd:roles:edit' },
      { label: '权限管理', onClick: onAddTree.bind(null, record), auth: 'systemd:roles:auth' },
    ];
  }

  /**
   * 上传弹出框
   */
  function showModal() {
    openUpload.value = true;
  }

  function handleOk() {
    openUpload.value = false;
  }
  function formatData(list) {
    list.forEach((item) => {
      (item['title'] = item.name), (item['key'] = item.id);
      if (item.children) {
        formatData(item.children);
      }
    });
  }
  // 获取权限列表
  async function getTee() {
    const res = await getTree({ dt: '0' });
    formatData(res);
    zTreeRoot = res;
  }
  getTee();
  function hasAuth(code) {
    return hasPermission(code);
  }
</script>

<style lang="less">
  @import 'index';
</style>
