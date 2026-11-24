<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="modalTitle"
    @ok="handleSubmit"
    width="700px"
  >
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { formSchema } from './whitelistRule.data';
  import { addWhitelistRule } from './whitelistRule.api';
  import { useMessage } from '/@/hooks/web/useMessage';

  const emit = defineEmits(['success', 'register']);
  const { createMessage } = useMessage();

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetForm();
  });

  // 表单
  const [registerForm, { validate, resetFields: resetForm, setFieldsValue }] = useForm({
    labelWidth: 80,
    schemas: formSchema,
    showActionButtonGroup: false,
  });

  // 模态框标题
  const modalTitle = computed(() => '添加规则');

  // 提交表单
  async function handleSubmit() {
    try {
      setModalProps({ confirmLoading: true });
      const formData = await validate();
      const ruleType = formData.ruleType;
      delete formData.ruleType;
      console.log('ruleType',ruleType);

      if(ruleType == '1'){
        // 组织类型
        formData.orgNamePath = formData.organization.namePath;
        formData.orgCode = formData.organization.key;
        delete formData.organization;
      }
      console.log('所有字段的值是',formData);
      await addWhitelistRule(formData);
      createMessage.success('保存成功');
      emit('success');
      closeModal();
    } catch (error) {
      console.error('表单验证或提交失败:', error);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script> 
