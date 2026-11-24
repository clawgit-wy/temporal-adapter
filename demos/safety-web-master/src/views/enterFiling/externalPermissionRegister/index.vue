<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <div class="flex flex-row justify-between items-center" style="width: 100%">
          <div class="text-sm text-gray-500" style="color: rgb(80, 140, 238)">
            注意：外部供应商在外网注册并由内部对接人授权后，可获得批量备案申请权限。外网地址：<a
              :href="computedRegisterUrl"
              target="_blank"
              style="color: #1890ff; text-decoration: underline"
              >{{ computedRegisterUrl }}</a
            >
          </div>
          <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls">导出</a-button>
        </div>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, TableAction, ActionItem } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns, searchFormSchema } from './register.data';
  import { list, deleteRecord, approveOrReject, Api } from './register.api';
  import { computed } from 'vue';

  defineOptions({ name: 'ExternalPermissionRegister' });

  const { createMessage, createConfirm } = useMessage();
  const computedRegisterUrl = computed(() => {
    if (import.meta.env.PROD) {
      return 'https://anrept.yutong.com/#/register';
    } else {
      return 'http://anrepttest.yutong.com/#/register';
    }
  });
  const { prefixCls, tableContext, onExportXls } = useListPage({
    designScope: 'external-permission-register',
    tableProps: {
      api: list,
      columns,
      size: 'middle',
      formConfig: { schemas: searchFormSchema },
      showIndexColumn: true,
      fetchSetting: {
        pageField: 'pageNum',
        sizeField: 'pageSize',
        listField: 'dataList',
        totalField: 'total',
      },
      actionColumn: {
        width: 120,
        title: '操作',
        fixed: 'right',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
    },
    exportConfig: {
      url: Api.export,
      name: '外部备案权限注册表',
    },
  });
  const [registerTable, { reload }] = tableContext;

  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    const actions: ActionItem[] = [];

    // 根据状态显示不同操作
    if (record.status == 20) {
      actions.push({
        label: '通过',
        color: 'info',
        onClick: handlePass.bind(null, record),
      });
      actions.push({
        label: '驳回',
        color: 'info',
        onClick: handleReject.bind(null, record),
      });
    } else {
      actions.push({
        label: '删除',
        color: 'error',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
        },
      });
    }

    return actions;
  }

  /**
   * 通过审批
   */
  async function handlePass(record: any) {
    createConfirm({
      iconType: 'warning',
      title: '确认操作',
      content: '确定通过该申请吗？',
      onOk: async () => {
        try {
          await approveOrReject({ id: record.id, status: 30, fdPhone: record.fdPhone });
          createMessage.success('审批通过');
          reload();
        } catch (error) {
          console.error('操作失败:', error);
          createMessage.error('操作失败');
        }
      },
    });
  }

  /**
   * 驳回审批
   */
  async function handleReject(record: any) {
    createConfirm({
      iconType: 'warning',
      title: '确认操作',
      content: '确定驳回该申请吗？',
      onOk: async () => {
        try {
          await approveOrReject({ id: record.id, status: 10, fdPhone: record.fdPhone });
          createMessage.success('已驳回');
          reload();
        } catch (error) {
          console.error('操作失败:', error);
          createMessage.error('操作失败');
        }
      },
    });
  }

  /**
   * 删除记录
   */
  async function handleDelete(record: any) {
    try {
      const res = await deleteRecord({ id: record.id });
      console.log(res, 'res');
      createMessage.success(res?.msg || '删除成功');
      reload();
    } catch (error) {
      console.error('删除失败:', error);
    }
  }

  /**
   * 获取状态颜色
   */
  function getStatusColor(status: string) {
    const colorMap = {
      待审批: 'processing',
      驳回: 'error',
      通过: 'success',
    };
    return colorMap[status] || 'default';
  }
</script>
