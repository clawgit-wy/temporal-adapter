<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <template #form-building="{ model, field }">
        <a-input v-model:value="model[field]" placeholder="请选择" readOnly @click="choseBuilding" />
      </template>

      <!--插槽:table标题-->
      <template #tableTitle>
        <!-- <a-button
          type="primary"
          preIcon="ant-design:import-outlined"
          @click="permissionChange()"
          v-if="hasPermission('gateAccess:employeeLastFactoryPermissions:permissionsChange')"
          >权限变更</a-button
        > -->
      </template>

      <!-- <template #deviceName="{ text, record }">
        <span style="color: blue; cursor: pointer" @click="showDevice(record)">{{ text }}</span>
      </template> -->

      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <!--新增/编辑 - 弹窗-->
    <ScmModal @register="registerScmModal" @success="handleSuccess" :factoryLists="factoryLists" :width="800" />

    <door-access-select
      v-model:value="showPersonSelect"
      v-model:selected="selectedDoors"
      :multipleNum="1"
      placeholder="请选择门禁"
      @ok="buidlSure"
      @exceed="exceed"
    />
    <door-access-select
      v-model:value="showTableDeviceSelect"
      v-model:selected="tableDeviceSelectedDoors"
      :multipleNum="1"
      placeholder="请选择门禁"
      @ok="buidlSure"
      @exceed="exceed"
      :disabled="true"
    />
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
  import { tableList, Api, fieldChildTree, cancelPower } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { Modal } from 'ant-design-vue';
  import DoorAccessSelect from '/@/components/DoorAccessSelect/index.vue';
  import { useRouter, useRoute } from 'vue-router';
  const route = useRoute();
  const router = useRouter();
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
      title: '员工最近一次厂区权限',
      immediate: false,
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
      beforeFetch(data) {
        const searchValues = Object.values(data).filter((item) => item);
        console.log('请求参数==', searchValues, Object.values(data));
        if (searchValues.length <= 4) {
          createMessage.warning('请输入查询条件');
          throw new Error('123');
        }
      },
    },
  });

  //注册table数据
  const [registerTable, { getForm, reload, updateTableDataRecord }] = tableContext;

  // 获取厂区列表
  const factoryLists = ref<Array<Object>>([]);
  async function getFactoryList() {
    const res = await fieldChildTree({ fieldType: 'factory' });
    console.log('获取到得厂区==', res);
    factoryLists.value = res;
  }
  getFactoryList();

  /**
   * 列表操作栏
   */
  function getTableAction(record: EditRecordRow): ActionItem[] {
    return [
      {
        label: '查看',
        onClick: handleEdit.bind(null, record),
      },
    ];
  }

  /**
   * 编辑事件
   */
  async function handleEdit(record: Recordable) {
    console.log('楼栋数据==', record);
    openModal(true, {
      record: record,
      isUpdate: true,
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
      content: '是否确认注销',
      async onOk() {
        await cancelPower({ empNo: record.empNo, userType: record.userType });
        createMessage.success('提交成功');
        reload();
      },
      onCancel() {},
    });
  }

  function permissionChange() {
    router.push('gateAccessRequestProcess/form');
  }
  //门禁展示
  const showTableDeviceSelect = ref(false);
  const tableDeviceSelectedDoors = ref([]);
  function showDevice(record) {
    tableDeviceSelectedDoors.value = record.deviceName
      ? record.deviceName.split(',').map((item, index) => {
          return {
            title: item,
            key: record.deviceCode.split(',')[index],
          };
        })
      : [];
    showPersonSelect.value = true;
  }

  //门禁选择
  const showPersonSelect = ref(false);
  const selectedDoors = ref([]);

  function choseBuilding() {
    console.log('增加', selectedDoors.value);
    showPersonSelect.value = true;
  }

  function buidlSure(buildList) {
    console.log('门禁选择==', buildList);
    if (buildList.length > 0) {
      getForm().setFieldsValue({ buildName: buildList[0].title, buildCode: buildList[0].key });
    } else {
      getForm().setFieldsValue({ buildName: '', buildCode: '' });
    }
  }

  function hasAuth(code) {
    return hasPermission(code);
  }
</script>

<style lang="less">
  .blacklistUpload {
    padding: 10px;
  }
</style>
