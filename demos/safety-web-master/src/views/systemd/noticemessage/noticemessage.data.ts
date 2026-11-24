import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';

const generalTypeList =  [
  {
    label: '短信提示',
    value: 'note',
    key: 1,
  },
  {
    label: '宇信通提示',
    value: 'shake',
    key: 2,
  },
  {
    label: '物流车入厂申报弹窗',
    value: 'smallPro',
    key: 2,
  },
  {
    label: '进厂宇信通提示',
    value: 'inNote',
    key: 3,
  },
  {
    label: '出厂宇信通提示',
    value: 'outNote',
    key: 4,
  },
  {
    label: '访客短信后缀',
    value: 'SMSLink',
    key: 4,
  },
  {
    label: '绝密区短息提示',
    value: 'secret',
    key: 5,
  },
  {
    label: '绝密区和非绝密区短信提示',
    value: 'unSecret',
    key: 6,
  },
];
export const columns: BasicColumn[] = [
  {
    title: '类型',
    dataIndex: 'generalDesc',
    width: 120,
  },
  {
    title: '开关',
    dataIndex: 'generalShow',
    width: 150,
    slots: { customRender: 'generalShow' },
  },
  {
    title: '提示内容',
    dataIndex: 'content',
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
    label: 'generalType',
    field: 'generalType',
    component: 'Input',
    show: false,
  },
  {
    label: '开关',
    field: 'generalShow',
    component: 'Switch',
    componentProps: {
      checkedValue: 0,
      unCheckedValue: 1,
    },
  },
  {
    label: '提示内容',
    field: 'content',
    component: 'InputTextArea',
    componentProps: {
      maxlength: 500,
      rows: 5
    },
    required: true,
    ifShow: ({ values }) => {
      return values.generalShow === 0 || values.generalType == 'smallPro' // 0为可见，1为不可见;
    },
  },
];
