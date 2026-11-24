import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '名称',
    dataIndex: 'fieldName',
    align: 'left',
    width: 200,
  },
  // {
  //   title: '移动',
  //   dataIndex: 'fieldSort',
  //   width: 100,
  //   slots: { customRender: 'fieldSort' },
  // },
];
export const factoryColumns: BasicColumn[] = [
  {
    title: '名称',
    dataIndex: 'fieldName',
    align: 'left',
    width: 200,
  },
  {
    title: '编码',
    dataIndex: 'fieldCode',
    width: 160,
    customRender: ({text, record}) => record.fieldType == 'factory' ? text : ''
  }
  // {
  //   title: '移动',
  //   dataIndex: 'fieldSort',
  //   width: 100,
  //   slots: { customRender: 'fieldSort' },
  // },
];
export const moveColumns: BasicColumn[] = [
  {
    title: '名称',
    dataIndex: 'fieldName',
    width: 80,
    align: 'left',
    customRender: ({text, record}) => {
      if (record.fieldType == 'factory' && record.fieldCode) {
        return `${text} (${record.fieldCode})`
      }else {
        return text
      }
    }
  },
  {
    title: '移动',
    dataIndex: 'fieldSort',
    width: 100,
    slots: { customRender: 'fieldSort' },
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
    label: '父元素',
    field: 'parentId',
    component: 'Input',
    show: false,
  },
  {
    label: '类型',
    field: 'fieldType',
    component: 'Input',
    show: false,
  },
  {
    label: '等级',
    field: 'fieldLevel',
    component: 'Input',
    show: false,
  },
  {
    label: 'code',
    field: 'fieldCode',
    component: 'Input',
    show: false,
  },
  {
    label: '名称',
    field: 'fieldName',
    component: 'Input',
    required: true,
  },
];

export const formSchemasVisit: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '类型',
    field: 'fieldType',
    component: 'Input',
    show: false,
  },
  {
    label: '名称',
    field: 'fieldName',
    component: 'Input',
    required: true,
  },
];
