/**
 * 工作流 API 调用
 */
import { request } from './client';
import { appConfig } from '@/lib/config';
import type {
  ListWorkflowExecutionsResponse,
  CountWorkflowExecutionsResponse,
  WorkflowExecutionAPIResponse,
  WorkflowStatusLower,
} from './types';
import { mapWorkflowStatus } from './types';
import type { Activity } from '@/lib/types';

const NAMESPACE = appConfig.namespace;

/**
 * 获取工作流列表
 */
export async function listWorkflows(
  query?: string,
  pageSize = 100,
): Promise<ListWorkflowExecutionsResponse> {
  return request<ListWorkflowExecutionsResponse>(
    `/namespaces/${NAMESPACE}/workflows`,
    {
      params: {
        query: query ?? '',
        pageSize: String(pageSize),
      },
    },
  );
}

/**
 * 获取工作流详情
 */
export async function getWorkflow(
  workflowId: string,
  runId?: string,
): Promise<WorkflowExecutionAPIResponse> {
  const params: Record<string, string | undefined> = {};
  if (runId) {
    params['execution.runId'] = runId;
  }
  return request<WorkflowExecutionAPIResponse>(
    `/namespaces/${NAMESPACE}/workflows/${encodeURIComponent(workflowId)}`,
    { params },
  );
}

/**
 * 启动工作流
 */
export async function startWorkflow(params: {
  workflowId: string;
  workflowType: string;
  taskQueue: string;
  input?: string;
  identity?: string;
}): Promise<{ runId: string }> {
  let payloads = null;
  if (params.input) {
    try {
      const parsed = JSON.parse(params.input);
      payloads = { payloads: [{ metadata: { encoding: 'json/plain' }, data: btoa(JSON.stringify(parsed)) }] };
    } catch {
      // 输入不是 JSON，作为字符串处理
    }
  }

  const body = JSON.stringify({
    workflowId: params.workflowId,
    taskQueue: { name: params.taskQueue },
    workflowType: { name: params.workflowType },
    input: payloads,
    ...(params.identity && { identity: params.identity }),
  });

  return request<{ runId: string }>(
    `/namespaces/${NAMESPACE}/workflows/${encodeURIComponent(params.workflowId)}`,
    { method: 'POST', body },
  );
}

/**
 * 终止工作流
 */
export async function terminateWorkflow(
  workflowId: string,
  runId: string,
  reason: string,
): Promise<null> {
  const body = JSON.stringify({
    reason,
    identity: 'temporal-ui',
  });
  return request<null>(
    `/namespaces/${NAMESPACE}/workflows/${encodeURIComponent(workflowId)}/terminate`,
    {
      method: 'POST',
      body,
      params: { 'execution.runId': runId },
    },
  );
}

/**
 * 取消工作流
 */
export async function cancelWorkflow(
  workflowId: string,
  runId: string,
): Promise<null> {
  const body = JSON.stringify({
    namespace: NAMESPACE,
    workflowExecution: { workflowId, runId },
    requestId: crypto.randomUUID(),
    identity: 'temporal-ui',
  });
  return request<null>(
    `/namespaces/${NAMESPACE}/workflows/${encodeURIComponent(workflowId)}/cancel`,
    { method: 'POST', body },
  );
}

/**
 * 查询工作流（QueryMethod）
 */
export async function queryWorkflow(
  workflowId: string,
  runId: string,
  queryType: string,
): Promise<any> {
  const body = JSON.stringify({
    query: { queryType },
    execution: { workflowId, runId },
  });
  return request<any>(
    `/namespaces/${NAMESPACE}/workflows/${encodeURIComponent(workflowId)}/query/${encodeURIComponent(queryType)}`,
    { method: 'POST', body },
  );
}

/**
 * 将输入值转换为 Temporal Payload 格式
 */
function toPayload(input: string): any {
  if (!input) return null;
  try {
    const parsed = JSON.parse(input);
    return {
      metadata: { encoding: 'json/plain' },
      data: btoa(unescape(encodeURIComponent(JSON.stringify(parsed)))),
    };
  } catch {
    return {
      metadata: { encoding: 'json/plain' },
      data: btoa(unescape(encodeURIComponent(JSON.stringify(input)))),
    };
  }
}

/**
 * 发送信号（Signal）到工作流
 * POST /namespaces/{ns}/workflows/{wid}/signal/{signalName}
 */
export async function signalWorkflow(
  workflowId: string,
  runId: string,
  signalName: string,
  input?: string,
): Promise<null> {
  const body = JSON.stringify({
    signalName,
    identity: 'temporal-ui',
    requestId: crypto.randomUUID(),
    ...(input ? { input: { payloads: [toPayload(input)] } } : {}),
  });
  return request<null>(
    `/namespaces/${NAMESPACE}/workflows/${encodeURIComponent(workflowId)}/signal/${encodeURIComponent(signalName)}`,
    {
      method: 'POST',
      body,
      params: { 'execution.runId': runId },
    },
  );
}

/**
 * 发送更新（Update）到工作流
 * POST /namespaces/{ns}/workflows/{wid}/update/{updateType}
 *
 * 注意：Update API 需要 waitForStage，部分 Temporal 版本可能不支持
 */
export async function updateWorkflow(
  workflowId: string,
  runId: string,
  updateType: string,
  input?: string,
): Promise<any> {
  const body = JSON.stringify({
    updateType,
    identity: 'temporal-ui',
    requestId: crypto.randomUUID(),
    waitForStage: 'COMPLETED',
    ...(input ? { input: { payloads: [toPayload(input)] } } : {}),
  });
  return request<any>(
    `/namespaces/${NAMESPACE}/workflows/${encodeURIComponent(workflowId)}/update/${encodeURIComponent(updateType)}`,
    {
      method: 'POST',
      body,
      params: { 'execution.runId': runId },
    },
  );
}

/**
 * 获取工作流计数（按状态分组）
 */
export async function countWorkflows(
  query?: string,
): Promise<CountWorkflowExecutionsResponse> {
  return request<CountWorkflowExecutionsResponse>(
    `/namespaces/${NAMESPACE}/workflow-count`,
    {
      params: query ? { query } : undefined,
    },
  );
}

/**
 * 获取工作流历史事件
 */
export async function getWorkflowHistory(
  workflowId: string,
  runId?: string,
): Promise<any> {
  const params: Record<string, string | undefined> = {};
  if (runId) {
    params['execution.runId'] = runId;
  }
  return request<any>(
    `/namespaces/${NAMESPACE}/workflows/${encodeURIComponent(workflowId)}/history`,
    { params },
  );
}

/**
 * 将 API 响应转换为前端工作流格式
 */
export interface FrontendWorkflow {
  id: string;
  workflowId: string;
  runId: string;
  name: string;
  status: WorkflowStatusLower;
  workflowType: string;
  taskQueue: string;
  startTime: string;
  closeTime: string;
  duration: number;
  input: Record<string, any>;
  result: Record<string, any>;
  activities: Activity[];
  searchAttributes: Record<string, any>;
}

export function mapToWorkflow(
  execution: ListWorkflowExecutionsResponse['executions'][number],
): FrontendWorkflow {
  const startTime = execution.startTime || '';
  const closeTime = execution.closeTime || '';
  let duration = 0;
  if (startTime) {
    const end = closeTime ? new Date(closeTime).getTime() : Date.now();
    duration = end - new Date(startTime).getTime();
  }

  return {
    id: execution.execution.workflowId,
    workflowId: execution.execution.workflowId,
    runId: execution.execution.runId,
    name: execution.type?.name ?? 'Unknown',
    status: mapWorkflowStatus(execution.status),
    workflowType: execution.type?.name ?? 'Unknown',
    taskQueue: execution.taskQueue ?? '',
    startTime,
    closeTime,
    duration,
    input: {},
    result: {},
    activities: [],
    searchAttributes: execution.searchAttributes?.indexedFields ?? {},
  };
}

/**
 * 获取工作流列表（转换为前端格式）
 */
export async function fetchWorkflows(
  query?: string,
): Promise<FrontendWorkflow[]> {
  const response = await listWorkflows(query);
  return (response.executions ?? []).map(mapToWorkflow);
}
