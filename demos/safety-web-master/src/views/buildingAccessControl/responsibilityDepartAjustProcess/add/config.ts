import { FormSchema } from '/@/components/Form';
import { BasicColumn } from '/@/components/Table';
import { fieldTree } from './field.api';
import {
  systemFlagOptions,
} from './const';
import { Modal } from 'ant-design-vue';
// 申请人信息
export function GetApplicantSchemas(isDisabled: boolean) {
  return [

    {
      label: '主题',
      field: 'processSubject',
      component: 'Input',
      colProps: { span: 12 },
      required: true,
      show: false,
      componentProps: {
        disabled: true,
        placeholder: '请输入主题',
        defaultValue: '申请楼宇门禁责任部门调整流程'
      }
    },
    {
      label: '角色',
      field: 'roleType',
      component: 'Input',
      colProps: { span: 12 },
      show: false,
    },

    {
      field: 'divider-create',
      component: 'Divider',
      label: '申请人信息',
    },

    {
      label: 'eip',
      field: 'createEmployeeLogin',
      component: 'SearchPerson',
      colProps: { span: 8 },
      show: false,
    },
    {
      label: '姓名',
      field: 'createEmployeeName',
      component: 'SearchPerson',
      colProps: { span: 8 },
      componentProps: {
        disabled: true,
      },
    },
    {
      label: '员工号',
      field: 'createEmployeeNo',
      component: 'Input',
      colProps: { span: 8 },
      componentProps: {
        disabled: true,
      },
    },
    {
      label: '申请人部门编码',
      field: 'createDeptCode',
      component: 'SearchPerson',
      colProps: { span: 8 },
      show: false,
    },

    {
      label: '提交人部门',
      field: 'createDeptName',
      component: 'Input',
      show: true,
      colProps: { span: 8 },
      componentProps: {
        disabled: true,
      },
    },
    {
      label: '责任部门编码',
      field: 'dutyDeptCode',
      component: 'SearchPerson',
      colProps: { span: 8 },
      show: false,
    },
    {
      label: '责任部门编码路径',
      field: 'dutyDeptCodePath',
      component: 'SearchPerson',
      colProps: { span: 8 },
      show: false,
    },
    {
      label: '责任部门',
      field: 'dutyDeptName',
      component: 'ApiSelect',
      colProps: { span: 12 },
      slot:'department',
      componentProps: {
        placeholder: '请选择责任部门',
      },
    },

    {
      label: '',
      field: 'buildingList',
      component: 'ApiSelect',
      // required: true,
      colProps: { span: 24 },
      slot:'buildingList',
      componentProps: {
        disabled: false,
      },
    },
    {
      label: '本部门部门长编码',
      field: 'leaderEmployeeLogin',
      component: 'SearchPerson',
      colProps: { span: 8 },
      show: false,
    },
    {
      label: '事由',
      field: 'remark',
      component: 'Input',
      required: true,
      colProps: { span: 24 },
      componentProps: {
        disabled: isDisabled,
        maxlength: 60,
      },
    },
    {
      label: '本部门部门长',
      field: 'leaderEmployeeName',
      component: 'Select',
      required: true,
      colProps: { span: 24 },
      slot:'departmentManager',
      componentProps: {
        placeholder: '请选择本部门部门长',
      },
    },

    {
      label: '部门门禁管理员对应eip角色',
      field: 'buildSysManagerRole',
      component: 'Select',
      required: false,
      show:false,
      colProps: { span: 12 },
      slot:'buildSysManagerRole',
      componentProps: {
        placeholder: '请输入角色名称或者编码后选择',
      },
    },
    {
      label: '部门门禁管理员对应eip角色',
      field: 'buildSysManagerRoleCode',
      component: 'Select',
      required: false,
      show:false,
      colProps: { span: 12 },
      componentProps: {
        placeholder: '请输入角色名称或者编码后选择',
      },
    },
    {
      label: '',
      field: 'tips',
      component: 'Select',
      colProps: { span: 24 },
      slot:'tips',
    },

  ];
}

// 门禁清单	
export const buildingColumns: BasicColumn[] = [
  {
    title: '目前管辖门禁',
    dataIndex: 'oldDeviceName',
    key: 'oldDeviceName',
    slots: {customRender: 'deviceName'},
    
  },
  {
    title: '变更后管辖门禁',
    dataIndex: 'newDeviceName',
    key: 'newDeviceName',
    slots: {customRender: 'newDeviceName'},
  },
  {
    title: '目前部门门禁管理员',
    dataIndex: 'oldDepartEmployeeName',
    key: 'oldDepartEmployeeName',
    width: 150,
    slots: {customRender: 'oldDepartEmployeeName'},
  },
  {
    title: '变更后部门门禁管理员',
    dataIndex: 'newDepartEmployeeName',
    key: 'newDepartEmployeeName',
    slots: {customRender: 'newEmployeeName'},
  },
];

