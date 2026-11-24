<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <template #form-select="{ model, field }">
        <a-input v-model:value="model[field]" placeholder="请选择" readOnly @click="deptChose" />
      </template>
      <!--插槽:table标题-->
      <template #tableTitle>
        <div style="display: flex; flex-direction: column; color: #508cee">
          <span>注意：</span>
          <span>1.白名单成员不纳入长期未进出查验内，但仍监控其部门组织架构变化</span>
          <span>2.管理员添加的白名单人员只能由管理员注销门禁权限</span>
        </div>
      </template>
      <template #toolbar>
        <a-button type="primary" preIcon="ant-design:import-outlined" v-if="hasPermission('buildingAccess:whiteManage:add')" @click="onAdd()"
          >新增</a-button
        >
        <a-button type="primary" preIcon="ant-design:import-outlined" v-if="hasPermission('buildingAccess:whiteManage:import')" @click="showModal"
          >导入</a-button
        >
        <a-button type="primary" preIcon="ant-design:import-outlined" v-if="hasPermission('buildingAccess:whiteManage:export')" @click="onExportXls()"
          >导出</a-button
        >
      </template>

      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
      <div class="blacklistUpload" ref="wrapEl">
        <p>请按照模板规范填写，否则无法正确导入<a-button @click="ondownLoadXls">下载模板</a-button></p>
        <p>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="uploadfile">上传</j-upload-button>
        </p>
      </div>
    </a-modal>
    <!--新增/编辑 - 弹窗-->
    <ScmModal @register="registerScmModal" @success="handleSuccess" />
    <DepartmentSelect v-model:visible="searchDepartVisible" @change="searchDepartChange"></DepartmentSelect>
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
  //ts语法
  import { ref, unref } from 'vue';
  import { cloneDeep } from 'lodash-es';
  import { BasicTable, TableAction, ActionItem, EditRecordRow } from '/@/components/Table';
  import ScmModal from './scm.modal.vue';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns, searchFormSchema } from './config';
  import { getListPage, Api, download, deleteItem, saveItem } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { downloadByData } from '/@/utils/file/download';
  import DepartmentSelect from '/@/components/DepartmentSelect.vue';
  import { useRouter, useRoute } from 'vue-router';
  import { Modal } from 'ant-design-vue';
  import { Loading, useLoading } from '/@/components/Loading';
  const wrapEl = ref<ElRef>(null);
  const [openWrapLoading, closeWrapLoading] = useLoading({
    target: wrapEl,
    props: {
      tip: '加载中...',
      absolute: true,
    },
  });
  const route = useRoute();
  const router = useRouter();
  // 消息
  const { createMessage } = useMessage();
  const userStore: any = useUserStore().getUserInfo;
  const openUpload = ref<boolean>(false);
  const { hasPermission } = usePermission();

  //新增/编辑弹窗
  const [registerScmModal, { openModal: openModal }] = useModal();

  // 列表页面公共参数、方法
  const { onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'goodsOut-list',
    tableProps: {
      title: '白名单管理',
      api: getListPage,
      columns: columns,
      size: 'small',
      ellipsis: false,
      formConfig: {
        schemas: searchFormSchema,
      },
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
      showIndexColumn: true,
    },
    exportConfig: {
      url: Api.exportData,
      name: '门禁管理员及责任部门管理',
    },
    importConfig: {
      url: Api.importData,
      success: () => {
        console.log('上传成功回调');
        openUpload.value = false;
        closeWrapLoading();
        reload();
      },
    },
  });

  function uploadfile(file) {
    console.log('参数==', file);
    openWrapLoading();
    onImportXls(file);
  }

  //注册table数据
  const [registerTable, { getForm, reload, updateTableDataRecord }] = tableContext;

  //组织选择
  const searchDepartVisible = ref(false);
  function deptChose() {
    searchDepartVisible.value = true;
  }
  const searchDepartChange = (depart) => {
    console.log('组织选择==', depart);
    getForm().setFieldsValue({ dutyDeptName: depart.title, dutyDeptCode: depart.key });
    searchDepartVisible.value = false;
  };

  function onAdd() {
    openModal(true, {
      record: { employeeCode: '', employeeName: '', createReason: '' },
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
        label: '删除',
        ifShow: () => hasPermission('buildingAccess:whiteManage:delete'),
        onClick: handleDelete.bind(null, record),
      },
    ];
  }
  /**
   * 编辑事件
   */
  async function handleDelete(record: Recordable) {
    Modal.confirm({
      title: '提示',
      content: '是否删除？',
      async onOk() {
        await deleteItem({ id: record.id });
        createMessage.success('操作成功');
        reload();
      },
      onCancel() {},
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
  /**
   * 上传弹出框
   */
  function showModal() {
    openUpload.value = true;
  }

  function handleOk() {
    console.log('上传成功回调===handleOk');
    openUpload.value = false;
  }

  function hasAuth(code) {
    return hasPermission(code);
  }
  async function ondownLoadXls() {
    const res = await download({});
    downloadByData(res, '白名单模板.xlsx');
  }
</script>

<style lang="less">
  .blacklistUpload {
    padding: 10px;
  }
</style>
