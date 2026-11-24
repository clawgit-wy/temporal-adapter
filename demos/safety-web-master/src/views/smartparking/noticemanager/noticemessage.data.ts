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
];
export const columns: BasicColumn[] = [
  {
    title: '类型',
    dataIndex: 'generalType',
    width: 120,
    customRender: ({ text }) => {
      return render.renderDictNative(text, generalTypeList, false);
    },
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
      return values.generalShow === 0 // 0为可见，1为不可见;
    },
  },
];
