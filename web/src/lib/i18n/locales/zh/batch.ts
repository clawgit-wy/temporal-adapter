export const Namespace = 'batch' as const;

export const Strings = {
  'nav-title': '批量',
  'list-page-title': '批量操作',
  'describe-page-title': '批量操作',
  'empty-state-title': '暂无批量操作',
  'back-link': '返回批量操作',
  'operation-type': '操作类型',
  details: '操作详情',
  identity: '身份',
  'total-operations': '操作总数',
  'operations-failed': '{{ count, number }} 个失败',
  'operations-succeeded': '{{ count, number }} 个成功',
  'operations-progress': '已完成 {{ percent }}%',
  results: '操作结果',
  'max-concurrent-alert-title': '已达到最大并发批量操作数',
  'max-concurrent-alert-description':
    '只允许 1 个进行中的批量操作。如果您在当前有一个批量操作正在运行时尝试创建新的批量操作，将会失败。',
  'job-id-input-hint':
    'Job ID 必须唯一。如果留空，将使用随机生成的 UUID。',
  'job-id-input-error': 'Job ID 只能包含 URL 安全字符',
} as const;
