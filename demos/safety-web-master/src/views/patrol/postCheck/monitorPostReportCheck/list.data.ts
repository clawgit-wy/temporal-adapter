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
    title: '位置/岗位',
    dataIndex: 'positionPost',
    width: 100,
  },
  {
    title: '验证时间',
    dataIndex: 'checkTimeStart',
    customRender: ({text, record}: {text: string, record: any}) => `${text}${record.checkTimeEnd ? `-${record.checkTimeEnd}` : ''}`,
    width: 160,
  },
  {
    title: '异常事件类型',
    dataIndex: 'abnormalType',
    width: 160,
  },
  {
    title: '异常事件描述',
    dataIndex: 'abnormalDesc',
    width: 120,
  },
  {
    title: '异常事件接收人',
    dataIndex: 'abnormalCreator',
    width: 130,
  },
  {
    title: '异常事件接收人部门班组',
    dataIndex: 'abnormalDept',
    width: 180,
  },
  {
    title: '验证对象',
    dataIndex: 'checkName',
    width: 100,
  },
  {
    title: '验证对象部门班组',
    dataIndex: 'checkDept',
    width: 130,
  },
  {
    title: '是否异常',
    dataIndex: 'checkException',
    width: 100,
  },
  {
    title: '是否考核兑现',
    dataIndex: 'examFulfill',
    width: 120,
  },
  {
    title: '整改处理结果',
    dataIndex: 'improveResult',
    width: 100,
  },
  {
    title: '镜头名称',
    dataIndex: 'lensName',
    width: 100,
  },
  {
    title: '方式途径',
    dataIndex: 'waysMeans',
    width: 100,
  },
  {
    title: '验证人姓名',
    dataIndex: 'submitName',
    width: 100,
  },
  {
    title: '验证人部门班组',
    dataIndex: 'submitDept',
    width: 130,
  },
  {
    title: '登记时间',
    dataIndex: 'createTime',
    customRender: ({text}: {text: string}) => `${text ? dayjs(text).format('YYYY-MM-DD HH:mm') : ''}`,
    width: 100,
  },
  {
    title: '处理时间',
    dataIndex: 'publishTime',
    customRender: ({text}: {text: string}) => `${text ? dayjs(text).format('YYYY-MM-DD HH:mm') : ''}`,
    width: 100,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 100,
  }
];

export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '厂区',
    field: 'factory',
    component: 'FactoryId',
    required: true,
  },
  {
    label: '类型',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
    required: true,
  },
  {
    label: '项目',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
    required: true,
  },
  {
    label: '位置',
    field: 'type',
    component: 'Input',
  },
  {
    label: '应用项目',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
    required: true,
  },
  {
    label: '任务日期',
    field: 'type',
    component: 'RangeDate',
  },
];
export const searchFormSchema: FormSchema[] =[
  
  {
    label: '厂区',
    field: 'factory',
    component: 'Factory',
  },
  {
    label: '位置/岗位',
    field: 'positionPost',
    component: 'Input',
  },
  {
    label: '异常事件类型',
    field: 'abnormalType',
    component: 'ApiSelect',
    componentProps: {
      labelField: 'fieldName',
      valueField: 'fieldName',
      api: () => fieldTree({parentId: 358})
    }
  },
  {
    label: '验证时间',
    field: 'checkTime',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '异常事件接收人',
    field: 'abnormalCreator',
    component: 'Input',
    componentProps: {
      placeholder: '请输入员工号'
    }
  },
  {
    label: '异常事件接收人部门班组',
    field: 'abnormalDept',
    component: 'Input',
  },
  {
    label: '验证对象',
    field: 'checkName',
    component: 'Input',
  },
  {
    label: '验证对象部门班组',
    field: 'checkDept ',
    component: 'Input',
  },
  {
    label: '方式途径',
    field: 'waysMeans',
    component: 'ApiSelect',
    componentProps: {
      labelField: 'fieldName',
      valueField: 'fieldName',
      api: () => fieldTree({parentId: 359})
    }
  },
  {
    label: '是否异常',
    field: 'checkException',
    component: 'Select',
    componentProps: {
      options: [
        {label: '是', value: '是'},
        {label: '否', value: '否'},
      ]
    }
  },
  {
    label: '是否考核兑现',
    field: 'examFulfill',
    component: 'Select',
    componentProps: {
      options: [
        {label: '是', value: '是'},
        {label: '否', value: '否'},
      ]
    }
  },
  {
    label: '验证人姓名',
    field: 'submitName',
    component: 'Input',
  },
  {
    label: '验证人部门班组',
    field: 'submitDept',
    component: 'Input',
  },
  {
    label: '整改处理结果',
    field: 'improveResult',
    component: 'Input',
  },
  {
    label: '事件编号',
    field: 'flowNo',
    component: 'Input',
  },
  {
    label: '登记时间',
    field: 'createTime',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '处理时间',
    field: 'auditTime',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  }
] 