import { h } from 'vue';
import { FormSchema } from '/@/components/Form';
import { BasicColumn } from '/@/components/Table';
import { fieldManageTree, fieldTree } from './field.api';
import {
  outTypeFlag,
  outPersonTypeOptions,
  outPersonTypeFlag,
  vehicleFlagOptions,
  vehicleFlag,
  goodsFlagOptions,
  vehicleTypeFlag,
  spareTireFlag,
  spareTireFlagOptions,
  GoodsTypeFlag,
  productionMaterialReasonType,
  testMaterialReasonType,
  toolReasonType,
  cateringAndPropertyReasonType,
  constructionReasonType,
  salesProductReasonType,
  GoodsTypeOptions,
  productionMaterialReasonTypeFlag,
  isAllReceivedOptions,
  isReceivedOptions,
  isSetReceiverOptions,
  testMaterialReasonTypeFlag,
  isSetReceiverFlag,
  toolReasonTypeFlag,
  cateringAndPropertyReasonTypeFlag,
  constructionReasonTypeFlag,
  salesProductReasonTypeFlag,
  systemFlagOptions,
  releaseStatusOptions,
  releaseStatusFlag,
  isAllReceivedMap,
  leaveDataFlagOptions,
  validateReceivedOptions,
} from './const';
import { ITAssetReasonType } from '../goods-exit-permit/const';
import { selectLinkageListNew } from '../goods-exit-permit/field.api';
// 物资清单table列选项
export const goodsListColumns: BasicColumn[] = [
  {
    title: '物资编号(若无，请填写无)',
    dataIndex: 'goodsCode',
    key: 'goodsCode',
    editRow: true,
    editComponentProps: {},
  },
  {
    title: '物资名称',
    dataIndex: 'goodsName',
    key: 'goodsName',
    editRow: true,
    editComponentProps: {},
  },

  {
    title: '单位(计量单位)',
    dataIndex: 'goodsUnit',
    key: 'goodsUnit',
    editRow: true,
    editComponentProps: {},
  },
  {
    title: '数量',
    dataIndex: 'goodsNum',
    key: 'goodsNum',
    editRow: true,
    editComponent: 'InputNumber',
    editComponentProps: {},
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    editRow: true,
  },
];
// 车辆清单
export const outerColumns: BasicColumn[] = [
  {
    title: '车工号/车牌号',
    dataIndex: 'vehicleNo',
    key: 'vehicleNo',
    editRow: true,
    editComponentProps: {},
  },
  {
    title: 'VIN号',
    dataIndex: 'vinNo',
    key: 'vinNo',
    editRow: true,
    editComponentProps: {},
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    editRow: true,
  },
];

export const receiveResultColumn: BasicColumn[] = [
  {
    title: '接收人',
    dataIndex: 'receiveEmp',
    key: 'receiveEmp',
  },
  {
    title: '接收结果',
    dataIndex: 'receiveStatus',
    key: 'vehicleNo',
    customRender: ({ text }) => {
      return isAllReceivedMap[text];
    },
  },
  {
    title: '原因描述',
    dataIndex: 'remark',
    key: 'remark',
  },
  {
    title: '照片上传',
    dataIndex: 'receiveImg',
    key: 'receiveImg',
    slots: { customRender: 'sceneImg' },
  },
];
// 申请人信息
export const ApplicantSchemas: FormSchema[] = [
  {
    label: 'EIP用户名',
    field: 'creator',
    component: 'Input',
    colProps: { span: 8 },

    show: false,
    componentProps: {},
  },
  {
    label: '主题',
    field: 'subject',
    component: 'Input',
    colProps: { span: 12 },

    componentProps: {
      placeholder: '请输入主题',
      defaultValue: '应用系统开发模块-出门申请',
    },
  },
  {
    label: '流程触发标识',
    field: 'systemFlag',
    component: 'RadioGroup',
    colProps: { span: 12 },

    componentProps: {
      disabled: true,
      options: systemFlagOptions,
    },
  },
  {
    field: 'divider-create',
    component: 'Divider',
    label: '申请人信息',
  },
  {
    label: '申请日期',
    field: 'createTime',
    component: 'DatePicker',
    colProps: { span: 8 },
    componentProps: {
      disabled: true,
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
  },
  {
    label: '提交人部门',
    field: 'submitDept',
    component: 'Input',
    //
    show: false,
    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '姓名',
    field: 'submitName',
    component: 'SearchPerson',

    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '员工号',
    field: 'submitNo',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '出厂厂区',
    field: 'outFactoryId',
    component: 'ApiSelect',
    colProps: { span: 8 },
    componentProps: {
      api: selectLinkageListNew,
      valueField: 'id',
      labelField: 'fieldName',
    },
  },
  {
    label: '出门类型',
    field: 'outTypeId',
    component: 'ApiSelect',

    colProps: { span: 8 },
    helpMessage: ({ values }) => {
      const { outTypeId } = values;
      if (outTypeId === outTypeFlag.ITAsset) {
        return '客车IT资产统一由“企业信息化管理处”审批。';
      }
      if (outTypeId === outTypeFlag.equipment || outTypeId === outTypeFlag.hazardous) {
        return '客车设备类/危险废弃物统一由“设备安全部”审批。';
      }
      if (outTypeId === outTypeFlag.metering) {
        return '客车计量器具类统一由“质量保证处”审批。';
      }
      if (outTypeId === outTypeFlag.waste) {
        return '客车废料统一由“生产处”审批。';
      }

      return '';
    },
    componentProps: {
      api: fieldTree,
      params: {
        fieldType: 'goodsOut_dict_0_type',
        topFlag: false,
      },
      valueField: 'id',
      labelField: 'fieldName',
    },
  },

  {
    label: '审核人',
    field: 'examineEmpName',
    component: 'ApiSelect',
    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { outFactoryId, outTypeId } = values;
      if (outFactoryId && outTypeId) {
        return true;
      }
      return false;
    },

    colProps: { span: 8 },
    slot: 'examineEmpName',
    componentProps: {
      disabled: false,
    },
  },
  {
    label: '审核人电话',
    field: 'examinePhone',
    component: 'Input',

    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '审核人工号',
    field: 'examineEmpNo',
    component: 'Input',

    colProps: { span: 8 },
    show: false,
    componentProps: {},
  },
  {
    label: '审核人EIP',
    field: 'examineEmpEip',
    component: 'Input',

    colProps: { span: 8 },
    show: false,
    componentProps: {},
  },
  {
    label: '审核部门',
    field: 'examineDept',
    component: 'Input',

    colProps: { span: 8 },
    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { examineEmpName } = values;
      if (examineEmpName) {
        return true;
      }
      return false;
    },
    helpMessage: [
      '1、客车请选择体系后面的二级部门;',
      '2、宇通重工、深澜动力、智驱科技、精益达汽车零部件请选择公司后面的部门，其他创业公司请选择公司名称；',
      '3、各部门及创业公司审核部门及审核人信息请查阅附件《电子出门证审核人员信息表》',
    ],
    componentProps: {},
  },
  { label: '微机管理员No', show: false, field: 'microManagerNo', component: 'Input', colProps: { span: 8 } },
  { label: '微机管理员EIP', show: false, field: 'microManagerEip', component: 'Input', colProps: { span: 8 } },
  {
    label: '申请人（主管级或以上）领导',
    show({ values }) {
      return values.outTypeId == 'IT资产出厂';
    },
    field: 'microManagerName',
    component: 'Input',
    colProps: { span: 8 },
    componentProps: { disabled: true },
  },
  { label: '微机管理员手机号', show: false, field: 'microManagerPhone', component: 'Input', colProps: { span: 8 } },
  {
    label: '审核人上级',
    field: 'superiorEmpNo',
    component: 'SearchPerson',
    colProps: { span: 8 },
    slot: 'superiorEmpNo',
    helpMessage: ['申请人未上传出场原因证明材料，流程需升级至审核人上级!'],
    required: true,
    ifShow: ({ values }) => {
      console.log('---------------------------------------');

      // 仅在物资列表为生产物料，且原因类别为供应商物资出厂时展示
      const { reasonType, goodsType, leaveData } = values;
      console.log(reasonType, goodsType, leaveData, 'reasonType, goodsType, leaveData');
      if (leaveData) {
        console.log('消失了');
      }
      if (leaveData) {
        return false;
      }
      console.log(2);

      if (
        goodsType === GoodsTypeFlag.productionMaterial &&
        (reasonType === productionMaterialReasonTypeFlag.supplierMaterialExit ||
          reasonType === productionMaterialReasonTypeFlag.outsideProcessing ||
          reasonType === productionMaterialReasonTypeFlag.outsideMaintenance ||
          reasonType === productionMaterialReasonTypeFlag.other)
      ) {
        console.log(3);

        return true;
      }
      if (
        goodsType === GoodsTypeFlag.toolingEquipment &&
        (reasonType === toolReasonTypeFlag.supplierMaterialExit || reasonType === toolReasonTypeFlag.other)
      ) {
        console.log(4);

        return true;
      }
      // if (goodsType !== GoodsTypeFlag.cateringAndPropertyManagement && reasonType === toolReasonTypeFlag.supplierMaterialExit) {
      //   console.log(5);

      //   return true;
      // }
      console.log(6);
      console.log('---------------------------------------');

      return false;
    },
    // ifShow: ({values}) => {
    //   return values.outTypeId == 'IT资产出厂'
    // },
  },
  { label: '审核人上级EIP', show: false, field: 'superiorEmpEip', component: 'Input', colProps: { span: 8 } },
  { label: '审核人上级部门', show: false, field: 'superiorDept', component: 'Input', colProps: { span: 8 } },
  { label: '审核人上级', show: false, field: 'superiorEmpName', component: 'Input', colProps: { span: 8 } },
  {
    label: '物资出厂权限',
    field: 'outPowerDesc',
    component: 'InputTextArea',
    colProps: { span: 8 },
    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { examineEmpName } = values;
      if (examineEmpName) {
        return true;
      }
      return false;
    },
    componentProps: {},
  },
  {
    field: 'divider-outer',
    component: 'Divider',
    label: '出门人员信息',
  },
  {
    label: '出门人员类型',
    field: 'outPersonType',
    component: 'Select',

    colProps: { span: 8 },
    componentProps: {
      options: outPersonTypeOptions,
    },
  },
  {
    label: '出门人员工号',
    field: 'gooutEmpNo',
    component: 'Input',
    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { outPersonType } = values;
      if (outPersonType === outPersonTypeFlag.innter) {
        return true;
      }
      return false;
    },

    colProps: { span: 8 },
    slot: 'gooutEmpNo',
    componentProps: {
      disabled: false,
    },
  },
  {
    label: '出门人姓名',
    field: 'gooutEmpName',
    component: 'Input',

    colProps: { span: 8 },
    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { outPersonType } = values;
      if (outPersonType === outPersonTypeFlag.innter) {
        return true;
      }
      return false;
    },
    componentProps: {},
  },
  {
    label: '联系电话',
    field: 'gooutPhone',
    component: 'Input',
    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { outPersonType } = values;
      if (outPersonType === outPersonTypeFlag.innter) {
        return true;
      }
      return false;
    },

    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '出厂人员部门',
    field: 'gooutEmpDept',
    component: 'Input',
    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { outPersonType } = values;
      if (outPersonType === outPersonTypeFlag.innter) {
        return true;
      }
      return false;
    },

    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '外部人员出门人姓名',
    field: 'outsideName',
    component: 'Input',

    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { outPersonType } = values;
      if (outPersonType === outPersonTypeFlag.outer) {
        return true;
      }
      return false;
    },
    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '外部人员联系电话',
    field: 'outsidePhone',
    component: 'Input',

    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { outPersonType } = values;
      if (outPersonType === outPersonTypeFlag.outer) {
        return true;
      }
      return false;
    },
    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '提货单位名称',
    field: 'deliveryUnitName',
    component: 'Input',

    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { outPersonType } = values;
      if (outPersonType === outPersonTypeFlag.outer) {
        return true;
      }
      return false;
    },
    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '是否短驳用车',
    field: 'shortBargeCar',
    component: 'RadioGroup',
    colProps: { span: 8 },
    componentProps: {
      options: vehicleFlagOptions,
      disabled: true,
    },

    // slot: 'shortBargeCar',
    // componentProps: {
    //   options: [{label: "是", value: '是'}, {label: '否', value: '否'}],
    // },
  },
  {
    label: '短驳运单号',
    field: 'shortBargeNum',
    component: 'Input',
    colProps: { span: 8 },
    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { shortBargeCar } = values;
      if (shortBargeCar === vehicleFlag.yes) {
        return true;
      }
      return false;
    },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '是否车辆运输',
    field: 'isVehicle',
    component: 'RadioGroup',

    colProps: { span: 8 },
    componentProps: {
      options: vehicleFlagOptions,
    },
  },
  {
    label: '车牌号',
    field: 'vehicleNo',
    component: 'Input',
    //
    ifShow: ({ values }) => {
      // 审核人搜索依赖出门类型和出门厂区
      const { isVehicle } = values;
      if (isVehicle === vehicleFlag.yes) {
        return true;
      }
      return false;
    },
    colProps: { span: 8 },
    componentProps: {},
  },
  {
    field: 'divider-basic',
    component: 'Divider',
    label: '详细出厂信息',
  },
  {
    label: '物资类别',
    field: 'goodsType',
    component: 'Select',
    //
    colProps: { span: 8 },
    ifShow: ({ values }) => {
      // 物资类别仅在出厂类别为物资出厂时才会展示
      const { outTypeId } = values;
      if (outTypeId === outTypeFlag.material) {
        return true;
      }
      return false;
    },
    componentProps: {
      options: GoodsTypeOptions,
      // api: fieldTree,
      // params: {
      //   fieldType: 'goodsOut_goods_type',
      //   topFlag: false,
      // },
      // valueField: 'fieldName',
      // labelField: 'fieldName',
    },
  },
  {
    label: '原因类别',
    field: 'reasonType',
    component: 'Select',
    //
    colProps: { span: 8 },
    ifShow: ({ values }) => {
      // 原因类别仅在物资类别非维修工具或其他的时候才会展示
      const { goodsType, outTypeId } = values;
      if (
        outTypeId === outTypeFlag.ITAsset ||
        goodsType === GoodsTypeFlag.productionMaterial ||
        goodsType === GoodsTypeFlag.testMaterial ||
        goodsType === GoodsTypeFlag.toolingEquipment ||
        goodsType === GoodsTypeFlag.cateringAndPropertyManagement ||
        goodsType === GoodsTypeFlag.constructionMaterial ||
        goodsType === GoodsTypeFlag.salesProduct
      ) {
        return true;
      }
      return false;
    },
    componentProps: ({ formModel }) => {
      console.log(formModel, 'formModel');
      if (formModel.outTypeId == outTypeFlag.ITAsset) {
        return ITAssetReasonType;
      }
      if (formModel.goodsType === GoodsTypeFlag.productionMaterial) {
        return {
          options: productionMaterialReasonType,
        };
      }
      if (formModel.goodsType === GoodsTypeFlag.testMaterial) {
        return {
          options: testMaterialReasonType,
        };
      }
      if (formModel.goodsType === GoodsTypeFlag.toolingEquipment) {
        return {
          options: toolReasonType,
        };
      }
      if (formModel.goodsType === GoodsTypeFlag.cateringAndPropertyManagement) {
        return {
          options: cateringAndPropertyReasonType,
        };
      }
      if (formModel.goodsType === GoodsTypeFlag.constructionMaterial) {
        return {
          options: constructionReasonType,
        };
      }
      if (formModel.goodsType === GoodsTypeFlag.salesProduct) {
        return {
          options: salesProductReasonType,
        };
      }
      return {};
    },
  },
  {
    label: '是否需要设置接收人',
    field: 'isSetAccept',
    component: 'RadioGroup',
    colProps: { span: 8 },
    //
    ifShow: ({ values }) => {
      // 仅在物资列表为试验物料，且原因类别为跨厂区转运时展示
      const { reasonType, goodsType } = values;
      if (goodsType === GoodsTypeFlag.testMaterial && reasonType === testMaterialReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      if (goodsType === GoodsTypeFlag.cateringAndPropertyManagement && reasonType === cateringAndPropertyReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      if (goodsType === GoodsTypeFlag.constructionMaterial && reasonType === constructionReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      // 跨厂区转运均需要接收人
      if (reasonType === toolReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      return false;
    },
    componentProps: {
      options: isSetReceiverOptions,
    },
  },
  {
    label: '质保部审批人',
    field: 'qaExamineEmp',
    component: 'Select',
    //
    colProps: { span: 8 },
    ifShow: ({ values }) => {
      // 仅在物资列表为生成物料，且原因类别为外购件质量问题退换货时展示
      const { reasonType, goodsType } = values;
      if (goodsType === GoodsTypeFlag.productionMaterial && reasonType === productionMaterialReasonTypeFlag.qualityProblemReturn) {
        return true;
      }
      return false;
    },
    componentProps: {},
    slot: 'qaExamineEmp',
  },
  {
    label: '质保部审批人员工号',
    field: 'qaExamineEmpNo',
    component: 'Input',
    //
    ifShow: false,
    colProps: { span: 8 },
    componentProps: {
      // disabled: true
    },
  },
  {
    label: '质保部审批人EIP',
    field: 'qaExamineEmpEip',
    component: 'Input',
    //
    ifShow: false,
    colProps: { span: 8 },
    componentProps: {
      // disabled: true
    },
  },
  {
    label: '原因说明',
    field: 'reasonDesc',
    //
    ifShow: ({ values }) => {
      // 物资类别仅在出厂类别为物资出厂时才会展示
      const { outTypeId } = values;
      if (
        outTypeId === outTypeFlag.material ||
        outTypeId === outTypeFlag.ITAsset ||
        outTypeId === outTypeFlag.equipment ||
        outTypeId === outTypeFlag.waste ||
        outTypeId === outTypeFlag.hazardous ||
        outTypeId === outTypeFlag.metering
      ) {
        return true;
      }
      return false;
    },
    component: 'Input',
    colProps: { span: 8 },
    componentProps: {
      options: [],
    },
  },
  // 物资清单
  {
    label: '',
    field: 'goodsList',
    component: 'Select',
    colProps: { span: 24 },
    ifShow: ({ values }) => {
      const { outTypeId, outTypeName } = values;
      if (
        outTypeId === outTypeFlag.material ||
        outTypeId === outTypeFlag.ITAsset ||
        outTypeId === outTypeFlag.equipment ||
        outTypeId === outTypeFlag.waste ||
        outTypeId === outTypeFlag.hazardous ||
        outTypeId === outTypeFlag.metering ||
        outTypeId === outTypeFlag.vehicle ||
        outTypeName === outTypeFlag.material ||
        outTypeName === outTypeFlag.ITAsset ||
        outTypeName === outTypeFlag.equipment ||
        outTypeName === outTypeFlag.waste ||
        outTypeName === outTypeFlag.hazardous ||
        outTypeName === outTypeFlag.vehicle ||
        outTypeName === outTypeFlag.metering
      ) {
        return true;
      }
      return false;
    },
    slot: 'goodsList',
    componentProps: {
      disabled: false,
    },
  },
  {
    label: '车辆类别',
    field: 'vehicleType',
    //
    component: 'ApiSelect',
    colProps: { span: 8 },
    ifShow: ({ values }) => {
      // 车辆类别仅在出厂类别为车辆出厂时才会展示
      const { outTypeId } = values;
      if (outTypeId === outTypeFlag.vehicle) {
        return true;
      }
      return false;
    },
    componentProps: {
      api: fieldTree,
      params: {
        fieldType: 'goodsOut_vehicle_type',
        topFlag: false,
      },
      valueField: 'id',
      labelField: 'fieldName',
    },
  },
  {
    label: '是否有携带物资出厂',
    field: 'isGoods',
    //
    component: 'RadioGroup',
    ifShow: ({ values }) => {
      // 仅在出厂类别为车辆出厂时才会展示
      const { outTypeId } = values;
      if (outTypeId === outTypeFlag.vehicle) {
        return true;
      }
      return false;
    },
    colProps: { span: 8 },
    componentProps: {
      options: goodsFlagOptions,
    },
  },
  {
    label: '是否有携带备胎出厂',
    field: 'isSpareTire',
    component: 'RadioGroup',
    //
    ifShow: ({ values }) => {
      const { vehicleType } = values;
      // 如果车辆类别为实验车的话展示此选项
      if (vehicleType === vehicleTypeFlag.syccc) {
        return true;
      }
      return false;
    },
    colProps: { span: 8 },
    componentProps: {
      options: spareTireFlagOptions,
    },
  },
  {
    label: '备胎数量',
    field: 'spareTireNum',
    component: 'InputNumber',
    //
    ifShow: ({ values }) => {
      const { isSpareTire } = values;
      // 如果车辆类别为实验车的话展示此选项
      if (isSpareTire === spareTireFlag.yes) {
        return true;
      }
      return false;
    },
    colProps: { span: 8 },
  },
  {
    label: '',
    field: 'vehicleList',
    component: 'Select', // 新组件，用于展示table类型数据
    colProps: { span: 24 },
    ifShow: ({ values }) => {
      // 仅在出厂类别为车辆出厂时才会展示
      const { outTypeId } = values;
      console.log(outTypeId, outTypeFlag.vehicle, 'outTypeFlag.vehicle');
      if (outTypeId === outTypeFlag.vehicle) {
        return true;
      }
      return false;
    },
    slot: 'vehicleList',
    componentProps: {
      disabled: false,
    },
  },
  {
    label: '',
    field: 'receiveList',
    component: 'JAddInput', // 新组件，用于展示table类型数据
    colProps: { span: 24 },
    ifShow: ({ values }) => {
      // 仅在物资列表为生成物料，且原因类别为跨厂区转运时展示
      const { reasonType, goodsType, isSetAccept } = values;
      if (reasonType === '公司内跨厂区办公') {
        return true;
      }
      if (goodsType === GoodsTypeFlag.productionMaterial && reasonType === productionMaterialReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }

      if (isSetAccept === isSetReceiverFlag.yes) {
        return true;
      }
      if (goodsType === GoodsTypeFlag.toolingEquipment && reasonType === toolReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      if (goodsType === GoodsTypeFlag.salesProduct && reasonType === salesProductReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      // 跨厂区转运均需要接收人
      if (reasonType === toolReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      return false;
    },
    slot: 'receiveList',
    componentProps: {
      disabled: false,
    },
  },
  {
    label: '上传现场照片',
    field: 'sceneImg',
    required: ({ values }) => {
      const { vehicleType } = values;
      return vehicleType != vehicleTypeFlag.syccc;
    },
    component: 'JCustomImageUpload',
    colProps: { span: 12 },
    helpMessage: ['车辆运输的必须为装车后现场拍摄照片'],
    componentProps: {
      fileMax: 10,
    },
  },
  {
    label: '是否上传过磅单',
    field: 'weightDataFlag',
    component: 'RadioGroup',
    required: true,
    ifShow: ({ values }) => {
      const { outTypeId } = values;
      // 如果车辆类别为实验车的话展示此选项
      if (outTypeId === outTypeFlag.waste || outTypeId === outTypeFlag.hazardous) {
        return true;
      }
      return false;
    },
    colProps: { span: 8 },
    componentProps: {
      options: [
        {
          label: '是',
          value: '是',
        },
        {
          label: '否',
          value: '否',
        },
      ],
      disabled: true,
    },
  },
  {
    label: '磅单上传',
    field: 'weightDataUrl',
    component: 'JCustomImageUpload',
    colProps: { span: 12 },
    ifShow: ({ values }) => {
      // 仅在出厂类别为废料出厂时才会展示
      const { weightDataFlag } = values;
      if (weightDataFlag == '是') {
        return true;
      }
      return false;
    },
    componentProps: {
      fileMax: 5,
      disabled: true,
    },
  },

  {
    label: h('div', {}, [
      h('span', '是否全部接收'),
      h('div', { style: { color: '#508cee', fontSize: '12px', lineHeight: '1.5', marginTop: '4px' } }, [
        h('div', '全部接收：确认收取出门证内全部物资，完成提交后，其他经办人无需重复执行接收操作。'),
        h('div', '部分接收：仅收取出门证内部分物资；若无其他签收经办人，单独选择部分接收时，请描述未全部接收原因并需审核人二次确认。'),
      ]),
    ]),
    field: 'receiveStatus',
    component: 'RadioGroup',
    colProps: { span: 24 },
    ifShow: false,
    defaultValue: '1',
    componentProps: {
      disabled: true,
      options: isAllReceivedOptions,
    },
  },
  {
    label: '上传接收确认照片',
    field: 'receiveConfirmImg',
    component: 'JCustomImageUpload',
    colProps: { span: 24 },
    ifShow: false,
    required: ({ values }) => {
      return values.receiveStatus != '0';
    },
    subLabel: '(车辆运输的必须为装车后现场拍摄照片)',
    // helpMessage: ['提醒：上传照片必须为现场拍摄(物资)照片车辆运输的必须为卸车前现场拍摄照片'],
    componentProps: {
      disabled: true,
      fileMax: 5,
    },
  },
  // {
  //   label: '是否接收',
  //   helpMessage: '接收环节流程端接收人未进行接收操作，请审核人确认接收人是否接收物资',
  //   field: 'checkReceiveStatus',
  //   component: 'RadioGroup',
  //   colProps: { span: 24 },
  //   ifShow: ({ values }) => {
  //     // 仅在物资列表为生成物料，且原因类别为跨厂区转运时展示
  //     const { reasonType, goodsType, isSetAccept } = values;
  //     if (goodsType === GoodsTypeFlag.productionMaterial && reasonType === productionMaterialReasonTypeFlag.crossFactoryTransfer) {
  //       return true;
  //     }
  //     if (isSetAccept === isSetReceiverFlag.yes) {
  //       return true;
  //     }
  //     if (goodsType === GoodsTypeFlag.toolingEquipment && reasonType === toolReasonTypeFlag.crossFactoryTransfer) {
  //       return true;
  //     }
  //     return false;
  //   },
  //   componentProps: {
  //     disabled: true,
  //     options: isReceivedOptions,
  //   },
  // },
  {
    label: '原因简要说明',
    field: 'unReceiveReason',
    component: 'Input',
    ifShow: false,
    colProps: { span: 24 },
    required: ({ values }) => {
      return values.receiveStatus != '1';
    },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '出厂原因证明材料',
    field: 'leaveData',
    component: 'JCustomImageUpload',
    colProps: { span: 24 },
    helpMessage: ['未上传出厂原因证明材料，流程升级至审核人上级!'],
    ifShow: ({ values }) => {
      // 仅在物资列表为生产物料，且原因类别为供应商物资出厂时展示
      const { reasonType, goodsType } = values;
      console.log(reasonType, goodsType, '/-/-/-/-/-');
      if (
        goodsType === GoodsTypeFlag.productionMaterial &&
        [
          productionMaterialReasonTypeFlag.other,
          productionMaterialReasonTypeFlag.supplierMaterialExit,
          productionMaterialReasonTypeFlag.outsideProcessing,
          productionMaterialReasonTypeFlag.outsideMaintenance,
        ].includes(reasonType)
      ) {
        return true;
      }
      if (
        goodsType === GoodsTypeFlag.toolingEquipment &&
        [productionMaterialReasonTypeFlag.other, productionMaterialReasonTypeFlag.supplierMaterialExit].includes(reasonType)
      ) {
        return true;
      }
      if (reasonType === productionMaterialReasonTypeFlag.other && [GoodsTypeFlag.productionMaterial].includes(goodsType)) {
        return true;
      }
      if (
        reasonType === productionMaterialReasonTypeFlag.supplierMaterialExit &&
        [
          GoodsTypeFlag.productionMaterial,
          GoodsTypeFlag.testMaterial,
          GoodsTypeFlag.toolingEquipment,
          GoodsTypeFlag.cateringAndPropertyManagement,
        ].includes(goodsType)
      ) {
        return true;
      }
      return false;
    },
    componentProps: {
      fileMax: 5,
    },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
    colProps: { span: 24 },
    componentProps: {
      disabled: true,
    },
  },
  {
    field: 'goodsFileUrl',
    component: 'FileUpload',
    // helpMessage: '最多上传3个文件',
    label: '上传附件',
    componentProps: { fileMax: 5, text: '上传附件', disabled: true },
  },
  // {
  //   label: '审核人上级',
  //   field: 'superiorEmpName',
  //   component: 'Input',
  //   required: true,
  //   ifShow: false,
  //   colProps: { span: 24 },
  //   componentProps: {},
  //   slot: 'superiorEmpName',
  // },
  // {
  //   label: '审核人上级员工号',
  //   field: 'superiorEmpNo',
  //   component: 'Input',
  //   required: true,
  //   ifShow: false,
  //   colProps: { span: 24 },
  //   componentProps: {
  //     // disabled: true
  //   },
  // },
  // {
  //   label: '审核人上级EIP',
  //   field: 'superiorEmpEip',
  //   component: 'Input',
  //   required: true,
  //   ifShow: false,
  //   colProps: { span: 24 },
  //   componentProps: {
  //     // disabled: true
  //   },
  // },
  // {
  //   label: '审核人上级部门',
  //   field: 'superiorDept',
  //   component: 'Input',
  //   required: true,
  //   ifShow: false,
  //   colProps: { span: 24 },
  //   componentProps: {
  //     // disabled: true
  //   },
  // },
];

// 放行信息
export const ReleaseSchemas: FormSchema[] = [
  {
    label: '门卫人员',
    field: 'releaseEmp',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: { disabled: true },
  },
  {
    label: '执行时间',
    field: 'releaseTime',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: { disabled: true },
  },
  {
    label: '放行状态',
    field: 'releaseStatus',
    component: 'Select',
    colProps: { span: 12 },
    required: true,
    slot: 'releaseStatus',
    // componentProps: {
    //   disabled: true,
    //   options: releaseStatusOptions,
    // },
  },
  {
    label: '异常类型',
    field: 'exceptionDesc',
    required: true,
    component: 'Select',
    colProps: { span: 8 },
    ifShow: ({ values }) => values.releaseStatus == '2',
    componentProps: {
      options: [
        { name: '数量不符', value: '数量不符' },
        { name: '种类不符', value: '种类不符' },
        { name: '信息填写错误', value: '信息填写错误' },
        { name: '无该类别审批权限', value: '无该类别审批权限' },
        { name: '其他', value: '其他' },
      ],
    },
  },
  // {
  //   label: '异常类型',
  //   field: 'exceptionDesc',
  //   required: true,
  //   component: 'ApiSelect',
  //   colProps: { span: 8 },
  //   componentProps: {
  //     api: fieldTree,
  //     params: {
  //       fieldType: 'goodsOut_vehicle_type',
  //       topFlag: false,
  //     },
  //     numberToString: true,
  //     valueField: 'id',
  //     labelField: 'fieldName',
  //   },
  // },
  {
    label: '原因描述',
    field: 'exceptionRemark',
    component: 'InputTextArea',
    colProps: { span: 12 },
    required: true,
    ifShow: ({ values }) => values.releaseStatus == '2',
    componentProps: {
      placeholder: '请输入异常原因',
    },
  },
];
// 抽查字段
export const CheckSchemas: FormSchema[] = [
  {
    label: '验证类型',
    field: 'businessType',
    component: 'Select',
    colProps: { span: 12 },
    required: true,
    componentProps: {
      placeholder: '请输入异常类型',
      options: [
        { label: '双工厂货车抽查', value: '双工厂货车抽查' },
        { label: '大宗物资抽查', value: '大宗物资抽查' },
        { label: '封箱物资抽查抽查', value: '封箱物资抽查抽查' },
        { label: '废料车复磅出厂抽查', value: '废料车复磅出厂抽查' },
        { label: '货车环保标准抽查', value: '货车环保标准抽查' },
      ],
    },
  },
  {
    label: '异常类型',
    field: 'exceptionType',
    component: 'ApiSelect',
    colProps: { span: 8 },
    required: true,
    componentProps: {
      api: (params) => fieldTree(params),
      labelField: 'fieldName',
      valueField: 'fieldName',
      params: { fieldType: 'goodsOut_exec_type', topFlag: false },
      // options: [{ name: "数量不符", value: '数量不符' }, { name: "种类不符" , value: "种类不符"}, { name: "信息填写错误", value: "信息填写错误" }, { name: "无该类别审批权限", value: "无该类别审批权限" }, { name: "其他", value: "其他" }]
    },
  },
  {
    label: '原因描述',
    field: 'exceptionDesc',
    component: 'Input',
    colProps: { span: 12 },
    required: true,
    componentProps: {
      placeholder: '请输入原因描述',
    },
  },
];
// 验证字段
export const validateSchemas: FormSchema[] = [
  {
    label: '物资是否接收',
    field: 'isReceiveShr',
    component: 'Select',
    colProps: { span: 12 },
    required: true,
    componentProps: {
      placeholder: '请选择',
      options: validateReceivedOptions,
    },
  },
  {
    label: '原因描述',
    field: 'reasonShr',
    component: 'InputTextArea',
    colProps: { span: 12 },
    required: true,
    componentProps: {
      placeholder: '请输入异常原因',
    },
  },
  {
    label: '照片上传',
    field: 'photoShr',
    component: 'JCustomImageUpload',
    colProps: { span: 24 },
    required: false,
    helpMessage: ['提醒：上传照片必须为现场拍摄(物资)照片车辆运输的必须为卸车前现场拍摄照片'],
    componentProps: {
      fileMax: 5,
    },
  },
];
