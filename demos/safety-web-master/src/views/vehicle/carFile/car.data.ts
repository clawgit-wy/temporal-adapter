import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';
import { h } from 'vue';

export const columns: BasicColumn[] = [
  {
    title: '车牌号',
    dataIndex: 'fdCarNo',
    width: 100,
  },
  {
    title: '备案厂区',
    dataIndex: 'fdFilingFactory',
    width: 100,
  },
  {
    title: '备案时间-始',
    dataIndex: 'fdFilingBegin',
    width: 100,
  },
  {
    title: '备案时间-终',
    dataIndex: 'fdFilingEnd',
    width: 100,
  },
  {
    title: '姓名',
    dataIndex: 'fdPerName',
    width: 100,
  },

  {
    title: '手机号',
    dataIndex: 'fdPerPhone',
    width: 150,
  },
  {
    title: '状态',
    dataIndex: 'inStatus',
    width: 120,
  },
  {
    title: '厂区',
    dataIndex: 'inFactory',
    width: 100,
  },

  {
    title: '入厂大门',
    dataIndex: 'inOperatorName',
    width: 100,
  },
  {
    title: '入厂时间',
    dataIndex: 'inTime',
    width: 100,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '入厂识别照片',
    dataIndex: 'inPicture',
    width: 150,
    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return h('a', { href: text, target: '_blank', style: { 'text-decoration': 'underline' } }, '点击查看');
      }
    },
  },
  {
    title: '出厂大门',
    dataIndex: 'outOperatorName',
    width: 150,
  },
  {
    title: '出厂时间',
    dataIndex: 'outTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '出厂识别照片',
    dataIndex: 'outPicture',
    width: 150,
    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return h('a', { href: text, target: '_blank', style: { 'text-decoration': 'underline' } }, '点击查看');
      }
    },
  },
  {
    title: '停车时长(小时)',
    dataIndex: 'stayLasts',
    width: 150,
  },
  {
    title: '备案期内进厂次数',
    dataIndex: 'enterNum',
    width: 150,
  },
  {
    title: '供应商名称',
    dataIndex: 'fdSupplierName',
    width: 150,
  },
  {
    title: '运输货物名称',
    dataIndex: 'fdGoodsName',
    width: 150,
  },
  {
    title: '车主姓名/运输公司名称',
    dataIndex: 'fdDriverName',
    width: 180,
  },
  {
    title: '接口人员工号',
    dataIndex: 'approveCode',
    width: 120,
  },
  {
    title: '接口人姓名',
    dataIndex: 'approveName',
    width: 100,
  },
  {
    title: '接口人部门',
    dataIndex: 'approveDepart',
    width: 100,
  },
];

//表单搜索字段
export const searchFormSchema: FormSchema[] = [
  {
    label: '接口人姓名', //显示label
    field: 'approveName', //查询字段
    component: 'Input', //渲染的组件
  },
  {
    label: '接口人工号',
    field: 'approveCode',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'fdPerName',
    component: 'Input',
  },

  {
    label: '手机号',
    field: 'fdPerPhone',
    component: 'Input',
  },
  {
    label: '备案时间',
    field: 'filingDate',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '车牌号',
    field: 'fdCarNo',
    component: 'Input',
  },
  {
    label: '厂区',
    field: 'fdFilingFactoryCode',
    component: 'FactoryId',
  },
  // {
  //   label: '出厂类型',
  //   field: 'factoryType',
  //   component: 'Select',
  //   componentProps: {
  //     options: [
  //       {label: '取货', value: '10'},
  //       {label: '送货', value: '20'},
  //     ]
  //   }
  // },
  // {
  //   label: '状态',
  //   field: 'inStatus',
  //   component: 'Select',
  //   componentProps: {
  //     options: [
  //       {label: '在厂', value: '在厂'},
  //       {label: '已出厂', value: '已出厂'},
  //       {label: '未入厂', value: '未入厂'},
  //     ]
  //   }
  // },

  {
    label: '供应商名称',
    field: 'fdSupplierName',
    component: 'Input',
  },
  {
    label: '运输货物名称',
    field: 'fdGoodsName',
    component: 'Input',
  },
  {
    label: '车主姓名/运输公司名称',
    field: 'fdDriverName',
    component: 'Input',
    disabledLabelWidth: true,
    itemProps: {
      labelCol: { span: 9 },
      wrapperCol: { span: 15 },
    },
  },
  {
    label: '入场时间',
    field: 'inTime',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
      
    },
  },
];

// export const addFormSchema: FormSchema[] = [{}];
