import { BasicColumn, FormSchema } from '/@/components/Table';
import { fieldTree, spotList } from './list.api'
import {ref} from 'vue'
import ApiSelect from '/@/components/Form/src/components/ApiSelect.vue';

export const columns: BasicColumn[] = [
  {
    title: '设备当前状态',
    dataIndex: 'deviceStatus',
    width: 120,
    customRender: ({text}) => text == 1 ? '异常' : '正常'
  },
  {
    title: '厂区',
    dataIndex: 'factoryName',
    width: 120,
  },
  {
    title: '类型',
    dataIndex: 'deviceType',
    width: 150,
  },
  {
    title: '项目',
    dataIndex: 'teamName',
    width: 150,
  },
  {
    title: '名称',
    dataIndex: 'deviceName',
    width: 100,
  },
  {
    title: '位置',
    dataIndex: 'location',
    width: 100,
  },
  {
    title: '设备编号',
    dataIndex: 'deviceCode',
    width: 130,
  },
  {
    title: '责任部门',
    dataIndex: 'dutyName',
    width: 100,
  },
  {
    title: '点检人',
    dataIndex: 'checkEmp',
    width: 100,
  },
  {
    title: '应用巡检标准',
    dataIndex: 'ruleNames',
    width: 100,
  },
  {
    title: '累计故障次数',
    dataIndex: 'faultTotal',
    width: 100,
  },
  {
    title: '临时点检人',
    dataIndex: 'proTemCheckEmp',
    width: 100,
  },
  {
    title: '临时点检人应用开始时间',
    dataIndex: 'proTemBeginDate',
    width: 200,
  },
  {
    title: '临时点检人应用结束时间',
    dataIndex: 'proTemEndDate',
    width: 200,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 100,
  },
];


const ruleIdsParams = ref({})
export const changeRuleParams = (value) => {
  ruleIdsParams.value = value
}

export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '厂区',
    field: 'factoryId',
    component: 'FactoryId',
    required: true,
  },
  {
    label: '设备类型',
    field: 'deviceType',
    component: 'Input',
    show: false
  },
  
  {
    label: '设备类型',
    field: 'deviceFieldCode',
    labelField: 'deviceType',
    component: 'ApiSelect',
    slot: 'deviceFieldCode',
    required: true,
  },
  {
    label: '项目名字',
    field: 'teamName',
    component: 'Input',
    show: false,
  },
  {
    label: '项目',
    field: 'teamId',
    slot: 'teamId',
    component: 'ApiSelect',
    required: true,
  },
  {
    label: '设备名称',
    field: 'deviceName',
    component: 'Input',
  },
  {
    label: '位置',
    field: 'location',
    component: 'Input',
  },
  {
    label: '责任部门',
    field: 'dutyName',
    component: 'Input',
  },
  {
    label: '点检人',
    field: 'checkEmpNo',
    labelField: 'checkEmp',
    component: 'SearchPerson',
    componentProps: {
      mode: 'multiple'
    }
  },
  {
    label: '点检人',
    field: 'checkEmp',
    component: 'Input',
    show: false,
  },
  {
    label: '应用巡检标准',
    field: 'ruleIds',
    component: 'ApiSelect',
    componentProps: {
      api: (params) => spotList(params),
      labelField: 'ruleName',
      valueField: 'id',
      mode: 'multiple',
      params: ruleIdsParams
    },
    required: true,
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    componentProps: {
      maxlength: 500,
      rows: 5
    },
  },
];
export const searchFormSchema: FormSchema[] =[
  {
    label: '厂区',
    field: 'factoryId',
    component: 'FactoryId',
  },
  {
    label: '设备当前状态',
    field: 'deviceStatus',
    component: 'Select',
    componentProps: {
      options: [
        {label: '正常', value: 0},
        {label: '异常', value: 1},
      ]
    }
  },
  {
    label: '类型',
    field: 'deviceType',
    component: 'Input',
  },
  {
    label: '项目',
    field: 'teamName',
    component: 'Input',
  },
  {
    label: '名称',
    field: 'deviceName',
    component: 'Input',
  },
  {
    label: '位置',
    field: 'location',
    component: 'Input',
  },
  {
    label: '设备号',
    field: 'deviceCode',
    component: 'Input',
  },
  {
    label: '责任部门',
    field: 'dutyName',
    component: 'Input',
  },
  {
    label: '点检人',
    field: 'checkEmp',
    component: 'Input',
  }
] 