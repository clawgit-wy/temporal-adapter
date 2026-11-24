import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
const deviceType = [
  {
    value: 0,
    label: '读卡器',
    key: '1',
  },
  {
    value: 1,
    label: '人脸平板',
    key: '2',
  },
  // {
  //   value: 2,
  //   label: '二维码识别',
  //   key: '3',
  // },
  {
    value: 3,
    label: '车道',
    key: '4',
  },
];

export const columns: BasicColumn[] = [
  {
    title: '厂区',
    dataIndex: 'factoryName',
    width: 150,
  },
  {
    title: '区域',
    dataIndex: 'regionName',
    width: 150,
  },
  {
    title: '设备类型',
    dataIndex: 'deviceType',
    width: 150,
    customRender: ({ text }) => {
      return render.renderDictNative(text, deviceType, false);
    },
  },
  {
    title: '设备号',
    dataIndex: 'deviceNo',
    width: 150,
  },
  {
    title: '设备名称',
    dataIndex: 'deviceName',
    width: 150,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 150,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '设备号',
    field: 'deviceNo',
    component: 'Input',
  },
  {
    label: '设备名称',
    field: 'deviceName',
    component: 'Input',
  },
  {
    label: '厂区',
    field: 'factoryId',
    component: 'Input',
    slot: 'factoryName',
  },
  {
    label: '区域',
    field: 'regionName',
    component: 'Input',
  },
  {
    label: '设备类型',
    field: 'deviceType',
    component: 'Select',
    componentProps: {
      options: [...deviceType],
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
    label: '厂区',
    field: 'factoryId',
    component: 'Select',
    required: true,
    slot: 'factoryName',
  },
  {
    label: '区域',
    field: 'regionId',
    component: 'Select',
    required: true,
    slot: 'regionName',
  },
  {
    label: '设备类型',
    field: 'deviceType',
    component: 'Select',
    required: true,
    componentProps: {
      options: [...deviceType],
    },
  },
  {
    label: '设备号',
    field: 'deviceNo',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 6,
    },
  },
  {
    label: '设备名称',
    field: 'deviceName',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 50,
    },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
    required: false,
    componentProps: {
      maxlength: 100,
    },
  },
];

export const deviceTypeList = [...deviceType];
