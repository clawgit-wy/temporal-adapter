import { BasicColumn, FormSchema, Search } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';

const noticeType =  [
  {
    label: '短信',
    value: '1',
    key: 1,
  },
  {
    label: '宇上信通提示',
    value: '2',
    key: 2,
  },
  {
    label: '宇上信通+短信',
    value: '1,2',
    key: 2,
  },
];
  const remindRuleOptions = [
    {label: '小于等于', value: '小于等于'},
    {label: '大于', value: '大于'},
  ]

export const columns: BasicColumn[] = [
  {
    title: '滞留通知时间(小时)',
    dataIndex: 'retentionHour',
    width: 120,
    // customRender: ({ text }) => {
    //   return render.renderDictNative(text, generalTypeList, false);
    // },
  },
  {
    title: '通知人员类型',
    dataIndex: 'carType',
    width: 150,
  },
  {
    title: '通知内容',
    dataIndex: 'content',
    width: 100,
  },
  {
    title: '提醒次数判断',
    dataIndex: 'remindDecide',
    width: 100,
  },
  {
    title: '通知次数',
    dataIndex: 'sendNum',
    width: 100,
  },
  {
    title: '通知间隔(分钟)',
    dataIndex: 'remindGap',
    width: 100,
  },
  {
    title: '通知方式',
    dataIndex: 'remindType',
    customRender: ({text}) => render.renderDictNative(text, noticeType, false),
    width: 100,
  },
  {
    title: '是否启用',
    dataIndex: 'isUsed',
    customRender: ({text}) => text == 1 ? '启用' : '关闭',
    width: 100,
  },
  {
    title: '修改时间',
    dataIndex: 'updateTime',
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
    label: '滞留通知时间(小时)',
    field: 'retentionHour',
    component: "InputNumber",
    required: true,
    componentProps: {
      placeholder: '同一通知人员类型滞留通知时间请保持一致！'
    }
  },
  {
    label: '通知人员类型',
    field: 'carType',
    component: "RadioGroup",
    required: true,
    componentProps: {
      options: [
        {label: '员工车辆', value: '员工车辆'},
        {label: '外协车辆', value: '外协车辆'}
      ]
    },

  },
  {
    label: '通知内容',
    field: 'content',
    required: true,
    component: 'InputTextArea',
    componentProps: {
      maxlength: 500,
      rows: 5
    },
  },
  {

    label: '提醒规则',
    field: 'remindRule',
    component: "Select",
    required: true,
    componentProps: {
      options: remindRuleOptions
    },
  },
  {
    label: '提醒次数判断',
    field: 'remindNum',
    required: true,
    component: "InputNumber",

  },
  {
    label: '通知次数',
    field: 'sendNum',
    required: true,
    component: "InputNumber",

  },
  {
    label: '通知间隔(分钟)',
    field: 'remindGap',
    required: true,
    component: "InputNumber",

  },
  {
    label: '通知方式',
    field: 'remindType',
    required: true,
    component: "Select",
    componentProps: {
      options: noticeType
    },

  },
  {
    label: '是否启用',
    field: 'isUsed',
    component: "RadioGroup",
    required: true,
    componentProps: {
      options: [
        {label: '启用', value: 1},
        {label: '关闭', value: 2}
      ]
    },

  },
];
export const searchFormSchema: FormSchema[] =[
  
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '通知人员类型',
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
    label: '通知方式',
    field: 'remindType',
    component: 'Select',
    componentProps: {
      options: [
        {
          label: '短信',
          value: '1',
          key: '1',
        },
        {
          label: '掌上宇通',
          value: '2',
          key: '2',
        },
        {
          label: '掌上宇通+短信',
          value: '1,2',
          key: '3',
        },
      ],
    },
  },
  {
    label: '是否启用',
    field: 'isUsed',
    component: 'Select',
    componentProps: {
      options: [
        {
          label: '启用',
          value: 1,
          key: '1',
        },
        {
          label: '关闭',
          value: 2,
          key: '2',
        },
      ],
    },
  },
] 