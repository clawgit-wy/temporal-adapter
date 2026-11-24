import {BasicColumn, FormSchema} from "/@/components/Table";
import {
  getInspectionMethod,
  getRiskLevel,
  getWorkStep
} from "/@/views/firePatrol/riskLedger/firePatrolSelectApi";

export const checkStandardColumns: BasicColumn[] = [
  {title: '序号', dataIndex: 'id', key: 'id',width:50},
  {title: '作业环节', dataIndex: 'workStepName', key: 'workStepName'},
  {title: '作业环节编码', dataIndex: 'workStepCode', key: 'workStepCode'},
  {title: '检查项目', dataIndex: 'inspectionItem', key: 'inspectionItem'},
  {title: '检查对象', dataIndex: 'inspectionObject', key: 'inspectionObject'},
  {title: '检查类别', dataIndex: 'inspectionCategory', key: 'inspectionCategory'},
  {title: '检查细分维度', dataIndex: 'inspectionSubDimension', key: 'inspectionSubDimension'},
  {title: '检查内容', dataIndex: 'inspectionContent', key: 'inspectionContent', width: 300},
  {title: '风险等级', dataIndex: 'riskLevel', key: 'riskLevel', width: 80},
  {title: '检查方法', dataIndex: 'inspectionMethod', key: 'inspectionMethod', width: 80},
  {title: '最近更新时间', dataIndex: 'updateTime', key: 'updateTime'},
  {title: '最近提交人', dataIndex: 'updateBy', key: 'updateBy', width: 100},
];

export const checkStandardMockData = Array.from({length: 100}, (_, index) => ({
  id: 'checkStandardMockData' + index,
  code: `检查编码-${index + 1}`,
  name: `作业环节-${index + 1}`,
  process: `检查项目-${index + 1}`,
  workUnit: `检查对象-${index + 1}`,
  workType: `检查类别-${index + 1}`,
  department: `检查细分维度-${index + 1}`,
  checkContent: `检查内容-${index + 1}`,
  riskLevel: `风险等级-${index + 1}`,
  checkMethod: `检查方法-${index + 1}`,
  lastUpdateTime: new Date().toISOString(),
  lastSubmitter: `最近提交人-${index + 1}`,
}));

export const checkStandardSearchFormSchema: FormSchema[] = [
  {
    label: '作业环节名称',
    field: 'workStepName',
    component: 'Input',
  },
  {
    label: '作业环节编码',
    field: 'workStepCode',
    component: 'Input',
  },
  {
    label: '检查细分维度',
    field: 'inspectionSubDimension',
    component: 'Input',
  },
  {
    label: '风险等级',
    field: 'riskLevel',
    component: 'ApiSelect',
    componentProps: {
      labelField: 'riskLevel',
      valueField: 'riskLevel',
      api: () => getRiskLevel()
    },
  },
  {
    label: '最近提交人',
    field: 'updateBy',
    component: 'Input',
  },
]

export const checkStandardAddFormSchemas: FormSchema[] = [
  {
    label: '作业环节',
    field: 'workStepCode',
    component: 'ApiSelect',
    componentProps: ({formActionType}) => {
      const {setFieldsValue} = formActionType;

      return {
        labelField: 'workStepName',
        valueField: 'workStepCode',
        api: () => getWorkStep(),
        onSearch: (value, all) => {
        },
        showSearch: true, // 开启搜索
        optionFilterProp: 'label', // 用于过滤的字段
        onChange: (id, item) => {
          setFieldsValue({
            workStepCode: id,
            workStepName: item.realName, // 这就是你要额外提交的字段
          });
        }
      }
    },
    required: true,
  },
  {
    field: 'workStepName',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
  {
    field: 'id',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
  {
    label: '检查项目',
    field: 'inspectionItem',
    component: 'Input',
    componentProps: {
      maxlength: 10,
      placeholder: '如燃烧三要素'
    },
    required: false,
  },
  {
    label: '检查对象',
    field: 'inspectionObject',
    component: 'Input',
    required: false,
    componentProps: {
      maxlength: 10,
      placeholder: '如烤箱'
    },
  },
  {
    label: '检查类别',
    field: 'inspectionCategory',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 10,
      placeholder: '如点火源'
    },
  },
  {
    label: '检查细分维度',
    field: 'inspectionSubDimension',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 10,
      placeholder: '如电器火源失控'
    },
  },
  {
    label: '检查内容',
    field: 'inspectionContent',
    component: 'InputTextArea',
    required: true,
    componentProps: {
      autosize: {minRows: 4},
      placeholder: '请输入具体检查内容，如：\n' +
        '①电气设备、线路短路\n' +
        '②电气设备、线路过载'
    },
  },
  {
    label: '风险等级',
    field: 'riskLevel',
    component: 'ApiSelect',
    componentProps: {
      labelField: 'riskLevel',
      valueField: 'riskLevel',
      api: () => getRiskLevel()
    },
    required: true,
  },
  {
    label: '检查方法',
    field: 'inspectionMethodId',
    component: 'ApiSelect',
    componentProps: ({formActionType}) => {
      const {setFieldsValue} = formActionType;
      return {
        numberToString: false,
        labelField: 'name',
        valueField: 'code',
        api: () => getInspectionMethod(),
        onChange: (newValue, option) => {
          setFieldsValue({
            inspectionMethod: option.label,
          });
        }, // 绑定事件
      }
    },
    required: true,
  },
  {
    field: 'inspectionMethod',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
];
