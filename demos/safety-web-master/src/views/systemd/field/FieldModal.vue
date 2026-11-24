<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { formSchemas } from './field.data';
  import { saveOrUpdate, getFactoryList } from './field.api';
  import { watchEffect, nextTick } from 'vue';

  const props = defineProps({
    subType: {
      type: String,
      default: '',
    },
  });
  watchEffect(() => {
    if (props.subType == '添加厂区') {
      nextTick(() => {
        appendSchemaByField(
          {
            field: 'fieldCode',
            component: 'Input',
            required: true,
            label: '编码',
          },
          ''
        );
      });
    } else {
      nextTick(() => removeSchemaByFiled('fieldCode'));
    }
  });
  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  // 注册 form
  const [registerForm, { resetFields, setFieldsValue, validate, setProps, appendSchemaByField, removeSchemaByFiled, updateSchema, resetSchema }] =
    useForm({
      schemas: formSchemas,
      showActionButtonGroup: false,
    });

  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    console.log('modal data======', data, data.record.fieldType.includes('buildingAccessFactoryManage_'));

    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    removeSchemaByFiled('fieldValue');
    removeSchemaByFiled('envirInfo');
    if (data.record.fieldType == 'temporaryTransMana_cllx_0') {
      appendSchemaByField(
        {
          field: 'fieldValue',
          component: 'Select',
          required: true,
          label: '是否临时运输车辆',
          componentProps: {
            options: [
              { label: '是', value: '是' },
              { label: '否', value: '否' },
            ],
          },
        },
        ''
      );
      appendSchemaByField(
        {
          field: 'envirInfo',
          component: 'Select',
          required: true,
          label: '是否填写环保清单',
          componentProps: {
            options: [
              { label: '是', value: '是' },
              { label: '否', value: '否' },
            ],
          },
        },
        ''
      );
    } else if (data.record.fieldType == 'temporaryTransMana_cq_1') {
      updateSchema({
        field: 'fieldName',
        component: 'ApiSelect',
        required: true,
        label: '厂区',
        componentProps: {
          api: (params) => getFactoryList(),
          labelField: 'fieldName',
          valueField: 'fieldName',
        },
      });
    } else if (data.record.fieldType == 'buildingAccessFactoryManage') {
      updateSchema({
        field: 'fieldName',
        component: 'ApiSelect',
        required: true,
        label: '厂区名称',
        componentProps: {
          api: (params) => getFactoryList(),
          labelField: 'fieldName',
          valueField: 'fieldName',
        },
      });
    } else if (!isNaN(data.record.fieldType) && data.record.fieldLevel == 4) {
      updateSchema({
        field: 'fieldName',
        component: 'Input',
        required: true,
        label: '名称',
        componentProps: {},
      });
    }

    await resetFields();
    await setFieldsValue({ ...data.record });
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });

  //表单提交事件
  async function onSubmit() {
    try {
      const values = await validate();
      // const params = {...values, fieldType: 'factory'}
      // console.log(values, "====");
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
</script>
