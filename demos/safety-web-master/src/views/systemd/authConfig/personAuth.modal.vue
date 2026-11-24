<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit" width="500px">
    <div class="person-auth-modal">
      <BasicForm @register="registerForm">
        <template #empNoSlot="{ model, field }">
          <a-input v-model:value="model[field]" placeholder="请输入员工号" @blur="onEmpNoBlur" />
        </template>
      </BasicForm>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup name="PersonAuthModal">
  import { ref, unref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { personAuthFormSchema } from './authConfig.data';
  import { authSaveAndUpdate, getEmployeeByWorkNo } from './authConfig.api';

  const emit = defineEmits(['success', 'register']);
  const { createMessage } = useMessage();
  const isUpdate = ref(false);
  const rowId = ref('');

  const [registerForm, { resetFields, setFieldsValue, validate, setProps }] = useForm({
    schemas: personAuthFormSchema,
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    await setProps({
      labelCol: { sm: { span: 7 } },
      wrapperCol: { sm: { span: 17 } },
    });
    await setModalProps({ confirmLoading: false, canFullscreen: false });
    isUpdate.value = !!data?.isUpdate;
    if (data.record) {
      rowId.value = data.record.id;
      setFieldsValue({
        empNo: data.record.empNo,
        empName: data.record.empName,
        empOrgNamePath: data.record.empOrgNamePath,
        intervieweeNum: data.record.intervieweeNum,
      });
    }
  });

  const getTitle = computed(() => (!unref(isUpdate) ? '新增人员授权' : '编辑人员授权'));

  // 员工号失焦自动带出姓名和组织
  async function onEmpNoBlur(e: any) {
    const workNo = e?.target?.value;
    if (!workNo) return;
    try {
      const userInfo = await getEmployeeByWorkNo({ workNo, phone: '' });
      if (userInfo) {
        setFieldsValue({
          empName: userInfo.empName || '',
          empOrgNamePath: userInfo.deptName || userInfo.orgName || '',
        });
      }
    } catch (e) {}
  }

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      const params: any = {
        configType: 1,
        empNo: values.empNo || '',
        intervieweeNum: values.intervieweeNum,
      };
      if (unref(isUpdate)) {
        params.id = rowId.value;
      }
      await authSaveAndUpdate(params);
      closeModal();
      emit('success', { isUpdate: unref(isUpdate) ? 2 : 1, values });
    } catch (err: any) {
      if (err && err.message) {
        createMessage.error({ content: err.message });
      }
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
.person-auth-modal {
  padding: 10px 0;
}
</style>
