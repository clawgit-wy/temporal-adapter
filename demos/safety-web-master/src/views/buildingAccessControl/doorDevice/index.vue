<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button
          type="primary"
          preIcon="ant-design:import-outlined"
          @click="onExportXls()"
          v-if="hasPermission('buildingAccess:deviceManage:device:export')"
          >导出</a-button
        >
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
    <CloneScmModal @register="registerCloneScmModal" @success="handleCloneSuccess" />
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
  //ts语法
  import { ref, unref } from 'vue';
  import { cloneDeep } from 'lodash-es';
  import { BasicTable, TableAction, ActionItem, EditRecordRow } from '/@/components/Table';
  import ScmModal from './scm.modal.vue';
  import CloneScmModal from './cloneScm.modal.vue';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns, searchFormSchema } from './config';
  import { tableList, Api, fieldChildTree } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { downloadByData } from '/@/utils/file/download';
  const factoryLists = ref<any>([]);
  // 消息
  const { createMessage } = useMessage();
  const userStore: any = useUserStore().getUserInfo;
  const openUpload = ref<boolean>(false);
  const { hasPermission } = usePermission();

  //新增/编辑弹窗
  const [registerScmModal, { openModal: openModal }] = useModal();
  const [registerCloneScmModal, { openModal: openCloneModal }] = useModal();

  // 列表页面公共参数、方法
  const { onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'goodsOut-list',
    tableProps: {
      title: '楼宇门禁设备',
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
    exportConfig: {
      url: Api.exportXls,
      name: '楼宇门禁设备',
    },
  });

  //注册table数据
  const [registerTable, { reload, updateTableDataRecord }] = tableContext;

  // 获取厂区列表
  async function getFactoryList() {
    const res = await fieldChildTree({ fieldType: 'factory' });
    factoryLists.value = res;
    console.log(factoryLists, 'factoryListsfactoryLists');
  }
  getFactoryList();

  /**
   * 列表操作栏
   */
  function getTableAction(record: EditRecordRow): ActionItem[] {
    return [
      {
        label: '编辑',
        ifShow: () => hasPermission('buildingAccess:deviceManage:device:edit'),
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '克隆权限',
        ifShow: () => hasPermission('buildingAccess:deviceManage:device:clone'),
        onClick: handleClone.bind(null, record),
      },
    ];
  }

  /**
   * 编辑事件
   */
  async function handleEdit(record: Recordable) {
    const building = await fieldChildTree({ fieldType: record.anFactoryCode });
    console.log('楼栋数据==', record, building);
    openModal(true, {
      record: record,
      isUpdate: true,
      showFooter: true,
      factoryLists: factoryLists.value,
      buildingLists: building,
    });
  }

  /**
   * 克隆权限
   */
  async function handleClone(record: Recordable) {
    console.log('楼栋数据==', record);

    openCloneModal(true, {
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
  /**
   * 成功回调
   * @param {number} isUpdate 操作类型：0 无操作 1 新增/新增加编辑 2 编辑
   * @param {object} values 编辑的数据
   */
  function handleCloneSuccess({ isUpdate, values }) {
    console.log('弹窗回调', isUpdate, values);
    // if (isUpdate == 2) {
    //   updateTableDataRecord(values.id, values);
    // } else if (isUpdate == 1) {
    //   reload();
    // }
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
    downloadByData(res, '审核人权限配置列表.xlsx');
  }
</script>

<style lang="less">
  .blacklistUpload {
    padding: 10px;
  }
</style>
