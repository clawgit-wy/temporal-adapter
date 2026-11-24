import { BasicColumn, FormSchema } from '/@/components/Table';
import { fieldTree } from './list.api'
import dayjs from 'dayjs';

export const columns: BasicColumn[] = [
  {
    title: '事件编号',
    dataIndex: 'flowNo',
    slots: { customRender: 'flowNo' },
    width: 200,
  },
  {
    title: '厂区',
    dataIndex: 'factory',
    width: 150,
  },
  {
    title: '验证项目',
    dataIndex: 'teamName',
    width: 160,
  },
  {
    title: '验证时间',
    dataIndex: 'validateBeginTime',
    customRender: ({text, record}: {text: string, record: any}) => `${text}-${record.validateEndTime}`,
    width: 160,
  },
  {
    title: '模拟验证位置',
    dataIndex: 'postName',
    width: 100,
  },
  {
    title: '到达现场用时',
    dataIndex: 'reachDuration',
    width: 100,
  },
  {
    title: '出警人',
    dataIndex: 'empName',
    width: 100,
  },
  {
    title: '出警人部门班组',
    dataIndex: 'empDept',
    width: 100,
  },
  {
    title: '验证过程动作及结构',
    dataIndex: 'validateDesc',
    width: 160,
  },
  {
    title: '验证是否合格',
    dataIndex: 'isPass',
    width: 130,
    customRender: ({text}) => text == '0' ? '否' : text == '1' ? '是' : ''
  },
  {
    title: '是否考核兑现',
    dataIndex: 'isExamine',
    width: 120,
    customRender: ({text}) => text == '0' ? '否' : text == '1' ? '是' : ''
  },
  {
    title: '整改处理结果',
    dataIndex: 'reformDesc',
    width: 120,
  },
  {
    title: '验证人姓名',
    dataIndex: 'empName',
    width: 100,
  },
  {
    title: '验证人部门班组',
    dataIndex: 'empDept',
    width: 130,
  },
  {
    title: '出警人直系领导',
    dataIndex: 'leaderEmpName',
    width: 130,
  },
  {
    title: '登记时间',
    dataIndex: 'createTime',
    customRender: ({text}: {text: string}) => `${dayjs(text).format('YYYY-MM-DD HH:mm')}`,
    width: 100,
  },
  {
    title: '处理时间',
    dataIndex: 'dealTime',
    customRender: ({text}: {text: string}) => `${text ? dayjs(text).format('YYYY-MM-DD HH:mm') : ''}`,
    width: 100,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 100,
  }
];

export const searchFormSchema: FormSchema[] =[
  {
    label: '类型',
    field: 'dataType',
    component: 'Input',
    defaultValue: 2,
    show: false,
  }, 
  {
    label: '厂区',
    field: 'factoryId',
    component: 'FactoryId',
  },
  {
    label: '验证项目',
    field: 'teamName',
    component: 'ApiSelect',
    componentProps: {
      labelField: 'fieldName',
      valueField: 'fieldName',
      api: () => fieldTree({parentId: 356})
    }
  },
  {
    label: '到达现场用时',
    field: 'reachDuration',
    component: 'Input',
  },
  {
    label: '验证时间',
    field: 'validate',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '出警人',
    field: 'empName',
    component: 'Input',
  },
  {
    label: '出警人部门班组',
    field: 'empDept',
    component: 'Input',
  },
  {
    label: '验证是否合格',
    field: 'isPass',
    component: 'Select',
    componentProps: {
      options: [
        {label: '是', value: 1},
        {label: '否', value: 0},
      ]
    }
  },
  {
    label: '出警人直系领导',
    field: 'leaderEmpName ',
    component: 'Input',
  },
  {
    label: '是否考核兑现',
    field: 'isExamine',
    component: 'Select',
    componentProps: {
      options: [
        {label: '是', value: 1},
        {label: '否', value: 0},
      ]
    }
  },
  {
    label: '整改处理结果',
    field: 'reformDesc',
    component: 'Input',
  },
  {
    label: '验证人姓名',
    field: 'createEmp',
    component: 'Input',
  },
  {
    label: '验证人部门班组',
    field: 'createDept',
    component: 'Input',
  },
  {
    label: '事件编号',
    field: 'flowNo',
    component: 'Input',
  },
  {
    label: '登记时间',
    field: 'create',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '处理时间',
    field: 'deal',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  }
] 