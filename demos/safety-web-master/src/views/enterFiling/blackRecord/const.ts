
// 状态: status
export enum StatusEnum {
    PendingReview = '20', 
    Rejected = '10', 
    Approved = '30', 
    Unaccepted = '40', 
    Abandoned='00'

}

export const StatusEnumMap = {
    [StatusEnum.PendingReview]: '待审核',
    [StatusEnum.Rejected]: '驳回',
    [StatusEnum.Approved]: '审核通过',
    [StatusEnum.Unaccepted]: '审核不通过',
    [StatusEnum.Abandoned]: '废弃',
}
export const StatusOptions = [
    {
        label: '待审核',
        value: StatusEnum.PendingReview,
    },
    {
        label: '驳回',
        value: StatusEnum.Rejected,
    },
    {
        label: '审核通过',
        value: StatusEnum.Approved,
    },
    {
        label: '审核不通过',
        value: StatusEnum.Unaccepted,
    },
    {
        label: '废弃',
        value: StatusEnum.Abandoned,
    }
]

export enum PeopleStatusEnum {
    PendingReview = '0', 
    Approved = '1', 
    Unaccepted = '2', 
}

export const PeopleStatusEnumMap = {
    [PeopleStatusEnum.PendingReview]: '待审核',
    [PeopleStatusEnum.Approved]: '审核通过',
    [PeopleStatusEnum.Unaccepted]: '审核不通过',
}

export const PeopleStatusOptions = [
    {
        label: '待审核',
        value: PeopleStatusEnum.PendingReview,
    },
    {
        label: '审核通过',
        value: PeopleStatusEnum.Approved,
    },
    {
        label: '审核不通过',
        value: PeopleStatusEnum.Unaccepted,
    },
]