import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';

export const columns: BasicColumn[] = [
  {
    title: '发起人姓名',
    dataIndex: 'creatorName',
    width: 100,
  },
  {
    title: '发起人员工号',
    dataIndex: 'creatorNo',
    width: 110,
  },
  {
    title: '员工号',
    dataIndex: 'empNo',
    width: 100,
  },
  {
    title: '姓名',
    dataIndex: 'empName',
    width: 100,
  },
  {
    title: '手机号',
    dataIndex: 'phoneNo',
    customCell: () => {
      return {
        style: {
          wordWrap: 'break-word',
          wordBreak: 'break-all',
          whiteSpace: 'normal',
          minHeight: '50px',
        },
      };
    },
    width: 120,
    align: 'center',
  },
  {
    title: '人脸识别照片',
    dataIndex: 'photoUrl',
    width: 100,
    customRender: ({ text }) => {
      if (!text) {
        return '无';
      } else {
        return render.renderImage2({ text });
      }
    },
  },
  {
    title: '外协组织',
    dataIndex: 'empOrg',
    width: 200,
    customRender: ({ text }) => {
      if (text) {
        return text.split('>>').reverse().join("<<");
      } else {
        return text;
      }
    }
  },
  {
    title: '已授权厂区',
    dataIndex: 'factoryName',
    width: 200,
    customRender: ({ text }) => {
      if (!text) {
        return '无';
      } else {
        return text;
      }
    },
  },
  {
    title: '调整时间',
    dataIndex: 'updateTime',
    width: 100,
    ellipsis: false,
  },
  {
    title: '用工结束时间',
    dataIndex: 'serviceEndDate',
    width: 110,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({text}) => text == '1' ? '在岗' : '离岗'
  },
  {
    title: '权限状态',
    dataIndex: 'statusDesc',
    width: 100,
  },
  {
    title: '备注',
    width: 200,
    dataIndex: 'remark',
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
    label: '主题',
    field: 'title',
    component: 'Input',
    required: true,
    componentProps: {
      disabled: true
    }
  },
  {
    label: '提交人',
    field: 'creatorName',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
    componentProps: {
      disabled: true
    }
  },
  {
    label: '员工号',
    field: 'creator',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
    componentProps: {
      disabled: true
    }
  },
  {
    label: '部门',
    field: 'creatorDept',
    component: 'Input',
    colProps: { span: 12 },
    required: true,
    componentProps: {
      disabled: true
    }
  },
  {
    label: '联系方式',
    field: 'creatorPhone',
    component: 'Input',
    required: true,
    colProps: { span: 12 },
    componentProps: {
      disabled: true
    }
  },
  {
    label: '业务说明',
    field: 'serviceDesc',
    component: 'InputTextArea',
    required: true,
  },
];


export const searchFormSchema: FormSchema[] =[
  {
    label: '发起人姓名/员工号',
    labelWidth: 80,
    field: 'creatorNo',
    component: 'Input',
  },
  {
    label: '员工号',
    field: 'empNo',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'empName',
    component: 'Input',
  },
  {
    label: '手机号',
    field: 'phoneNo',
    component: 'Input',
  },
  {
    label: '调整时间',
    field: 'updateTime',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  { label: '是否有照片', labelWidth: 80, field: 'photoFlag', component: 'Select',
    componentProps: {
      options: [
        {label: '有照片', value: '1'},
        {label: '无照片', value: '0'},
      ]
    }
  },
  { label: '状态', labelWidth: 80, field: 'status', component: 'Select',
    componentProps: {
      options: [
        {label: '在岗', value: 1},
        {label: '离岗', value: 0},
      ]
    }
  },
  { label: '状权限状态', labelWidth: 80, field: 'powerStatus', component: 'Select',
    componentProps: {
      options: [
        {label: '权限正常', value: '1'},
        {label: '授权已到期', value: '2'},
        {label: '卡已注销', value: '3'},
        {label: '已离职', value: '4'},
      ]
    }
  },

]
