import { BasicColumn, FormSchema } from '/@/components/Table';
import { getEmpPost } from './api'
import { render } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';

const visitorList = [
  {
    label: '是',
    value: 0,
    key: 0,
  },
  {
    label: '否',
    value: 1,
    key: 1,
  },
]
export const columns: BasicColumn[] = [
  {
    title: '类别',
    dataIndex: 'positionType',
    width: 120,
  },
  {
    title: '岗位',
    dataIndex: 'positionName',
    width: 100,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 150,
  },
  {
    title: '是否可当被访人',
    dataIndex: 'visitorState',
    customRender: ({ text }) => {
      return render.renderDictNative(text, visitorList, false);
    },
    width: 100,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 150,
  },
  {
    title: '修改时间',
    dataIndex: 'updateTime',
    customRender: ({ text }) => dayjs(text).format('YYYY-MM-DD HH:mm:ss'),
    width: 100,
  },
  
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '岗位',
    field: 'positionName',
    component: 'Input',
  },
  {
    label: '类别',
    field: 'positionType',
    component: 'Select',
    componentProps: {
      options: [
        { label: '外包岗位', value: '外包岗位'},
        { label: '直接岗位', value: '直接岗位'},
        { label: '间接岗位', value: '间接岗位' },
        { label: '空', value: '空' },
      ],
    }
  },
  {
    label: '是否可当被访人',
    field: 'visitorState',
    component: 'Select',
    componentProps: {
      options: visitorList
    },
  }, 
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
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
    label: '类别',
    field: 'positionType',
    component: 'Select',
    required: true,
    componentProps: {
      options: [
        { label: '外包岗位', value: '外包岗位'},
        { label: '直接岗位', value: '直接岗位'},
        { label: '间接岗位', value: '间接岗位' },
        { label: '空', value: '空' },
      ],
    },
  },
  {
    label: '岗位',
    field: 'positionName',
    component: 'Input',
  },
  {
    label: '优先级',
    required: true,
    field: 'priority',
    component: 'Select',
    componentProps: {
      options: [
        {label: 1, value: 1},
        {label: 2, value: 2},
        {label: 3, value: 3},
        {label: 4, value: 4},
        {label: 5, value: 5},
        {label: 6, value: 6},
        {label: 7, value: 7},
        {label: 8, value: 8},
        {label: 9, value: 9},
        {label: 10, value: 10},
      ]
    },
  },
  {
    label: '是否可当被访人',
    field: 'visitorState',
    component: 'RadioGroup',
    defaultValue: 1,
    componentProps: {
      options: visitorList
    },
  }, 
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    componentProps: {
      maxlength: 100,
    },
  },
];
