<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="title"
    :width="800"
    :centered="true"
    :maskClosable="false"
    okText="确认"
    cancelText="取消"
    @ok="handleSubmit"
  >
    <BasicForm @register="registerForm" />
    <a-table
      :row-selection="{ selectedRows: selectedRowKeys, onChange: onSelectionChange }"
      :columns="doorColumns"
      :data-source="tableData"
      :pagination="{ pageSize: pageSize, change: pageChange }"
    ></a-table>
    <!--    <BasicTable @register="registerTable" :rowSelection="rowSelection"/>-->
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, h } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, FormSchema, useForm } from '/@/components/Form/index';
  import { BasicTable, BasicColumn } from '/@/components/Table';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { dealBuild } from './personChangeAuthConfirm.api';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { testData } from '/@/views/buildingAccessControl/personChangeAuthConfirm/personChangeAuthConfirm.data';

  const emit = defineEmits(['success', 'register']);
  const { createMessage } = useMessage();

  const selectedRowKeys = ref([]);
  const tableData = ref([]);
  const page = ref(1);
  const pageSize = ref(5);

  // 表单数据
  // const actionType = ref('');
  const record = ref<Recordable>({});
  const title = ref('');

  // 门禁列表列定义
  const doorColumns: BasicColumn[] = [
    {
      title: '授权门禁',
      dataIndex: 'deviceName',
      width: 600,
      align: 'center',
      customRender: ({ record }: any) => {
        // 如果isAdd为true，则显示蓝色字体
        if (record.isAdd) {
          return h('span', { style: { color: '#1890ff' } }, `${record.deviceName}(本部门新增)`);
        }
        return record.deviceName;
      },
    },
  ];

  function pageChange(e) {
    console.log('pageChange>>>>>>>', e);
    page.value = e;
  }

  function onSelectionChange(sec) {
    selectedRowKeys.value = sec;
    console.log('selectedRowKeys>>>>>', selectedRowKeys.value);
  }

  // 使用 useListPage 注册表格
  // const {tableContext} = useListPage({
  //   designScope: 'building-access-control',
  //   tableProps: {
  //     title: '',
  //     columns: doorColumns,
  //     dataSource: [],
  //     bordered: true,
  //     canResize: false,
  //     useSearchForm: false,
  //     showTableSetting: false,
  //     showIndexColumn: false,
  //     showActionColumn: false,
  //     clickToRowSelect: true,
  //     rowKey: 'id',
  //     rowSelection: {
  //       type: 'checkbox',
  //       columnWidth: 100,
  //     },
  //     //设置页数
  //     pagination: {
  //       pageSize: 5,
  //       showSizeChanger: true,
  //       showQuickJumper: false,
  //     },
  //   },
  // });

  // 解构表格上下文
  // const [registerTable, {setTableData, clearSelectedRowKeys}, {
  //   rowSelection,
  //   selectedRows
  // }] = tableContext;

  // 表单配置
  const formSchema: FormSchema[] = [
    {
      field: 'employeeName',
      label: '姓名',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
      colProps: {
        span: 12,
      },
    },
    {
      field: 'employeeCode',
      label: '员工号',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
      colProps: {
        span: 12,
        offset: 0,
      },
    },
  ];

  // 注册表单
  const [registerForm, { resetFields, setFieldsValue }] = useForm({
    labelWidth: 60,
    labelAlign: 'left',
    schemas: formSchema,
    showActionButtonGroup: false,
    rowProps: { gutter: 30, justify: 'start' },
  });

  // 注册弹框
  const [registerModal, { closeModal, setModalProps }] = useModalInner(async (data) => {
    // clearSelectedRowKeys();
    // 重置表单
    resetFields();
    console.log('.........');
    if (data) {
      record.value = data.record;
      title.value = data.title;
      record.value.allPowerList = data.record.allPowerList?.map((item: any) => {
        return {
          ...item,
          key: item.id,
          isAdd: record.value.localPowerList?.some((power: any) => power.id === item.id) || false,
        };
      });
      setFieldsValue({
        employeeCode: data.record.employeeCode,
        employeeName: data.record.employeeName,
      });
      tableData.value = data.record.allPowerList;
      page.value = 1;
      // setTableData(data.record.allPowerList);
    }
  });

  // 提交表单
  async function handleSubmit() {
    try {
      // 验证是否选择了人员记录
      console.log('selectedRowKeys>>>>', selectedRowKeys.value);
      if (selectedRowKeys.value.length === 0) {
        createMessage.warning('请先选择门禁');
        return false;
      }

      setModalProps({ confirmLoading: true });
      await dealBuild({
        changeId: record.value.id,
        closeIdStr: selectedRowKeys.value.join(','),
        // closeIdStr: selectedRows.value.map((item: any) => item.id).join(','),
      });
      createMessage.success('取消授权成功');

      closeModal();
      emit('success');
      return true;
    } catch (error) {
      console.error('取消授权操作失败:', error);
      return false;
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-form-item-control) {
    max-width: 100%;
  }

  :deep(.ant-table-wrapper) {
    padding: 0;
    margin-top: 16px;
  }

  :deep(.ant-form) {
    padding: 10px 0;
  }

  .alert {
    margin-bottom: 8px;
    background-color: #e6f7ff;
    border: 1px solid #91d5ff;
    color: #1890ff;
  }
</style>
