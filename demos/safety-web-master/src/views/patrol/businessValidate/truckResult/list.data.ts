import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  { title: '事件编号', dataIndex: 'flowNo', width: 220 },
  { title: '厂区', dataIndex: 'factory', width: 150 },
  { title: '位置', dataIndex: 'location', width: 160 },
  { title: '抽查时间',  dataIndex: 'checkTime',  width: 150 },
  { title: '车牌号', dataIndex: 'carNumber', width: 100 },
  { title: '驾驶员姓名', dataIndex: 'driverName', width: 100 },
  { title: '联系电话', dataIndex: 'telephone', width: 100 },
  { title: '排放标准', dataIndex: 'emissionStandard', width: 100 },
  { title: '是否加入黑名单', dataIndex: 'isBlacklist', width: 150, customRender: ({text}) => text == 1 ? '是' : '否' },
  { title: '抽查人', dataIndex: 'checkPerson', width: 100 },
  { title: '抽查人部门', dataIndex: 'checkDept', width: 100 },
  { title: '加入黑名单时间', dataIndex: 'checkTime', width: 140 },
  { title: '加入黑名单结果', dataIndex: 'blackResult', width: 140 },
  { title: '出入口接口返回状态', dataIndex: 'interfaceStatus', width: 160 },
];
export const searchFormSchema: FormSchema[] =[
  { label: '厂区', field: 'factoryId', component: 'FactoryId'},
  { label: '位置', field: 'location', component: 'Input'},
  { label: '车牌号', field: 'carNumber', component: 'Input'},
  { label: '驾驶员姓名', field: 'driverName', component: 'Input'},
  { label: '排放标准', field: 'emissionStandard', component: 'Select',
    componentProps: {
      options: [
        {label: '国三', value: '国三'},
        {label: '国四', value: '国四'},
        {label: '国五', value: '国五'},
        {label: '国六', value: '国六'},
      ]
    },
  },
  { label: '抽查时间', field: 'checkTime', component: 'RangePicker',
    componentProps: {
      valueType: 'date'
    }
  },
  { label: '抽查人', field: 'checkPerson', component: 'Input'},
  { label: '抽查人部门', field: 'checkDept', component: 'Input'},
  { label: '事件编号', field: 'flowNo', component: 'Input'},
] 