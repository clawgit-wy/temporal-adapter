<template>
  <div :class="prefixCls">
    <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
      <a-tab-pane key="device" tab="监控设备基础信息">
        <BasicTable @register="registerDeviceTable">
          <template #tableTitle>
            <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleAdd('device')">添加</a-button>
          </template>
          <template #action="{ record }">
            <TableAction :actions="getTableAction(record, 'device')" />
          </template>
        </BasicTable>
      </a-tab-pane>
      <a-tab-pane key="threshold" tab="人脸对比阈值配置">
        <BasicTable @register="registerThresholdTable">
          <template #tableTitle>
            <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleAdd('threshold')">添加</a-button>
          </template>
          <template #action="{ record }">
            <TableAction :actions="getTableAction(record, 'threshold')" />
          </template>
        </BasicTable>
      </a-tab-pane>
    </a-tabs>
    <HikDeviceConfigModal @register="registerModal" @success="handleModalSuccess" />
  </div>
</template>

<script lang="ts">
  export default {
    name: 'AccessAnalysisHikDeviceConfig',
  };
</script>

<script lang="ts" setup>
  import { onMounted, ref } from 'vue';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getAreaCascaderProps, getAreaCodes, loadAreaOptions } from '../common/area';
  import { deleteSingle, deviceList, syncMonitorInfo, thresholdList } from './api';
  import { deviceColumns, deviceSearchFormSchema, thresholdColumns, thresholdSearchFormSchema } from './data';
  import HikDeviceConfigModal from './HikDeviceConfigModal.vue';

  type ConfigType = 'device' | 'threshold';

  const activeTab = ref<ConfigType>('device');
  const { createMessage } = useMessage();
  const [registerModal, { openModal }] = useModal();

  const { prefixCls, tableContext: deviceTableContext } = useListPage({
    designScope: 'access-analysis-hik-device-config',
    tableProps: {
      title: '监控设备基础信息',
      api: deviceList,
      columns: deviceColumns,
      formConfig: {
        schemas: deviceSearchFormSchema,
      },
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      showIndexColumn: true,
      ellipsis: true,
      fetchSetting: {
        pageField: 'pageNum',
        sizeField: 'pageSize',
        listField: 'dataList',
        totalField: 'total',
      },
      beforeFetch: normalizeAreaParams,
      actionColumn: {
        width: 140,
        title: '操作',
        dataIndex: 'action',
        fixed: 'right',
        slots: { customRender: 'action' },
      },
    },
  });

  const { tableContext: thresholdTableContext } = useListPage({
    designScope: 'access-analysis-hik-threshold-config',
    tableProps: {
      title: '人脸对比阈值配置',
      api: thresholdList,
      columns: thresholdColumns,
      formConfig: {
        schemas: thresholdSearchFormSchema,
      },
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      showIndexColumn: true,
      ellipsis: true,
      fetchSetting: {
        pageField: 'pageNum',
        sizeField: 'pageSize',
        listField: 'dataList',
        totalField: 'total',
      },
      actionColumn: {
        width: 140,
        title: '操作',
        dataIndex: 'action',
        fixed: 'right',
        slots: { customRender: 'action' },
      },
    },
  });

  const [registerDeviceTable, { reload: reloadDeviceTable }] = deviceTableContext;
  const [, { getForm: getDeviceForm }] = deviceTableContext;
  const [registerThresholdTable, { reload: reloadThresholdTable }] = thresholdTableContext;

  onMounted(async () => {
    await loadAreaOptions();
    await getDeviceForm().updateSchema({
      field: 'areaCode',
      componentProps: getAreaCascaderProps(),
    });
  });

  function handleTabChange(activeKey) {
    activeTab.value = activeKey as ConfigType;
  }

  function handleAdd(configType: ConfigType) {
    if (configType === 'device') {
      handleSyncMonitorInfo();
      return;
    }
    openModal(true, {
      title: '新增',
      isUpdate: false,
      showFooter: true,
      configType,
      record: {},
    });
  }

  function handleEdit(record, configType: ConfigType) {
    openModal(true, {
      title: '编辑',
      isUpdate: true,
      showFooter: true,
      configType,
      record,
    });
  }

  async function handleDelete(record, configType: ConfigType) {
    await deleteSingle({ id: record.id, configType });
    createMessage.success('删除成功');
    handleModalSuccess(configType);
  }

  async function handleSyncMonitorInfo() {
    await syncMonitorInfo();
    createMessage.success('操作成功');
    await reloadDeviceTable();
  }

  function handleModalSuccess(configType: ConfigType = activeTab.value) {
    if (configType === 'device') {
      reloadDeviceTable();
      return;
    }
    reloadThresholdTable();
  }

  function getTableAction(record, configType: ConfigType): ActionItem[] {
    if (configType === 'device') {
      return [{ label: '编辑', onClick: handleEdit.bind(null, record, configType) }];
    }
    return [
      { label: '编辑', onClick: handleEdit.bind(null, record, configType) },
      {
        label: '删除',
        color: 'error',
        popConfirm: { title: '删除后不可恢复，请确认', confirm: handleDelete.bind(null, record, configType) },
      },
    ];
  }

  function normalizeAreaParams(params) {
    const { areaCode, ...rest } = params || {};
    const { factoryCode, buildCode } = getAreaCodes(areaCode);
    return {
      ...rest,
      factoryCode,
      buildCode,
    };
  }
</script>

<style lang="less" scoped>
  @import 'index';
</style>
