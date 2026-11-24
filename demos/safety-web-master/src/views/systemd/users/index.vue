<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" v-if="hasAuth('systemd:users:add')" @click="onAdd">添加</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" v-if="hasAuth('systemd:users:export')" @click="onExportXls"> 导出</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" v-if="hasAuth('systemd:users:import')" @click="showModal"> 导入</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
      <template #form-factoryName="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择厂区" mode="multiple" allowClear>
          <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
        </a-select>
      </template>
      <template #form-powerFactoryName="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择厂区" mode="multiple" allowClear>
          <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
        </a-select>
      </template>
    </BasicTable>
    <BindingModal @register="registerModal" @success="reload" :rolesList="rolesList" :factoryLists="factoryLists" />
    <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
      <div class="blacklistUpload">
        <p>请按照模板规范填写，否则无法正确导入<a-button @click="ondownLoadXls">下载模板</a-button></p>
        <p>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">上传</j-upload-button>
        </p>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" name="device-binding" setup>
  import { ref } from 'vue';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import BindingModal from './UsersModal.vue';
  import { Api, list, deleteSingle, getRoles, download, factoryList } from './users.api';
  import { columns, searchFormSchema } from './users.data';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { downloadByData } from '/@/utils/file/download';
  // import dayjs from 'dayjs';
  // import { useMessage } from '/@/hooks/web/useMessage';
  // const { createMessage } = useMessage();
  const openUpload = ref<boolean>(false);
  const rolesList = ref<Array>([]);
  const { hasPermission } = usePermission();
  const factoryLists = ref([]); // 厂区列表
  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'device-binding',
    tableProps: {
      title: '用户管理列表数据',
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
      name: '用户管理列表',
    },
    importConfig: {
      url: Api.importXls,
      success: () => reload(),
    },
  });

  // 注册 ListTable
  const [registerTable, { reload, setLoading, getForm }, { rowSelection, selectedRowKeys, selectedRows }] = tableContext;
  const [registerModal, { openModal }] = useModal();

  function onAdd() {
    openModal(true, {
      title: '添加用户',
      isUpdate: false,
      showFooter: true,
      record: {},
      rolesList,
    });
  }

  function onEdit(record) {
    const info = { ...record };
    if (record.factoryId) {
      info.factoryId = record.factoryId.split(',').map((item) => Number(item));
    } else {
      info.factoryId = [];
    }
    if (record.powerFactoryId) {
      info.powerFactoryId = record.powerFactoryId.split(',').map((item) => Number(item));
    } else {
      info.powerFactoryId = [];
    }
    info.roleId = record.roleId ? record.roleId.split(',').map((item) => Number(item)) : [];
    openModal(true, {
      title: '修改用户',
      isUpdate: true,
      record: info,
      showFooter: true,
    });
  }

  async function onDelete(record) {
    if (record) {
      await deleteSingle({ id: String(record.id) }, true);
      await reload();
    }
  }

  // 获取用户列表
  async function getRolesList() {
    const res = await getRoles({});
    rolesList.value = res;
  }
  getRolesList();
  // 获取厂区列表
  async function getFactoryList() {
    const res = await factoryList({});
    factoryLists.value = res.factoryList;
  }
  getFactoryList();
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    //update-begin---author:wangshuai ---date:20221123  for：[VUEN-2807]消息模板加一个查看功能------------
    return [
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'systemd:users:edit' },
      { label: '删除', onClick: onDelete.bind(null, record), auth: 'systemd:users:delete' },
    ];
    //update-end---author:wangshuai ---date:20221123  for：[VUEN-2807]消息模板加一个查看功能------------
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

  function hasAuth(code) {
    return hasPermission(code);
  }
  async function ondownLoadXls() {
    const res = await download({});
    downloadByData(res, '用户管理列表.xlsx');
  }
</script>

<style lang="less" scoped>
  @import 'index';
  .blacklistUpload {
    padding: 10px;
  }
</style>
