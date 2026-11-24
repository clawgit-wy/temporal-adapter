<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <!-- <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd">添加</a-button> -->
        <a-button
          type="primary"
          preIcon="ant-design:export-outlined"
          @click="onExportXls"
          v-if="hasAuth('buildingAccessControl:baseInfo:accessManage:export')"
        >
          导出</a-button
        >
        <a-button
          type="primary"
          preIcon="ant-design:import-outlined"
          @click="showModal"
          v-if="hasAuth('buildingAccessControl:baseInfo:accessManage:import')"
        >
          导入</a-button
        >
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <AccessManageModal @register="registerModal" @success="reload" />
    <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
      <div class="accessManageUpload">
        <p>请按照模板规范填写，否则无法正确导入<a-button @click="ondownLoadXls">下载模板</a-button></p>
        <p>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">上传</j-upload-button>
        </p>
      </div>
    </a-modal>
    <door-access-select
      v-model:value="showPersonSelect"
      v-model:selected="selectedDoors"
      placeholder="请选择门禁"
    />
  </div>
</template>

<script lang="ts" name="buildingAccessControl-baseInfo-accessManage" setup>
  import { ref } from 'vue';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import AccessManageModal from './AccessManageModal.vue';
  import { Api, list, deleteSingle, download } from './accessManage.api';
  import { columns, searchFormSchema } from './accessManage.data';
  import { downloadByData } from '/@/utils/file/download';
  // import PersonSelectTree from '/@/components/PersonSelectTree/index.vue';
  import DoorAccessSelect from '/@/components/DoorAccessSelect/index.vue';
  import { debounce } from 'lodash-es';

  const showPersonSelect = ref(false);
  const selectedDoors = ref([]);

  const openUpload = ref<boolean>(false);

  interface AccessManageRecord {
    deviceCode: string;
    deviceName: string;
    factoryName: string;
    dutyDeptList: Array<{
      id: string;
      employeeCode: string;
      employeeName: string;
      dutyDeptCode: string;
      dutyDeptName: string;
      [key: string]: any;
    }>;
    [key: string]: any;
  }

  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'buildingAccessControl-baseInfo-accessManage',
    tableProps: {
      title: '门禁管理列表数据',
      isTreeTable: true,
      api: async (params) => {
        // 调用原始API获取数据
        const result = await list(params);
        let res = result.dataList || []
        res = res.map(item => {
          if (item.dutyDeptList && item.dutyDeptList.length > 0) {
            const obj = {
              ...item
            }
            delete obj.dutyDeptList
            const children = item.dutyDeptList.map(duty=> {
              return {
                // ...obj,
                dutyDeptName: duty.dutyDeptName,
                employeeName: duty.employeeName,
              }
            })
            return {
              ...obj,
              children
            }
          }
          return {
            ...item
          }
        })
        console.log(res, '=======')
        return {...result, dataList:res};
      },
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
      indexColumnProps: {
        customCell: (record: any) => {
          return {
            rowSpan: record.rowSpan,
          };
        },
      },
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
      actionColumn: {
        width: 100,
        title: '操作',
        fixed: 'right',
        dataIndex: 'action',
        slots: { customRender: 'action' },
        customCell: (record: any) => {
          return {
            rowSpan: record.rowSpan,
          };
        },
      },
    },
    exportConfig: {
      url: Api.exportXls,
      name: '门禁管理列表',
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
    console.log('增加', selectedDoors.value);
    showPersonSelect.value = true;
    return;
    openModal(true, {
      title: '添加门禁',
      isUpdate: false,
      showFooter: true,
      record: {},
    });
  }

  // 原始编辑函数
  function editRecord(record) {
    openModal(true, {
      title: '编辑门禁',
      isUpdate: true,
      record: record,
      showFooter: true,
    });
  }

  // 使用防抖包装的编辑函数
  const onEdit = debounce(editRecord, 300);

  // 原始删除函数
  async function deleteRecord(record) {
    if (record) {
      // 确保使用正确的ID
      await deleteSingle({ id: String(record.id) }, true);
      await reload();
    }
  }

  // 使用防抖包装的删除函数
  const onDelete = debounce(deleteRecord, 300);

  // 确保lint工具识别到onDelete被使用
  const actionHandlers = { onEdit, onDelete };

  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    // 只有在记录可见（即_rowSpan > 0）时才显示操作按钮
    if (record.deviceName) {
      return [
        // { label: '编辑', onClick: onEdit.bind(null, record), auth: 'buildingAccessControl:baseInfo:accessManage:edit' },
        { label: '编辑', onClick: actionHandlers.onEdit.bind(null, record) },
        // { label: '删除', onClick: actionHandlers.onDelete.bind(null, record), color: 'error' },
      ];
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
    // reload();
  }

  function hasAuth(code) {
    return hasPermission(code);
  }

  async function ondownLoadXls() {
    const res = await download({});
    downloadByData(res, '门禁管理列表.xlsx');
  }
</script>

<style lang="less" scoped>
  .accessManageUpload {
    padding: 10px;
  }
</style>
