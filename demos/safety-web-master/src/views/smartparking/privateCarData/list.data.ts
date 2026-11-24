import { BasicColumn, FormSchema, Search } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import { getGroupTree } from './list.api';
import { h } from 'vue';

export const columns: BasicColumn[] = [
  {
    title: '车牌号',
    dataIndex: 'carNo',
    width: 100,
  },
  {
    title: '车辆颜色',
    dataIndex: 'carColor',
    width: 100,
  },
  {
    title: '车辆型号',
    dataIndex: 'vehicleType',
    width: 100,
  },
  {
    title: '行驶证',
    dataIndex: 'drivingLicenseUrl',
    width: 100,
    customRender: ({text}) => h('a', { href: text, target: '_blank' }, '查看')
  },
  {
    title: '员工姓名',
    dataIndex: 'employeeName',
    width: 100,
  },
  {
    title: '员工号',
    dataIndex: 'employeeNo',
    width: 100,
  },
  {
    title: '手机号',
    dataIndex: 'phoneNo',
    width: 100,
  },
  {
    title: '驾驶证',
    dataIndex: 'driverCard',
    width: 100,
    customRender: ({text}) => h('a', { href: text, target: '_blank' }, '查看')
  },
  {
    title: '是否本人车辆',
    dataIndex: 'myselfCar',
    width: 100,
  },
  {
    title: '所属部门',
    dataIndex: 'deptNo',
    width: 600,
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
    label: '员工号',
    field: 'staffNo',
    required: true,
    component: 'Input',
    slot: 'staffNo'
  },
  {
    label: '姓名',
    field: 'staffName',
    component: 'Input',
    componentProps: {
      disabled: true,
    }
  },
  {
    label: '部门',
    field: 'staffGroup',
    component: 'Input',
    componentProps: {
      disabled: true,
    }
  },
  {
    label: '公出开始时间',
    field: 'beginDate',
    component: 'DatePicker',
    required: true,
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '公出结束时间',
    field: 'endDate',
    component: 'DatePicker',
    required: true,
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    componentProps: {
      maxlength: 500,
      rows: 5
    },
  }
];
export const searchFormSchema: FormSchema[] =[
  {
    label: '车牌号',
    field: 'carNo',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'employeeName',
    component: 'Input',
  },
  {
    label: '员工号',
    field: 'employeeNo',
    component: 'Input',
  },
  {
    label: '所属部门',
    field: 'deptNo',
    component: 'Input',
  },
  
] 