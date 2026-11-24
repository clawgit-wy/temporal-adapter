import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    "title": "姓名",
    "dataIndex": "employeeName",
    "width": 80
  },
  {
    "title": "员工号",
    "dataIndex": "employeeCode",
    "width": 80
  },
  {
    "title": "岗位",
    "dataIndex": "positionName",
    "width": 120
  },
  {
    "title": "责任部门",
    "dataIndex": "dutyDeptName",
    "width": 120
  },
  {
    "title": "可授权白名单人数",
    "dataIndex": "emposerNum",
    "width": 80
  },
  {
    "title": "管理门禁",
    "dataIndex": "deviceName",
    "width": 170
  },
  {
    "title": "可管理门禁数",
    "dataIndex": "buildNum",
    "width": 80
  },
  {
    "title": "更新时间",
    "dataIndex": "createTime",
    "width": 100
  },
  
  { title: '来源', dataIndex: 'processId', width: 140, slots: { customRender: 'processId' } },
 
]

export const searchFormSchema: FormSchema[] =
  [
    {
      label: '姓名',
      field: 'employeeName',
      "component": "Input",
      "componentProps": {
        "placeholder": "请输入姓名"
      }
    },
    {
      "label": "员工号",
      "field": "employeeCode",
      component: 'Input',
      colProps: { span: 8 },
      getLabel:true,
      "componentProps": {
        "placeholder": "请输入员工号"
      }
    },
    {
      label: '责任部门',
      field: 'dutyDeptCode',
      "component": "Input",
      show:false,
    },
    {
      label: '责任部门',
      field: 'dutyDeptName',
      "component": "Input",
      slot:'select',
      "componentProps": {
        "placeholder": "请选择"
      }
    },
    {
      label: '管理门禁',
      field: 'deviceCode',
      show:false,
      "component": "Input",
      "componentProps": {
        "placeholder": "请输入管理门禁"
      }
    },
    {
      label: '管理门禁',
      field: 'deviceName',
      slot:'deviceName',
      "component": "Input",
      "componentProps": {
        "placeholder": "请输入管理门禁"
      }
    },
  ]


export const scmSchema: FormSchema[] =
  [
    {
      "label": "部门",
      "field": "dutyDeptCode",
      "component": "Input",
      show:false,
    },
    {
      "label": "部门",
      "field": "dutyDeptName",
      "component": "Input",
      "colProps": { "span": 24 },
      slot:'department',
      "componentProps": {
        "placeholder": "请选择"
      },
    },
    {
      "label": "可授权白名单数",
      "field": "emposerNum",
      "component": "Input",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请输入可授权白名单数"
      }
    },
    {
      "label": "可管理门禁数",
      "field": "buildNum",
      "component": "Input",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请输入可管理门禁数"
      }
    },
    
  ]