import { BasicColumn, FormSchema } from '/@/components/Table';
import { getAreaCascaderProps } from '../common/area';

export const factoryOptions = [
  { label: '十八里河', value: '十八里河' },
  { label: '新能源', value: '新能源' },
];

export const buildingOptions = [
  { label: '技术南楼', value: '技术南楼' },
  { label: '财务南楼', value: '财务南楼' },
  { label: '/', value: '/' },
];

export const entryModeOptions = [
  { label: '车行', value: '车行' },
  { label: '步行', value: '步行' },
];

export const deviceColumns: BasicColumn[] = [
  {
    title: '设备名称',
    dataIndex: 'name',
    width: 160,
  },
  {
    title: '设备编码',
    dataIndex: 'indexCode',
    width: 140,
  },
  {
    title: '所属厂区',
    dataIndex: 'factoryName',
    width: 140,
  },
  {
    title: '所属楼栋',
    dataIndex: 'buildName',
    width: 140,
  },
  {
    title: '出入方式',
    dataIndex: 'accessMethod',
    width: 120,
  },
  {
    title: '最近更新时间',
    dataIndex: 'updateTime',
    width: 160,
  },
  {
    title: '最近提交人',
    dataIndex: 'updateBy',
    width: 120,
  },
];

export const thresholdColumns: BasicColumn[] = [
  {
    title: '场景名称',
    dataIndex: 'sceneName',
    width: 220,
  },
  {
    title: '阈值',
    dataIndex: 'sceneValue',
    width: 120,
  },
  {
    title: '最近更新时间',
    dataIndex: 'updateTime',
    width: 160,
  },
  {
    title: '最近提交人',
    dataIndex: 'updateBy',
    width: 120,
  },
];

export const deviceSearchFormSchema: FormSchema[] = [
  {
    label: '设备名称',
    field: 'name',
    component: 'Input',
  },
  {
    label: '所属区域',
    field: 'areaCode',
    component: 'Cascader',
    componentProps: getAreaCascaderProps,
  },
  {
    label: '最近提交人',
    field: 'updateBy',
    component: 'Input',
  },
];

export const thresholdSearchFormSchema: FormSchema[] = [
  {
    label: '场景名称',
    field: 'sceneName',
    component: 'Input',
  },
  {
    label: '最近提交人',
    field: 'updateName',
    component: 'Input',
  },
];

export const deviceFormSchemas: FormSchema[] = [
  {
    label: 'id',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: 'indexCode',
    field: 'indexCode',
    component: 'Input',
    show: false,
  },
  {
    label: '所属区域',
    field: 'areaCode',
    component: 'Cascader',
    required: true,
    componentProps: getAreaCascaderProps,
  },
  {
    label: '出入方式',
    field: 'entryMode',
    component: 'Select',
    required: true,
    componentProps: {
      options: entryModeOptions,
    },
  },
];

export const thresholdFormSchemas: FormSchema[] = [
  {
    label: 'id',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: 'sceneCode',
    field: 'sceneCode',
    component: 'Input',
    show: false,
  },
  {
    label: '场景名称',
    field: 'sceneName',
    component: 'Input',
    required: true,
  },
  {
    label: '判断人脸相似阈值',
    field: 'threshold',
    component: 'InputNumber',
    required: true,
    rules: [{ validator: validateThreshold, trigger: 'change' }],
    componentProps: {
      min: 0,
      max: 100,
      step: 1,
      precision: 0,
      addonAfter: '%',
      parser: parseThresholdInput,
      keyboard: false,
      onKeydown: handleThresholdKeydown,
      onPaste: handleThresholdPaste,
    },
  },
];

function parseThresholdInput(value) {
  const text = String(value ?? '').replace(/[^\d]/g, '');
  return text || '';
}

function validateThreshold(_, value) {
  if (value === undefined || value === null || value === '') {
    return Promise.reject('请输入阈值');
  }
  const num = Number(value);
  if (!Number.isInteger(num) || num < 0 || num > 100) {
    return Promise.reject('请输入0-100的整数');
  }
  return Promise.resolve();
}

const ALLOW_KEYS = new Set(['Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 'Home', 'End', 'Enter']);

function handleThresholdKeydown(event) {
  const e = event as KeyboardEvent;
  if (e.ctrlKey || e.metaKey || e.altKey) {
    return;
  }
  if (ALLOW_KEYS.has(e.key)) {
    return;
  }
  if (!/^\d$/.test(e.key)) {
    e.preventDefault();
    return;
  }
  const input = e.target as HTMLInputElement;
  if (!input) {
    return;
  }
  const start = input.selectionStart ?? input.value.length;
  const end = input.selectionEnd ?? input.value.length;
  const next = `${input.value.slice(0, start)}${e.key}${input.value.slice(end)}`.replace(/[^\d]/g, '');
  if (next && Number(next) > 100) {
    e.preventDefault();
  }
}

function handleThresholdPaste(event) {
  const e = event as ClipboardEvent;
  const text = e.clipboardData?.getData('text') || '';
  if (!/^\d+$/.test(text)) {
    e.preventDefault();
    return;
  }
  const input = e.target as HTMLInputElement;
  if (!input) {
    return;
  }
  const start = input.selectionStart ?? input.value.length;
  const end = input.selectionEnd ?? input.value.length;
  const next = `${input.value.slice(0, start)}${text}${input.value.slice(end)}`.replace(/[^\d]/g, '');
  if (next && Number(next) > 100) {
    e.preventDefault();
  }
}
