<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm">
      <template #deviceFieldCode="{model, field}">
        <a-select
          v-model:value="model[field]"
          :options="deviceOptions"
          @change="(_, option) => deviceFieldCodeChange(option, model)"
        ></a-select>
      </template>
      <template #teamId="{model, field}">
        <a-select
          v-model:value="model[field]"
          :options="teamOptions"
          @change="(_, option) => teamIdChange(option, model)"
        ></a-select>
      </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { formSchemas, changeRuleParams } from './list.data';
  import { saveOrUpdate, fieldTree } from './list.api';

  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  // 注册 form
  const [registerForm, { resetFields, setFieldsValue, validate, setProps }] = useForm({
    schemas: formSchemas,
    showActionButtonGroup: false,
  });
  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter, destroyOnClose: true });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    getTeamOptions(data.record.deviceFieldCode)
    changeRuleParams({teamId: data.record.teamId})
    await resetFields();
    await setFieldsValue({ ...data.record });
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });

  //表单提交事件
  async function onSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      // 提交表单
      await saveOrUpdate({...values, ruleId: values.id}, isUpdate.value);
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
  // 设备类型
  const deviceOptions = ref([])
  const getDeviceOptions = () => {
    fieldTree({fieldType: 'spotTeam', topFlag: false}).then(res => {
      deviceOptions.value = res.map(item => ({...item, label: item.fieldName, value: item.id}))
    })
  }
  getDeviceOptions()
   // 项目
  const teamOptions = ref([])
  const getTeamOptions = (parentId) => {
    fieldTree({parentId, topFlag: false}).then(res => {
      teamOptions.value = res.map(item => ({...item, label: item.fieldName, value: item.id}))
    })
  }
  const teamIdChange = (item, model) => {
    model['teamName'] = item.fieldName
    model['ruleIds'] = null
    changeRuleParams({teamId: item.value})
  }
  const deviceFieldCodeChange = (item, model) => {
    model['deviceType'] = item.fieldName
    model['teamName'] = ''
    model['teamId'] = ''
    getTeamOptions(item.id)
  }

</script>
