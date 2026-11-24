
// 状态: status
export enum StatusEnum {
    Publish = '30',
    Approving = '20',
    Discard = '00',
    TimeOut = '60'

}
// 流程状态 30-发布 20-审批中 00-废弃
export const vehicleMap = {
    yes: '是',
    no: '否',
}

export const StatusEnumMap = {
    [StatusEnum.Publish]: '发布',
    [StatusEnum.Approving]: '审批中',
    [StatusEnum.Discard]: '废弃',
    [StatusEnum.TimeOut]: '超时结束',
}
export const StatusOptions = [
    {
        label: '发布',
        value: StatusEnum.Publish,
    },
    {
        label: '审批中',
        value: StatusEnum.Approving,
    },
    {
        label: '废弃',
        value: StatusEnum.Discard,
    },
    {
        label: '超时结束',
        value: StatusEnum.TimeOut,
    }
]
// 出门人类型，outPersonType
// 出门人员类型选项
export enum outPersonTypeFlag {
    innter = '1',
    outer = '2',
}

export const outPersonTypeMap = {
    [outPersonTypeFlag.innter]: '公司内部人员',
    [outPersonTypeFlag.outer]: '外部人员',
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

export enum isAllReceivedFlag {
    allReceived = '1',
    partReceived = '2',
    notReceived = '0'
}
export const isAllReceivedMap ={
    [isAllReceivedFlag.allReceived]: '全部接收',
    [isAllReceivedFlag.partReceived]: '部分接收',
    [isAllReceivedFlag.notReceived]: '未接收'
}