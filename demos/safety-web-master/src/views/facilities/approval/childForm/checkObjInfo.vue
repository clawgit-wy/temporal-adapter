<template>
    <div>
      <a-card title="点检对象信息">
        <BasicForm class="top-form" @register="registerForm" />
      </a-card>
    </div>
</template>
<script setup lang="ts">
import { useForm, BasicForm, FormSchema } from '/@/components/Form';
import {watchEffect, defineExpose} from 'vue'
const props = defineProps({
  info: {
    type: Object,
    default: () => {}
  }
})
//点检人表单字段
const formSchemas: FormSchema[] = [
{
    label: '项目',
    field: 'teamName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '名称',
    field: 'deviceName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '设备编号',
    field: 'deviceCode',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '位置',
    field: 'location',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  { 
    label: '计划整改完成日期',
    field: 'reformTime',
    component: 'DatePicker',
    colProps: { span: 12 },
    componentProps: {
      format:'YYYY-MM-DD',
      valueFormat:'YYYY-MM-DD',
    }
  },
  {
    label: '整改责任人',
    field: 'reformUserInfo',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
];
const [registerForm, {setFieldsValue, getFieldsValue, setProps, updateSchema}] = useForm({
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
  if (Object.keys(props.info).length > 0) {
    const {teamName, deviceName, deviceCode, location, reformTime, reformUserInfo, nodeFlag} = props.info
    if (reformTime && nodeFlag != 1) {
      setProps({
        disabled: nodeFlag != 1 ? true : false,
      })
      updateSchema([
        {
          field: 'reformTime',
          component: 'Input',
        }
      ])
    }
    setFieldsValue({
      teamName,
      deviceName,
      deviceCode,
      location,
      reformTime,
      reformUserInfo
    })
  }
})
const getTime = async () => {
  const values = await getFieldsValue()
  return values.reformTime
}
defineExpose({
  getTime
})
</script>
<style scoped>

</style>