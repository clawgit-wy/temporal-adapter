import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';
import { useGlobSetting } from '/@/hooks/setting';
import { h } from 'vue';
const globSetting = useGlobSetting();
const statusList = [
  {
    value: 0,
    label: '未发卡',
    key: '1',
  },
  {
    value: 1,
    label: '已发卡',
    key: '2',
  },
  {
    value: 2,
    label: '已退卡',
    key: '3',
  },
  {
    value: 3,
    label: '审批中',
    key: '4',
  },
  {
    value: 4,
    label: '超时关闭',
    key: '5',
  },
  {
    value: 5,
    label: '未到访',
    key: '6',
  },
  {
    value: 6,
    label: '驳回',
    key: '7',
  },
  {
    value: 7,
    label: '废弃',
    key: '8',
  },
  {
    value: 8,
    label: '已进厂',
    key: '9',
  },
  {
    value: 9,
    label: '已出厂',
    key: '10',
  },
  {
    value: 10,
    label: '滞留',
    key: '11',
  },
];

export const columns: BasicColumn[] = [
  {
    title: '起草时间',
    dataIndex: 'eipCreateTime',
    width: 100,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '访客姓名',
    dataIndex: 'visitorName',
    width: 80,
    customRender: ({ text, record}) => {
      if (record.eipFlowId) {
        let url = `http://ekp.yutong.com/ekp/km/review/km_review_main/kmReviewMain.do?method=view&fdId=${record.eipFlowId}`
        return h('a', { href: url, target: '_blank' }, text)
      }
      return text
    },
  },
  {
    title: '访客手机号',
    dataIndex: 'visitorPhone',
    width: 100,
  },
  {
    title: '访客身份证号',
    dataIndex: 'visitorIdCard',
    width: 170,
  },
  {
    title: '访问厂区',
    dataIndex: 'vendorFactory',
    width: 120,
    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return render.renderTip(text, 10);
      }
    },
  },
  {
    title: '访问区域',
    dataIndex: 'vendorRegion',
    width: 120,
    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return render.renderTip(text, 10);
      }
    },
  },
  {
    title: '是否访问绝密区',
    dataIndex: 'secretState',
    width: 80,
    customRender: ({ text }) => (text === 1 ? '是' : '否'),
  },
  {
    title: '绝密区',
    dataIndex: 'secretRegion',
    width: 100,
    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return render.renderTip(text, 10);
      }
    },
  },
  {
    title: '被访人部门',
    dataIndex: 'visitedDept',
    width: 100,
    customRender: ({ text }) => {
      const text1 = text ? text.split('>>').reverse().join("<<") : "";
      if (!text1) {
        return text1;
      } else {
        return render.renderTip(text1, 10);
      }
    },
  },
  {
    title: '被访人姓名',
    dataIndex: 'visitedName',
    width: 100,
  },
  {
    title: '被访人手机号',
    dataIndex: 'visitedPhone',
    width: 110,
  },
  {
    title: '访问事由',
    dataIndex: 'vendorMatter',
    width: 100,
  },
  {
    title: '入厂开始时间',
    dataIndex: 'startTime',
    width: 100,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '入厂结束时间',
    dataIndex: 'endTime',
    width: 100,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '访客单位',
    dataIndex: 'vendorUnit',
    width: 100,
  },
  {
    title: '车牌号',
    dataIndex: 'cardNo',
    width: 100,
  },
  {
    title: '照片',
    dataIndex: 'photo',
    width: 80,
    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return render.renderImage2({ text });
      }
    },
  },
  {
    title: '访问状态',
    dataIndex: 'vendorState',
    width: 100,
    customRender: ({ text, record }) => {
      let newTime = record.nowTime;
      if (text == 1 && newTime > record.endTime) {
        return '未到访'
      } else if (text == 2) {
        return newTime > record.endTime?"访问结束" : "已退卡（发卡有效）"
      } else if(text == 9 && newTime > record.endTime) {
        return '访问结束'
      }  else {
        return render.renderDictNative(text, statusList, false);
      }
      
    },
  },
  {
    title: '实际进出厂',
    dataIndex: 'actInFactory',
    width: 100,
  },
  {
    title: '进出权限状态',
    dataIndex: 'inFactoryProcess',
    width: 100,
  },
  {
    title: '发卡时间',
    dataIndex: 'hairpinTime',
    width: 100,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '退卡时间',
    dataIndex: 'recoveryTime',
    width: 100,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '卡ID',
    dataIndex: 'hairpinNo',
    width: 100,
  },
  // {
  //   title: '备注',
  //   dataIndex: 'remark',
  //   width: 150,
  // },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '访客姓名',
    field: 'visitorName',
    component: 'Input',
  },
  {
    label: '访客手机号',
    field: 'visitorPhone',
    component: 'Input',
  },
  {
    label: '访客身份证号',
    field: 'visitorIdCard',
    component: 'Input',
  },
  {
    label: '被访人姓名',
    field: 'visitedName',
    component: 'Input',
  },
  {
    label: '被访人手机号',
    field: 'visitedPhone',
    component: 'Input',
  },
  {
    label: '访问状态',
    field: 'vendorState',
    component: 'Select',
    componentProps: {
      options: [...statusList],
    },
  },
  {
    label: '实际进出厂',
    field: 'actInFactory',
    component: 'FactoryId'
  },
  {
    label: '进出权限状态',
    field: 'inFactoryProcess',
    component: 'Select',
    componentProps: {
      options: [{label: '正常', value: '正常'},{label: '异常', value: '异常'},{label: '注销', value: '注销'}]
    }
  },
  {
    label: '卡ID',
    field: 'hairpinNo',
    component: 'Input',
  },
  {
    label: '访问厂区',
    field: 'factoryId',
    component: 'Select',
    slot: 'factoryName',
  },
  {
    label: '访问区域',
    field: 'vendorRegion',
    component: 'Input',
  },
  {
    label: '访问事由',
    field: 'vendorMatter',
    component: 'Input',
  },
  {
    label: '访客单位',
    field: 'vendorUnit',
    component: 'Input',
  },
  {
    label: '访问时间',
    field: 'date',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '发卡时间',
    field: 'hairpinTime',
    component: 'DatePicker',
    componentProps: {
      placeholder: '请选择发卡时间',
      valueFormat: 'YYYY-MM-DD',
    },
  },
  {
    label: '退卡时间',
    field: 'recoveryTime',
    component: 'DatePicker',
  },
  {
    label: '车牌号',
    field: 'cardNo',
    component: 'Input',
  },
  {
    label: '是否访问绝密区',
    field: 'secretState',
    component: 'Select',
    componentProps: {
      options: [ {
        value: 0,
        label: '否',
        key: '1',
      }, {
        value: 1,
        label: '是',
        key: '2',
      }],
    },
  },
  {
    label: '绝密区',
    field: 'secretRegion',
    component: 'Input',
  },
];

export const rules = {
  visitedName: [{ required: true, message: '请输入被访人姓名', trigger: 'blur' }],
  visitedPhone: [{ required: true, message: '请输入被访人手机号', trigger: 'blur' }],
  visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
  visitorIdCard: [{ required: true, message: '请输入访客身份证号', trigger: 'blur' }],
  vendorUnit: [{ required: true, message: '请输入访客单位', trigger: 'blur' }],
  visitorPhone: [{ required: true, message: '请输入访客手机号', trigger: 'blur' }],
  vendorMatter: [{ required: true, message: '请输入访问事由', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择入厂开始时间', trigger: 'blur' }],
  endTime: [{ required: true, message: '请选择入厂结束时间', trigger: 'blur' }],
};
