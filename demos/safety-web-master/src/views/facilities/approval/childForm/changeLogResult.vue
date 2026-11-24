<template>
    <BasicForm class="top-form" @register="registerForm" />
</template>
<script setup lang="ts">
import { onMounted } from 'vue';
import { useForm, BasicForm, FormSchema } from '/@/components/Form';
const props = defineProps({
  info: {
    type: Object,
    default: () => {}
  }
})
console.log(props.info, 'iinnnfo');

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
const [registerForm, {getFieldsValue, setProps, setFieldsValue}] = useForm({
disabled: true,
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
onMounted(() => {
  if (props.info) {
    // 兼容低版本浏览器找不到实列问题
    setTimeout(() => {
      setFieldsValue({
        reformFinish: Number(props.info.reformFinish),
        reformImg: props.info.reformImg ? props.info.reformImg.split(',') : []
      })
    })
}
})

</script>