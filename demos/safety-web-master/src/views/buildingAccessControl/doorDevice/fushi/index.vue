<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button
          type="primary"
          preIcon="ant-design:import-outlined"
          v-if="hasPermission('buildingAccess:deviceManage:fsDevice:export')"
          @click="onExportXls()"
          >导出</a-button
        >
      </template>
      <template #toolbar>
        <a-button
          type="primary"
          preIcon="ant-design:import-outlined"
          v-if="hasPermission('buildingAccess:deviceManage:fsDevice:handUpdata')"
          @click="handUpData"
          >手动更新</a-button
        >
      </template>
      <template #deviceCategory="{ record, text }">
        <Select
          v-model:value="record.deviceCategory"
          :options="deviceTypeOptions"
          @change="(value) => handleEmpowerTypeRecordChange(record, value)"
          placeholder="请选择"
          style="width: 200px"
        />
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
  //ts语法
  import { ref, unref } from 'vue';
  import { BasicTable, TableAction, ActionItem, EditRecordRow } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns, deviceTypeOptions } from './config';
  import { tableList, Api, updateAndSaveToCrk, updateDeviceType } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { Modal, Select } from 'ant-design-vue';
  const factoryLists = ref<any>([]);
  // 消息
  const { createMessage } = useMessage();
  const userStore: any = useUserStore().getUserInfo;
  const { hasPermission } = usePermission();

  //新增/编辑弹窗
  const [registerScmModal, { openModal: openModal }] = useModal();

  // 列表页面公共参数、方法
  const { onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'goodsOut-list',
    tableProps: {
      title: '富士设备分类',
      api: tableList,
      columns: columns,
      size: 'small',
      useSearchForm: false,
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
      afterFetch(value) {
        console.log('列表返回值==', value);
        return value.map((item) => {
          return {
            ...item,
            deviceCategory: undefined,
          };
        });
      },
    },
    exportConfig: {
      url: Api.export,
      name: '富士设备分类',
    },
  });

  //注册table数据
  const [registerTable, { reload, updateTableDataRecord }] = tableContext;

  /**
   * 列表操作栏
   */
  function getTableAction(record: EditRecordRow): ActionItem[] {
    return [
      {
        label: '提交',
        onClick: handleSubmit.bind(null, record),
      },
    ];
  }

  // 手动更新
  const handUpData = async () => {
    let res = await updateAndSaveToCrk({});
    console.log('手动更新==', res);
    if (!res) {
      reload();
      createMessage.success('更新成功');
    } else {
      createMessage.error('操作失败');
    }
  };

  function handleEmpowerTypeRecordChange(record, value) {
    console.log('类型选择==', record, value);
  }

  /**
   * 提交事件
   */
  async function handleSubmit(record: Recordable) {
    console.log('楼栋数据==', record);
    if (!record.deviceCategory) {
      createMessage.error('请选择设备类型再提交');
      return;
    }
    Modal.confirm({
      title: '提示',
      content: '提交后不可变更，请确认后再提交',
      async onOk() {
        let params = [{ deviceCategory: record.deviceCategory, id: record.id }];
        let res = await updateDeviceType(params);
        console.log('提交结果==', res);
        if (res) {
          createMessage.success('提交成功');
          reload();
        } else {
          createMessage.error(res.mes || '操作失败');
        }
      },
      onCancel() {},
    });
  }
</script>

<style lang="less">
  .blacklistUpload {
    padding: 10px;
  }
</style>
