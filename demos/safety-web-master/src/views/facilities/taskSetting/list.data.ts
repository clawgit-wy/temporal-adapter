import dayjs from 'dayjs';
import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '任务名称',
    dataIndex: 'planName',
    width: 150,
  },
  {
    title: '应用厂区',
    dataIndex: 'factoryName',
    width: 100,
  },
  {
    title: '任务周期',
    dataIndex: 'cycle',
    width: 100,
  },
  {
    title: '应用项目',
    dataIndex: 'teamName',
    width: 160,
  },
  {
    title: '休眠开始日期',
    dataIndex: 'dormancyBegin',
    width: 100,
  },
  {
    title: '休眠结束日期',
    dataIndex: 'dormancyEnd',
    width: 100,
  },
  {
    title: '任务开始日期',
    dataIndex: 'beginDate',
    width: 100,
  },
  {
    title: '任务结束日期',
    dataIndex: 'endDate',
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
    label: '任务名称',
    field: 'planName',
    component: 'Input',
    required: true,
  },
  {
    label: '应用厂区',
    field: 'factoryName',
    show: false,
    component: 'Input',
  },
  {
    label: '应用厂区',
    field: 'factoryId',
    labelField: 'factoryName',
    component: 'FactoryId',
    required: true,
    componentProps: {
      mode:'multiple',
    }
  },
  {
    label: '任务周期',
    field: 'cycle',
    component: 'InputNumber',
    required: true,
    componentProps: {
      min: 1
    }
  },
  {
    label: '应用项目',
    field: 'teamName',
    component: 'Input',
    show: false
  },
  {
    label: '应用项目',
    field: 'teamId',
    labelField: 'teamName',
    component: 'Select',
    // componentProps: {
    //   options: teamOptions.value

      
    // },
    required: true,
  },
  {
    label: '任务开始日期',
    field: 'beginDate',
    component: 'DatePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '任务结束日期',
    field: 'endDate',
    component: 'DatePicker',
    slot: 'endDate',
    componentProps: {
      valueType: 'Date',
    },
  },
];
export const searchFormSchema: FormSchema[] =[
  
  {
    label: '任务名称',
    field: 'planName',
    component: 'Input',
  },
  {
    label: '厂区',
    field: 'factoryId',
    component: 'FactoryId',
  },
  {
    label: '应用项目',
    field: 'teamName',
    component: 'Input',
  },
  {
    label: '任务时间',
    field: 'date',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },

] 