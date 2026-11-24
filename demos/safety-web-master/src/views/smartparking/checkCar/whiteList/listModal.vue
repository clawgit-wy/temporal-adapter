<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm" >
        <template #empNo="{model, field}">
          <a-input placeholder="请输入员工号" v-model:value="model[field]" @blur="staffNoBlur($event, model)"></a-input>
        </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { formSchemas } from './list.data';
  import { saveOrUpdate, getMemberInfo } from './list.api';

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
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
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
      await saveOrUpdate(values, isUpdate);
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
  const staffNoBlur = (e, model) => {
    const no = e.target.value
    console.log(no);
    
    no && getMemberInfo(no).then(res => {
      const {dataList} = res
      if (dataList.length > 0) {
        model['empName'] = dataList[0].empName
        model['dept'] = dataList[0].orgNamePath
      }
      
    })
    
  }
</script>
