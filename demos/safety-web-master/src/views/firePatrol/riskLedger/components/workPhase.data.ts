import {BasicColumn, FormSchema} from "/@/components/Table";
import {
  selectGroupTree
} from "/@/views/firePatrol/riskLedger/components/workPhaseApi";

export const columns: BasicColumn[] = [
  {title: '作业环节编码', dataIndex: 'workStepCode', key: 'workStepCode', ellipsis: true,width:270},
  {title: '作业环节名称', dataIndex: 'workStepName', key: 'workStepName'},
  {title: '工序', dataIndex: 'process', key: 'process'},
  {title: '作业单元', dataIndex: 'workUnit', key: 'workUnit'},
  {title: '作业类型', dataIndex: 'workType', key: 'workType'},
  {
    title: '作业部门', dataIndex: 'orgNamePath', key: 'orgNamePath',
    customRender: ({text}) => text && text.length > 0 && text.split(',').length > 0 ? text.split(',').reverse().join('<') : text,
  },
  {title: '最近更新时间', dataIndex: 'updateTime', key: 'updateTime', ellipsis: true},
  {title: '最近提交人', dataIndex: 'updateBy', key: 'updateBy'},
];

export const searchFormSchema: FormSchema[] = [

  {
    label: '作业环节名称',
    field: 'workStepName',
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
        // multiple: true,
        //请求参数
        params: {},
        //返回结果字段
      }
    },
  },
  {
    label: '作业环节编码',
    field: 'workStepCode',
    component: 'Input',
  },
  {
    label: '最近提交人',
    field: 'updateBy',
    component: 'Input',
  },

]

export function getWorkPhaseAddFormSchemas(isEdit: boolean) {
  const workPhaseAddFormSchemas: FormSchema[] = [
    {
      //标题名称
      label: '作业环节名称',
      //字段
      field: 'workStepName',
      //组件 支持组件详见 components/Form/src/types/index.ts 中的 ComponentType
      component: 'Input',
      //一列占比总共24，比如一行显示2列
      required: true,
    },
    {
      label: '作业编码',
      field: 'workStepCode',
      //子标题名称（在主标题后面）
      component: 'Input',
      componentProps: {
        disabled: isEdit,
      },
      //一列占比总共24，比如一行显示2列
      required: true,
    },
    {
      label: '工序',
      field: 'process',
      //子标题名称（在主标题后面）
      component: 'Input',
      //一列占比总共24，比如一行显示2列
      required: true,
    },
    {
      label: '作业单元',
      field: 'workUnit',
      //子标题名称（在主标题后面）
      component: 'Input',
      //一列占比总共24，比如一行显示2列
      required: true,
    },
    {
      label: '作业类型',
      field: 'workType',
      //子标题名称（在主标题后面）
      component: 'Input',
      //一列占比总共24，比如一行显示2列
      required: true,
    },
    {
      label: '作业部门',
      field: 'workDeptCode',
      component: 'ApiTreeSelect',
      required: true,
      componentProps: ({formActionType}) => {
        const {setFieldsValue} = formActionType;
        return {
          api: () => selectGroupTree(),
          resultField: 'dataList', // 返回数据的字段名
          valueField: 'key', // 唯一标识字段
          labelField: 'title', // 显示名称字段
          treeNodeFilterProp: 'title', // 搜索过滤字段
          showSearch: true, // 开启搜索
          optionFilterProp: 'label', // 用于过滤的字段
          immediate: true,
          onChange: (value, node) => {
            if (node.length > 0 && node[0].length > 0) {
              setFieldsValue({
                workDeptName: node[0], // 这就是你要额外提交的字段
              });
            }
          },
          //请求参数
          params: {},
          //返回结果字段
        }
      },
    },
    {
      field: 'workDeptName',
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
  ];
  return workPhaseAddFormSchemas;
}

export const workPhaseAddFormSchemas: FormSchema[] = [
  {
    //标题名称
    label: '作业环节名称',
    //字段
    field: 'workStepName',
    //组件 支持组件详见 components/Form/src/types/index.ts 中的 ComponentType
    component: 'Input',
    //一列占比总共24，比如一行显示2列
    required: true,
  },
  {
    label: '作业编码',
    field: 'workStepCode',
    //子标题名称（在主标题后面）
    component: 'Input',
    //一列占比总共24，比如一行显示2列
    required: true,
  },
  {
    label: '工序',
    field: 'process',
    //子标题名称（在主标题后面）
    component: 'Input',
    //一列占比总共24，比如一行显示2列
    required: true,
  },
  {
    label: '作业单元',
    field: 'workUnit',
    //子标题名称（在主标题后面）
    component: 'Input',
    //一列占比总共24，比如一行显示2列
    required: true,
  },
  {
    label: '作业类型',
    field: 'workType',
    //子标题名称（在主标题后面）
    component: 'Input',
    //一列占比总共24，比如一行显示2列
    required: true,
  },
  {
    label: '作业部门',
    field: 'workDeptCode',
    component: 'ApiTreeSelect',
    required: true,
    componentProps: ({formActionType}) => {
      const {setFieldsValue} = formActionType;
      return {
        api: () => selectGroupTree(),
        resultField: 'dataList', // 返回数据的字段名
        valueField: 'key', // 唯一标识字段
        labelField: 'title', // 显示名称字段
        treeNodeFilterProp: 'title', // 搜索过滤字段
        showSearch: true, // 开启搜索
        optionFilterProp: 'label', // 用于过滤的字段
        immediate: true,
        onChange: (value, node) => {
          if (node.length > 0 && node[0].length > 0) {
            setFieldsValue({
              workDeptName: node[0], // 这就是你要额外提交的字段
            });
          }
        },
        //请求参数
        params: {},
        //返回结果字段
      }
    },
  },
  {
    field: 'workDeptName',
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
];
