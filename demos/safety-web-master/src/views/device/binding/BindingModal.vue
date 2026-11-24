<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm">
      <template #factoryName="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择厂区" allowClear @change="factoryChange(model)">
          <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
        </a-select>
      </template>
      <template #regionName="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择区域" allowClear>
          <a-select-option v-for="item in regionList" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
        </a-select>
      </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref, computed } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { formSchemas } from './binding.data';
  import { saveOrUpdate } from './binding.api';

  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  // const props = defineProps(['factoryLists']);
  const factoryLists = ref<Array<Object>>([]);
  const regionList = ref<Array<Object>>([]);
  // 注册 form
  //update-begin---author:wangshuai ---date:20221123  for：[VUEN-2807]消息模板加一个查看功能------------
  const [registerForm, { resetFields, setFieldsValue, validate, setProps }] = useForm({
    //update-end---author:wangshuai ---date:20221123  for：[VUEN-2807]消息模板加一个查看功能--------------z
    schemas: formSchemas,
    showActionButtonGroup: false,
  });
  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    factoryLists.value = unref(data.factoryLists);
    const record = data.record || {};
    getregionList(record.factoryId);
    await resetFields();
    await setFieldsValue({ ...data.record });
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });

  //表单提交事件
  async function onSubmit() {
    try {
      const values = await validate();
      const params = { ...values };
      const factory = factoryLists.value.find((item) => item.id === values.factoryId);
      if (factory) {
        params.factoryName = factory.fieldName;
      }
      const region = regionList.value.find((item) => item.id === values.regionId);
      if (region) {
        params.regionName = region.fieldName;
      }
      setModalProps({ confirmLoading: true });
      // 提交表单
      await saveOrUpdate(params, isUpdate);
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
  function factoryChange(model) {
    console.log(model, 'factoryChange');
    model.regionId = '';
    getregionList(model.factoryId);
  }

  function getregionList(val) {
    const data = factoryLists.value.find((item) => {
      return item.id === val;
    });
    let result = [];
    if (data && data.regionList) {
      result = data.regionList;
    }
    regionList.value = result;
  }
</script>
