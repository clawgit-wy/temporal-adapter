/**
 * Temporal UI Server API 响应类型定义
 * 对应 /api/v1/* REST API 端点
 */

// 工作流状态枚举
export type WorkflowStatus =
  | 'RUNNING'
  | 'COMPLETED'
  | 'FAILED'
  | 'TIMED_OUT'
  | 'TERMINATED'
  | 'CANCELED'
  | 'CONTINUED_AS_NEW';

// 前端使用的小写状态
export type WorkflowStatusLower =
  | 'running'
  | 'completed'
  | 'failed'
  | 'timed_out'
  | 'terminated'
  | 'canceled';

// Temporal API 工作流执行响应
export interface WorkflowExecutionAPIResponse {
  workflowExecutionInfo?: {
    execution: {
      workflowId: string;
      runId: string;
    };
    type: { name: string };
    taskQueue: string;
    startTime: string;
    closeTime?: string;
    status: string;
    historyLength: string;
    searchAttributes?: {
      indexedFields: Record<string, any>;
    };
    memo?: {
      fields: Record<string, any>;
    };
  };
  // 兼容旧字段名（部分 API 版本可能使用 executionInfo）
  executionInfo?: {
    execution: {
      workflowId: string;
      runId: string;
    };
    type: { name: string };
    taskQueue: string;
    startTime: string;
    closeTime?: string;
    status: string;
    historyLength: string;
    searchAttributes?: {
      indexedFields: Record<string, any>;
    };
    memo?: {
      fields: Record<string, any>;
    };
  };
}

// Temporal API 工作流列表响应
// 注意：proto3 JSON 序列化会省略空数组，所以 executions 可能为 undefined
export interface ListWorkflowExecutionsResponse {
  executions?: Array<{
    execution: {
      workflowId: string;
      runId: string;
    };
    type: { name: string };
    startTime: string;
    closeTime?: string;
    status: string;
    taskQueue: string;
    searchAttributes?: {
      indexedFields: Record<string, any>;
    };
  }>;
  nextPageToken?: string;
}

// 工作流计数响应
// 注意：proto3 JSON 序列化会省略空值，所以 count 可能为 undefined
export interface CountWorkflowExecutionsResponse {
  count?: string;
  groups?: Array<{
    count: string;
    values: string[];
  }>;
}

// 集群信息响应
export interface GetClusterInfoResponse {
  clusterName?: string;
  clusterId?: string;
  historyShardCount?: string;
  persistenceStore?: string;
  visibilityStore?: string;
}

// 系统信息响应
export interface GetSystemInfoResponse {
  serverVersion?: string;
  capabilities?: {
    signalAndQueryHeader: boolean;
    internalErrorDifferentiation: boolean;
    activityFailureIncludeDetails: boolean;
    supportsSchedules: boolean;
    nestedWorkflowIds: boolean;
    syncWorkflowState: boolean;
    eagerWorkflowStart: boolean;
    telemetry: boolean;
  };
}

// 设置响应
export interface Settings {
  auth: {
    enabled: boolean;
    options: string[];
  };
  basenames: string[];
  clusterName?: string;
  disableWriteActions: boolean;
  defaultNamespace: string;
  namespaces: string[];
  runtimeEnvironment: {
    isCloud: boolean;
    isLocal: boolean;
    isDevelopment: boolean;
  };
  showTemporalSystemNamespace: boolean;
  version: string;
  webUrl: string;
  codec: {
    endpoint: string;
    passAccessToken: boolean;
    includeCredentials: boolean;
  };
  notifyOnWorkflowError: boolean;
}

// 任务队列响应
// 注意：proto3 JSON 序列化会省略空数组，所以 pollers 可能为 undefined
export interface TaskQueueResponse {
  pollers?: Array<{
    identity: string;
    lastAccessTime: string;
    ratePerSecond: number;
    workerVersioningId?: string;
  }>;
  taskQueueInfo?: {
    name: string;
    kind: string;
    type: string;
    ackLevel: string;
  };
}

// 命名空间响应
export interface DescribeNamespaceResponse {
  namespaceInfo: {
    name: string;
    state: string;
    description: string;
  };
  config: {
    workflowExecutionRetentionTtl: string;
    historyArchivalState: string;
    visibilityArchivalState: string;
  };
  replicationInfo?: Record<string, any>;
}

// 命名空间列表响应
export interface ListNamespacesResponse {
  namespaces: DescribeNamespaceResponse[];
}

// 调度列表响应
export interface ListSchedulesResponse {
  schedules: Array<{
    info: {
      workflowId: string;
      workflowType: { name: string };
      taskQueue: { name: string };
      scheduleId: string;
      spec: {
        cronString: string;
      };
    };
    scheduleId: string;
    paused: boolean;
    notes: string;
  }>;
}

// 事件历史响应
export interface WorkflowHistoryResponse {
  history: {
    events: Array<{
      eventId: string;
      eventTime: string;
      eventType: string;
      taskId: string;
      workerMayProgress: boolean;
    }>;
  };
  nextPageToken?: string;
}

// 工作流状态映射：API → 前端小写
// 支持 WORKFLOW_EXECUTION_STATUS_RUNNING 和 RUNNING 两种格式
export function mapWorkflowStatus(apiStatus: string): WorkflowStatusLower {
  const normalized = apiStatus.replace('WORKFLOW_EXECUTION_STATUS_', '');
  const map: Record<string, WorkflowStatusLower> = {
    RUNNING: 'running',
    COMPLETED: 'completed',
    FAILED: 'failed',
    TIMED_OUT: 'timed_out',
    TERMINATED: 'terminated',
    CANCELED: 'canceled',
    CONTINUED_AS_NEW: 'completed',
  };
  return map[normalized] ?? 'running';
}
