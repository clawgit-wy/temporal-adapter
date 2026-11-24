import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: "镜头编号",
    dataIndex: "lensNum",
  },
  {
    title: "镜头名称",
    dataIndex: "lensName",
  },
  {
    title: "SN号",
    dataIndex: "lensSn",
  },
  {
    title: "IP地址",
    dataIndex: "lensIp",
  },
  {
    title: "型号",
    dataIndex: "lensModel",
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
