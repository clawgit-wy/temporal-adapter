<template>
  <div class="container">
      <h1>监控岗位上报</h1>
      <BasicForm @register="registerForm" @submit="handleSubmit" />
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { useForm, BasicForm, FormSchema } from '/@/components/Form';
import { useUserStore } from '/@/store/modules/user';
import '/@/assets/bpm/domain'
import {saveAndCreateProcess, fieldTree, getEmployeeByWorkNo} from './field.api'
import { onMounted } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import {useRouter} from 'vue-router'
const router = useRouter()
const { createMessage } = useMessage();
const userStore = useUserStore();

//自定义表单字段
const formSchemas: FormSchema[] = [
  {
      label: '上报人信息',
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
    label: '姓名',
    field: 'submitName',
    componentProps: {
      disabled: true,
    },
    component: 'Input',
    colProps: { span: 12 },
  },
  // {
  //   label: '上报人员工号',
  //   field: 'submitNo',
  //   component: 'Input',
  //   colProps: { span: 12 },
  //   componentProps: {
  //     disabled: true,
  //   },
  // },
  {
    label: '部门班组',
    field: 'submitDept',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
      label: '事件信息',
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
    label: '厂区',
    field: 'factory',
    component: 'FactoryId',
    colProps: { span: 12 },
  },
  {
    label: '位置/岗位',
    field: 'positionPost',
    component: 'Input',
    colProps: { span: 12 },
  },
  {
    label: '镜头名称',
    field: 'lensName',
    component: 'Input',
    colProps: { span: 12 },
  },
  {
    label: '方式途径',
    field: 'waysMeans',
    component: 'ApiRadioGroup',
    colProps: { span: 12 },
    defaultValue: 1,
    componentProps: {
      api: (params) => fieldTree(params),
      labelField: "fieldName",
      valueField: "fieldName",
      params: {parentId: 359, topFlag: false},

    },
  },
  {
    label: '验证时间',
    field: 'date',
    component: 'RangePicker',
    componentProps: {
      showTime: true,
        //日期格式化
      format:'YYYY/MM/DD HH:mm',
      valueType: 'Date',
    },
    colProps: { span: 12 },
  },
  {
    label: '异常事件类型',
    field: 'abnormalType',
    component: 'ApiSelect',
    colProps: { span: 12 },
    componentProps: {
      api: (params) => fieldTree(params),
      labelField: "fieldName",
      valueField: "fieldName",
      params: {parentId: 358, topFlag: false},
    },
  },
  {
    label: '异常事件描述',
    field: 'abnormalDesc',
    component: 'InputTextArea',
    colProps: { span: 12 },
  },
  {
    label: '异常事件接收人部门',
    field: 'abnormalDept',
    component: 'Input',
    show: false,
    colProps: { span: 12 },
  },
  {
    label: '异常事件接收人姓名',
    show: false,
    field: 'abnormalCreator',
    component: 'Input',
    colProps: { span: 12 },
  },
  {
    label: '异常事件接收人',
    field: 'abnormalCreatorNo',
    labelField: 'abnormalCreator',
    component: 'SearchPerson',
    colProps: { span: 12 },
    componentProps: {
      onSelect(val, option){
        console.log(option);
        
        setFieldsValue({abnormalDept: option.orgName})
      },
      fieldNames:{
        label: 'empName',
        value: 'empNo'
      },
      
    }
  },
];
const [registerForm, {setFieldsValue,resetFields}] = useForm({
  //注册表单列
  schemas: formSchemas,
  //回车提交
  autoSubmitOnEnter: true,
  //不显示重置按钮
  showResetButton: false,
  //自定义提交按钮文本和图标
  submitButtonOptions: { text: '提交', preIcon: '' },
  //查询列占比 24代表一行 取值范围 0-24
  actionColOptions: { span: 17 },
  fieldMapToTime: [['date', ['checkTimeStart', 'checkTimeEnd'], 'YYYY-MM-DD HH:mm']]
});
function handleSubmit(values: any) {
  saveAndCreateProcess({
    ...values, title: `${values.submitName}发起的监控岗位上报流程`, creator: values.submitNo,
    processParam: {type: 'add', formId: 'safety_monitor_post_process', modelId: 'monitor_post', subject: `${values.submitName}发起的监控岗位上报流程`}
  }).then(res => {
    resetFields()
    createMessage.success('流程创建成功')
    router.push({name: 'monitorPostReportCheck'})
  })
}
const setDept = (workNo) => {
  getEmployeeByWorkNo({workNo}).then(res => {
    setFieldsValue({
      submitDept: res.orgName
    })
  })
}
onMounted(() => {
  const info = userStore.getUserInfo
  setDept(info.workNo)
  setFieldsValue({
    submitName: info.realName,
    submitNo: info.workNo,
  })
})
</script>
<style scoped lang="less">
.container{
  background-color: #fff;
  width: 100%;
  max-width: 100%;
}
h1{
  font-size: 18px;
  padding-left: 50px;
  font-weight: 600;
  padding-top: 30px;
}
:deep(.ant-divider-plain.ant-divider-with-text) {
  font-size: 16px;
  font-weight: 400;
}
iframe{
  width: 100%;
  height: 600px;
}
</style>