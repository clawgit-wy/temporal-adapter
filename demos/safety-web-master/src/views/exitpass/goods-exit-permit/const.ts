// 流程触发标志选项
export enum SystemFlagEnum {
    EIP = '1',
    ZSYT = '2',
    ZYMini = '3',
}
export const systemFlagOptions = [
    {
        label: 'EIP',
        value: SystemFlagEnum.EIP,
    },
    {
        label: '掌上宇通',
        value: SystemFlagEnum.ZSYT,
    },
    {
        label: '掌宇小程序',
        value: SystemFlagEnum.ZYMini,
    }
]

// 出门人员类型选项
export enum outPersonTypeFlag {
    innter = '1',
    outer = '2',
}

export const outPersonTypeOptions = [
    {
        label: '公司内部人员',
        value: outPersonTypeFlag.innter,
    },
    {
        label: '外部人员',
        value: outPersonTypeFlag.outer,
    },
];
// 出门类型
export enum outTypeIdMap {
    
}
// 是否有携带物资出厂
export enum goodsFlag {
    yes = '1',
    no = '0',
}
export const goodsFlagOptions = [
    {
        label: '是',
        value: goodsFlag.yes,
    },
    {
        label: '否',
        value: goodsFlag.no,
    },
];
// 是否有车辆运输
export enum vehicleFlag {
    yes = '是',
    no = '否',
}
export const vehicleFlagOptions = [
    {
        label: '是',
        value: vehicleFlag.yes,
    },
    {
        label: '否',
        value: vehicleFlag.no,
    },
];
// 出厂厂区
export enum outFactoryIdFlag {
    oneFactory = '一工厂（十八里河）', // 一工厂（十八里河）
    twoFactory = '二工厂（新能源）', // 二工厂（新能源）
    researchCenter = '研发中心', // 研发中心
    marketingCenter = '营销中心', // 营销中心
    yutongHeavy = '宇通重工', // 宇通重工
    shenlanPower = '深澜动力', // 深澜动力
    zhituDrive = '智驱科技', // 智驱科技
    specialVehicleFactory = '专用车工厂', // 专用车工厂
    jiuLongFinishedProduct = '九龙成品库', // 九龙成品库
    engineeringTestCenter = '工程试验中心', // 工程试验中心
    jiuLongWarehouseKD = '九龙仓库（KD）', // 九龙仓库（KD）
    jiuLongWarehouseAfterSales = '九龙仓库（售后）', // 九龙仓库（售后）
    leanDistrictWest = '精益达西区（高新区）', // 精益达西区（高新区）
    leanDistrictEast = '精益达东区（经开区）', // 精益达东区（经开区）
    chassisDivision = '底盘事业部（八大街）', // 底盘事业部（八大街）
    oldFactory = '老厂家属区', // 老厂家属区
    zhenggongFamily = '郑工家属区', // 郑工家属区
}
export const outFactoryIdMap = {
    [outFactoryIdFlag.oneFactory]: '一工厂（十八里河）',
    [outFactoryIdFlag.twoFactory]: '二工厂（新能源）',
    [outFactoryIdFlag.researchCenter]: '研发中心',
    [outFactoryIdFlag.marketingCenter]: '营销中心',
    [outFactoryIdFlag.yutongHeavy]: '宇通重工',
    [outFactoryIdFlag.shenlanPower]: '深澜动力',
    [outFactoryIdFlag.zhituDrive]: '智驱科技',
    [outFactoryIdFlag.specialVehicleFactory]: '专用车工厂',
    [outFactoryIdFlag.jiuLongFinishedProduct]: '九龙成品库',
    [outFactoryIdFlag.engineeringTestCenter]: '工程试验中心',
    [outFactoryIdFlag.jiuLongWarehouseKD]: '九龙仓库（KD）',
    [outFactoryIdFlag.jiuLongWarehouseAfterSales]: '九龙仓库（售后）',
    [outFactoryIdFlag.leanDistrictWest]: '精益达西区（高新区）',
    [outFactoryIdFlag.leanDistrictEast]: '精益达东区（经开区）',
    [outFactoryIdFlag.chassisDivision]: '底盘事业部（八大街）',
    [outFactoryIdFlag.oldFactory]: '老厂家属区',
    [outFactoryIdFlag.zhenggongFamily]: '郑工家属区',
}
export enum outTypeFlag {
    material = '物资出厂', // 物资出厂
    vehicle = '车辆出厂', // 车辆出厂
    ITAsset = 'IT资产出厂', // IT资产出厂
    equipment = '设备出厂', // 设备出厂
    metering = '计量器具出厂', // 计量器具出厂
    waste = '废料出厂', // 废料出厂
    hazardous = '危险废弃物出厂', // 危险废弃物出厂
}
export const outTypeFlagMap = {
    [outTypeFlag.material]: '物资出厂',
    [outTypeFlag.vehicle]: '车辆出厂',
    [outTypeFlag.ITAsset]: 'IT资产出厂',
    [outTypeFlag.equipment]: '设备出厂',
    [outTypeFlag.metering]: '计量器具出厂',
    [outTypeFlag.waste]: '废料出厂',
    [outTypeFlag.hazardous]: '危险废弃物出厂',
}
// 出厂车辆类别
export enum vehicleTypeFlag {
    // 试制车出厂
    szccc = '试制车出厂',
    // 路试车出厂
    lsccc = '路试车出厂',
    // 维修车出厂
    wxccc = '维修车出厂',
    // 试验车出厂
    syccc = '试验车出厂',
    // 工艺车出厂
    gyccc = '工艺车出厂',
    // 其它
    qt = '其它',
}

// 是否携带备胎出厂
export enum spareTireFlag {
    yes = '1',
    no = '0',
}
export const spareTireFlagOptions = [
    {
        label: '是',
        value: spareTireFlag.yes,
    },
    {
        label: '否',
        value: spareTireFlag.no,
    }]

// 物资类别
export enum GoodsTypeFlag {
    // 生产物料
    productionMaterial = '生产物料',
    // 试验物料
    testMaterial = '试验物料',
    // 工装器具
    toolingEquipment = '工装器具',
    // 餐饮及物业类物资
    cateringAndPropertyManagement = '餐饮及物业类物资',
    // 施工类物资
    constructionMaterial = '施工类物资',
    // 销售产品
    salesProduct = '销售产品',
    //维修工具
    maintenanceTool = '维修工具',
    // 其它
    other = '其它',
}
export const GoodsTypeOptions = [
    {
        label: '生产物料',
        value: GoodsTypeFlag.productionMaterial,
    },
    {
        label: '试验物料',
        value: GoodsTypeFlag.testMaterial,
    },
    {
        label: '工装器具',
        value: GoodsTypeFlag.toolingEquipment,
    },
    {
        label: '餐饮及物业类物资',
        value: GoodsTypeFlag.cateringAndPropertyManagement,
    },
    {
        label: '施工类物资',
        value: GoodsTypeFlag.constructionMaterial,
    },
    {
        label: '销售产品',
        value: GoodsTypeFlag.salesProduct,
    },
    {
        label: '维修工具',
        value: GoodsTypeFlag.maintenanceTool,
    },
    {
        label: '其它',
        value: GoodsTypeFlag.other,
    }
]

// 当物资类别为生成物料时，对应的原因类型选项
export enum productionMaterialReasonTypeFlag {
    crossFactoryTransfer = '跨厂区转运',
    supplierMaterialExit = '供应商物资出厂',
    qualityProblemReturn = '外购件质量问题退换货',
    outsideProcessing = '外出代加工',
    outsideMaintenance = '外出维修',
    outsideOutLine = '外部电脑退网',
    companyOut = '公司以外区域办公',
    companyWork = '公司内跨厂区办公',
    ItAsset = 'IT返厂维修',
    other = '其它',
}
export const productionMaterialReasonType = [
    {
        label: '跨厂区转运',
        value: productionMaterialReasonTypeFlag.crossFactoryTransfer
    },
    {
        label: '供应商物资出厂',
        value: productionMaterialReasonTypeFlag.supplierMaterialExit
    },
    {
        label: '外购件质量问题退换货',
        value: productionMaterialReasonTypeFlag.qualityProblemReturn
    },
    {
        label: '外出代加工',
        value: productionMaterialReasonTypeFlag.outsideProcessing
    },
    {
        label: '外出维修',
        value: productionMaterialReasonTypeFlag.outsideMaintenance
    },
    {
        label: '其它',
        value: productionMaterialReasonTypeFlag.other
    }
]
export const ITAssetReasonType = [
    {
        label: '外部电脑退网',
        value: productionMaterialReasonTypeFlag.outsideOutLine,
        disabled: true,
    },
    {
        label: '公司以外区域办公',
        value: productionMaterialReasonTypeFlag.companyOut,
        disabled: true,
    },
    {
        label: '公司内跨厂区办公',
        value: productionMaterialReasonTypeFlag.companyWork
    },
    {
        label: 'IT返厂维修',
        value: productionMaterialReasonTypeFlag.ItAsset
    },
    
]

// 当物资类别为试验物料时，对应的原因类型选项
export enum testMaterialReasonTypeFlag {
    crossFactoryTransfer = '跨厂区转运',
    supplierMaterialExit = '供应商物资出厂',
    other = '其它'
}
export const testMaterialReasonType = [
    {
        label: '跨厂区转运',
        value: testMaterialReasonTypeFlag.crossFactoryTransfer
    },
    {
        label: '供应商物资出厂',
        value: testMaterialReasonTypeFlag.supplierMaterialExit
    },
    {
        label: '其它',
        value: testMaterialReasonTypeFlag.other
    }]
// 当物资类别为工装器具时，对应的原因类型选项
export enum toolReasonTypeFlag {
    crossFactoryTransfer = '跨厂区转运',
    supplierMaterialExit = '供应商物资出厂',
    other = '其它'
}
// 确认是否接收物资：已接收，未接收
export enum leaveDataFlagEnum {
    received = '是',
    notReceived = '否'
}
export const leaveDataFlagOptions = [
    {
        label: '是',
        value: leaveDataFlagEnum.received
    },
    {
        label: '否',
        value: leaveDataFlagEnum.notReceived
    }
]
export const toolReasonType = [
    {
        label: '跨厂区转运',
        value: toolReasonTypeFlag.crossFactoryTransfer
    },
    {
        label: '供应商物资出厂',
        value: toolReasonTypeFlag.supplierMaterialExit
    },
    {
        label: '其它',
        value: toolReasonTypeFlag.other
    }]
// 当物资类别为餐饮及物业类物资时，对应的原因类型选项
export enum cateringAndPropertyReasonTypeFlag {
    crossFactoryTransfer = '跨厂区转运',
    supplierMaterialExit = '供应商物资出厂',
    // 外出售卖
    outSale = '外出售卖',
    other = '其它'
}
export const cateringAndPropertyReasonType = [
    {
        label: '跨厂区转运',
        value: cateringAndPropertyReasonTypeFlag.crossFactoryTransfer
    },
    {
        label: '供应商物资出厂',
        value: cateringAndPropertyReasonTypeFlag.supplierMaterialExit
    },
    {
        label: '外出售卖',
        value: cateringAndPropertyReasonTypeFlag.outSale
    },
    {
        label: '其它',
        value: cateringAndPropertyReasonTypeFlag.other
    }]
// 当物资类别为施工类物资时，对应的原因类型选项
export enum constructionReasonTypeFlag {
    crossFactoryTransfer = '跨厂区转运',
    // 施工物资出厂
    constructionMaterialExit = '施工物资出厂',
    other = '其它'
}
export const constructionReasonType = [
    {
        label: '跨厂区转运',
        value: constructionReasonTypeFlag.crossFactoryTransfer
    },
    {
        label: '施工物资出厂',
        value: constructionReasonTypeFlag.constructionMaterialExit
    },
    {
        label: '其它',
        value: constructionReasonTypeFlag.other
    }]
// 当物资类别为销售产品时，对应的原因类型选项
export enum salesProductReasonTypeFlag {
    crossFactoryTransfer = '跨厂区转运',
    other = '外销'
}
export const salesProductReasonType = [
    {
        label: '跨厂区转运',
        value: salesProductReasonTypeFlag.crossFactoryTransfer
    },
    {
        label: '外销',
        value: salesProductReasonTypeFlag.other
    }]

// 是否全部接收：全部接收，部分接收，未接收
export enum isAllReceivedFlag {
    allReceived = '全部接收',
    partReceived = '部分接收',
    notReceived = '未接收'
}
export const isAllReceivedOptions = [
    {
        label: '全部接收',
        value: isAllReceivedFlag.allReceived
    },
    {
        label: '部分接收',
        value: isAllReceivedFlag.partReceived
    },
    {
        label: '未接收',
        value: isAllReceivedFlag.notReceived
    }
]
// 确认是否接收物资：已接收，未接收
export enum isReceivedFlag {
    received = '已接收',
    notReceived = '未接收'
}
export const isReceivedOptions = [
    {
        label: '已接收',
        value: isReceivedFlag.received
    },
    {
        label: '未接收',
        value: isReceivedFlag.notReceived
    }
]
// 是否需要设置接收人：是，否
export enum isSetReceiverFlag {
    yes = '是',
    no = '否'
}
export const isSetReceiverOptions = [
    {
        label: '是',
        value: isSetReceiverFlag.yes
    },
    {
        label: '否',
        value: isSetReceiverFlag.no
    }
]

// 放行状态：0-待放行,1-已放行,2-放行异常,3-废弃
export enum releaseStatusFlag {
    waitExit = '待放行',
    exited = '已放行',
    exitAbnormal = '放行异常',
    discard = '废弃'
}
export const releaseStatusOptions = [
    {
        label: '待放行',
        value: releaseStatusFlag.waitExit
    },
    {
        label: '已放行',
        value: releaseStatusFlag.exited
    },
    {
        label: '放行异常',
        value: releaseStatusFlag.exitAbnormal
    },
    {
        label: '废弃',
        value: releaseStatusFlag.discard
    }
]