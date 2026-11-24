<template>
  <div class="container">
      <h1>出门证违规事件上报</h1>
      <BasicForm @register="registerForm" @submit="handleSubmit">
        <template #responsiblePersonEmployeeId="{ model, field }">
          <a-input-group compact>
            <a-input disabled v-model:value="model[field]" style="width: calc(100% - 200px)" />
            <a-button type="primary" @click="searchPerson">选择</a-button>
          </a-input-group>
        </template>
      </BasicForm>
      <div style="padding: 20px;">
        <iframe v-if="getbpmUrl()" :src="getbpmUrl()" id="bpmIframe" style="height: 1350px;"></iframe>
      </div>
      <PersonSelect v-model:visible="visible" @change="perChange"></PersonSelect>
      <div v-if="currentNodeId == 'N4' && userStore.getUserInfo.workNo == currentNodePerWorkNo" class="approve-box">
        <a-button @click="approve" type="primary" :loading="loading">提交</a-button>
      </div>
  </div>
</template>
<script setup lang="ts">
import { useForm, BasicForm, FormSchema } from '/@/components/Form';
import { useUserStore } from '/@/store/modules/user';
import '/@/assets/bpm/domain'
import {create, employeeList, view, fieldTree, sendApprove} from './list.api'
import { onMounted, ref } from 'vue';
import { useMessage } from '/@/hooks/web/useMessage';
import { useRoute, useRouter } from 'vue-router';
import dayjs from 'dayjs'
import {useTabs} from '/@/hooks/web/useTabs'
import PersonSelect from '/@/components/PersonSelect.vue'
import { useGlobSetting } from '/@/hooks/setting';
import '/@/assets/bpm/domain'
import { router } from '/@/router';
const globSetting = useGlobSetting();
const {bpmUrl} = globSetting;
const { createMessage } = useMessage();
const userStore = useUserStore();
const route = useRoute()
const visible = ref(false)

const searchPerson = () => {
  visible.value = true
}

const getbpmUrl = () => {
  const query = location.href.split('?')[1]
  if (query && !query.includes('processId')) {
    return false
  }
  if(query){
    const url = `${bpmUrl}?method=showProcess&${query}`
    return url
  }

  
}

//自定义表单字段
const formSchemas: FormSchema[] = [
  {
      label: '上报人信息',
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
    field: 'reporterName',
    componentProps: {
      disabled: true,
    },
    required: true,
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '员工号',
    field: 'reporterEmployeeId',
    component: 'Input',
    required: true,
    colProps: { span: 6 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '部门班组',
    field: 'reporterDepartment',
    required: true,
    component: 'Input',
    colProps: { span: 6 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '上报时间',
    field: 'creationTime',
    component: 'Input',
    required: true,
    colProps: { span: 6 },
    componentProps: {
      disabled: true,
    },
    defaultValue: dayjs().format('YYYY-MM-DD HH:mm:ss')
  },
  {
      label: '事件信息',
      field: 'dLine',
      component: 'Divider',
  },
  {
    label: '关联流程编号',
    field: 'flowNo',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {disabled: true}
  },
  {
    label: '关联流程Id',
    field: 'relatedProcessId',
    component: 'Input',
    show: false,
    colProps: { span: 12 },
  },
  {
    label: '关联流程主题',
    field: 'subject',
    component: 'Input',
    componentProps: {disabled: true},
    colProps: { span: 12 },
  },
  {
    label: '厂区',
    field: 'siteName',
    show: false,
    component: 'Input',
  },
  {
    label: '厂区',
    field: 'siteId',
    labelField: 'siteName',
    component: 'FactoryId',
    required: true,
    colProps: { span: 8, className: 'ant-col ant-col-8 langer-label' },
  },
  {
    label: '事件发生时间',
    field: 'eventOccurrenceTime',
    component: 'DatePicker',
    required: true,
    componentProps: {
      showTime: true,
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      valueType: 'Date',
    },
    colProps: { span: 8, className: 'ant-col ant-col-8 langer-label' },
  },
  {
    label: '具体位置',
    field: 'specificLocation',
    component: 'Input',
    required: true,
    colProps: { span: 8, className: 'ant-col ant-col-8 langer-label' },
  },
  {
    label: '事件类型',
    field: 'eventType',
    component: 'ApiSelect',
    required: true,
    colProps: { span: 8, className: 'ant-col ant-col-8 langer-label' },
    componentProps: {
      api: fieldTree,
        params: {
          fieldType: 'inspection001_zaxclx_0',
          topFlag: false,
        },
        valueField: 'fieldName',
        labelField: 'fieldName',
      // options: [
      //   {label: '治安违规类', value: '治安违规类'},
      //   {label: '治安隐患类', value: '治安隐患类'},
      //   {label: '消防违规类', value: '消防违规类'},
      //   {label: '消防隐患类', value: '消防隐患类'},
      //   {label: '能源类', value: '能源类'},
      //   {label: '其他', value: '其他'},
      // ]
    },
  },
  {
    label: '问题类型',
    field: 'issueType',
    component: 'Select',
    required: true,
    colProps: { span: 8, className: 'ant-col ant-col-8 langer-label' },
    componentProps: {
      options: [
        {label: '流程超时', value: '流程超时'},
        {label: '放行异常', value: '放行异常'},
        {label: '抽查异常', value: '抽查异常'},
        {label: '超时未接收', value: '超时未接收'},
        {label: '未接收/部分接收', value: '未接收/部分接收'},
        {label: '超时未出厂', value: '超时未出厂'},
        {label: '申请照片上传不规范', value: '申请照片上传不规范'},
        {label: '填写不规范', value: '填写不规范'},
        {label: '票务不符', value: '票务不符'},
        {label: '多开少拉', value: '多开少拉'},
        {label: '少开多拉', value: '少开多拉'},
        {label: '其他', value: '其他'},
      ]
    },
  },
  {
    label: '事件等级',
    field: 'eventLevel',
    component: 'Select',
    required: true,
    colProps: { span: 8, className: 'ant-col ant-col-8 langer-label' },
    componentProps: {
      //options里面由一个一个的radio组成,支持disabled
      options: [
        { label: '一般隐患/违规', value: '一般隐患/违规' },
        { label: '较大隐患/违规', value: '较大隐患/违规' },
        { label: '重大隐患/违规', value: '重大隐患/违规' },
      ],
    },
  },
  {
    label: '问题描述',
    field: 'issueDescription',
    required: true,
    component: 'InputTextArea',
    colProps: { span: 24, className: 'ant-col ant-col-24 small-label' },
  },
  {
    label: '责任人姓名',
    field: 'responsiblePersonName',
    // show: false,
    slot: 'responsiblePersonEmployeeId',
    component: 'Input',
    colProps: { span: 12 },
  },
  {
    label: '责任人EIP',
    field: 'responsiblePersonEip',
    show: false,
    component: 'Input',
  },
  {
    label: '责任人工号',
    field: 'responsiblePersonEmployeeId',
    labelField: 'empName',
    component: 'Input',
    show: false,
  },
  {
    label: '责任部门',
    field: 'responsiblePersonDepartment',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
    componentProps: {disabled: true}
  },
  {
      label: '整改措施',
      field: 'divisionLines',
      component: 'Divider',
      show: () => !!route.query.formInstanceId,
  },
  {
    label: '整改完成时间',
    field: 'remediationCompletionTime',
    component: 'DatePicker',
    required: true,
    ifShow: () => !!route.query.formInstanceId,
    componentProps: {
      showTime: true,
        //日期格式化
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      valueType: 'Date',
    },
    colProps: { span: 8, className: 'ant-col ant-col-8 langer-label' },
  },
  {
    label: '整改负责人员工号',
    field: 'remediationEmployeeId',
    component: 'Input',
    required: true,
    ifShow: () => !!route.query.formInstanceId,
    colProps: { span: 8, className: 'ant-col ant-col-8 langer-label' },
    componentProps: {
      onBlur(e) {
        employeeList({empNo: e.target.value}).then(res => {
          if (res.dataList.length > 0) {
            const person = res.dataList[0]
              setFieldsValue({
                remediationName: person.empName,
                remediationEip: person.email.split('@')[0],
                remediationPhoneNumber: person.phoneNo
              })
          }
          
        })
      },
      // onChange: (val, opt) => {
      
      // }
    }
  },
  {
    label: '整改负责人姓名',
    field: 'remediationName',
    component: 'Input',
    required: true,
    ifShow: () => !!route.query.formInstanceId,
    colProps: { span: 8, className: 'ant-col ant-col-8 langer-label' },
  },
  {
    label: '整改负责人EIP',
    field: 'remediationEip',
    show: false,
    ifShow: () => !!route.query.formInstanceId,
    component: 'Input',
  },
  {
    label: '整改负责人手机号',
    field: 'remediationPhoneNumber',
    show: false,
    ifShow: () => !!route.query.formInstanceId,
    component: 'Input',
  },
  {
    label: '整改措施',
    field: 'remediationMeasure',
    required: true,
    ifShow: () => !!route.query.formInstanceId,
    component: 'InputTextArea',
    colProps: { span: 24, className: 'ant-col ant-col-24 small-label' },
  },
];
const perChange = (per) => {
  setFieldsValue({
    responsiblePersonEmployeeId: per.empNo,
    responsiblePersonDepartment: per.orgName,
    responsiblePersonEip: per.email.split('@')[0],
    responsiblePersonName: per.empName
  })
  visible.value = false
}
const currentNodeId = ref()
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
});
const loading = ref(false)
const handleSubmit = async() => {
  const formData = await validate()
  const {eventOccurrenceTime, siteName, eventType, issueType} = formData
  loading.value = true
  create({...formData, processSubject: `${eventOccurrenceTime}-${siteName}-${eventType}-${issueType}-出门证事件上报`, exceptionId: route.query.id}).then((res) => {
    createMessage.success('操作成功')
    router.replace({name: 'abnormal'})
  }).finally(() => {
    loading.value = false
  })
  // if(route.query.formInstanceId) {
  //   update({...datas, processId: route.query.processId}).then(res => {
  //     createMessage.success('审批成功')
  //     router.push({name: 'spotPostCheck'})
  //   })
  // }else{

  //   saveOrUpdate(datas).then(res => {
  //     createMessage.success('流程创建成功')
  //     router.push({name: 'spotPostCheck'})
  //   })
  // }
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
    subject: route.query.subject,
    relatedProcessId: route.query.relatedProcessId,
    flowNo: route.query.flowNo,
    reporterDepartment: dept,
    reporterName: info.realName,
    reporterEmployeeId: info.workNo
  })
}
const currentNodePerWorkNo = ref('')
const remediationEmployeeId = ref('')
const getInfo = async () => {
  const res = await view({id: route.query.formInstanceId})
  currentNodeId.value = res.currentNodeId
  currentNodePerWorkNo.value = res.responsiblePersonEmployeeId
  remediationEmployeeId.value = res.remediationEmployeeId
  setFieldsValue({
    ...res,
    creationTime: dayjs(res.creationTime).format('YYYY-MM-DD HH:mm:ss'),
    eventOccurrenceTime: dayjs(res.eventOccurrenceTime).format('YYYY-MM-DD HH:mm:ss'),
    remediationCompletionTime: res.remediationCompletionTime ? dayjs(res.remediationCompletionTime).format('YYYY-MM-DD HH:mm:ss') : '',
    siteId: Number(res.siteId)
  })
  if(route.query.formInstanceId) {
    updateSchema([
      { field: 'reporterName', componentProps: { disabled: true, } },
      { field: 'reporterEmployeeId', componentProps: { disabled: true, } },
      { field: 'reporterDepartment', componentProps: { disabled: true, } },
      { field: 'creationTime', componentProps: { disabled: true, } },
      { field: 'siteId', componentProps: { disabled: true, } },
      { field: 'eventOccurrenceTime', componentProps: { disabled: true, } },
      { field: 'specificLocation', componentProps: { disabled: true, } },
      { field: 'eventType', componentProps: { disabled: true, } },
      { field: 'eventLevel', componentProps: { disabled: true, } },
      { field: 'issueDescription', componentProps: { disabled: true, } },
      { field: 'responsiblePersonEmployeeId', componentProps: { disabled: true, } },
      { field: 'issueType', componentProps: { disabled: true, } },
      { field: 'responsiblePersonDepartment', componentProps: { disabled: true, } },
    ])
  }
  if (res.remediationEmployeeId) {
    setProps({disabled: true})
  }
  const info = userStore.getUserInfo
  if (res.leaderEmpNo != info.workNo || res.isExamine != null) {
    setProps({
      showSubmitButton: false
    })
  }
  
}
// 获取流程
const getBpmData = () => {
  return new Promise((resolve, reject) => {
    const flowIFrame = document.getElementById('bpmIframe');
    const domain = window?.domain || '';
    if (domain) {
      domain.call(flowIFrame.contentWindow, 'lbpm_getApprovalData', [], function (data) {
        const res = JSON.parse(data);
        const result = res['sysWfBusinessForm.fdParameterJson'];
        if (result) {
          resolve(JSON.parse(result));
        }
        reject('获取iframe数据失败');
      });
    } else {
      reject();
    }
  });
};

const approve = async() => {
  let fdParameterJson:any = ''
  fdParameterJson = await getBpmData()
  const {remediationCompletionTime, remediationEmployeeId, remediationName, remediationEip, remediationPhoneNumber, remediationMeasure, eventOccurrenceTime, siteName, eventType, issueType} = await validate()
  const data = {
    processParam: {
      type: 'audit', formId: 'goods_out_events', modelId: 'safety_goods_out_events_process', subject: `${eventOccurrenceTime}-${siteName}-${eventType}-${issueType}出门证事件上报`,
      fdParameterJson: JSON.stringify({'sysWfBusinessForm.fdParameterJson': fdParameterJson}),
      flowId: route.query.processId
    },
    remediationCompletionTime,
    remediationEmployeeId,
    remediationName,
    remediationEip,
    remediationPhoneNumber,
    remediationMeasure,
    id: route.query.formInstanceId

  }
  sendApprove(data).then(() => {
    createMessage.success('操作成功')
    router.replace({name: 'abnormal'})
  })
  
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
.approve-box{
  position: fixed;
  width: 100%;
  bottom: 0;
  left: 0;
  padding: 10px;
  background-color: #fff;
  display: flex;
  justify-content: center;
}
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
:deep(.langer-label .ant-form-item-label) {
  flex: 0 0 25% !important;
  max-width: none;
}
:deep(.small-label .ant-form-item-label) {
  flex: 0 0 8.3% !important;
  max-width: none;
}
:deep(.langer-label .ant-form-item-control) {
  max-width: 65% !important;
}
:deep(.ant-picker-default) {
  width: 100% !important;
}

:deep(.langer-label) {
  max-width: 33% !important;
}
</style>