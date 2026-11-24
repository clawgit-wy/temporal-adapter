<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <template #form-deptName="{ model, field }">
        <a-input v-model:value="model[field]" placeholder="请选择" readOnly @click="deptChose" />
      </template>

      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onAdd()" v-if="hasPermission('gateAccess:defaultFactoryRules:add')"
          >新增</a-button
        >
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <DepartmentSelect v-model:visible="searchDepartVisible" @change="searchDepartChange"></DepartmentSelect>
    <!--新增/编辑 - 弹窗-->
    <ScmModal @register="registerScmModal" @success="handleSuccess" :factoryLists="factoryLists" />
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
  //ts语法
  import { ref, unref } from 'vue';
  import { BasicTable, TableAction, ActionItem } from '/@/components/Table';
  import ScmModal from './scm.modal.vue';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns, searchFormSchema } from './config';
  import { tableList, Api, fieldChildTree, deleteItem } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import DepartmentSelect from '/@/components/DepartmentSelect.vue';
  import { Modal } from 'ant-design-vue';
  // 消息
  const { createMessage } = useMessage();
  const userStore: any = useUserStore().getUserInfo;
  const { hasPermission } = usePermission();

  //新增/编辑弹窗
  const [registerScmModal, { openModal: openModal }] = useModal();

  // 列表页面公共参数、方法
  const { tableContext } = useListPage({
    designScope: 'factoryrules',
    tableProps: {
      title: '默认下发厂区规则',
      api: tableList,
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
  });

  //注册table数据
  const [registerTable, { getForm, reload, updateTableDataRecord }] = tableContext;

  // 获取厂区列表
  const factoryLists = ref<Array<Object>>([]);
  async function getFactoryList() {
    const res = await fieldChildTree({ fieldType: 'factory' });
    factoryLists.value = res;
  }
  getFactoryList();

  /**
   * 列表操作栏
   */
  function getTableAction(record: EditRecordRow): ActionItem[] {
    return [
      {
        label: '编辑',
        ifShow: () => hasPermission('gateAccess:defaultFactoryRules:edit'),
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '删除',
        ifShow: () => hasPermission('gateAccess:defaultFactoryRules:delete'),
        onClick: handleDelete.bind(null, record),
      },
    ];
  }

  /**
   * 编辑事件
   */
  async function handleEdit(record: Recordable) {
    console.log('楼栋数据==', record);
    const info = { ...record };
    if (record.factoryPower) {
      info.factoryPower = record.factoryPower.split(',').map((item) => Number(item));
    } else {
      info.factoryPower = [];
    }
    openModal(true, {
      record: info,
      isUpdate: true,
      showFooter: true,
    });
  }

  async function onAdd() {
    const record = {};
    openModal(true, {
      record: record,
      isUpdate: false,
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

  //删除事件
  function handleDelete(record: Recordable) {
    Modal.confirm({
      title: '提示',
      content: '是否确认删除',
      async onOk() {
        await deleteItem({ idsStr: record.id });
        createMessage.success('提交成功');
      },
      onCancel() {},
    });
  }

  //组织选择
  const searchDepartVisible = ref(false);
  function deptChose() {
    searchDepartVisible.value = true;
  }
  const searchDepartChange = (depart) => {
    console.log('组织选择==', depart);
    getForm().setFieldsValue({ orgName: depart.title, orgCode: depart.key });
    searchDepartVisible.value = false;
  };

  function hasAuth(code) {
    return hasPermission(code);
  }
</script>

<style lang="less">
  .blacklistUpload {
    padding: 10px;
  }
</style>
