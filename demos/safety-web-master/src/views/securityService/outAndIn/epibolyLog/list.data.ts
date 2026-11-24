import { BasicColumn, FormSchema } from '/@/components/Table';
import dayjs from 'dayjs';

const statusDic = {
  '10': '草稿',
  '20': '审批中',
  '21': '驳回',
  '30': '已审批',
  '31': '手动添加',
  '40': '废弃',
  '50': '已过期',
  '60': '超时结束',
}
export const columns: BasicColumn[] = [
  { title: '提交人', dataIndex: 'submitter', width: 100 },
  { title: '提交人员工号', dataIndex: 'submitterId', width: 110 },
  { title: '部门', dataIndex: 'department',ellipsis: false, width: 200,},
  {
    title: '外协人员',
    dataIndex: 'cooperativePerson',
    width: 100,
  },
  {
    title: '外协人员员工号',
    dataIndex: 'cooperativePersonId',
    width: 120,
    ellipsis: false,
  },
  {
    title: '外协组织',
    dataIndex: 'cooperativeOrganization',
    width: 200,
    ellipsis: false,
  },
  
  {
    title: '变更前厂区',
    dataIndex: 'authorizedFacility',
    width: 200,
    ellipsis: false,
    customRender: ({ text }) => {
      if (!text) {
        return '无';
      } else {
        return text;
      }
    },
  },
  {
    title: '变更后厂区',
    dataIndex: 'changedFacility',
    width: 200,
    ellipsis: false,
  },
  {
    title: '变更前日期',
    dataIndex: 'beforeEndDate',
    ellipsis: false,
    ifShow:true,
   
  },
  {
    title: '变更后日期',
    dataIndex: 'afterEndDate',
    ellipsis: false,
    ifShow:true,
  },
  {
    title: '任职部门科长',
    dataIndex: 'departmentHead',
    width: 110,
  },
  {
    title: '流程状态',
    dataIndex: 'processStatus',
    width: 90,
    customRender: ({ text }) => statusDic[text]
  },
  
  {
    title: '提交时间',
    dataIndex: 'submissionTime',
    width: 100,
    ellipsis: false,
    customRender: ({text}) => dayjs(text).format('YYYY-MM-DD HH:mm:ss')
  },
];
export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '主题',
    field: 'title',
    component: 'Input',
    required: true,
    componentProps: {
      disabled: true
    }
  },
  {
    label: '提交人',
    field: 'creatorName',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
    componentProps: {
      disabled: true
    }
  },
  {
    label: '员工号',
    field: 'creator',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
    componentProps: {
      disabled: true
    }
  },
  {
    label: '部门',
    field: 'creatorDept',
    component: 'Input',
    colProps: { span: 12 },
    required: true,
    componentProps: {
      disabled: true
    }
  },
  {
    label: '联系方式',
    field: 'creatorPhone',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
    componentProps: {
      disabled: true
    }
  },
  {
    label: '业务说明',
    field: 'serviceDesc',
    component: 'InputTextArea',
    required: true,
  },
];


export const searchFormSchema: FormSchema[] =[
  { label: '提交人', labelWidth: 80, field: 'submitter', component: 'Input' },
  { label: '提交人员工号', field: 'submitterId', component: 'Input' },
  { label: '部门', field: 'department', component: 'Input' },
  { label: '外协人员', field: 'cooperativePerson', component: 'Input' },
  { label: '外协人员员工号', labelWidth: 80, field: 'cooperativePersonId', component: 'Input' },
  { label: '外协组织', labelWidth: 80, field: 'cooperativeOrganization', component: 'Input' },
  // { label: '变更前厂区', labelWidth: 80, field: 'authorizedFacility', component: 'Input' },
  // { label: '变更后厂区', labelWidth: 80, field: 'changedFacility', component: 'Input' },
  { label: '任职部门科长', labelWidth: 80, field: 'departmentHead', component: 'Input' },
  { label: '流程状态', labelWidth: 80, field: 'processStatus', component: 'Select',
    componentProps: {
      options: [
        {label: '草稿', value: '10'},
        {label: '审批中', value: '20'},
        {label: '驳回', value: '21'},
        {label: '已审批', value: '30'},
        {label: '手动添加', value: '31'},
        {label: '废弃', value: '40'},
        {label: '已过期', value: '50'},
        {label: '超时结束', value: '60'},
      ]
    }
  },
  { label: '提交时间', labelWidth: 80, field: 'submissionTime', component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    }
  },
  

]
