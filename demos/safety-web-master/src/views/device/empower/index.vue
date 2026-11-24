<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" v-if="hasAuth('device:empower:add')" @click="onAdd">添加</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" v-if="hasAuth('device:empower:export')" @click="onExportXls"> 导出</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" v-if="hasAuth('device:empower:import')" @click="showModal"> 导入</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>

      <template #form-factoryName="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择厂区" allowClear>
          <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
        </a-select>
      </template>
    </BasicTable>
    <EmpowerModal @register="registerModal" @success="reload" />
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
  import EmpowerModal from './EmpowerModal.vue';
  import { Api, list, deleteSingle, getfactoryList, detail, download } from './empower.api';
  import { columns, searchFormSchema } from './empower.data';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { downloadByData } from '/@/utils/file/download';
  // import dayjs from 'dayjs';
  // import { useMessage } from '/@/hooks/web/useMessage';
  // const { createMessage } = useMessage();
  const openUpload = ref<boolean>(false);
  const factoryLists = ref<Array>([]);
  const { hasPermission } = usePermission();
  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'device-empower',
    tableProps: {
      title: '授权区域设备绑定列表数据',
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
      ellipsis: false,
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
      name: '授权区域设备绑定列表',
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
      title: '添加设备',
      isUpdate: false,
      showFooter: true,
      record: {},
      factoryLists: factoryLists.value,
    });
  }

  async function onEdit(record) {
    const res = await detail({ id: record.id });
    openModal(true, {
      title: '修改设备',
      isUpdate: true,
      record: res,
      showFooter: true,
      factoryLists: factoryLists.value,
    });
  }
  // 获取厂区列表
  async function getFactoryList1() {
    const res = await getfactoryList({});
    factoryLists.value = res.factoryList;
    console.log(factoryLists, 'factoryListsfactoryLists');
  }
  getFactoryList1();

  async function onDelete(record) {
    if (record) {
      //update-end-author:taoyan date:2022-7-14 for: VUEN-1652【bug】应用状态下不允许删除
      // doDeleteDepart([record.id], false);
      await deleteSingle({ id: String(record.id) }, true);
      await reload();
    }
  }

  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    //update-begin---author:wangshuai ---date:20221123  for：[VUEN-2807]消息模板加一个查看功能------------
    return [
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'device:empower:edit' },
      { label: '删除', onClick: onDelete.bind(null, record), auth: 'device:empower:delete' },
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
    downloadByData(res, '授权区域设备管理.xlsx');
  }
</script>

<style lang="less">
  @import 'index';
  .blacklistUpload {
    padding: 10px;
  }
</style>
