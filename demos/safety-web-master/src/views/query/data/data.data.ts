import { BasicColumn, FormSchema } from '/@/components/Table';
import dayjs from 'dayjs';
import { render } from '/@/utils/common/renderUtils';

const sendStateList = [
  {
    value: 0,
    label: '人脸下发成功',
    key: '1',
  },
  {
    value: 1,
    label: '二维码下发失败',
    key: '2',
  },
  {
    value: 2,
    label: '人脸下发失败',
    key: '3',
  },
  {
    value: 3,
    label: '二维码下发成功',
    key: '4',
  },
  {
    value: 4,
    label: '门禁下发失败',
    key: '5',
  },
  {
    value: 5,
    label: '门禁下发成功',
    key: '6',
  },
];
export const columns: BasicColumn[] = [
  {
    title: '起草节点',
    dataIndex: 'eipCreateTime',
    width: 100,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '访客姓名',
    dataIndex: 'visitorName',
    width: 80,
  },
  {
    title: '访客身份证号',
    dataIndex: 'visitorIdCard',
    width: 180,
  },
  {
    title: '访客手机号',
    dataIndex: 'visitorPhone',
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
    title: '照片',
    dataIndex: 'photo',
    width: 100,

    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return render.renderImage2({ text });
      }
    },
  },
  {
    title: '访问厂区',
    dataIndex: 'vendorFactory',
    width: 180,
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
    width: 180,
    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return render.renderTip(text, 10);
      }
    },
  },
  {
    title: '下发时间',
    dataIndex: 'sendTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '下发结果',
    dataIndex: 'sendState',
    width: 150,
    customRender: ({ text }) => {
      return render.renderDictNative(text, sendStateList, false);
    },
  },
  {
    title: '出入口接口返回状态',
    dataIndex: 'jointResult',
    width: 150,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '访客姓名',
    field: 'visitorName',
    component: 'Input',
  },
  {
    label: '访客身份证号',
    field: 'visitorIdCard',
    component: 'Input',
  },
  {
    label: '手机号',
    field: 'visitorPhone',
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
    label: '下发结果',
    field: 'sendState',
    component: 'Input',
    slot: 'sendState',
  },
  {
    label: '下发时间',
    field: 'date',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
];

export const sendStateLists = [...sendStateList];
