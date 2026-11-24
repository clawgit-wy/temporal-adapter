<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm">
      <template #endDate="{ model, field }">
        <a-date-picker v-model:value="model[field]" :showToday="false">
          <template #renderExtraFooter>
            <div style="text-align: center;"><a-button type="link" @click="model[field] = dayjs('2099-12-31')">长期</a-button></div>
          </template>
        </a-date-picker>
      </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { formSchemas } from './list.data';
  import { fieldTree, saveOrUpdate } from './list.api';
  import dayjs from 'dayjs'



  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  // 注册 form
  const [registerForm, { resetFields, setFieldsValue, validate, setProps, updateSchema }] = useForm({
    labelCol: { style: { width: '160px' } },
    schemas: formSchemas,
    showActionButtonGroup: false,
  });
  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter, destroyOnClose: true });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    await resetFields();
    await setFieldsValue({ ...data.record });
    await updateSchema({
        field: 'teamId',
        componentProps: {
          options: teamOptions.value
        }
      })
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });

  //表单提交事件
  async function onSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      // 提交表单
      await saveOrUpdate({...values, beginDate: dayjs(values.beginDate).format('YYYY-MM-DD'), endDate: dayjs(values.endDate).format('YYYY-MM-DD'), ruleId: values.id}, isUpdate);
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
  const teamOptions = ref([{label: 1, value: 2}])

  fieldTree({fieldType: 'spotTeam', topFlag: false}).then(res => {
    teamOptions.value = res.map(item => ({label: item.fieldName, options: item.regionList.map(child => ({label: child.fieldName, value: child.id}))}))
    console.log(teamOptions.value);
    
  })
</script>
