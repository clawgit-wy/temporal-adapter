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
    label: '项目',
    field: 'empName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '名称',
    field: 'empNo',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '设备编号',
    field: 'dutyName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '位置',
    field: 'checkTIme',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
];
const [registerForm, {setFieldsValue}] = useForm({
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
  fieldMapToTime: [['date', ['checkTimeStart', 'checkTimeEnd'], 'YYYY-MM-DD']]
});
</script>