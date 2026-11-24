import { BasicColumn, FormSchema, Search } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';

export const columns: BasicColumn[] = [
  {
    title: '车牌号',
    dataIndex: 'carNo',
    width: 120,
  },
  {
    title: '员工号',
    dataIndex: 'staffNo',
    width: 150,
  },
  {
    title: '姓名',
    dataIndex: 'staffName',
    width: 100,
  },
  {
    title: '厂区',
    dataIndex: 'factory',
    width: 100,
  },
  {
    title: '类型',
    dataIndex: 'carType',
    width: 100,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 100,
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
    label: '车牌号',
    field: 'carNo',
    component: "Input",

  },
  {
    label: '员工号',
    field: 'staffNo',
    component: 'Input',
    slot: 'staffNo'
  },
  {
    label: '姓名',
    field: 'staffName',
    component: 'Input'
  },
  {
    label: '厂区',
    field: 'factory',
    component: 'Factory',
  },
  {
    label: '类型',
    field: 'carType',
    required: true,
    component: 'Select',
    componentProps: {
      options: [
        {
          label: '员工车辆',
          value: '员工车辆',
          key: '1',
        },
        {
          label: '外协车辆',
          value: '外协车辆',
          key: '2',
        },
      ],
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
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '车牌号',
    field: 'carNo',
    component: 'Input',
  },
  {
    label: '厂区',
    field: 'factory',
    component: 'Factory',
  },
  {
    label: '类型',
    field: 'carType',
    component: 'Select',
    componentProps: {
      options: [
        {
          label: '员工车辆',
          value: '员工车辆',
          key: '1',
        },
        {
          label: '外协车辆',
          value: '外协车辆',
          key: '2',
        },
      ],
    },
  },
  {
    label: '员工号',
    field: 'staffNo',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'staffName',
    component: 'Input',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
] 