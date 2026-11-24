import { FormSchema } from '/@/components/Form';
import { BasicColumn } from '/@/components/Table';
import { fieldTree, selectLinkageListNew } from './field.api';
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
  leaveDataFlagOptions,
  ITAssetReasonType,
} from './const';
import { Modal } from 'ant-design-vue';
// 物资清单table列选项
export const goodsListColumns: BasicColumn[] = [
  {
    title: '物资编号(若无，请填写无)',
    dataIndex: 'goodsCode',
    key: 'goodsCode',
    slots: { customRender: 'goodsCode' },
  },
  {
    title: '物资名称',
    dataIndex: 'goodsName',
    key: 'goodsName',
    slots: { customRender: 'goodsName' },
  },

  {
    title: '单位(计量单位)',
    dataIndex: 'goodsUnit',
    key: 'goodsUnit',
    slots: { customRender: 'goodsUnit' },
  },
  {
    title: '数量',
    dataIndex: 'goodsNum',
    key: 'goodsNum',
    slots: { customRender: 'goodsNum' },
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    slots: { customRender: 'remark' },
  },
];
// 车辆清单
export const outerColumns: BasicColumn[] = [
  {
    title: '车工号/车牌号',
    dataIndex: 'vehicleNo',
    key: 'vehicleNo',
    editRow: true,
    editComponentProps: {
      required: true,
    },
  },
  {
    title: 'VIN号',
    dataIndex: 'vinNo',
    key: 'vinNo',
    editRow: true,
    editComponentProps: {
      required: true,
    },
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
    dataIndex: 'vehicleNo',
    key: 'vehicleNo',
  },
  {
    title: '接收结果',
    dataIndex: 'vehicleNo',
    key: 'vehicleNo',
  },
  {
    title: '原因描述',
    dataIndex: 'vehicleNo',
    key: 'vehicleNo',
  },
  {
    title: '照片上传',
    dataIndex: 'sceneImg',
    key: 'sceneImg',
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
    required: true,
    show: false,
    componentProps: {},
  },
  {
    label: '主题',
    field: 'subject',
    component: 'Input',
    colProps: { span: 12 },
    required: true,
    componentProps: {
      disabled: true,
      placeholder: '请输入主题',
      defaultValue: '应用系统开发模块-出门申请',
    },
  },
  {
    label: '流程触发标识',
    field: 'systemFlag',
    component: 'RadioGroup',
    colProps: { span: 12 },
    required: true,
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
    // required: true,
    show: false,
    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '姓名',
    field: 'submitName',
    component: 'SearchPerson',
    required: true,
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
    required: true,
    colProps: { span: 8 },
    componentProps: ({ formModel }) => {
      console.log(formModel, 'formModels---');

      return {
        api: selectLinkageListNew,
        valueField: 'id',
        labelField: 'fieldName',
        onChange: (value: string) => {
          formModel.examineEmpName = '';
          formModel.examineEmpEip = '';
          formModel.examineDept = '';
          formModel.outPowerDesc = '';
          formModel.examineEmpNo = '';
          formModel.examinePhone = '';
        },
      };
    },
  },
  {
    label: '出门类型',
    field: 'outTypeId',
    component: 'ApiSelect',
    required: true,
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
    componentProps: ({ formModel }) => {
      return {
        api: fieldTree,
        params: {
          fieldType: 'goodsOut_dict_0_type',
          topFlag: false,
        },
        valueField: 'fieldName',
        labelField: 'fieldName',
        onChange: (value: string) => {
          formModel.examineEmpName = '';
          formModel.examineEmpEip = '';
          formModel.examineDept = '';
          formModel.outPowerDesc = '';
          formModel.examineEmpNo = '';
          formModel.examinePhone = '';
        },
      };
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
    required: true,
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
    // required: true,
    colProps: { span: 8 },
    show: false,
    componentProps: {},
  },
  {
    label: '审核人工号',
    field: 'examineEmpNo',
    component: 'Input',
    required: true,
    colProps: { span: 8 },
    show: false,
    componentProps: {},
  },
  {
    label: '审核人EIP',
    field: 'examineEmpEip',
    component: 'Input',
    required: true,
    colProps: { span: 8 },
    show: false,
    componentProps: {},
  },
  {
    label: '审核部门',
    field: 'examineDept',
    component: 'Input',
    required: true,
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
  {
    label: '申请人（主管级或以上）领导',
    field: 'microManagerNo',
    component: 'SearchPerson',
    colProps: { span: 8 },
    slot: 'microManagerNo',
    required: true,
    ifShow: ({ values }) => {
      return values.outTypeId == 'IT资产出厂';
    },
  },
  { label: '微机管理员EIP', show: false, field: 'microManagerEip', component: 'Input', colProps: { span: 8 } },
  { label: '微机管理员姓名', show: false, field: 'microManagerName', component: 'Input', colProps: { span: 8 } },
  { label: '微机管理员手机号', show: false, field: 'microManagerPhone', component: 'Input', colProps: { span: 8 } },
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
    field: 'divider-basic',
    component: 'Divider',
    label: '详细出厂信息',
  },
  {
    label: '物资类别',
    field: 'goodsType',
    component: 'Select',
    required: true,
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
    },
  },
  {
    label: '原因类别',
    field: 'reasonType',
    component: 'Select',
    required: true,
    colProps: { span: 8 },
    ifShow: ({ values }) => {
      // 原因类别仅在物资类别非维修工具或其他的时候才会展示
      const { goodsType, outTypeId } = values;
      console.log(goodsType, 'goodsTupe');
      if (
        outTypeId === outTypeFlag.ITAsset ||
        (outTypeId === outTypeFlag.material &&
          (goodsType === GoodsTypeFlag.productionMaterial ||
            goodsType === GoodsTypeFlag.testMaterial ||
            goodsType === GoodsTypeFlag.toolingEquipment ||
            goodsType === GoodsTypeFlag.cateringAndPropertyManagement ||
            goodsType === GoodsTypeFlag.constructionMaterial ||
            goodsType === GoodsTypeFlag.salesProduct))
      ) {
        return true;
      }
      return false;
    },
    dynamicPropskey: 'options',
    dynamicPropsVal: ({ model }) => {
      if (model.outTypeId == outTypeFlag.ITAsset) {
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
      onClick(val) {
        if (val.target.innerHTML == '外部电脑退网' || val.target.innerHTML == '公司以外区域办公') {
          Modal.confirm({
            title: '提示',
            content: '此类电脑需经IT处理后方能离厂，请在EIP提交流程，路径：流程中心--IT管理(J)>>信息安全管理(B)>>电脑离厂信息安全管控流程>>新建 ',
            onOk() {
              return;
            },
            onCancel() {
              return;
            },
          });
        }
      },
    },
  },
  {
    label: '是否需要设置接收人',
    field: 'isSetAccept',
    component: 'RadioGroup',
    colProps: { span: 8 },
    required: true,
    defaultValue: isSetReceiverFlag.yes,
    show: ({ values }) => {
      const { reasonType, goodsType, outTypeId } = values;
      const requireGoodsKeys = ['生产物料', '工装器具', '销售产品'];
      if (outTypeId == '物资出厂' && reasonType == '跨厂区转运' && requireGoodsKeys.includes(goodsType)) {
        return false;
      }
      return true;
    },
    ifShow: ({ values }) => {
      const { reasonType, goodsType, outTypeId } = values;
      if (reasonType === '公司内跨厂区办公') {
        return true;
      }
      // 实验物料&& 跨厂区转运
      if (goodsType === GoodsTypeFlag.testMaterial && reasonType === testMaterialReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      // 餐饮及物业类物资 && 跨厂区转运
      if (goodsType === GoodsTypeFlag.cateringAndPropertyManagement && reasonType === cateringAndPropertyReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      // 施工类物资 && 跨厂区转运
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
    required: true,
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
    required: true,
    ifShow: ({ values }) => {
      // 仅在物资列表为生成物料，且原因类别为外购件质量问题退换货时展示
      const { reasonType, goodsType } = values;
      if (goodsType === GoodsTypeFlag.productionMaterial && reasonType === productionMaterialReasonTypeFlag.qualityProblemReturn) {
        return true;
      }
      return false;
    },
    colProps: { span: 8 },
    componentProps: {
      // disabled: true
    },
  },
  {
    label: '质保部审批人EIP',
    field: 'qaExamineEmpEip',
    component: 'Input',
    required: true,
    ifShow: ({ values }) => {
      // 仅在物资列表为生成物料，且原因类别为外购件质量问题退换货时展示
      const { reasonType, goodsType } = values;
      if (goodsType === GoodsTypeFlag.productionMaterial && reasonType === productionMaterialReasonTypeFlag.qualityProblemReturn) {
        return true;
      }
      return false;
    },
    colProps: { span: 8 },
    componentProps: {
      // disabled: true
    },
  },
  {
    label: '原因说明',
    field: 'reasonDesc',
    required: true,
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

  {
    label: '车辆类别',
    field: 'vehicleType',
    required: true,
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
      valueField: 'fieldName',
      labelField: 'fieldName',
    },
  },
  {
    label: '是否有携带物资出厂',
    field: 'isGoods',
    required: true,
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
    required: true,
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
    required: true,
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
  // 物资清单
  {
    label: '',
    field: 'goodsList',
    component: 'Select',
    colProps: { span: 24 },
    ifShow: ({ values }) => {
      const { outTypeId, isGoods } = values;
      if (isGoods == '1') return true;
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
    slot: 'goodsList',
    componentProps: {
      disabled: false,
    },
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
      if (isSetAccept === isSetReceiverFlag.yes) {
        return true;
      }
      if (goodsType === GoodsTypeFlag.productionMaterial && reasonType === productionMaterialReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }

      if (goodsType === GoodsTypeFlag.toolingEquipment && reasonType === toolReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      if (goodsType === GoodsTypeFlag.salesProduct && reasonType === salesProductReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
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
    field: 'divider-outer',
    component: 'Divider',
    label: '出门人员信息',
  },
  {
    label: '出门人员类型',
    field: 'outPersonType',
    component: 'Select',
    required: true,
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
    required: true,
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
    required: true,
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
    required: true,
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
    required: true,
    colProps: { span: 8 },
    componentProps: {},
  },
  {
    label: '外部人员出门人姓名',
    field: 'outsideName',
    component: 'Input',
    required: true,
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
    required: true,
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
    required: true,
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
    required: true,
    colProps: { span: 8 },
    slot: 'shortBargeCar',
    // componentProps: {
    //   options: [{label: "是", value: '是'}, {label: '否', value: '否'}],
    // },
  },
  {
    label: '短驳运单号',
    field: 'shortBargeNum',
    component: 'Input',
    required: true,
    colProps: { span: 8 },
    ifShow: ({ values }) => {
      return values.shortBargeCar == '是';
    },
  },
  {
    label: '是否车辆运输',
    field: 'isVehicle',
    component: 'RadioGroup',
    required: true,
    colProps: { span: 8 },
    dynamicDisabled: ({ values }) => values.shortBargeCar == '是',
    componentProps: {
      options: vehicleFlagOptions,
    },
  },
  {
    label: '车牌号',
    field: 'vehicleNo',
    component: 'Input',
    required: true,
    rules: [
      { required: true, message: '请输入车牌号', trigger: 'blur' },
      {
        validator: (_, value) => {
          if (
            !/^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-HJ-NP-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$/.test(
              value.trim()
            )
          ) {
            return Promise.reject('车牌号有误，请重填');
          }
          return Promise.resolve();
        },
        message: '车牌号有误，请重填',
      },
    ],
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
    label: '上传现场照片',
    field: 'sceneImg',
    required: ({ values }) => {
      const { vehicleType } = values;
      return vehicleType != vehicleTypeFlag.syccc;
    },
    component: 'JCCustomUploadMultipleImages',
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
    required: true,
    // dynamicRules: ({ values }) => {
    //   const { weightDataFlag } = values;
    //   return weightDataFlag == '是' ? [{ required: true, message: '请上传磅单' }] : [];
    // },
    componentProps: {
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
  //     options: isReceivedOptions
  //   },
  // },

  {
    label: '出厂原因证明材料',
    field: 'leaveData',
    component: 'JCCustomUploadMultipleImages',
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
    label: '原因简要说明',
    field: 'unReceiveReason',
    component: 'Input',
    ifShow: ({ values }) => {
      // 仅在物资列表为生成物料，且原因类别为跨厂区转运时展示
      const { reasonType, goodsType, isSetAccept } = values;
      if (goodsType === GoodsTypeFlag.productionMaterial && reasonType === productionMaterialReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      if (isSetAccept === isSetReceiverFlag.yes) {
        return true;
      }
      if (goodsType === GoodsTypeFlag.toolingEquipment && reasonType === toolReasonTypeFlag.crossFactoryTransfer) {
        return true;
      }
      return false;
    },
    colProps: { span: 24 },
    componentProps: {
      disabled: true,
    },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
    colProps: { span: 24 },
    dynamicRules: ({ values }) => {
      const { weightDataFlag } = values;
      return weightDataFlag == '否' ? [{ required: true }] : [];
    },
  },

  {
    field: 'goodsFileUrl',
    component: 'FileUpload',
    // helpMessage: '最多上传3个文件',
    label: '物资清单附件',
    componentProps: { fileMax: 5, text: '上传附件' },
  },
  {
    label: '参考附件',
    field: 'referAttachment',
    component: 'Input',
    colProps: { span: 24 },
    slot: 'referAttachment',
  },
];

// 放行信息
export const ReleaseSchemas: FormSchema[] = [
  {
    label: '放行状态',
    field: 'releaseStatus',
    component: 'Select',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
      defaultValue: releaseStatusFlag.waitExit,
      options: releaseStatusOptions,
    },
  },
  // {
  //   label: '异常类型',
  //   field: 'vehicleType',
  //   required: true,
  //   component: 'ApiSelect',
  //   colProps: { span: 8 },
  //   componentProps: {
  //     api: fieldTree,
  //     params: {
  //       fieldType: 'goodsOut_vehicle_type',
  //       topFlag: false,
  //     },
  //     valueField: 'id',
  //     labelField: 'fieldName',
  //   },
  // },
  {
    label: '放行异常原因',
    field: 'exceptionDesc',
    component: 'InputTextArea',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
      placeholder: '请输入异常原因',
    },
  },
];
// 抽查字段
export const CheckSchemas: FormSchema[] = [
  {
    label: '抽查类型',
    field: 'releaseStatus',
    component: 'Select',
    colProps: { span: 12 },
    componentProps: {
      placeholder: '请输入异常类型',
      disabled: true,
      options: releaseStatusOptions,
    },
  },

  {
    label: '抽查人',
    field: 'checkPerson',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
      placeholder: '请输入抽查人',
    },
  },
  {
    label: '抽查结果',
    field: 'exceptionDesc',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
      placeholder: '请输入抽查结果',
    },
  },
  {
    label: '异常类型',
    field: 'vehicleType',
    component: 'ApiSelect',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
      placeholder: '请输入异常类型',
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
    label: '原因描述',
    field: 'exceptionDesc',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: {
      disabled: true,
      placeholder: '请输入原因描述',
    },
  },
];
