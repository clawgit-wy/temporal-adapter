<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { formSchemas, onPlanTypeChange } from './list.data';
  import { saveOrUpdate } from './list.api';

  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  // 注册 form
  const [registerForm, { resetFields, setFieldsValue, validate, setProps, updateSchema }] = useForm({
    labelCol: { style: { width: '160px' } },
    schemas: formSchemas,
    fieldMapToTime: [['date', ['beginDate', 'endDate'], 'YYYY-MM-DD']],
    showActionButtonGroup: false,
  });
  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter, destroyOnClose: true });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    onPlanTypeChange(data.record.planType)
    await resetFields();
    await setFieldsValue({ ...data.record });
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });

  //表单提交事件
  async function onSubmit() {
    try {
      const values = await validate();
      if (values.planType === 3) {
        values['gwRwlx'] = 1
      }
      setModalProps({ confirmLoading: true });
      // 提交表单
      await saveOrUpdate({...values, ruleId: values.id}, isUpdate);
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }

</script>
