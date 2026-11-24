import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  { title: '厂区', dataIndex: 'factoryName', width: 150 },
  { title: '类型',  dataIndex: 'deviceType',  width: 150 },
  { title: '项目', dataIndex: 'teamName', width: 150 },
  { title: '点检周期', dataIndex: 'cycleDesc', width: 160 },
  { title: '当期点检开始日期', dataIndex: 'taskBeginDate', width: 130 },
  { title: '当期点检结束日期', dataIndex: 'taskEndDate', width: 130 },
  { title: '期次', dataIndex: 'curOrder', width: 100 },
  { title: '项目设备数量', dataIndex: 'deviceNum', width: 120 },
  { title: '周期内已点检设备数', dataIndex: 'checkNum', width: 150 },
  { title: '周期内未点检设备数', dataIndex: 'unCheckNum', width: 150 },
  { title: '设备点检覆盖率', dataIndex: 'checkRate', customRender: ({text}) => text?`${text}%`:'-', width: 130 },
  { title: '点检延期数量', dataIndex: 'spotDelayNum', width: 120 },
  { title: '点检延期率', dataIndex: 'spotDelayRate', customRender: ({text}) => text?`${text}%`:'-', width: 100 },
  { title: '点检周期内异常数量', dataIndex: 'exceptionNum', width: 150 },
  { title: '已整改数量', dataIndex: 'reformNum', width: 100 },
  { title: '整改完成率', dataIndex: 'reformRate', customRender: ({text}) => text?`${text}%`:'-', width: 100 },
  { title: '整改延期数量', dataIndex: 'reformDelayNum', width: 120 },
  { title: '整改延期率', dataIndex: 'reformDelayRate', customRender: ({text}) => text?`${text}%`:'-', width: 100 },
  { title: '整改验证数量', dataIndex: 'validateNum', width: 120 },
];
export const searchFormSchema: FormSchema[] =[
  { label: '厂区', field: 'factoryId', component: 'FactoryId'},
  { label: '类型', field: 'deviceType', component: 'Input'},
  { label: '项目', field: 'teamName', component: 'Input'},
  { label: '点检周期', field: 'cycle', component: 'InputNumber'},
  { label: '点检日期', field: 'taskDate', component: 'RangeDate',
    componentProps: {
      valueType: 'date'
    }
  },
] 