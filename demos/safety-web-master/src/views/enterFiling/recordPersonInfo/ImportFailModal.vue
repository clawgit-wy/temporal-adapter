<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="批量导入结果弹窗"
    :width="1400"
    :okText="'发起二级黑名单入厂申请'"
    :showCancelBtn="false"
    @ok="handleOk"
    :showOkBtn="canApply"
    :closable="true"
    :maskClosable="false"
    :closeFunc="handleCloseConfirm"
  >
    <div class="import-fail-modal">
      <div class="summary-info">
        <span class="summary-text">{{ tipMsg }}</span>
      </div>

      <BasicTable @register="registerTable" :pagination="false">
        <template #tableTitle>
          <span>导入失败列表</span>
        </template>
        <template #toolbar>
          <a-button type="primary" class="custom-button" @click="handleExport">导出</a-button>
        </template>
      </BasicTable>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup name="ImportFailModal">
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { ActionItem, BasicTable, TableAction, useTable, BasicColumn } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { Api } from './api.ts';
  import { useRouter } from 'vue-router';
  const router = useRouter();
  const emit = defineEmits(['register', 'close']);
  const { createMessage, createConfirm } = useMessage();

  // 导入结果数据
  const importData = ref<any>({
    // successCount: 0,
    // failCount: 0,
    failList: [],
    msg: '',
  });

  const tipMsg = computed(() => importData.value.msg || '');
  const versionNo = computed(() => (importData.value.failList.length > 0 ? importData.value.failList[0].versionNo : ''));
  const canApply = computed(() => importData.value.failList.some((item) => item.failRemark.includes('二级黑名单')));

  // 表格列配置
  const columns: BasicColumn[] = [
    {
      title: '备案类型',
      dataIndex: 'recordType',
      width: 100,
    },
    {
      title: '姓名',
      dataIndex: 'name',
      width: 100,
    },
    {
      title: '公司名称',
      dataIndex: 'companyName',
      width: 150,
    },
    {
      title: '身份证号',
      dataIndex: 'cardNo',
      width: 180,
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      width: 120,
    },
    {
      title: '准入厂区',
      dataIndex: 'enterFactory',
      width: 200,
    },
    {
      title: '准入时间-始',
      dataIndex: 'enterStartTime',
      width: 160,
    },
    {
      title: '准入时间-终',
      dataIndex: 'enterEndTime',
      width: 160,
    },
    {
      title: '是否夜间作业',
      dataIndex: 'nightFlag',
      width: 100,
      customRender: ({ text }) => {
        console.log(text, String(text), 'text');
        return String(text) == '0' ? '否' : '是';
      },
    },
    {
      title: '备注',
      dataIndex: 'remark',
      width: 150,
    },
    {
      title: '失败原因',
      dataIndex: 'failRemark',
      width: 200,
      fixed: 'right',
    },
  ];

  const { onExportXls, tableContext } = useListPage({
    tableProps: {
      title: '',
      showActionColumn: false,
      showTableSetting: false,
      useSearchForm: false,
      ellipsis: true,
      columns,
      dataSource: [],
      showIndexColumn: false,
      bordered: true,
      size: 'small',
      scroll: { x: 1800, y: 300 },
    },
    exportConfig: {
      url: Api.exportFailUrl,
      name: '备案人员信息导入失败表',
      params: { versionNo: versionNo },
    },
  });
  //BasicTable绑定注册
  const [registerTable, { setTableData }] = tableContext;

  // 注册弹窗
  const [registerModal, { closeModal }] = useModalInner((data) => {
    console.log('接收到的导入失败数据:', data);
    importData.value = data;
    setTableData(data.failList || []);
  });

  // 处理关闭确认 - closeFunc 返回 Promise<boolean>
  const handleCloseConfirm = (): Promise<boolean> => {
    return new Promise((resolve) => {
      createConfirm({
        iconType: 'warning',
        title: '确认失败列表已导出并关闭弹窗吗？',
        content: '',
        onOk: () => {
          emit('close');
          resolve(true); // 返回 true 允许关闭
        },
        onCancel: () => {
          resolve(false); // 返回 false 阻止关闭
        },
      });
    });
  };

  // 处理导出
  const handleExport = () => {
    // createMessage.info('导出功能开发中...');
    // TODO: 实现导出功能
    // 可以调用导出接口，将失败列表导出为Excel
    onExportXls();
  };
  // 处理黑名单申请
  const handleOk = () => {
    console.log('需要传递的值==', versionNo);
    router.push({
      path: '/blackListEnterApply',
      query: {
        versionNo: versionNo.value,
      },
    });
  };
</script>

<style lang="less" scoped>
  .import-fail-modal {
    .summary-info {
      margin-bottom: 16px;
      padding: 12px 16px;
      background-color: #f5f5f5;
      border-radius: 4px;

      .summary-text {
        font-size: 14px;
        color: #333;
        font-weight: 500;
      }
    }

    .fail-table-title {
      margin-bottom: 12px;
      font-size: 14px;
      font-weight: 500;
      color: #333;
    }
  }
</style>
