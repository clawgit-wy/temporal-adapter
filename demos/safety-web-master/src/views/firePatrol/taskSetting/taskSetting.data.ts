import {BasicColumn, FormSchema} from "/@/components/Table";
import {getRiskLevel, getWorkStep} from "/@/views/firePatrol/riskLedger/firePatrolSelectApi";
import {selectGroupTree} from "/@/views/firePatrol/riskLedger/components/workPhaseApi";

export const pageSubDimensColumns: BasicColumn[] = [
  {title: '序号', dataIndex: 'id', key: 'id', width: 50},
  {title: '检查项目', dataIndex: 'inspectionItem', key: 'inspectionItem'},
  {title: '检查维度', dataIndex: 'inspectionSubDimension', key: 'inspectionSubDimension'},
  {title: '检查对象', dataIndex: 'inspectionObject', key: 'inspectionObject'},
  {title: '检查内容', dataIndex: 'inspectionContent', key: 'inspectionContent'},
  {title: '检查方法', dataIndex: 'inspectionMethod', key: 'inspectionMethod', width: 80},
];

export const taskSettingColumns: BasicColumn[] = [
  {title: '任务ID', dataIndex: 'id', key: 'id', width: 70},
  {title: '任务名称', dataIndex: 'taskName', key: 'taskName'},
  {title: '作业环节名称', dataIndex: 'workStepName', key: 'workStepName'},
  {
    title: '作业部门', dataIndex: 'orgNamePath', key: 'orgNamePath',
    customRender: ({text}) => text && text.length > 0 && text.split(',').length > 0 ? text.split(',').reverse().join('<') : text,
  },
  {title: '风险等级', dataIndex: 'riskLevel', key: 'riskLevel', width: 80},
  {title: '检查级别', dataIndex: 'inspectionLevel', key: 'inspectionLevel', width: 80},
  {title: '检查周期', dataIndex: 'inspectionCycle', key: 'inspectionCycle', width: 80},
  {
    title: '任务对象', dataIndex: 'empName', key: 'empName',
    customRender: ({text, record}) => Number(record.taskObjectType) === 0 ? '默认' : text
  },
  {title: '开始日期', dataIndex: 'taskBeginDate', key: 'taskBeginDate'},
  {title: '结束日期', dataIndex: 'taskEndDate', key: 'taskEndDate'},
  {
    title: '检查细分维度',
    dataIndex: 'inspectionSubDimensionNum',
    key: 'inspectionSubDimensionNum',
    slots: {customRender: 'inspectionSubDimensionNum'},
    width: 120,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 50,
    customRender: ({text}) => text.toString() === '0' ? '正常' : '暂停'
  },
  {title: '最新提交时间', dataIndex: 'updateTime', key: 'updateTime'},
  {title: '最新提交人', dataIndex: 'updateBy', key: 'updateBy', width: 100},
];


export const taskSettingSearchFormSchema: FormSchema[] = [
  {
    label: '作业环节名称',
    field: 'workStepName',
    component: 'Input',
  },
  {
    label: '任务名称',
    field: 'taskName',
    component: 'Input',
  },
  {
    label: '最新提交人',
    field: 'updateBy',
    component: 'Input',
  },
  {
    label: '作业部门',
    field: 'workDeptCode',
    component: 'ApiTreeSelect',
    componentProps: () => {
      return {
        api: () => selectGroupTree(),
        resultField: 'dataList', // 返回数据的字段名
        valueField: 'key', // 唯一标识字段
        labelField: 'title', // 显示名称字段
        treeNodeFilterProp: 'title', // 搜索过滤字段
        showSearch: true, // 开启搜索
        optionFilterProp: 'label', // 用于过滤的字段
        immediate: true,
        multiple: false,
        //请求参数
        params: {},
        //返回结果字段
      }
    },
  },
]

export const taskSettingAddFormSchemas: FormSchema[] = [
  {
    label: '任务名称',
    field: 'taskName',
    component: 'Input',
    componentProps: {
      maxlength: 20,
    },
    required: true,
  },
  {
    label: '作业环节',
    field: 'workStepCode',
    component: 'ApiSelect',
    componentProps: ({formActionType}) => {
      const {setFieldsValue} = formActionType;
      return {
        labelField: 'workStepName',
        valueField: 'workStepCode',
        showSearch: true, // 开启搜索
        optionFilterProp: 'label', // 用于过滤的字段
        api: () => getWorkStep(),
        onChange: (id, item) => {
          setFieldsValue({
            workStepCode: id,
            workStepName: item.realName, // 这就是你要额外提交的字段
            workDeptCode: item.workDeptCode,
            workDeptName: item.workDeptName
          });
        }
      }
    },
    required: true,
  },
  {
    field: 'workDeptCode',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
  {
    field: 'workDeptName',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
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
    label: '风险等级',
    field: 'riskLevel',
    component: 'ApiSelect',
    componentProps: ({formActionType}) => {
      const {setFieldsValue} = formActionType;
      return {
        labelField: 'riskLevel',
        valueField: 'riskLevel',
        api: () => getRiskLevel(),
        onChange: (id, item) => {
          if (item) {
            setFieldsValue({
              inspectionLevel: item.inspectionLevel,
              inspectionCycle: item.inspectionCycle
            })
          } else {
            setFieldsValue({
              inspectionLevel: '',
              inspectionCycle: ''
            })
          }
        }
      }
    },
    required: true
  },
  {
    label: '检查级别',
    field: 'inspectionLevel',
    component: 'Input',
    //填写组件Select的属性
    componentProps: {
      disabled: true,
    },
    required: true,
  },
  {
    label: '检查周期',
    field: 'inspectionCycle',
    component: 'Input',
    //填写组件Select的属性
    componentProps: {
      disabled: true,
    },
    required: true,
  },
  {
    label: '任务对象',
    field: 'taskObject',
    component: 'Input',
    required: ({values}) => {
      return Number(values.taskObjectType) === 1
    },
    slot: 'taskObject',
    ifShow: ({values}) => {
      // 根据作业环节和风险等级的值来决定是否显示“任务对象”
      return values.workDeptCode && values.riskLevel && values.workDeptCode.length > 0 && values.riskLevel.length > 0;
    }
  },
  {
    field: 'empNo',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
  {
    field: 'empName',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
  {
    label: '起始日期',
    field: 'date',
    component: 'RangeDate',
    required: true,
    componentProps: ({formActionType}) => {
      const {setFieldsValue} = formActionType;
      return {
        //日期格式化
        format: 'YYYY-MM-DD',
        //范围文本描述用集合
        placeholder: '请选择开始日期,请选择结束日期',
        // 禁止选择次日以前的日期
        disabledDate: (current) => {
          // 获取次日的日期
          const nextDay = new Date();
          nextDay.setDate(nextDay.getDate());
          // 禁止选择当前日期及之前的日期
          return current && current.valueOf() < nextDay.valueOf();
        },
        onChange: (value) => {
          let valuesArr = value.split(',')
          if (valuesArr.length === 2) {
            setFieldsValue({
              taskBeginDate: valuesArr[0],
              taskEndDate: valuesArr[1],
            })
          }
        }
      }
    },
  },
  {
    field: 'taskBeginDate',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
  {
    field: 'taskEndDate',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
  {
    field: 'taskObjectType',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
];
