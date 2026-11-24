import { BasicColumn, FormSchema, Search } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
const noticeTypes = [
  {label: '短信', value: "1"},
  {label: '掌上宇通', value: "2"},
  {label: '短信+掌上宇通', value: "1,2"},
]
export const columns: BasicColumn[] = [
  {
    title: '车牌号',
    dataIndex: 'carNo',
    width: 120,
  },
  {
    title: '车辆类别',
    dataIndex: 'carType',
    width: 120,
  },
  {
    title: '车辆类型',
    dataIndex: 'carCate',
    width: 100,
  },
  {
    title: '姓名',
    dataIndex: 'staffName',
    width: 100,
  },
  {
    title: '员工号',
    dataIndex: 'staffNo',
    width: 100,
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    width: 100,
  },
  {
    title: '厂区',
    dataIndex: 'factory',
    width: 100,
  },
  {
    title: '入厂时间',
    dataIndex: 'inTime',
    width: 150,
  },
  {
    title: '停车时长',
    dataIndex: 'stopHours',
    width: 80,
  },
  {
    title: '通知次数',
    dataIndex: 'noticeNums',
    width: 80,
  },
  {
    title: '滞留次数',
    dataIndex: 'retentionNums',
    width: 80,
  },
  {
    title: '应有通知方式',
    dataIndex: 'noticeType',
    customRender: ({text}) => render.renderDictNative(text, noticeTypes, false),
    width: 120,
  },
  {
    title: '实际通知方式',
    dataIndex: 'remindType',
    customRender: ({text}) => render.renderDictNative(text, noticeTypes, false),
    width: 100,
  },
  {
    title: '通知时间',
    dataIndex: 'noticeTime',
    width: 150,
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
    label: '车牌号',
    field: 'carNo',
    component: 'Input',
  },
  {
    label: '员工号',
    field: 'staffNo',
    component: 'Input',
  },
  {
    label: '厂区',
    field: 'factory',
    component: 'Factory',
  },
  {
    label: '通知时间',
    field: 'date',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    }
  },
  {
    label: '通知方式',
    field: 'noticeType',
    component: 'Select',
    componentProps: {
      options: noticeTypes,
    },
  },
] 