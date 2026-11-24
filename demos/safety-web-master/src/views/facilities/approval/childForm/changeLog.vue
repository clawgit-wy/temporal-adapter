<template>
  <div class="container">
      <BasicForm class="top-form" @register="registerForm" />
  </div>
</template>
<script setup lang="ts">
import { useForm, BasicForm, FormSchema } from '/@/components/Form';

//点检人表单字段
const formSchemas: FormSchema[] = [
{
  label: '是否已整改',
  field: 'reformFinish',
  component: 'RadioGroup',
  colProps: { span: 12 },
  componentProps: {
    options: [
      {label: '是', value: 1},
    ]
  },
},

{
  label: '图片上传',
  field: 'reformImg',
  colProps: { span: 12 },
  component: 'JImageUpload',
  componentProps: {
    fileMax: 9,
  },
},
];
const [registerForm, {getFieldsValue}] = useForm({
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
});
const getLog = async () => {
  const values = await getFieldsValue()
  return values
}

defineExpose({
  getLog,
})
</script>