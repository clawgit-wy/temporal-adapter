export const Namespace = 'standalone-nexus-operations' as const;

export const Strings = {
  'standalone-nexus-operations': '独立 Nexus 操作',
  'standalone-nexus-operations-disabled':
    '此命名空间已禁用独立 Nexus 操作。',
  'standalone-nexus-operations-enablement':
    '如果要在全局启用独立 Nexus 操作，请确保在动态配置中设置以下值。',
  'standalone-nexus-operations-enablement-per-namespace':
    '如果仅要为此命名空间启用独立 Nexus 操作，请包含以下内容',
  'nexus-operations-plural_one': '独立 Nexus 操作',
  'nexus-operations-plural_other': '独立 Nexus 操作',
  'recent-nexus-operations': '最近的独立 Nexus 操作',
  'nexus-operations-error-querying': '查询 Nexus 操作时出错',
  'nexus-operation-query-error-state': '筛选 Nexus 操作出错',
  'all-nexus-operations': '全部',
  'running-nexus-operations': '运行中',
  'completed-nexus-operations': '已完成',
  'failed-nexus-operations': '已失败',
  'canceled-nexus-operations': '已取消',
  'terminated-nexus-operations': '已终止',
  'timed-out-nexus-operations': '已超时',
  'empty-state-title': '未找到 Nexus 操作',
  'empty-state-description': '没有 Nexus 操作匹配当前筛选条件。',
  'nexus-operations-table': '独立 Nexus 操作表',
  'empty-state-no-data-title':
    '此命名空间尚无任何独立 Nexus 操作。',
  'empty-state-value-proposition':
    '独立 Nexus 操作可以通过简单的 SDK 客户端调用从现有代码调用任何 Temporal 托管的端点——无需设置工作流或运行 Worker。开箱即用地获得所有持久执行优势，如自动重试、超时和至少一次执行保证。',
  'empty-state-read-docs-prefix': '阅读',
  'empty-state-nexus-docs-link': 'Nexus 文档',
  'empty-state-read-docs-middle': '并查看',
  'empty-state-nexus-eval-guide-link': 'Nexus 评估指南',
  'empty-state-read-docs-suffix':
    '，了解 Nexus 如何融入您的操作。',
  'empty-state-code-samples-title':
    '探索独立 Nexus 操作的代码示例',
  'start-standalone-nexus-operation': '启动独立 Nexus 操作',
  'back-to-nexus-operations': '返回独立 Nexus 操作',
  'layout-tabs-label': 'Nexus 操作详情',
  'layout-details-tab': '详情',
  'layout-search-attributes-tab': '搜索属性',
  'layout-user-metadata-tab': '元数据',
  'operation-id': '操作 ID',
  'run-id': '运行 ID',
  'operation-token': '操作令牌',
  'request-id': '请求 ID',
  identity: '身份',
  endpoint: '端点',
  service: '服务',
  operation: '操作',
  state: '状态',
  'blocked-reason': '阻塞原因',
  'schedule-time': '调度时间',
  'expiration-time': '过期时间',
  'close-time': '结束',
  'execution-duration': '执行持续时间',
  'state-transitions': '状态转换',
  attempt: '尝试',
  'last-attempt-complete-time': '上次尝试完成时间',
  'next-attempt-schedule-time': '下次尝试计划时间',
  'last-attempt-failure': '上次尝试失败',
  'schedule-to-close-timeout': '调度到关闭超时',
  'schedule-to-start-timeout': '调度到开始超时',
  'start-to-close-timeout': '开始到关闭超时',
  'cancellation-info': '取消信息',
  'cancellation-requested-time': '请求时间',
  'cancellation-state': '取消状态',
  'cancellation-reason': '原因',
  'nexus-header': 'Nexus 标头',
  'operation-event-history': '操作事件历史',
  'run-details-section': '运行详情',
  'operation-details-section': '操作详情',
  'attempt-section': '尝试',
  'timeout-configuration': '超时配置',
  'endpoint-name': '端点名称',
  'service-name': '服务名称',
  'operation-name': '操作名称',
  'handler-namespace': '处理器命名空间',
  'handler-operation-link': '处理器操作链接',
  'handler-namespace-note':
    '注意：只有当您的账户具有处理器命名空间的访问权限时，链接才会生效。',
  'request-cancellation': '请求取消',
  'cancel-modal-title': '取消 Nexus 操作',
  'cancel-modal-confirmation':
    '确定要请求取消此 Nexus 操作吗？',
  'cancel-success': '取消请求已成功发送。',
  terminate: '终止',
  'terminate-modal-title': '终止 Nexus 操作',
  'terminate-modal-confirmation':
    '确定要终止此 Nexus 操作吗？',
  'terminate-success': 'Nexus 操作已成功终止。',
  'start-nexus-operation-like-this-one': '启动类似的 Nexus 操作',
  'form-page-description':
    '使用独立 Nexus 操作来调用不同命名空间中的 Nexus 操作。',
  'form-page-allowlist-note':
    '注意：要使此操作成功，此命名空间（{{namespace}}）必须在要调用的端点的允许列表中。',
  'form-nexus-operation-details-heading': 'Nexus 操作详情',
  'form-operation-id-label': '操作 ID',
  'form-operation-id-body':
    '此独立 Nexus 操作的唯一 ID。如果留空，我们将生成一个唯一 ID。',
  'form-operation-id-required': '操作 ID 为必填项。',
  'form-endpoint-label': '目标端点名称',
  'form-endpoint-body': '目标（处理器）命名空间中的端点名称。',
  'form-endpoint-required': '目标端点名称为必填项。',
  'form-service-label': '服务名称',
  'form-service-body':
    '处理器命名空间中服务的名称。不精确匹配将导致操作失败。',
  'form-service-required':
    '处理器命名空间中的 Nexus 服务名称为必填项。',
  'form-operation-name-label': '操作名称',
  'form-operation-name-body':
    '处理器命名空间中操作的名称。不精确匹配将导致操作失败。',
  'form-operation-name-required':
    '处理器命名空间服务中的操作名称为必填项。',
  'form-timeouts-heading': '超时',
  'form-schedule-to-close-timeout-label': '调度到关闭超时',
  'form-schedule-to-start-timeout-label': '调度到开始超时',
  'form-start-to-close-timeout-label': '开始到关闭超时',
  'form-random-uuid': '随机 UUID',
  'form-operation-policies-heading': '操作策略',
  'form-operation-policies-description':
    '操作策略允许您自定义此 Nexus 操作的行为。了解更多关于',
  'form-operation-policies-link': 'Nexus 操作策略',
  'form-edit-operation-policies': '编辑操作策略',
  'form-closed-operation-id-reuse-label': '已关闭操作 ID 重用',
  'form-running-operation-id-conflict-label':
    '运行中操作 ID 冲突处理',
  'form-timeouts-summary-label': '超时',
  'form-timeouts-default': '默认超时（无时间限制）',
  'form-id-reuse-policy-default': '允许',
  'form-id-conflict-policy-default': '失败且不启动',
  'form-id-policies-heading': 'ID 策略',
  'form-id-reuse-policy-label': 'ID 重用策略',
  'form-id-conflict-policy-label': 'ID 冲突策略',
  'form-nexus-header-heading': 'Nexus 标头',
  'form-nexus-header-key-placeholder': '键',
  'form-nexus-header-value-placeholder': '值',
  'form-add-nexus-header': '添加标头',
  'form-search-attributes-heading': '搜索属性',
  'form-search-attributes-description':
    '用于在表格和列表中筛选 Nexus 操作的自定义字段。',
  'form-user-metadata-heading': '用户元数据',
  'form-user-metadata-description':
    '为 Nexus 操作添加上下文，以帮助识别和理解其操作。',
  'operation-summary-heading': '操作摘要',
  'operation-summary-start-date': '开始日期',
  'operation-summary-target-endpoint': '目标端点和命名空间',
  'operation-summary-service-name': '服务名称',
  'operation-summary-operation-name': '操作名称',
  'form-nexus-operation-started': 'Nexus 操作已启动。',
  'form-operation-id-duplicate-toast':
    '无法创建独立 Nexus 操作，因为操作 ID 已被现有操作使用。',
  'form-operation-id-duplicate-error':
    '操作 ID 已被使用。将操作 ID 重用策略更改为"不允许重复操作 ID"以外的任何选项以解决此问题。',
  'form-operation-id-duplicate-completed-toast':
    '无法创建独立 Nexus 操作，因为操作 ID 已被已完成的操作使用。',
  'form-operation-id-duplicate-completed-error':
    '操作 ID 已被已完成的操作使用。将操作 ID 重用策略更改为允许重复，或选择不同的操作 ID。',
  'form-operation-id-conflict-toast':
    '无法创建独立 Nexus 操作，因为操作 ID 已被现有操作使用。',
  'form-operation-id-conflict-hint': '操作 ID 已被使用。',
  'form-operation-id-conflict-banner-prefix':
    '此操作 ID 已被',
  'form-operation-id-conflict-banner-suffix':
    '"使用。由于此操作在创建独立 Nexus 操作时仍在运行，因此与您的 ID 冲突策略冲突，无法创建。如果运行中的操作可能即将关闭，您可以重试。否则，请使用不同的操作 ID，取消或终止当前运行的操作，或者将此字段留空，我们将为您生成一个唯一的操作 ID。',
  'form-id-reuse-policy-heading': '已关闭操作 ID 重用策略',
  'form-id-reuse-policy-description':
    '确定是否允许此独立 Nexus 操作与处于已完成、已取消、已终止、已超时或已失败状态的操作共享相同的操作 ID。',
  'form-id-conflict-policy-heading': '运行中操作 ID 冲突策略',
  'form-id-conflict-policy-description':
    '确定当操作 ID 已被运行中的 Nexus 操作使用时的处理方式。',
  'form-timeouts-description':
    '定义操作在视为失败之前应等待响应的时间。',
  'form-id-reuse-policy-allow-duplicate-label':
    '允许重复操作 ID（默认）',
  'form-id-reuse-policy-allow-duplicate-description':
    '允许多个操作使用相同的操作 ID 运行。',
  'form-id-reuse-policy-allow-duplicate-failed-only-label':
    '仅当重复项属于已取消、已终止、已超时或已失败的操作时才允许重复',
  'form-id-reuse-policy-allow-duplicate-failed-only-description':
    '如果操作 ID 属于已完成的操作，则不允许重复。',
  'form-id-reuse-policy-reject-duplicate-label':
    '不允许重复操作 ID',
  'form-id-reuse-policy-reject-duplicate-description':
    '如果 ID 与任何现有操作的 ID 重复，则不允许启动该操作。',
  'form-id-conflict-policy-fail-label':
    '在启动前使新操作失败（默认）',
  'form-id-conflict-policy-fail-description':
    '创建操作但不启动它。',
  'form-id-conflict-policy-use-existing-label':
    '不启动；返回已使用该 ID 的操作的句柄',
  'form-id-conflict-policy-use-existing-description':
    '不启动新操作，但在新操作的详情中返回运行中操作的链接，以便您进行故障排除。',
  'form-schedule-to-close-timeout-hint':
    '如果未设置或设置为零，则不强制超时。最大超时为 60 天。',
  'form-schedule-to-start-timeout-hint':
    '如果未设置或设置为零，则不强制超时。',
  'form-start-to-close-timeout-hint':
    '如果未设置或设置为零，则不强制超时。',
  'form-update-policies-button': '更新策略',
} as const;
