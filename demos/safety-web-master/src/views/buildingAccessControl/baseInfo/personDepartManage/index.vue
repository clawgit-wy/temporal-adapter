<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <template #form-select="{ model, field }">
        <a-input v-model:value="model[field]" placeholder="请选择" readOnly @click="deptChose" />
      </template>
      <template #form-deviceName="{ model, field }">
        <a-input v-model:value="model[field]" placeholder="请选择" readOnly @click="deviceChose" />
      </template>
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button
          type="primary"
          preIcon="ant-design:import-outlined"
          @click="onAdd()"
          v-if="hasPermission('buildingAccess:baseInfo:personDepartManage:add')"
          >新增</a-button
        >
        <a-button
          type="primary"
          preIcon="ant-design:import-outlined"
          @click="onExportXls()"
          v-if="hasPermission('buildingAccess:baseInfo:personDepartManage:export')"
          >导出</a-button
        >
      </template>
      <template #processId="{ text, record }">
        <span style="color: blue; cursor: pointer" @click="goInfo(record)">{{ text }}</span>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
      <div class="blacklistUpload">
        <p>请按照模板规范填写，否则无法正确导入<a-button @click="ondownLoadXls">下载模板</a-button></p>
        <p>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">上传</j-upload-button>
        </p>
      </div>
    </a-modal>
    <!--新增/编辑 - 弹窗-->
    <ScmModal @register="registerScmModal" @success="handleSuccess" />
    <DepartmentSelect v-model:visible="searchDepartVisible" @change="searchDepartChange"></DepartmentSelect>
    <door-access-select v-model:value="showPersonSelect" v-model:selected="selectedDoors" :multipleNum="1" placeholder="请选择门禁" @ok="buidlSure" />
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
  import { getListPage, Api, deleteData } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { downloadByData } from '/@/utils/file/download';
  import { addFullParam } from '/@/utils/router/fullParam';
  import DepartmentSelect from '/@/components/DepartmentSelect.vue';
  import DoorAccessSelect from '/@/components/DoorAccessSelect/index.vue';
  import { useRouter, useRoute } from 'vue-router';
  const route = useRoute();
  const router = useRouter();
  // 消息
  const { createMessage, createConfirm } = useMessage();
  const userStore: any = useUserStore().getUserInfo;
  const openUpload = ref<boolean>(false);
  const { hasPermission } = usePermission();

  //新增/编辑弹窗
  const [registerScmModal, { openModal: openModal }] = useModal();

  // 列表页面公共参数、方法
  const { onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'goodsOut-list',
    tableProps: {
      title: '门禁管理员及责任部门管理',
      api: getListPage,
      columns: columns,
      size: 'small',
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
      beforeFetch: (params) => {
        let _params = Object.assign({}, { modifyBy: userStore.empNo }, params);
        return _params;
      },
    },
    exportConfig: {
      url: Api.exportData,
      name: '门禁管理员及责任部门管理',
    },
  });

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

  //门禁选择
  //筛选项 门禁
  const showPersonSelect = ref(false);
  const selectedDoors = ref([]);
  function deviceChose() {
    console.log('增加', selectedDoors.value);
    showPersonSelect.value = true;
  }

  function buidlSure(buildList) {
    console.log('门禁选择==', buildList);
    getForm().setFieldsValue({
      deviceCode: buildList[0].key,
      deviceName: buildList[0].title,
    });
    showPersonSelect.value = false;
  }

  const goInfo = (record) => {
    // window.open(record.flowUrl, '_blank');
    router.push(addFullParam({
      path: '/responsibilityDepartAjustProcess/add',
      query: {
        formInstanceId: record.processId,
        processId: record.bpmFlowId,
      },
    }));
  };

  function onAdd() {
    router.push(addFullParam({
      path: '/responsibilityDepartAjustProcess/add',
    }));
  }

  /**
   * 列表操作栏
   */
  function getTableAction(record: EditRecordRow): ActionItem[] {
    return [
      {
        label: '编辑',
        ifShow: () => hasPermission('buildingAccess:baseInfo:personDepartManage:edit'),
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '删除',
        ifShow: () => hasPermission('buildingAccess:baseInfo:personDepartManage:delete'),
        color: 'error',
        onClick: handleDelete.bind(null, record),
      },
    ];
  }
  /**
   * 编辑事件
   */
  async function handleEdit(record: Recordable) {
    openModal(true, {
      record: record,
      isUpdate: true,
      showFooter: true,
    });
  }
  /**
   * 删除事件
   */
  async function handleDelete(record: Recordable) {
    createConfirm({
      iconType: 'warning',
      title: '提示',
      content: '删除后不可恢复，请确认。',
      onOk: async () => {
        await deleteData({ empNo: record.employeeCode, dutyDeptCode: record.dutyDeptCode });
        createMessage.success('删除成功');
        reload();
      },
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
    openUpload.value = false;
  }

  function hasAuth(code) {
    return hasPermission(code);
  }
  async function ondownLoadXls() {
    // const res = await download({});
    // downloadByData(res, '审核人权限配置列表.xlsx');
  }
</script>

<style lang="less">
  .blacklistUpload {
    padding: 10px;
  }
</style>
