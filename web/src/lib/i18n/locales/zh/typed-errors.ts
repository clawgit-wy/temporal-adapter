export const Namespace = 'typed-errors';

export const Strings = {
  'link-preface': '了解更多关于 ',
  Unspecified: {
    title: '未指定',
    description: '工作流任务失败。详情请查看错误。',
  },
  UnhandledCommand: {
    title: '未处理的命令',
    description:
      '工作流任务失败，因为自上一个工作流任务开始以来有新的可用事件。已调度重试工作流任务，工作流将有机会处理这些新事件。',
  },
  BadScheduleActivityAttributes: {
    title: '错误的调度活动属性',
    description:
      '工作流任务失败，因为缺少或不正确的 ScheduleActivity 属性。',
  },
  BadRequestCancelActivityAttributes: {
    title: '错误的请求取消活动属性',
    description:
      '工作流任务失败，因为错误的 RequestCancelActivity 属性。一个活动被调度为取消，但调度事件 ID 从未被设置。',
  },
  BadStartTimerAttributes: {
    title: '错误的启动计时器属性',
    description:
      '工作流任务失败，因为调度事件缺少计时器 ID。',
  },
  BadCancelTimerAttributes: {
    title: '错误的取消计时器属性',
    description:
      '工作流任务在尝试取消计时器时失败，因为计时器 ID 未设置。',
  },
  BadRecordMarkerAttributes: {
    title: '错误的记录标记属性',
    description:
      '工作流任务失败，因为缺少或无效的标记名称。',
  },
  BadCompleteWorkflowExecutionAttributes: {
    title: '错误的完成工作流执行属性',
    description:
      '工作流任务失败，因为 CompleteWorkflowExecution 上有未设置的属性。',
  },
  BadFailWorkflowExecutionAttributes: {
    title: '错误的失败工作流执行属性',
    description:
      '工作流任务失败，因为未设置的 FailWorkflowExecution 属性或失败。',
  },
  BadCancelWorkflowExecutionAttributes: {
    title: '错误的取消工作流执行属性',
    description:
      '工作流任务失败，因为 CancelWorkflowExecution 上有未设置的属性。',
  },
  BadRequestCancelExternalAttributes: {
    title: '错误的请求取消外部属性',
    description:
      '工作流任务失败，因为取消外部工作流的请求上有无效属性。详情请查看失败消息。',
  },
  BadContinueAsNewAttributes: {
    title: '错误的继续为新属性',
    description:
      '工作流任务失败，因为无法验证 ContinueAsNew 属性。详情请查看失败消息。',
  },
  StartTimerDuplicateId: {
    title: '启动计时器重复 ID',
    description:
      '工作流任务失败，因为具有给定计时器 ID 的计时器已经启动。',
  },
  ResetStickyTaskQueue: {
    title: '重置粘性任务队列',
    description:
      '工作流任务失败，因为需要重置粘性任务队列。系统将自动重试。',
  },
  WorkflowWorkerUnhandledFailure: {
    title: '工作流 Worker 未处理的失败',
    description:
      '工作流任务失败，因为工作流代码有未处理的失败。',
  },
  WorkflowTaskHeartbeatError: {
    title: '工作流任务心跳错误',
    description:
      '工作流任务在执行长时间运行的本地活动时无法发送心跳。这些本地活动将在下一次工作流任务尝试时重新执行。如果此错误持续存在，这些本地活动将反复运行，直到工作流超时。',
  },
  BadSignalWorkflowExecutionAttributes: {
    title: '错误的信号工作流执行属性',
    description:
      '工作流任务无法验证 SignalWorkflowExecution 的属性。详情请查看失败消息。',
  },
  BadStartChildExecutionAttributes: {
    title: '错误的启动子执行属性',
    description:
      '工作流任务无法验证 StartChildWorkflowExecution 所需的属性。详情请查看失败消息。',
  },
  ForceCloseCommand: {
    title: '强制关闭命令',
    description:
      '工作流任务被强制关闭。如果错误可恢复，将调度重试。',
  },
  FailoverCloseCommand: {
    title: '故障转移关闭命令',
    description:
      '工作流任务因命名空间故障转移而被强制关闭。将自动调度重试。',
  },
  BadSignalInputSize: {
    title: '错误的信号输入大小',
    description:
      '负载已超过信号上的可用输入大小。',
  },
  BadBinary: {
    title: '错误的二进制',
    description:
      '系统使此工作流任务失败，因为此 Worker 的部署被标记为错误的二进制。',
  },
  ScheduleActivityDuplicateId: {
    title: '调度活动重复 ID',
    description:
      '工作流任务失败，因为活动 ID 已被使用，请检查您是否在工作流中指定了相同的活动 ID。',
  },
  BadSearchAttributes: {
    title: '错误的搜索属性',
    description:
      '搜索属性缺失或值超过限制。这可能导致工作流任务持续重试而不成功。',
    action: '配置搜索属性',
    link: 'https://docs.temporal.io/visibility#search-attribute',
  },
  NonDeterministicError: {
    title: '非确定性错误',
    description:
      '非确定性错误导致工作流任务失败。这通常意味着工作流代码有非向后兼容的更改，而没有适当的版本控制分支。',
    action: '确定性约束',
    link: 'https://docs.temporal.io/workflows/#deterministic-constraints',
  },
  BadModifyWorkflowPropertiesAttributes: {
    title: '错误的修改工作流属性属性',
    description:
      '工作流任务无法验证更新插入备忘录上 ModifyWorkflowProperty 的属性。详情请查看失败消息。',
  },
  PendingChildWorkflowsLimitExceeded: {
    title: '已超过待处理子工作流限制',
    description:
      '已达到待处理子工作流的容量。工作流任务被失败以防止添加更多子工作流。',
  },
  PendingActivitiesLimitExceeded: {
    title: '已超过待处理活动限制',
    description:
      '已达到待处理活动的容量。工作流任务被失败以防止创建另一个活动。',
  },
  PendingSignalsLimitExceeded: {
    title: '已超过待处理信号限制',
    description:
      '已达到从此工作流发送待处理信号的容量。',
  },
  PendingRequestCancelLimitExceeded: {
    title: '已超过待处理请求取消限制',
    description:
      '已达到取消其他工作流的待处理请求的容量。',
  },
  BadUpdateWorkflowExecutionMessage: {
    title: '错误的更新',
    description:
      '工作流执行在收到更新之前尝试完成。',
  },
  UnhandledUpdate: {
    title: '未处理的更新',
    description:
      '当工作流任务正在 Worker 上处理时，Temporal 服务器收到了工作流更新。',
  },
  BadScheduleNexusOperationAttributes: {
    title: '错误的调度 Nexus 操作属性',
    description:
      '工作流任务完成时带有无效的 ScheduleNexusOperation 命令。',
  },
  PendingNexusOperationsLimitExceeded: {
    title: '已超过待处理 Nexus 操作限制',
    description:
      '工作流任务完成时请求调度超过服务器配置限制的 Nexus 操作。',
  },
  BadRequestCancelNexusOperationAttributes: {
    title: '错误的请求取消 Nexus 操作属性',
    description:
      '工作流任务完成时带有无效的 RequestCancelNexusOperation 命令。',
  },
  FeatureDisabled: {
    title: '功能已禁用',
    description:
      '工作流任务完成时请求了服务器上禁用的功能（系统范围或 - 通常 - 工作流的命名空间）。详情请查看工作流任务失败消息。',
  },
  GrpcMessageTooLarge: {
    title: 'gRPC 消息过大',
    description:
      '工作流任务失败，因为 gRPC 消息超过了允许的最大大小。',
  },
  WorkflowTaskTimedOut: {
    title: '工作流任务超时',
    description: '工作流任务遇到超时。',
  },
} as const;
