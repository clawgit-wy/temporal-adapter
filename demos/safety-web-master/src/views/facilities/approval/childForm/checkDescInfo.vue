<template>
  <BasicForm class="top-form" @register="registerForm" />
</template>
<script setup lang="ts">
  import { useForm, BasicForm, FormSchema } from '/@/components/Form';
  import { watchEffect, defineExpose } from 'vue';
  const props = defineProps({
    info: {
      type: Object,
      default: () => {},
    },
  });
  //点检人表单字段
  const formSchemas: FormSchema[] = [
    {
      label: '验证详请描述',
      field: 'validRemark',
      colProps: { span: 12 },
      component: 'InputTextArea',
      required: true,
      componentProps: {
        autoSize: {
          //最小显示行数
          minRows: 5,
          //最大显示行数
          maxRows: 5,
        },
        maxlength: 200,
      },
    },
  ];
  const [registerForm, { setFieldsValue, getFieldsValue, setProps, updateSchema }] = useForm({
    //注册表单列
    schemas: formSchemas,
    //回车提交
    autoSubmitOnEnter: true,
    //不显示重置按钮
    showResetButton: false,
    // 不显示提交按钮
    showSubmitButton: false,
    //查询列占比 24代表一行 取值范围 0-24
    actionColOptions: { span: 17 },
    fieldMapToTime: [['date', ['checkTimeStart', 'checkTimeEnd'], 'YYYY-MM-DD']],
  });

  const getRemark = async () => {
    const values = await getFieldsValue();
    console.log('表单字段==', values);
    return values;
  };
  defineExpose({
    getRemark,
  });
</script>
<style scoped>
  ::v-deep .ant-col-12 {
    display: block;
    flex: 0 0 100%;
    max-width: 100%;
  }
</style>
