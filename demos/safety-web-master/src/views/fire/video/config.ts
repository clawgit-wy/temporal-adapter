import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: "事件",
    dataIndex: "infoName",
  },
  {
    title: "提醒内容",
    dataIndex: "infoContent",
  },
  {
    title: "开关",
    dataIndex: "showFlag",
    width: 80,
    slots: { customRender: 'generalShow' },
  },
 
]

export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: 'infoName',
    field: 'infoName',
    component: 'Input',
    show: false,
  },
  {
    label: '开关',
    field: 'showFlag',
    component: 'Switch',
    componentProps: {
      checkedValue: '1',
      unCheckedValue: '0',
    },
  },
  {
    label: '提醒内容',
    field: 'infoContent',
    component: 'InputTextArea',
    componentProps: {
      maxlength: 500,
      rows: 5
    },
    required: true,
    ifShow: ({ values }) => {
      return values.showFlag == 1 || values.showFlag == 'smallPro' // 1为可见，0为不可见;
    },
  },
];
