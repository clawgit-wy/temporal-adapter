<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit" width="500px">
    <div class="org-auth-modal">
      <BasicForm @register="registerForm">
        <template #orgSelect="{ model, field }">
          <div style="position: relative;">
            <DepartmentSelectWrapper
              :value="model[field]"
              @change="onOrgChange"
            />
            <div v-if="orgSelectDisabled" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 9999; cursor: not-allowed;"></div>
          </div>
        </template>
      </BasicForm>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup name="OrgAuthModal">
  import { ref, unref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { orgAuthFormSchema } from './authConfig.data';
  import { authSaveAndUpdate } from './authConfig.api';
  import DepartmentSelectWrapper from '/@/components/Form/src/components/DepartmentSelectWrapper.vue';

  const emit = defineEmits(['success', 'register']);
  const { createMessage } = useMessage();
  const isUpdate = ref(false);
  const rowId = ref('');
  const selectedOrg = ref<any>(null);
  const orgSelectDisabled = ref(false);

  const [registerForm, { resetFields, setFieldsValue, validate, setProps }] = useForm({
    schemas: orgAuthFormSchema,
    showActionButtonGroup: false,
  });

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    console.log('[OrgAuthModal] useModalInner data:', JSON.parse(JSON.stringify(data)));
    await resetFields();
    selectedOrg.value = null;
    orgSelectDisabled.value = false;
    await setProps({
      labelCol: { sm: { span: 7 } },
      wrapperCol: { sm: { span: 17 } },
    });
    await setModalProps({ confirmLoading: false, canFullscreen: false });
    isUpdate.value = !!data?.isUpdate;
    if (data.record) {
      rowId.value = data.record.id;
      // 当 empOrgCodePath === 0 && configType === 0 时，组织架构不可选择
      const isOrgLocked = Number(data.record.empOrgCodePath) === 0 && Number(data.record.configType) === 0;
      console.log('[OrgAuthModal] isOrgLocked:', isOrgLocked, 'empOrgCodePath:', data.record.empOrgCodePath, 'configType:', data.record.configType);
      orgSelectDisabled.value = isOrgLocked;
      selectedOrg.value = {
        title: data.record.empOrgNamePath,
        namePath: data.record.empOrgNamePath,
        codePath: data.record.empOrgCodePath,
      };
      setFieldsValue({
        orgSelect: selectedOrg.value,
        empOrgNamePath: data.record.empOrgNamePath,
        empOrgCodePath: data.record.empOrgCodePath,
        intervieweeNum: data.record.intervieweeNum,
      });
    }
  });

  const getTitle = computed(() => (!unref(isUpdate) ? '新增组织授权' : '编辑组织授权'));

  // 组织选择回调
  function onOrgChange(item: any) {
    if (item) {
      selectedOrg.value = item;
      setFieldsValue({
        orgSelect: item,
        empOrgNamePath: item.namePath || item.title || '',
        empOrgCodePath: item.codePath || item.orgCodePath || '',
      });
    }
  }

  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      const params: any = {
        configType: 0,
        empOrgNamePath: selectedOrg.value?.namePath || selectedOrg.value?.title || '',
        empOrgCodePath: selectedOrg.value?.codePath || selectedOrg.value?.orgCodePath || '',
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
.org-auth-modal {
  padding: 10px 0;
}
</style>
