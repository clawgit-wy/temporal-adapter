<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <div class="tips"> 注意：终止后将停止为组织内新增人员自动授权 </div>
      </template>
      <!--来源列插槽-->
      <template #source="{ text, record }">
        <span style="color: blue; cursor: pointer" @click="handleSourceClick(record)">{{ text }}</span>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>

      <!-- 流程链接插槽 -->
      <template #processLink="{ record }">
        <span v-if="record.processId" class="process-link" @click="navigateToProcessDetail(record.processId, record.bpmFlowId, 'autoAuthOrg', '2')">
          {{ record.processId }}
        </span>
        <span v-else>-</span>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="buildingAccessControl-autoAuthOrganization" setup>
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useMessage } from '/@/hooks/web/useMessage';
  import * as apiModule from './autoAuthOrg.api';
  import { columns, searchFormSchema } from './autoAuthOrg.data';

  const { createConfirm, createMessage } = useMessage();
  import { useProcessNavigation } from '/@/views/buildingAccessControl/hooks/useProcessNavigation';
  const { navigateToProcessDetail } = useProcessNavigation();
  // 列表页面配置
  const tableConfig = {
    title: '自动授权组织列表',
    api: apiModule.listAutoAuthOrg,
    columns,
    pagination: {
      pageSize: 15,
      showSizeChanger: false,
      showQuickJumper: false,
    },
    formConfig: {
      schemas: searchFormSchema,
      fieldMapToTime: [['timeRange', ['beginTime', 'endTime'], 'YYYY-MM-DD']],
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
      width: 150,
      title: '操作',
      fixed: 'right',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  };

  // 列表页面公共参数、方法
  const { prefixCls, tableContext } = useListPage({
    designScope: 'buildingAccessControl-autoAuthOrg',
    tableProps: tableConfig,
  });

  // 注册 ListTable
  const [registerTable, { reload }] = tableContext;

  // 终止自动授权
  const terminationAction = async (record) => {
    createConfirm({
      iconType: 'warning',
      title: '确认终止?',
      content: '',
      onOk: async () => {
        await apiModule.terminateAutoAuth(record.id);
        createMessage.success('终止成功');
        reload();
      },
    });
  };

  // 操作栏配置
  const getTableAction = (record): ActionItem[] => {
    return [
      {
        label: '终止',
        onClick: () => terminationAction(record),
      },
    ];
  };

  const handleSourceClick = (record: any) => {
    console.log('点击了流程ID:', record);
    // 这里可以添加具体的点击事件处理逻辑
  };
</script>

<style lang="less" scoped>
  .tips {
    color: blue;
    font-size: 14px;
  }

  .process-link {
    color: blue;
    cursor: pointer;
  }
</style>
