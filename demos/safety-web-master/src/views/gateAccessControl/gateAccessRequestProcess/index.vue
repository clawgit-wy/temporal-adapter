<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <template #processCode="{ record, text }">
        <span style="color: blue; cursor: pointer" @click="navigateToProcessDetail(record.id, record.bpmFlowId, 'gateAccessRequestProcess')">{{ text }}</span>
      </template>
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" @click="handleCreate"> <Icon icon="ant-design:plus-outlined" /> 新增申请 </a-button>
        <!-- <a-button type="primary" @click="handleMain"> <Icon icon="ant-design:plus-outlined" /> 主页 </a-button> -->
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="gateAccessControl-gateAccessRequestProcess" setup>
  import { BasicTable, TableAction, ActionItem } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { Api, list } from './gateAccessRequest.api';
  import { columns, searchFormSchema, statusOptions } from './gateAccessRequest.data';
  import { useRouter } from 'vue-router';
  import { ref, onMounted } from 'vue';
  import { addFullParam } from '/@/utils/router/fullParam';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { Icon } from '/@/components/Icon';
  import { useUserStore } from '/@/store/modules/user';
  import { useProcessNavigation } from '/@/views/gateAccessControl/gateAccessRequestProcess/useProcessNavigation';
  const { navigateToProcessDetail } = useProcessNavigation();
  // 类型定义
  interface RecordType {
    id: string;
    status: string;
    departmentAuditor?: string;
    bpmFlowId?: string;
    [key: string]: any;
  }

  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'gateAccessControl-gateAccessRequestProcess',
    tableProps: {
      title: '大门权限申请流程明细列表',
      api: list,
      columns: columns,
      pagination: {
        pageSize: 15,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['createTimeRange', ['createStartTime', 'createEndTime'], 'YYYY-MM-DD']],
      },
      showIndexColumn: true,
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
        width: 150,
        title: '操作',
        fixed: 'right',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
    },
  });

  // 注册 ListTable
  const [registerTable, { reload }] = tableContext;
  const router = useRouter();

  onMounted(async () => {
    const userStore = useUserStore();
  });

  // 创建新申请
  function handleCreate() {
    router.push(addFullParam({
      path: '/gateAccessRequestProcess/main',
    }));
  }

  // 审批
  function handleApprove(record: RecordType) {
    navigateToProcessDetail(record.id, record.bpmFlowId, 'gateAccessRequestProcess')
  }
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    if (record.status !== '20') {
      return [];
    }
    console.log('record', record);
    const actions: ActionItem[] = [{ icon: 'ant-design:audit-outlined', label: '审批', onClick: handleApprove.bind(null, record) }];
    return actions;
  }
  function handleMain() {
    router.push({
      path: '/gateAccessRequestProcess/main',
    });
  }
</script>

<style lang="less" scoped>
  .gate-access-process {
    padding: 20px;
  }
</style>
