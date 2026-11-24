import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  { title: '流程ID', dataIndex: 'flowId', width: 100, slots: { customRender: 'flowId' } },
  { title: '厂区', dataIndex: 'factoryName', width: 150 },
  { title: '类型',  dataIndex: 'deviceType',  width: 120 },
  { title: '项目', dataIndex: 'teamName', width: 120 },
  { title: '位置', dataIndex: 'location', width: 160 },
  { title: '设备编号', dataIndex: 'deviceCode', width: 140 },
  { title: '责任部门', dataIndex: 'dutyName', width: 100 },
  { title: '点检时间', dataIndex: 'checkTimeStr', width: 150 },
  { title: '设备状态', dataIndex: 'deviceStatus', width: 100, customRender: ({text}) => text == 1 ? '异常' : '正常' },
  { title: '异常描述', dataIndex: 'exceptionDesc', width: 100 },
  { title: '设备照片', dataIndex: 'checkImg', width: 100, slots: { customRender: 'checkImg' } },
  { title: '报修情况', dataIndex: 'deviceEipStatus', width: 100,customRender: ({text, record}) => record.validateStatus == 1 ? '/' : text ? text : '/' },
  { title: '实际点检人', dataIndex: 'checkEmp', width: 100 },
  { title: '点检人员工号', dataIndex: 'checkEmpNo', width: 120 },
  { title: '点检人部门', dataIndex: 'checkOrgName', width: 100 },
  { title: '预计整改完成时间', dataIndex: 'planReformDate', width: 140 },
  { title: '整改次数', dataIndex: 'reformNum', width: 100 },
  { title: '整改状态', dataIndex: 'isReform', width: 100,
    customRender: ({text}) => text === 0 ? '未整改' : text === 1 ? '已整改' : ''
  },
  { title: '当场整改照片', dataIndex: 'reformImg', width: 120, slots: { customRender: 'reformImg' } },
  { title: '整改责任人', dataIndex: 'reformEmp', width: 100 },
  { title: '整改验证结果', dataIndex: 'validateStatus', width: 120, customRender: ({text}) => text == 1 ? '已验证' : text == 0 ? '未验证' : text == 4 ? '整改不合格' : '' },
  { title: '验证时间', dataIndex: 'validateTimeStr', width: 100 },
  { title: '验证人', dataIndex: 'validateEmp', width: 100 },
];
export const searchFormSchema: FormSchema[] =[
  { label: '厂区', field: 'factoryId', component: 'FactoryId'},
  { label: '类型', field: 'deviceType', component: 'Input'},
  { label: '项目', field: 'teamName', component: 'Input'},
  { label: '位置', field: 'location', component: 'Input'},
  { label: '设备编号', field: 'deviceCode', component: 'Input'},
  { label: '责任部门', field: 'dutyName', component: 'Input'},
  { label: '点检时间', field: 'spot', component: 'RangeDate',
    componentProps: {
      valueType: 'date'
    }
  },
  { label: '设备状态', field: 'deviceStatus', component: 'Select',
    componentProps: {
      options: [
        {label: '正常', value: 0},
        {label: '异常', value: 1},
      ]
    },
  },
  { label: '报修情况', field: 'deviceEipStatus', component: 'Input'},
  { label: '点检人', field: 'spotEmp', component: 'Input'},
  { label: '点检人员工号', field: 'spotEmpNo', component: 'Input'},
  { label: '点检人部门', field: 'checkOrgName', component: 'Input'},
  { label: '预计整改完成时间', field: 'planReform', component: 'RangeDate',
    componentProps: {
      valueType: 'date'
    }
  },
  { label: '整改次数', field: 'reformNum', component: 'Input'},
  { label: '整改状态', field: 'isReform', component: 'Select',
    componentProps: {
      options: [
        {label: '未整改', value: '0'},
        {label: '已整改', value: '1'},
      ]
    },
  },
  { label: '整改责任人', field: 'reformEmp', component: 'SearchPerson'},
  { label: '整改验证结果', field: 'validateStatus', component: 'Select',
    componentProps: {
      options: [
        {label: '未验证', value: '0'},
        {label: '已验证', value: '1'},
        {label: '整改不合格', value: '4'},
      ]
    },
  },
  { label: '验证时间', field: 'validate', component: 'RangeDate',
    componentProps: {
      valueType: 'date'
    }
  },
  { label: '验证人', field: 'validateEmp', component: 'SearchPerson'},
  { label: '点检状态', field: 'status', defaultValue: 1, component: 'Select',
    componentProps: {
      options: [
        {label: '已点检', value: 1},
        {label: '待点检', value: 0},
      ]
    },
  },
] 