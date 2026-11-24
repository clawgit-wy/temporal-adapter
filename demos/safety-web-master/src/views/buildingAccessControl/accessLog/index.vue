<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出 </a-button>
        <a-button type="primary" @click="handleManualDownload"> 手动下发 </a-button>
      </template>

      <!--来源列插槽-->
      <template #source="{ record }">
        <template v-if="record.sourceType == '2'"> 人员变更确认 </template>
        <!-- <span
          v-else
          style="color: blue; cursor: pointer"
          @click="navigateToProcessDetail(record.targetId, record.bpmFlowId, 'accessLog', record.roleType)"
          >{{ record.targetId }}</span
        > -->
        <span v-else style="color: blue; cursor: pointer" @click="handleSourceClick(record)">{{ record.targetId }}</span>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="buildingAccessControl-accessLog" setup>
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { Api, list, downBySelf } from './accessLog.api';
  import { columns, searchFormSchema } from './accessLog.data';
  import { useMessage } from '/@/hooks/web/useMessage';
  import dayjs from 'dayjs';
  import { useProcessNavigation } from '/@/views/buildingAccessControl/hooks/useProcessNavigation';
  const { navigateToProcessDetail } = useProcessNavigation();
  import { useProcessNavigation as useGateProcessNavigation } from '/@/views/gateAccessControl/gateAccessRequestProcess/useProcessNavigation';
  const { navigateToProcessDetail: navigateToGateProcessDetail } = useGateProcessNavigation();

  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'buildingAccessControl-accessLog',
    tableProps: {
      title: '门禁授权日志列表',
      api: list,
      columns: columns,
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['createTimeRange', ['createTimeBegin', 'createTimeEnd'], 'YYYY-MM-DD']],
        autoSubmitOnEnter: true,
        submitOnReset: true,
        submitOnChange: true,
        defaultValues: {
          createTimeRange: [dayjs().subtract(3, 'month').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')],
        },
      },
      showIndexColumn: false, // 已经在列中定义了序号列
      ellipsis: true,
      showActionColumn: false,
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
    },
    exportConfig: {
      url: Api.exportXls,
      name: '门禁授权日志',
    },
  });

  // 注册 ListTable
  const [registerTable, { reload }] = tableContext;
  const { hasPermission: hasAuth } = usePermission();
  const { createMessage, createConfirm } = useMessage();

  // 手动下发
  function handleManualDownload() {
    createConfirm({
      iconType: 'warning',
      title: '确认手动下发',
      content: '是否确认进行手动下发操作？',
      async onOk() {
        try {
          await downBySelf();
          createMessage.success('下发操作已提交');
          reload();
        } catch (error) {
          createMessage.error('下发失败，请稍后重试');
        }
      },
    });
  }

  const handleSourceClick = (record: any) => {
    console.log('点击了流程ID:', record.bpmFlowId);
    // 这里可以添加具体的点击事件处理逻辑
    if (!record.modelId) {
      navigateToProcessDetail(record.targetId, record.bpmFlowId, 'accessLog', record.roleType);
    } else {
      navigateToGateProcessDetail(record.targetId, '', 'gateAccessRequestProcess');
    }
  };
</script>

<style lang="less" scoped>
  .access-log {
    padding: 20px;
  }
</style>
