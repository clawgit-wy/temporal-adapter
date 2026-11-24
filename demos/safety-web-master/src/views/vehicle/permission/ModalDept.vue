<template>
  <div ref="refModal">
    <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit" :getContainer="() => $refs.refModal">
      <BasicForm @register="registerForm">
      </BasicForm>
    </BasicModal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { addDeptFormSchema } from './data';
  import { addUpdate, detail, vaPhone } from './api';
  const id = ref();
  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  // 注册 form
  const [registerForm, { resetFields, setFieldsValue, validate, setProps, getFieldsValue }] = useForm({
    schemas: addDeptFormSchema,
    showActionButtonGroup: false,
  });
  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter, loading: true });
    isUpdate.value = unref(data.isUpdate);
    if (!isUpdate.value) {
      resetFields();
    }
    title.value = unref(data.title);
    id.value = data.record.id;
    if (isUpdate.value) {
      getData(data.record.id);
    }

    //
    setModalProps({ loading: false });
    setProps({ disabled: !data?.showFooter });
  });
  // 员工号输入框 鼠标失去焦点触发事件
  async function onBlur(e) {
    console.log(e.target.value, '失去焦点时========');
    let userInfo = await vaPhone({ empNo: e.target.value });
    if (userInfo) {
      setFieldsValue({ fdEmpName: userInfo.fdEmpName });
      setFieldsValue({ fdEmpDepartment: userInfo.fdEmpDepartment });
      setFieldsValue({ fdPhone: userInfo.fdPhone });
    }
  }
  // 详情
  async function getData(id) {
    const res = await detail({ id: id });
    console.log(res, '7777777777');
    if (res) {
      setFieldsValue(res);

      if (res.fdFilingUserTypeCode) {
        setFieldsValue({ fdFilingUserType: res.fdFilingUserTypeCode.split(',') });
      } else {
        setFieldsValue({ fdFilingUserType: undefined });
      }
      setFieldsValue({ fdFilingType: res.fdFilingTypeCode.split(',') });
      setFieldsValue({ fdFactory: res.fdFactoryCode.split(',') });

      setFieldsValue({ fdAuth: res.fdAuthCode.split(',') });
    }
  }

  // watch(
  //   () => getFieldsValue().fdEmpNo,
  //   (n) => {}
  // );

  //表单提交事件
  async function onSubmit() {
    setModalProps({ confirmLoading: true });
    try {
      const values = await validate();

      if (isUpdate.value) {
        values.id = id.value;
        if (!values.fdFilingType.includes('20')) {
          values.fdFilingUserType = '';
        }
      }

      await addUpdate({...values, fdConfigType: 2}, isUpdate.value);
      // 提交表单
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
<style lang="less" scoped>
  ::v-deep(.ant-modal-content) {
    border-radius: 4px !important;
    padding: 20px !important;
  }
  ::v-deep(.ant-modal-footer) {
    text-align: center;
    .ant-btn:nth-child(1) {
      width: 80px;
      border-color: #797979;
      border-radius: 4px;
    }
    .ant-btn:nth-child(2) {
      width: 80px;

      border-radius: 4px;
      margin-left: 20px;
    }
    .ant-input-affix-wrapper {
      border-color: #797979;
      border-radius: 4px;
    }
  }
</style>
