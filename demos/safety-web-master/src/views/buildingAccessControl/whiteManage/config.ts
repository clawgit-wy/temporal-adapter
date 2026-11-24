import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: "姓名",
    dataIndex: "employeeName",
    width: 80
  },
  {
    title: "员工号",
    dataIndex: "employeeCode",
    width: 80
  },
  {
    title: "岗位",
    dataIndex: "positionName",
    width: 120
  },
  {
    title: "责任部门",
    dataIndex: "dutyDeptName",
    width: 120
  },
  
  {
    title: "授权门禁",
    dataIndex: "deviceName",
    width: 170
  },
  {
    title: "添加角色",
    dataIndex: "createUserRole",
    width: 80
  },
  {
    title: "添加人",
    dataIndex: "createUserName",
    width: 80
  },
  {
    title: "添加时间",
    dataIndex: "createTime",
    width: 100
  },
  {
    title: "原因",
    dataIndex: "createReason",
    width: 170
  },
  
 
 
]

export const searchFormSchema: FormSchema[] =
  [
    {
      label: '姓名',
      field: 'employeeName',
      component: "Input",
      componentProps: {
        placeholder: "请输入姓名"
      }
    },
    {
      label: "员工号",
      field: "employeeCode",
      component: 'Input',
      colProps: { span: 8 },
      getLabel:true,
      componentProps: {
        placeholder: "请输入员工号"
      }
    },
    {
      label: '责任部门',
      field: 'dutyDeptName',
      component: "Input",
      componentProps: {
        placeholder: "请输入"
      }
    },
    {
      label: '责任部门',
      field: 'dutyDeptName',
      show:false,
      component: "Input",
      slot:'select',
      componentProps: {
        placeholder: "请选择"
      }
    },
    {
      label: '添加人',
      field: 'createUserName',
      component: "Input",
      componentProps: {
        placeholder: "请输入"
      }
    },
    {
      label: '原因',
      field: 'createReason',
      component: "Input",
      componentProps: {
        placeholder: "请输入"
      }
    },
    
  ]


export const scmSchema: FormSchema[] =
  [
    {
      label: "员工号",
      field: "employeeCode",
      required:true,
      component: "Input",
      slot:"empNo",
      componentProps: {
        placeholder: "请输入"
      },
    },
    {
      label: "姓名",
      field: "employeeName",
      required:true,
      component: "Input",
      colProps: { "span": 24 },
      componentProps: {
        placeholder: "自动带出",
        disabled:true,
      },
    },
    {
      label: "添加原因",
      field: "createReason",
      required:true,
      colProps: { "span": 24 },
      component: 'InputTextArea',
      componentProps: {
        placeholder: "请输入",
        maxlength: 100,
        showCount: true,
      }
    },
    {
      label: "岗位",
      field: "positionCode",
      show:false,
      required:true,
      component: 'InputTextArea',
    },
    {
      label: "岗位",
      field: "positionName",
      show:false,
      required:true,
      component: 'InputTextArea',
    },
    
    
  ]