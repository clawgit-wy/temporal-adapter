<template>
    <div class="container">
        <BasicForm class="top-form" @register="registerForm" />
    </div>
</template>
<script setup lang="ts">
import { onMounted } from 'vue';
import { useForm, BasicForm, FormSchema } from '/@/components/Form';
const props = defineProps({
    disableDic: {
        type: Object,
        default: () => ({
            checkUserno: false,
            checkException: false,
            examFulfill: false,
            improveResult: false,
            remark: false
        })
    },
    processEnd: {
        type: Boolean,
        default: false
    },
    info: {
      type: Object,
      default: () => {}
    }
})
//点检人表单字段
const formSchemas: FormSchema[] = [
  {
      label: props.info.ruleName,
      field: 'divisionLine',
      component: 'Divider',
      componentProps: {
        //是否虚线
        dashed: false,
        //分割线标题的位置（left | right | center）
        orientation: 'left',
        //文字是否显示为普通正文样式
        plain:true,
        //水平还是垂直类型（horizontal | vertical）
        type:'horizontal',
      },
    },
  
  {
    label: '是否异常',
    field: 'isException',
    component: 'RadioGroup',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
      options: [
        {label: '正常', value: 0},
        {label: '异常', value: 1},
      ]
    },
  },
  {
    label: '问题描述',
    field: 'excepDesc',
    colProps: { span: 12 },
    component: 'InputTextArea',
    componentProps: {
      rows: 3,
      maxlength: 200,

    }
  },
  {
    label: '图片上传',
    field: 'imgUrl',
    colProps: { span: 12 },
    component: 'JImageUpload',
    componentProps: {
      fileMax: 9,
      disabled:true,
    },
  },
  {
    label: '是否当场整改',
    field: 'isLocal',
    component: 'RadioGroup',
    colProps: { span: 12 },
    componentProps: {
      options: [
        {label: '是', value: 1},
        {label: '否', value: 0},
      ]
    },
  },
  {
    label: '整改完成照片',
    field: 'finishUrl',
    colProps: { span: 12 },
    component: 'JImageUpload',
    componentProps: {
      fileMax: 9,
      disabled:true,
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
  disabled: true,
  //查询列占比 24代表一行 取值范围 0-24
  actionColOptions: { span: 17 },
  fieldMapToTime: [['date', ['checkTimeStart', 'checkTimeEnd'], 'YYYY-MM-DD']]
});

onMounted(() => {
  const {isException, excepDesc, imgUrl, isLocal, finishUrl} = props.info
  // 兼容低版本浏览器可能找不到表单实例问题
  setTimeout(() => {
    setFieldsValue({
      isException,
      excepDesc,
      imgUrl: imgUrl.split(','),
      isLocal,
      finishUrl: finishUrl.split(',')
    })
  })
})

</script>