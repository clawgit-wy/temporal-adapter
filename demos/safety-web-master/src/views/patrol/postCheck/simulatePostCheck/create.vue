<template>
  <div class="container">
      <h1>模拟岗位上报</h1>
      <BasicForm @register="registerForm" @submit="handleSubmit" />
  </div>
</template>
<script setup lang="ts">
import { useForm, BasicForm, FormSchema } from '/@/components/Form';
import { useUserStore } from '/@/store/modules/user';
import '/@/assets/bpm/domain'
import {saveOrUpdate, fieldTree, employeeList, view, update} from './list.api'
import { onMounted, ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { useRoute, useRouter } from 'vue-router';
const { createMessage } = useMessage();
const userStore = useUserStore();
const route = useRoute()
const router = useRouter()

//自定义表单字段
const formSchemas: FormSchema[] = [
  {
      label: '验证人信息',
      field: 'divisionLine',
      component: 'Divider',
  },
  {
    label: 'id',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '姓名',
    field: 'createEmp',
    componentProps: {
      disabled: true,
    },
    required: true,
    component: 'Input',
    colProps: { span: 12 },
  },
  {
    label: '上报人员工号',
    field: 'createEmpNo',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
    show: false,
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '部门班组',
    field: 'createDept',
    required: true,
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
    },
  },
  {
      label: '事件信息',
      field: 'dLine',
      component: 'Divider',
  },
  {
    label: '厂区',
    field: 'factory',
    show: false,
    component: 'Input',
  },
  {
    label: '厂区',
    field: 'factoryId',
    labelField: 'factory',
    component: 'FactoryId',
    required: true,
    colProps: { span: 12 },
  },
  {
    label: '验证项目名字',
    field: 'teamName',
    component: 'Input',
    show: false,
  },
  {
    label: '验证项目',
    field: 'teamId',
    component: 'ApiSelect',
    required: true,
    colProps: { span: 12 },
    labelField: 'teamName',
    componentProps: {
      api: (params) => fieldTree(params),
      numberToString: true,
      labelField: "fieldName",
      valueField: "id",
      params: {parentId: 356, topFlag: false},
    },
  },
  {
    label: '验证时间',
    field: 'validate',
    component: 'RangePicker',
    required: true,
    componentProps: {
      showTime: true,
        //日期格式化
      format:'YYYY/MM/DD HH:mm',
      valueType: 'Date',
    },
    colProps: { span: 12 },
  },
  {
    label: '验证对象姓名',
    field: 'empName',
    show: false,
    component: 'Input',
  },
  {
    label: '出警人',
    field: 'empNo',
    labelField: 'empName',
    component: 'SearchPerson',
    required: true,
    colProps: { span: 12 },
    componentProps: {
      onChange: (val, opt) => {
        setFieldsValue({
          empDept: opt.orgName
        })
      }
    }
  },
  {
    label: '出警人部门班组',
    field: 'empDept',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
  },
  {
    label: '验证人是否为出警人的直系领导',
    field: 'leaderFlag',
    required: true,
    component: 'RadioGroup',
    labelWidth: 400,
    ifShow: () => {
      return !route.query.formInstanceId
    },
    componentProps: {
      options: [
        { label: '是', value: 0 },
        { label: '否', value: 1 },
      ],
      onChange: (e) => {
        updateSchemaForLaderFlag(e.target.value)
      }
    },
  },
  {
    label: '直系领导姓名',
    field: 'leaderEmpName',
    component: 'Input',
    show: false,
    colProps: { span: 12 },
  },
  {
    label: '验证对象的直系领导',
    field: 'leaderEmpNo',
    labelField: 'leaderEmpName',
    required: true,
    component: 'SearchPerson',
    ifShow: () => {
      return !route.query.formInstanceId || nodeFlag.value == '-1'
    },
    colProps: { span: 12 },
  },
  {
    label: '模拟验证位置',
    field: 'postName',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
  },
  {
    label: '到达现场用时',
    field: 'reachDuration',
    component: 'InputNumber',
    required: true,
    colProps: { span: 12 },
    componentProps: {
      addonAfter: '分钟',
      precision: 0
    },
  },
  {
    label: '验证过程动作及结果',
    field: 'validateDesc',
    required: true,
    component: 'InputTextArea',
    colProps: { span: 12 },
  },
  {
    label: '验证是否合格',
    field: 'isPass',
    component: 'RadioGroup',
    required: true,
    colProps: { span: 12 },
    componentProps: {
      //options里面由一个一个的radio组成,支持disabled
      options: [
        { label: '是', value: 1 },
        { label: '否', value: 0 },
      ],
    },
  },
  {
    label: '是否考核兑现',
    field: 'isExamine',
    required: true,
    component: 'RadioGroup',
    colProps: { span: 12 },
    ifShow: ({values}) => {
      const info = userStore.getUserInfo
      console.log(info.workNo, values.leaderEmpNo, 'rolues');
      
      return values.isPass == 0 && (info.workNo == values.leaderEmpNo || nodeFlag.value == '-1')
    },
    componentProps: {
      //options里面由一个一个的radio组成,支持disabled
      options: [
        { label: '是', value: 1 },
        { label: '否', value: 0 },
      ],
    },
  },
  {
    label: '处理/整改结果',
    field: 'reformDesc',
    component: 'InputTextArea',
    required: true,
    colProps: { span: 12 },
    ifShow: ({values}) => {
      const info = userStore.getUserInfo
      return values.isPass == 0 && (info.workNo == values.leaderEmpNo || nodeFlag.value == '-1')
    },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    colProps: { span: 12 },
  },
];

// 是否直系领导
const updateSchemaForLaderFlag = (val) => {
  // 如果是直系领导
  if (val === 0) {
    const info = userStore.getUserInfo
    setFieldsValue({
      leaderEmpName: info.realName,
      leaderEmpNo: info.workNo
    })
  }else {
    setFieldsValue({
      leaderEmpName: '',
      leaderEmpNo: ''
    })
  }
}
const nodeFlag = ref()
const [registerForm, {setFieldsValue, validate, updateSchema, setProps}] = useForm({
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
  fieldMapToTime: [['validate', ['validateBeginTime', 'validateEndTime'], 'YYYY-MM-DD HH:mm']]
});
const handleSubmit = async() => {
  const formData = await validate()
  console.log(formData);
  const info = userStore.getUserInfo
  const datas = {
    dataType: 2,
    personCategory: -1,
    nodeFlag: (formData.leaderEmpName == info.realName || formData.isPass == 1) ? '-1' : '',
    ...formData,
  }
  if(route.query.formInstanceId) {
    update({...datas, processId: route.query.processId}).then(res => {
      createMessage.success('审批成功')
      router.push({name: 'simulatePostCheck'})
    })
  }else{

    saveOrUpdate(datas).then(res => {
      createMessage.success('流程创建成功')
      router.push({name: 'simulatePostCheck'})
    })
  }
}
const initForm = async () => {
  const info = userStore.getUserInfo
  const {dataList} = await employeeList({email: info.email})
  const [curPerson] = dataList
  let dept = ''
  if (curPerson && curPerson.orgName) {
    dept = curPerson.orgName
  }
  setFieldsValue({
    createDept: dept,
    createEmp: info.realName,
    createEmpNo: info.workNo
  })
}
const getInfo = async () => {
  const res = await view({id: route.query.formInstanceId})
  nodeFlag.value = res.nodeFlag
  if (res.nodeFlag == '-1') {
    setProps({
      showActionButtonGroup: false,
      disabled: true
    })
  }
  setFieldsValue({
    ...res,
    validate: [res.validateBeginTime, res.validateEndTime]
  })
  if(res.isExamine == null) {
    updateSchema([
      { field: 'factoryId', componentProps: { disabled: true, } },
      { field: 'postName', componentProps: { disabled: true, } },
      { field: 'empNo', componentProps: { disabled: true, } },
      { field: 'empDept', componentProps: { disabled: true, } },
      { field: 'validate', componentProps: { disabled: true, } },
      { field: 'teamName', componentProps: { disabled: true, } },
      { field: 'personCategory', componentProps: { disabled: true, } },
      { field: 'validateTeam', componentProps: { disabled: true, } },
      { field: 'validateDesc', componentProps: { disabled: true, } },
      { field: 'isPass', componentProps: { disabled: true, } },
      { field: 'remark', componentProps: { disabled: true, } },
      { field: 'teamId', componentProps: { disabled: true, } },
      { field: 'reachDuration', componentProps: { disabled: true, } },
      
    ])
  }else{
    setProps({disabled: true})
  }
  const info = userStore.getUserInfo
  if (res.leaderEmpNo != info.workNo || res.isExamine != null) {
    setProps({
      showSubmitButton: false
    })
  }
  
}
onMounted(() => {
  if (route.query.formInstanceId) {
    getInfo()
    
  }else {
    initForm()
  }
  
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
:deep(.ant-input-number-group-wrapper) {
  width: 100%;
}
iframe{
  width: 100%;
  height: 600px;
}
</style>