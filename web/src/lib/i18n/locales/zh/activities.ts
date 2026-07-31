export const Namespace = 'activities' as const;

export const Strings = {
  'pause-modal-confirmation': '暂停活动 {{activityId}}',
  'pause-modal-description':
    '暂停会停止新的尝试，但活动的超时截止时间会继续计算。在长时间暂停前，请使用"更新活动选项"延长超时时间。',
  'pause-modal-docs-link': '重要注意事项',
  'unpause-modal-confirmation': '恢复活动 {{activityId}}',
  'unpause-modal-description': '恢复执行此活动。',
  'paused-since': '暂停时间',
  'paused-by': '暂停者',
  'pause-reason': '暂停原因',
  'reset-modal-confirmation': '重置活动 {{activityId}}',
  'reset-modal-description':
    '将此活动的执行重置为初始尝试。',
  'reset-heartbeat-details': '重置心跳详情（可选）',
  'reset-success': '活动 {{activityId}} 已成功重置。',
  'resume-tooltip': '恢复此活动',
  'pause-tooltip':
    '在此活动下次重试或心跳之前暂停它。暂停期间超时截止时间会继续计算。',
  'retry-max-attempts': '重试最大次数',
  'retry-max-attempts-description':
    '最大尝试次数。超过此值后，即使尚未过期也会停止重试。1 表示禁用重试。0 表示无限制（受超时限制）。',
  'retry-backoff-coefficient': '重试退避系数',
  'retry-backoff-coefficient-description':
    '用于计算下次重试间隔的系数。下次重试间隔为前一次间隔乘以该系数。必须大于或等于 1。',
  'retry-initial-interval-duration': '重试初始间隔时长',
  'retry-initial-interval-duration-description':
    '第一次重试的间隔。如果 retryBackoffcoefficient 为 1.0，则所有重试都使用此间隔。',
  'schedule-to-start-timeout-duration': '调度到开始超时时长',
  'schedule-to-start-timeout-duration-description':
    '限制活动任务在被 Worker 获取之前可以在任务队列中停留的时间。如果未指定，默认为"调度到关闭超时"。计时器在任何启动延迟之后开始。此超时始终不可重试。',
  'schedule-to-close-timeout-duration': '调度到关闭超时时长',
  'schedule-to-close-timeout-duration-description':
    '调用方愿意等待活动完成的时间。限制重试的持续时间。计时器在任何启动延迟之后开始。',
  'start-to-close-timeout-duration': '开始到关闭超时时长',
  'start-to-close-timeout-duration-description':
    '活动在被 Worker 获取后允许执行的最大时间。开始时间在任何启动延迟之后计算。此超时始终可重试。',
  'heartbeat-timeout-duration': '心跳超时时长',
  'heartbeat-timeout-duration-description':
    '成功的 Worker 心跳之间允许的最大时间。',
  'task-queue-name': '任务队列名称',
} as const;
