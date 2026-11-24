<template>
  <div ref="refModal">
    <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit" :getContainer="() => $refs.refModal">
      <BasicForm @register="registerForm" />
    </BasicModal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, FormSchema, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { addFormSchema, deleteFormSchema } from './data';
  import { addUpdate, detail, deleteItem } from './api';
  const id = ref();
  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  // 注册 form
  const [registerForm, { resetFields, resetSchema, setFieldsValue, validate, setProps }] = useForm({
    schemas: [...addFormSchema, ...deleteFormSchema],
    showActionButtonGroup: false,
  });
  // // 注册 form
  // const [
  //   registerDeleteForm,
  //   { resetFields: resetDeleteFields, setFieldsValue: setDeleteFieldsValue, validate: deleteValidate, setProps: setDeleteProps },
  // ] = useForm({
  //   schemas: deleteFormSchema,
  //   showActionButtonGroup: false,
  // });
  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter, loading: true });
    isUpdate.value = unref(data.isUpdate);
    if (!isUpdate.value) {
      resetFields();
    }
    title.value = unref(data.title);
    id.value = data.record.id;
    if (title.value == '编辑') {
      await resetSchema(addFormSchema);
      await setFieldsValue(data.record);
      setProps({ disabled: !data?.showFooter });
    } else {
      await resetSchema(deleteFormSchema);
      resetFields();
    }

    // getData(data.record.id);
    //
    setModalProps({ loading: false });
  });
  // 详情
  async function getData(id) {
    const res = await detail({ id: id });
    console.log(res, '想起');
    if (res) {
      setFieldsValue(res);
      setFieldsValue({ fdFilingType: res.fdFilingType.split(',') });
      setFieldsValue({ fdFactory: res.fdFactory.split(',') });
      setFieldsValue({ fdFilingUserType: res.fdFilingUserType.split(',') });
      setFieldsValue({ fdAuth: res.fdAuth.split(',') });
    }
  }

  //表单提交事件
  async function onSubmit() {
    setModalProps({ confirmLoading: true });
    try {
      const values = await validate();
      console.log(values);
      if (isUpdate.value) {
        values.id = id.value;
      }
      if (title.value == '删除') {
        await deleteItem(values);
      } else {
        await addUpdate(values, isUpdate.value);
      }
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
