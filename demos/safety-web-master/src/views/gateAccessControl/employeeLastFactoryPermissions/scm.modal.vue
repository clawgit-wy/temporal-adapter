<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="查看厂区授权" @ok="handleSubmit">
    <div class="scm-modal">
      <BasicForm @register="registerForm">
        <template #factoryName="{ model, field }">
          <BasicTable @register="registerTable" :dataSource="tableData"> </BasicTable>
        </template>
      </BasicForm>
    </div>
  </BasicModal>
</template>
<script lang="ts" setup name="ScmModal">
  import { ref, reactive, computed, unref } from 'vue';
  import { debounce } from 'lodash-es';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useUserStore } from '/@/store/modules/user';
  import { scmSchema, userTypeList, schemTablecolumns } from './config';
  import { fieldChildTree, viewDetail } from './field.api';
  import { BasicTable, TableAction, useTable, BasicColumn, ActionItem } from '/@/components/Table';

  //表单表格
  const tableData = ref([]);
  const [registerTable, { reload, getDataSource, setTableData, updateTableDataRecord, deleteSelectRowByKey, deleteTableDataRecord }] = useTable({
    rowKey: 'id',
    bordered: true,
    columns: schemTablecolumns,
    pagination: false,
    showSizeChanger: false,
    showTableSetting: false,
    showIndexColumn: false,
    useSearchForm: false,
    showActionColumn: false,
    setShowPagination: false,
  });

  const [registerForm, { resetFields, setFieldsValue, validate, setProps, updateSchema, getFieldsValue, clearValidate }] = useForm({
    schemas: scmSchema,
    showActionButtonGroup: false,
  });
  // 表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    console.log('厂区规则=======', data);

    //重置表单
    await resetFields();
    await setProps({
      labelCol: {
        sm: { span: 5 },
      },
      wrapperCol: {
        sm: { span: 17 },
      },
      baseRowStyle: { width: 50 },
    });
    await setModalProps({ confirmLoading: false, canFullscreen: false, showCancelBtn: false, showOkBtn: false });
    //表单赋值
    if (typeof data.record === 'object') {
      setFieldsValue({
        ...data.record,
      });
    }
    const result = data.record.detailArray || [];
    if (result != null) {
      tableData.value = result.map((item) => {
        return {
          ...item,
          powerTime: item.powerBeginTime ? `${item.powerBeginTime}-${item.powerEndTime}` : `${item.powerEndTime}`,
        };
      });
    }
  });
</script>
<style lang="less">
  .scm-modal {
    .ant-row {
      .ant-col {
        // width: 100%;
        .ant-calendar-picker {
          display: block;
        }
      }
    }
  }
  .ant-col-sm-17 {
    display: block;
    flex: 0 0 70.83333333%;
    max-width: 100%;
  }
</style> 
