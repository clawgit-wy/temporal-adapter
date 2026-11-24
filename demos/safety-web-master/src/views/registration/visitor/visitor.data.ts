import { BasicColumn, FormSchema } from '/@/components/Table';
import dayjs from 'dayjs';
import { render } from '/@/utils/common/renderUtils';
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
    label: '审批通过',
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
  {
    value: 11,
    label: '权限已下发',
    key: '11',
  },
];
export const columns: BasicColumn[] = [
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
    title: '入厂开始时间',
    dataIndex: 'startTime',
    width: 110,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '入厂结束时间',
    dataIndex: 'endTime',
    width: 110,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
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
  // 0未发卡,1已发卡,2已退卡,3审批中
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
      } else {
        return render.renderDictNative(text, statusList, false);
      }
    },
    // customRender: ({ text }) => ({ 0: '未发卡', 1: '已发卡', 2: '已退卡', 3: '审批中', 4: '超时关闭', 5: '未到访' }[text]),
  },
  {
    title: '实际进出厂',
    dataIndex: 'actInFactory',
    width: 100,
  },
  {
    title: '进出权限状态',
    dataIndex: 'inFactoryProcess',
    width: 110,
  },
  {
    title: '授权厂区',
    dataIndex: 'issueFactory',
    width: 120,
  }, 
  {
    title: '绝密区',
    dataIndex: 'secretRegion',
    width: 120,
  }, 
  {
    title: '是否访问绝密区',
    dataIndex: 'secretState',
    width: 140,
    customRender: ({ text }) => (text === 1 ? '是' : '否'),
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
    title: '被授权人',
    dataIndex: 'authorizedByName',
    width: 100,
  },
  {
    title: '被访人上级',
    dataIndex: 'authorizedName',
    width: 100,
  },
  {
    title: '起草时间',
    dataIndex: 'eipCreateTime',
    width: 90,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '发卡卡号',
    dataIndex: 'hairpinNo',
    width: 100,
  },
  {
    title: '回收卡号',
    dataIndex: 'recoveryNo',
    width: 100,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '访客姓名',
    field: 'visitorName',
    component: 'Input',
    componentProps: { autocomplete: 'new-password' },
  },
  {
    label: '访客手机号',
    field: 'visitorPhone',
    component: 'Input',
    componentProps: { autocomplete: 'new-password' },
  },
  {
    label: '访客身份证号',
    field: 'visitorIdCard',
    component: 'Input',
    componentProps: { autocomplete: 'new-password' },
  },
  {
    label: '访问厂区',
    field: 'factoryId',
    component: 'Select',
    slot: 'factoryName',
  },
  {
    label: '被访人姓名',
    field: 'visitedName',
    component: 'Input',
    componentProps: { autocomplete: 'new-password' },
  },
  {
    label: '被访人手机号',
    field: 'visitedPhone',
    component: 'Input',
    componentProps: { autocomplete: 'new-password' },
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
    label: '进出权限状态',
    field: 'inFactoryProcess',
    component: 'Select',
    componentProps: {
      options: [ {
        value: '异常',
        label: '异常',
        key: '1',
      }, {
        value: '正常',
        label: '正常',
        key: '2',
      }, {
        value: '注销',
        label: '注销',
        key: '3',
      }],
    },
  },
  {
    label: '授权厂区不等访问厂区',
    field: 'equalFactory',
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
];

export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '卡号',
    field: 'recoveryNo',
    component: 'Input',
    required: true,
  },
  {
    label: '访客姓名',
    field: 'visitorName',
    component: 'Input',
    required: true,
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
