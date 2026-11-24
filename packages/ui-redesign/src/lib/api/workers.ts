/**
 * Worker / Task Queue API 调用
 */
import { request } from './client';
import { appConfig } from '@/lib/config';
import type { TaskQueueResponse } from './types';

const NAMESPACE = appConfig.namespace;

/**
 * 获取任务队列详情（包含 poller 信息）
 */
export async function getTaskQueue(
  taskQueue: string,
  taskQueueType: string = '1',
): Promise<TaskQueueResponse> {
  return request<TaskQueueResponse>(
    `/namespaces/${NAMESPACE}/task-queues/${encodeURIComponent(taskQueue)}`,
    {
      params: { taskQueueType },
    },
  );
}

/**
 * 获取任务队列兼容性信息
 */
export async function getTaskQueueCompatibility(
  taskQueue: string,
): Promise<any> {
  return request<any>(
    `/namespaces/${NAMESPACE}/task-queues/${encodeURIComponent(taskQueue)}/worker-build-id-compatibility`,
  );
}

/**
 * 前端 Worker 格式
 */
export interface FrontendWorker {
  id: string;
  name: string;
  identity: string;
  status: 'running' | 'unhealthy' | 'drained' | 'draining';
  taskQueue: string;
  buildId: string;
  version: string;
  startTime: string;
  lastHeartbeat: string;
  pollerCount: number;
  slotsUsed: number;
  slotsMax: number;
  resourceInfo: {
    cpuUsage: number;
    memoryUsage: number;
  };
}

/**
 * 将 poller 信息转换为前端 Worker 格式
 */
function mapPollerToWorker(
  poller: TaskQueueResponse['pollers'][number],
  taskQueue: string,
  index: number,
): FrontendWorker {
  const lastAccess = poller.lastAccessTime || '';
  const lastAccessTime = new Date(lastAccess).getTime();
  const now = Date.now();
  const heartbeatAge = now - lastAccessTime;

  // 超过 30 秒未心跳视为异常
  const isHealthy = heartbeatAge < 30000;

  return {
    id: `worker-${index}`,
    name: poller.identity || `worker-${index}`,
    identity: poller.identity || '',
    status: isHealthy ? 'running' : 'unhealthy',
    taskQueue,
    buildId: poller.workerVersioningId || 'unknown',
    version: poller.workerVersioningId || 'unknown',
    startTime: lastAccess,
    lastHeartbeat: lastAccess,
    pollerCount: 1,
    slotsUsed: 0,
    slotsMax: 0,
    resourceInfo: {
      cpuUsage: 0,
      memoryUsage: 0,
    },
  };
}

/**
 * 获取任务队列的 Worker 列表
 * 如果 TaskQueue API 返回空（无活跃 poller），则返回一个占位 Worker
 */
export async function fetchWorkers(
  taskQueue: string,
): Promise<FrontendWorker[]> {
  const response = await getTaskQueue(taskQueue);
  const pollers = response.pollers ?? [];

  if (pollers.length === 0) {
    // TaskQueue API 无 poller 数据时，返回占位信息
    return [{
      id: `worker-placeholder-${taskQueue}`,
      name: `${taskQueue} Worker`,
      identity: '',
      status: 'running',
      taskQueue,
      buildId: 'unknown',
      version: 'unknown',
      startTime: '',
      lastHeartbeat: '',
      pollerCount: 0,
      slotsUsed: 0,
      slotsMax: 0,
      resourceInfo: { cpuUsage: 0, memoryUsage: 0 },
    }];
  }

  return pollers.map((poller, index) =>
    mapPollerToWorker(poller, taskQueue, index),
  );
}

/**
 * 批量获取多个任务队列的 Worker
 */
export async function fetchAllWorkers(
  taskQueues: string[],
): Promise<FrontendWorker[]> {
  const results = await Promise.allSettled(
    taskQueues.map((queue) => fetchWorkers(queue)),
  );

  const workers: FrontendWorker[] = [];
  let globalIndex = 0;
  for (const result of results) {
    if (result.status === 'fulfilled') {
      for (const worker of result.value) {
        worker.id = `worker-${globalIndex++}`;
        workers.push(worker);
      }
    }
  }
  return workers;
}
