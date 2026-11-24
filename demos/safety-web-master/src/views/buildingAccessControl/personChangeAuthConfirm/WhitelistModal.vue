<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="title"
    :width="500"
    :minHeight="60"
    :centered="true"
    :maskClosable="false"
    okText="确认"
    cancelText="取消"
    @ok="handleSubmit"
  >
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, FormSchema, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { addWhiteList ,batchDealWhite} from './personChangeAuthConfirm.api';
  const emit = defineEmits(['success', 'register', 'error']);
  const { createMessage } = useMessage();

  // 表单数据
  const actionType = ref('');
  const selectedIds = ref('');
  const isDepartmentChange = ref(false);
  const title = ref('加入白名单');
  // 表单配置
  const formSchema: FormSchema[] = [
    {
      field: 'reason',
      label: '原因',
      component: 'InputTextArea',
      componentProps: {
        placeholder: '请输入，100字内',
        maxlength: 100,
        rows: 4,
      },
      required: true,
      rules: [{ required: true, message: '请输入加入白名单的原因' }],
    },
  ];

  // 注册表单
  const [registerForm, { resetFields, validate }] = useForm({
    labelWidth: 60,
    schemas: formSchema,
    showActionButtonGroup: false,
  });

  // 注册弹框
  const [registerModal, { closeModal }] = useModalInner(async (data) => {
    // 重置表单
    resetFields();

    if (data) {
      actionType.value = data.actionType || '';
      selectedIds.value = data.selectedIds || '';
      isDepartmentChange.value = data.isDepartmentChange || false;
      title.value = data.title || '加入白名单';
    }
  });

  // 提交表单
  async function handleSubmit() {
    if (isDepartmentChange.value) {
      try {
        // 表单验证
        const formValues = await validate();
        await batchDealWhite({
          dealType: title.value === '保留白名单' ? 1 : 2,
          reason: formValues.reason,
          idStr: selectedIds.value,
        });
        createMessage.success('操作成功');
        closeModal();
        emit('success');
        return true;
      } catch (error) {
        // createMessage.error(error.message);
        closeModal();
        emit('error');
        return false;
      }
    } else {
      try {
        // 表单验证
        const formValues = await validate();
        await addWhiteList({
          reason: formValues.reason,
          idStr: selectedIds.value,
        });
        createMessage.success('加入白名单成功');
        closeModal();
        emit('success');
        return true;
      } catch (error) {
        // createMessage.error(error.message);
        closeModal();
        emit('error');
        return false;
      }
    }
  }
</script>
