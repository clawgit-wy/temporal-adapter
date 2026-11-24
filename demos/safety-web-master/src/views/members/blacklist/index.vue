<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd" v-if="hasAuth('members:blacklist:add')">添加</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls" v-if="hasAuth('members:blacklist:export')"> 导出</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="showModal" v-if="hasAuth('members:blacklist:import')"> 导入</a-button>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleTasksWebMessage" v-if="hasAuth('members:blacklist:task')"
          >获取增量数据</a-button
        >
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <BlacklistModal @register="registerModal" @success="reload" />
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

<script lang="ts" name="members-blacklists" setup>
  import { ref } from 'vue';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import BlacklistModal from './BlacklistModal.vue';
  import { Api, list, deleteSingle, download, tasksWebMessage } from './blacklist.api';
  import { columns, searchFormSchema } from './blacklist.data';
  import { downloadByData } from '/@/utils/file/download';
  // import dayjs from 'dayjs';
  import { useMessage } from '/@/hooks/web/useMessage';
  const { createMessage } = useMessage();
  const openUpload = ref<boolean>(false);
  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'members-blacklist',
    tableProps: {
      title: '黑名单管理列表数据',
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
        width: 100,
        title: '操作',
        //是否锁定操作列取值 right ,left,false
        fixed: 'right',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
    },
    exportConfig: {
      url: Api.exportXls,
      name: '黑名单管理列表',
    },
    importConfig: {
      url: Api.importXls,
      success: () => reload(),
    },
  });

  // 注册 ListTable
  const [registerTable, { reload }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  const { hasPermission } = usePermission();

  function onAdd() {
    openModal(true, {
      title: '添加人员',
      isUpdate: false,
      showFooter: true,
      record: {},
    });
  }

  function onEdit(record) {
    openModal(true, {
      title: '修改黑名单信息',
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
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'members:blacklist:edit' },
      { label: '删除', onClick: onDelete.bind(null, record), auth: 'members:blacklist:delete' },
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
    // reload();
  }

  function hasAuth(code) {
    return hasPermission(code);
  }

  async function ondownLoadXls() {
    const res = await download({});
    downloadByData(res, '黑名单列表.xlsx');
  }

  // 手动拉取增量数据
  async function handleTasksWebMessage() {
    await tasksWebMessage();
    createMessage.success('拉取成功');
    reload();
  }
</script>

<style lang="less" scoped>
  @import 'index';
  .blacklistUpload {
    padding: 10px;
  }
</style>
