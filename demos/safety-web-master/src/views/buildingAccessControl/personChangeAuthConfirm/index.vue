<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #headerTop>
        <div class="notice-container" v-if="currentCategory === '2'">
          <div class="notice-title">注意：</div>
          <div class="notice-items">
            <div class="notice-item">
              <span class="notice-item-number">1、</span>
              <span class="notice-item-content">门禁管理员可随时进行员工长期未进出门禁权限确认，并对本部门人员门禁权限负责</span>
            </div>
            <div class="notice-item">
              <span class="notice-item-number">2、</span>
              <span class="notice-item-content"
                >当员工授权门禁存在长期未进出时长达{{
                  cancelAuthDays
                }}天时，系统自动推送员工确认本人外部门管理门禁权限，确认保留的长期未进出门禁时长将累计。</span
              >
            </div>
            <div class="notice-item">
              <span class="notice-item-number">3、</span>
              <span class="notice-item-content">门禁管理员需确认员工本部门门禁权限，如果超时未确认，本部门门禁权限继续保留。</span>
            </div>
            <div class="notice-item">
              <span class="notice-item-number">4、</span>
              <span class="notice-item-content">将人员加入白名单将不再监控长期未进出门禁情况</span>
            </div>
          </div>
        </div>
        <div class="notice-container" v-else>
          <div class="notice-title"> 注意： </div>
          <div class="notice-item">
            <span class="notice-item-content">请在变更时间+7天内确认完毕，如有超时未确认的，自动取消授权并移除白名单</span>
          </div>
        </div>
      </template>
      <template #toolbar>
        <template v-if="isDepartmentChange">
          <a-button
            type="primary"
            @click="handleConfirmBatch('一键保留授权')"
            v-if="hasAuth('buildingAccess:personChangeAuthConfirm:oneKeyReserveAuth')"
            >一键保留授权</a-button
          >
          <a-button
            type="primary"
            danger
            @click="handleConfirmBatch('一键取消授权')"
            v-if="hasAuth('buildingAccess:personChangeAuthConfirm:oneKeyCancelAuth')"
            >一键取消授权</a-button
          >
          <a-button
            type="primary"
            @click="handleConfirmBatch('一键保留白名单')"
            v-if="hasAuth('buildingAccess:personChangeAuthConfirm:oneKeyReserveWhitelist')"
            >一键保留白名单</a-button
          >
          <a-button
            type="primary"
            danger
            @click="handleConfirmBatch('一键取消白名单')"
            v-if="hasAuth('buildingAccess:personChangeAuthConfirm:oneKeyCancelWhitelist')"
            >一键取消白名单</a-button
          >
        </template>
        <template v-else>
          <a-button
            type="primary"
            danger
            @click="handleConfirmBatch('一键取消授权')"
            v-if="hasAuth('buildingAccess:personChangeAuthConfirm:oneKeyCancelAuthLongTime')"
            >一键取消授权</a-button
          >
          <a-button
            type="primary"
            @click="handleConfirmBatch('一键加入白名单')"
            v-if="hasAuth('buildingAccess:personChangeAuthConfirm:oneKeyAddWhitelistLongTime')"
            >一键加入白名单</a-button
          >
        </template>
      </template>
      <template #tableTitle>
        <a-radio-group :value="currentCategory" @change="categoryChange">
          <a-radio-button value="1">部门人员变动</a-radio-button>
          <a-radio-button value="2">长期未进出</a-radio-button>
        </a-radio-group>
      </template>
      <template #form-noAccessDays="{ model }: { model: any }">
        <div style="display: flex; align-items: center">
          <a-input-number v-model:value="model.minDay" style="width: 100px" placeholder="最小天数" />
          <span style="margin: 0 8px">至</span>
          <a-input-number v-model:value="model.maxDay" style="width: 100px" placeholder="最大天数" />
        </div>
      </template>
      <template #action="{ record }">
        <TableAction :actions="createActions(record)" />
      </template>
    </BasicTable>
    <PersonChangeAuthConfirmModal @register="registerModal" @success="handleSuccess" @error="handleError" />
    <WhitelistModal @register="registerWhitelistModal" @success="handleSuccess" @error="handleError" />
    <CancelAuthModal @register="registerCancelAuthModal" @success="handleSuccess" @error="handleError" />
  </div>
</template>

<script lang="ts" setup>
  import { usePermission } from '/@/hooks/web/usePermission';
  import { BasicTable, TableAction, BasicColumn } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { columns, longTimeNoAccessColumns, searchFormSchema } from './personChangeAuthConfirm.data';
  import {
    getPersonChangeList,
    getLongTimeNoAccessList,
    cancelBuild,
    batchDealBuild,
    batchDealWhite,
    dealBuild,
  } from './personChangeAuthConfirm.api';
  import { getAuthConfig } from '/@/views/systemd/authConfig/authConfig.api';

  import PersonChangeAuthConfirmModal from './PersonChangeAuthConfirmModal.vue';
  import WhitelistModal from './WhitelistModal.vue';
  import CancelAuthModal from './CancelAuthModal.vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { ref, computed, onMounted } from 'vue';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { ActionItem } from '/@/components/Table';
  const { hasPermission } = usePermission();
  import { useRoute } from 'vue-router';
  const route = useRoute();
  const { createMessage, createConfirm } = useMessage();
  const currentCategory = ref('1');
  //授权配置-长期未进出取消授权天数
  let cancelAuthDays = ref(0);
  if (route.query.tab === '2') {
    currentCategory.value = '2';
  }
  //是否是 部门人员变动
  const isDepartmentChange = computed(() => currentCategory.value === '1');
  function hasAuth(code) {
    return hasPermission(code);
  }
  // 使用 useListPage 替代原有的 useTable
  const { tableContext } = useListPage({
    designScope: 'building-access-control',
    tableProps: {
      title: '',
      api: (parmas) => {
        if (isDepartmentChange.value) {
          return getPersonChangeList(parmas);
        } else {
          return getLongTimeNoAccessList(parmas);
        }
      },
      columns,
      // size: 'small',
      formConfig: {
        labelWidth: 150,
        schemas: searchFormSchema(!isDepartmentChange.value),
        // 设置表单项的全局布局，确保每行有4个表单项
        baseColProps: { span: 8 },
        // 表单项布局模式
        layout: 'horizontal',
        // 自动收起超出的行
        autoAdvancedLine: 1,
        // 显示展开/收起按钮
        showAdvancedButton: true,
        autoAdvancedCol: 2,
        // 操作按钮位置调整
        actionColOptions: {
          span: 8,
          style: { textAlign: 'left' },
        },
      },
      beforeFetch: (params) => {
        delete params.noAccessDays;
        console.log('params的值是', params);

        return params;
      },
      clearSelectOnPageChange: true,
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      useSearchForm: true,
      showTableSetting: true,
      bordered: true,
      showIndexColumn: false,
      rowKey: 'id',
      rowSelection: {
        type: 'checkbox',
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
        width: 360,
        title: '操作',
        dataIndex: 'action',
        fixed: 'right',
        align: 'center',
        slots: { customRender: 'action' },
      },
    },
  });
  function createActions(record: Recordable): ActionItem[] {
    if (currentCategory.value === '1') {
      return [
        {
          label: '保留权限',
          onClick: handleRecord.bind(null, record, '保留授权'),
        },
        {
          label: '取消授权',
          color: 'error',
          onClick: handleRecord.bind(null, record, '取消授权'),
        },
        {
          label: '保留白名单',
          onClick: handleRecord.bind(null, record, '保留白名单'),
          ifShow: () => {
            return record.isWhite == '是';
          },
        },
        {
          label: '取消白名单',
          color: 'error',
          onClick: handleRecord.bind(null, record, '取消白名单'),
          ifShow: () => {
            return record.isWhite == '是';
          },
        },
      ];
    } else {
      return [
        {
          label: '取消授权',
          color: 'error',
          onClick: handleRecord.bind(null, record, '取消授权'),
        },
        {
          label: '加入白名单',
          onClick: handleRecord.bind(null, record, '加入白名单'),
        },
      ];
    }
  }
  // 从 tableContext 中解构出需要的方法和属性
  const [registerTable, { reload, setColumns, clearSelectedRowKeys, setProps, getForm }, { rowSelection, selectedRows }] = tableContext;
  const [registerModal] = useModal();
  const [registerWhitelistModal, { openModal: openWhitelistModal }] = useModal();
  const [registerCancelAuthModal, { openModal: openCancelAuthModal }] = useModal();

  // 查看详情
  function handleRecord(record: Recordable, actionType: string) {
    if (actionType === '保留授权') {
      createConfirm({
        iconType: 'warning',
        title: '确认授权',
        content: `新部门门禁管理员对员工门禁授权负责！<br>
        如果员工有外部门管理的机密区和绝密区门禁权限，<br>
        将给对接部门门禁管理员触发待办确认。
        `,
        onOk: async () => {
          await dealBuild({
            changeId: record.id,
            keepIdStr: record.allPowerList?.map((power) => power.id).join(',') || '',
          });
          createMessage.success('操作成功');
          reload();
        },
      });
    } else if (actionType === '取消授权') {
      if (isDepartmentChange.value) {
        openCancelAuthModal(true, {
          title: '取消授权',
          record,
        });
        return;
      }
      handleCancelAuth(record.id, false);
    } else if (actionType === '保留白名单') {
      openWhitelistModal(true, {
        isDepartmentChange: isDepartmentChange.value,
        title: '保留白名单',
        actionType,
        selectedIds: record.id,
      });
    } else if (actionType === '加入白名单') {
      // 处理白名单逻辑
      openWhitelistModal(true, {
        actionType,
        selectedIds: record.id,
      });
    } else if (actionType === '取消白名单') {
      openWhitelistModal(true, {
        isDepartmentChange: isDepartmentChange.value,
        title: '取消白名单',
        actionType,
        selectedIds: record.id,
      });
    }
  }
  //处理取消授权
  async function handleCancelAuth(ids: string, isBatch: boolean) {
    createConfirm({
      iconType: 'warning',
      title: '确认取消授权',
      content: isBatch ? `确认批量取消已选择授权门禁?` : `确认取消此门禁授权?`,
      onOk: async () => {
        try {
          await cancelBuild({
            idStr: ids,
          });
          createMessage.success('取消授权成功');
          reload();
        } catch (error) {
          clearSelectedRowKeys();
        }
      },
    });
  }
  //部门人员变动 --  一键保留/取消收取
  async function handleBatchDealBuild(records: Recordable[], isSave: boolean) {
    try {
      await batchDealBuild({
        confirmList: records.map((record) => ({
          allPowerIdStr: record.allPowerList?.map((power) => power.id).join(',') || '',
          changeId: record.id,
          localPowerIdStr: record.localPowerList?.map((power) => power.id).join(',') || '',
        })),
        dealType: isSave ? 1 : 2, //1保留 2取消
      });
      createMessage.success('操作成功');
      reload();
    } catch (error) {
      clearSelectedRowKeys();
    }
  }
  // 批量确认
  async function handleConfirmBatch(actionType: string) {
    if (actionType === '一键保留授权') {
      createConfirm({
        iconType: 'warning',
        title: '确认授权',
        content: `新部门门禁管理员对员工门禁授权负责！<br>
        如果员工有外部门管理的机密区和绝密区门禁权限，<br>
        将给对接部门保密人触发待办确认。
        `,
        onOk: async () => {
          // 获取选中行的ID
          if (selectedRows.value.length === 0) {
            createMessage.warning('请选择需要操作的记录');
            return;
          }
          handleBatchDealBuild(selectedRows.value, true);
        },
      });
    } else if (actionType === '一键取消授权') {
      // 获取选中行的ID
      if (selectedRows.value.length === 0) {
        createMessage.warning('请选择需要操作的记录');
        return;
      }
      if (isDepartmentChange.value) {
        createConfirm({
          iconType: 'warning',
          title: '确认取消授权',
          content: `确认取消原有部门授权门禁（不包含本部门新增）?`,
          onOk: async () => {
            handleBatchDealBuild(selectedRows.value, false);
          },
        });
      } else {
        handleCancelAuth(selectedRows.value.map((row) => row.id).join(','), true);
      }
    } else if (actionType === '一键加入白名单') {
      // 获取选中行的ID
      if (selectedRows.value.length === 0) {
        createMessage.warning('请选择需要操作的记录');
        return;
      }

      // 打开白名单弹框
      openWhitelistModal(true, {
        actionType,
        selectedIds: selectedRows.value.map((row) => row.id).join(','),
      });
    } else if (actionType === '一键取消白名单' || actionType === '一键保留白名单') {
      if (selectedRows.value.length === 0) {
        createMessage.warning('请选择需要操作的记录');
        return;
      }
      // 打开白名单弹框
      openWhitelistModal(true, {
        isDepartmentChange: isDepartmentChange.value,
        title: actionType === '一键保留白名单' ? '保留白名单' : '取消白名单',
        actionType,
        selectedIds: selectedRows.value.map((row) => row.id).join(','),
      });
    }
  }

  // 操作成功后刷新表格
  function handleSuccess() {
    clearSelectedRowKeys();
    reload();
  }

  // 操作失败后刷新表格
  function handleError() {
    clearSelectedRowKeys();
  }

  // 切换类别
  async function categoryChange(target: any) {
    clearSelectedRowKeys();
    currentCategory.value = target.target.value;
    let currentColumns: BasicColumn[] = [];
    let actionColumnWidth = 180;
    if (currentCategory.value === '1') {
      currentColumns = columns;
      actionColumnWidth = 360;
    } else {
      currentColumns = longTimeNoAccessColumns;
      actionColumnWidth = 180;
    }

    // 更新列宽
    await setProps({
      actionColumn: {
        width: actionColumnWidth,
        title: '操作',
        dataIndex: 'action',
        fixed: 'right',
        align: 'center',
        slots: { customRender: 'action' },
      },
      // 更新API
      api: currentCategory.value === '1' ? getPersonChangeList : getLongTimeNoAccessList,
    });

    // 更新表单项
    const formInstance = getForm();
    // 重新获取搜索表单定义并更新表单结构
    const newSchemas = searchFormSchema(!isDepartmentChange.value);
    formInstance.updateSchema(newSchemas);

    await setColumns(currentColumns);
    reload();
  }
  //获取授权配置
  async function getAuthConfigResult() {
    const res = await getAuthConfig();
    console.log('res的值是', res);
    cancelAuthDays.value = res.cancelAccessNum && res.cancelAccessNum - 7 > 0 ? res.cancelAccessNum - 7 : 0;
  }
  onMounted(() => {
    getAuthConfigResult();
  });
</script>

<style lang="less" scoped>
  .notice-container {
    padding: 12px 16px;
    background-color: #f8f8f8;
    margin-bottom: 16px;
    font-size: 14px;
    line-height: 1;
    color: #333;

    .notice-title {
      font-weight: bold;
      color: #1890ff;
      margin-bottom: 8px;
    }

    .notice-items {
      padding-left: 8px;
    }

    .notice-item {
      display: flex;
      margin-bottom: 6px;

      &:last-child {
        margin-bottom: 0;
      }

      .notice-item-number {
        color: #1890ff;
        font-weight: bold;
        margin-right: 4px;
        flex-shrink: 0;
      }

      .notice-item-content {
        flex: 1;
        color: #1890ff;
      }
    }
  }
</style>
