import dayjs from 'dayjs';
import { h } from 'vue';
import { FormSchema, BasicColumn, TableImg } from '/@/components/Table';
import { getNewDictItems } from '/@/api/common/api';
// import { fieldTree } from '/@/views/exitpass/goods-exit-permit/field.api';
import {
  outTypeFlag,
  GoodsTypeFlag,
  productionMaterialReasonType,
  testMaterialReasonType,
  toolReasonType,
  cateringAndPropertyReasonType,
  constructionReasonType,
  salesProductReasonType,
  GoodsTypeOptions,
  ITAssetReasonType,
} from '/@/views/exitpass/goods-exit-permit/const';
import { StatusOptions, StatusEnumMap, outPersonTypeMap, outPersonTypeFlag,isAllReceivedMap } from './const'
import { fieldTree } from './user.api';
export const columns: BasicColumn[] = [
  { title: '流程编号', dataIndex: 'flowNo', width: 120, slots: { customRender: 'flowNo' } },
  { title: '主题', dataIndex: 'subject', width: 120 },
  { title: '提交时间', dataIndex: 'createTime', width: 120, customRender: ({ text }) => dayjs(text).format('YYYY-MM-DD HH:mm:ss') },
  {
    title: '状态',
    dataIndex: 'status',
    width: 120,
    customRender: ({ text }) => {
      console.log(text, String(text), 'text');
      return StatusEnumMap[String(text)];
    },
  },
  { title: '申请人姓名', dataIndex: 'submitName', width: 120 },
  { title: '申请人员工号/手机号', dataIndex: 'submitNo', width: 120 },
  { title: '申请人部门', dataIndex: 'submitDept', width: 120 },
  { title: '出门厂区', dataIndex: 'outFactoryName', width: 120 },
  { title: '出门类型', dataIndex: 'outTypeName', width: 120 },
  { title: '物资类别', dataIndex: 'goodsType', width: 120 },
  { title: '原因类别', dataIndex: 'reasonType', width: 120 },
  { title: '车辆类别', dataIndex: 'vehicleType', width: 120 },
  { title: '审核人', dataIndex: 'examineEmpName', width: 120 },
  { title: '审核人员工号', dataIndex: 'examineEmpNo', width: 120 },
  { title: '审核人部门', dataIndex: 'examineDept', width: 120 },
  { title: '出门人类型', dataIndex: 'outPersonType', width: 120, customRender: ({ text }) => outPersonTypeMap[text] },
  {
    title: '出门人姓名',
    dataIndex: 'gooutEmpName',
    width: 120,
    customRender: ({ text, record }) => {
      const { outsideName = '' } = record;
      return text || outsideName;
    },
  },
  {
    title: '出门人手机号/员工号',
    dataIndex: 'gooutEmpNo',
    width: 120,
    customRender: ({ text, record }) => {
      const { outsidePhone = '' } = record;
      return text || outsidePhone;
    },
  },
  {
    title: '出门人部门',
    dataIndex: 'gooutEmpDept',
    width: 120,
    customRender: ({ text, record }) => {
      const { outPersonType = '' } = record;
      if (outPersonType == outPersonTypeFlag.innter) {
        return text;
      } else {
        return outPersonTypeMap[outPersonTypeFlag.outer];
      }
    },
  },
  { title: '放行人姓名', dataIndex: 'releaseEmp', width: 120 },
  { title: '放行时间', dataIndex: 'releaseTime', width: 120 },
  { title: '放行状态', dataIndex: 'releaseStatus', width: 120 ,customRender: ({ text, record }) => {
    //0-待放行,1-已放行,2-放行异常,3-废弃
    if(text == 0){
      return '待放行'
    }
    if(text == 1){
      return '已放行'
    }
    if(text == 2){
      return '放行异常'
    }
    if(text == 3){
      return '废弃'
    }
  }
},
  { title: '是否短驳用车', dataIndex: 'shortBargeCar', width: 120 },
  { title: '短驳运单号', dataIndex: 'shortBargeNum', width: 120 },
  { title: '是否车辆运输', dataIndex: 'isVehicle', width: 120 },
  { title: '车牌号', dataIndex: 'vehicleNo', width: 120 },
  { title: '是否需要接受人确认', dataIndex: 'isSetAccept', width: 120 },
  {
    title: '接收人',
    dataIndex: 'receiver',
    width: 120,
    customRender: ({ record }) => {
      const { receiveList = [] } = record || {};
      return (receiveList || [])
        .map((item) => {
          return `${item.receiveEmp}-${item.receiveEmpNo}-${item.receiveEmpDept}`;
        })
        .join(',');
    },
  },
  { title: '现场照片', dataIndex: 'sceneImg', width: 120, slots: { customRender: 'sceneImg' } },
  {
    title: '接收照片',
    dataIndex: 'receiptPhoto',
    width: 120,
    customRender: ({ record }) => {
      const { receiveList = [] } = record || {};
      const imglist = (receiveList || []).reduce((cur, item) => {
        console.log(item, 'item');
        const { receiveImg = '' } = item;
        if (!receiveImg) return cur;
        const temp = (receiveImg || '').split(/[,;|]+/g);
        return [...cur, ...temp];
      }, []);
      return h(TableImg, {
        size: '60',
        simpleShow: true,
        imgList: imglist,
      });
    },
  },
  {
    title: '物资是否接收',
    dataIndex: 'receiveStatus',
    width: 120,
    customRender: ({ record }) => {
      const { receiveStatus = '' } = record || {};

      return isAllReceivedMap[receiveStatus];
    },
  },
  { title: '接收异常原因', dataIndex: 'unReceiveReason', width: 120 },
  { title: '证明材料', dataIndex: 'leaveData', width: 200, slots: { customRender: 'leaveData' } },
  {
    title: '过磅单',
    dataIndex: 'weightDataUrl',
    width: 120,
    customRender: ({ record }) => {
      const { weightDataUrl = '' } = record || {};
      if (!weightDataUrl) return '';
      return h(TableImg, {
        size: '60',
        simpleShow: true,
        imgList: weightDataUrl.split(/[,;|]+/g),
      });
    },
  },
  { title: '原因', dataIndex: 'reasonDesc', width: 120 },
];

// 表格搜索配置
export const searchFormSchema: FormSchema[] = [
  {
    label: '申请人',
    field: 'submitEmp',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '姓名/员工号/手机号',
    },
  },
  {
    label: '审核人',
    field: 'examineEmp',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '姓名/员工号/手机号',
    },
  },
  {
    label: '出门人',
    field: 'goOutEmp',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '姓名/员工号/手机号',
    },
  },
  {
    label: '放行人',
    field: 'releaseEmp',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '姓名/员工号/手机号',
    },
  },
  {
    label: '是否车辆运输',
    field: 'isVehicle',
    component: 'Select',
    colProps: { span: 8 },
    componentProps: {
      options: [
        { label: '是', value: '是' },
        { label: '否', value: '否' },
      ],
    },
  },
  {
    label: '车牌号',
    field: 'vehicleNo',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '请输入车牌号',
    },
  },
  {
    label: '状态',
    field: 'status',
    component: 'Select',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '请选择状态',
      options: StatusOptions,
    },
  },
  {
    label: '出门厂区',
    field: 'outFactoryId',
    component: 'ApiSelect',
    componentProps: (params) => {
      const { formActionType } = params;
      const { setFieldsValue } = formActionType;
      return {
        api: async () => {
          const res = await getNewDictItems('factory');
          return res;
        },
        placeholder: '请选择厂区',
        valueField: 'id',
        labelField: 'fieldName',
        mode: 'multiple',
      };
    },
  },
  {
    label: '目的厂区',
    field: 'targetFactoryId',
    component: 'ApiSelect',
    componentProps: (params) => {
      const { formActionType } = params;
      const { setFieldsValue } = formActionType;
      return {
        api: async () => {
          const res = await getNewDictItems('factory');
          return res;
        },
        placeholder: '请选择厂区',
        valueField: 'id',
        labelField: 'fieldName',
        mode: 'multiple',
      };
    },
  },
  {
    label: '出门类型',
    field: 'outTypeName',
    component: 'ApiSelect',
    colProps: { span: 8 },
    componentProps: ({ formModel }) => {
      return {
        api: fieldTree,
        params: {
          fieldType: 'goodsOut_dict_0_type',
          topFlag: false,
        },
        valueField: 'fieldName',
        labelField: 'fieldName',
      };
    },
  },
  {
    label: '物资类别',
    field: 'goodsType',
    component: 'Select',
    colProps: { span: 8 },
    componentProps: ({ formActionType }) => {
      return {
        options: GoodsTypeOptions,
        onChange: (value) => {
          formActionType.setFieldsValue({
            reasonType: undefined,
          });
        },
      };
    },
  },
  {
    label: '原因类别',
    field: 'reasonType',
    component: 'Select',
    colProps: { span: 8 },
    dynamicPropskey: 'options',
    dynamicPropsVal: ({ model }) => {
      if (model.outTypeName == outTypeFlag.ITAsset) {
        return ITAssetReasonType;
      }
      if (model.goodsType === GoodsTypeFlag.productionMaterial) {
        return productionMaterialReasonType;
      }
      if (model.goodsType === GoodsTypeFlag.testMaterial) {
        return testMaterialReasonType;
      }
      if (model.goodsType === GoodsTypeFlag.toolingEquipment) {
        return toolReasonType;
      }
      if (model.goodsType === GoodsTypeFlag.cateringAndPropertyManagement) {
        return cateringAndPropertyReasonType;
      }
      if (model.goodsType === GoodsTypeFlag.constructionMaterial) {
        return constructionReasonType;
      }
      if (model.goodsType === GoodsTypeFlag.salesProduct) {
        return salesProductReasonType;
      }
      return [];
    },
    componentProps: {
      placeholder: '请选择原因类别',
    },
  },
  {
    label: '车辆类别',
    field: 'vehicleType',
    component: 'ApiSelect',
    componentProps: {
          api: fieldTree,
          params: {
            fieldType: 'goodsOut_vehicle_type',
            topFlag: false,
          },
          valueField: 'fieldName',
          labelField: 'fieldName',
        },
  },
  {
    label: '流程编号',
    field: 'flowNo',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '请输入流程编号',
    },
  },
  {
    label: '主题',
    field: 'subject',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '请输入主题',
    },
  },
  {
    label: '提交人部门',
    field: 'submitDept',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '请输入提交人部门',
    },
  },
  {
    label: '出门人部门',
    field: 'gooutEmpDept',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '请输入出门人部门',
    },
  },
  {
    label: '审核人部门',
    field: 'examineDept',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '请输入审核人部门',
    },
  },
  // 放行状态：0-待放行,1-已放行,2-放行异常,3-废弃
  {
    label: '放行状态',
    field: 'releaseStatus',
    component: 'Select',
    colProps: { span: 8 },
    componentProps: {
      placeholder: '放行状态',
      options: [
        { label: '待放行', value: '0' },
        { label: '已放行', value: '1' },
        { label: '放行异常', value: '2' },
        { label: '废弃', value: '3' },
      ],
    },
  },
  {
    label: '提交时间',
    field: 'submissionTime',
    component: 'RangeDate',
    colProps: { span: 8 },
    defaultValue:`${dayjs().subtract(7, 'day').format('YYYY-MM-DD')},${dayjs().format('YYYY-MM-DD')}`,
    componentProps: {
      placeholder: '请选择提交时间',
    },
  },
  {
    label: '是否短驳用车',
    field: 'shortBargeCar',
    component: 'Select',
    componentProps: {
      options: [
        { label: '是', value: '是' },
        { labeL: '否', value: '否' },
      ],
    },
  },
  { label: '短驳运单号', field: 'shortBargeNum', component: 'Input' },
];
