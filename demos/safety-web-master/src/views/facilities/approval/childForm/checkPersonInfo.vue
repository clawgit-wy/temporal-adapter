<template>
    <div>
      <a-card title="点检人信息">
        <BasicForm class="top-form" @register="registerForm" />
      </a-card>
    </div>
</template>
<script setup lang="ts">
import { useForm, BasicForm, FormSchema } from '/@/components/Form';
import {watchEffect} from 'vue'
const props = defineProps({
  info: {
    type: Object,
    default: () => {}
  }
})
//点检人表单字段
const formSchemas: FormSchema[] = [
  {
    label: '姓名',
    field: 'checkName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '员工号',
    field: 'checkEmpNo',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '部门',
    field: 'dutyName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '点检时间',
    field: 'checkTime',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
      valueType: 'date'
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
watchEffect(() => {
  console.log(props.info, 'baseInfo');
  
  if (Object.keys(props.info).length > 0) {
    const {checkName, checkEmpNo, dutyName, checkTime} = props.info
    setFieldsValue({
      checkName,
      checkEmpNo,
      dutyName,
      checkTime,
    })
  }
})
</script>
<style scoped>

</style>