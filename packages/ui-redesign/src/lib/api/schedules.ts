/**
 * 调度 API 调用
 */
import { request } from './client';
import { appConfig } from '@/lib/config';
import type { ListSchedulesResponse } from './types';

const NAMESPACE = appConfig.namespace;

/**
 * 获取调度列表
 */
export async function listSchedules(): Promise<ListSchedulesResponse> {
  return request<ListSchedulesResponse>(
    `/namespaces/${NAMESPACE}/schedules`,
  );
}

/**
 * 获取调度计数
 */
export async function countSchedules(): Promise<{ count: string }> {
  return request<{ count: string }>(
    `/namespaces/${NAMESPACE}/schedule-count`,
  );
}

/**
 * 暂停/恢复/删除调度
 */
export async function patchSchedule(
  scheduleId: string,
  action: 'pause' | 'unpause' | 'delete',
): Promise<null> {
  const body = JSON.stringify({
    [action]: true,
  });
  return request<null>(
    `/namespaces/${NAMESPACE}/schedules/${encodeURIComponent(scheduleId)}/patch`,
    { method: 'POST', body },
  );
}

/**
 * 创建调度请求参数
 */
export interface CreateScheduleParams {
  scheduleId: string;
  workflowType: string;
  taskQueue: string;
  cronExpression: string;
  input?: string;
  notes?: string;
}

/**
 * 创建调度
 * POST /namespaces/{ns}/schedules/{scheduleId}
 */
export async function createSchedule(params: CreateScheduleParams): Promise<null> {
  let payloads = null;
  if (params.input) {
    try {
      const parsed = JSON.parse(params.input);
      payloads = {
        payloads: [
          {
            metadata: { encoding: 'json/plain' },
            data: btoa(unescape(encodeURIComponent(JSON.stringify(parsed)))),
          },
        ],
      };
    } catch {
      // 非 JSON 输入跳过
    }
  }

  const body = JSON.stringify({
    scheduleId: params.scheduleId,
    spec: {
      cronString: params.cronExpression,
    },
    action: {
      startWorkflow: {
        workflowType: { name: params.workflowType },
        taskQueue: { name: params.taskQueue },
        ...(payloads ? { input: payloads } : {}),
      },
    },
    ...(params.notes ? { notes: params.notes } : {}),
  });

  return request<null>(
    `/namespaces/${NAMESPACE}/schedules/${encodeURIComponent(params.scheduleId)}`,
    { method: 'POST', body },
  );
}

/**
 * 前端调度格式
 */
export interface FrontendSchedule {
  id: string;
  name: string;
  status: 'active' | 'paused';
  workflowType: string;
  taskQueue: string;
  cronExpression: string;
  lastRunTime: string;
  nextRunTime: string;
  runCount: number;
  failureCount: number;
}

/**
 * 将 API 调度转换为前端格式
 * 注意：API 字段可能缺失，所有访问都需防御性处理
 */
export function mapToSchedule(
  schedule: any,
): FrontendSchedule {
  const info = schedule?.info ?? {};
  const spec = info?.spec ?? {};
  const workflowType = info?.workflowType?.name ?? info?.workflowType ?? 'Unknown';
  const taskQueue = info?.taskQueue?.name ?? info?.taskQueue ?? '';
  const cronExpression = spec?.cronString ?? spec?.cron ?? '';
  const scheduleId = schedule?.scheduleId ?? info?.scheduleId ?? `schedule-${Math.random().toString(36).slice(2, 8)}`;
  return {
    id: scheduleId,
    name: scheduleId,
    status: schedule?.paused ? 'paused' : 'active',
    workflowType,
    taskQueue,
    cronExpression,
    lastRunTime: info?.lastRunTime ?? '',
    nextRunTime: spec?.startTime ?? info?.nextRunTime ?? '',
    runCount: Number(info?.runCount ?? 0),
    failureCount: Number(info?.failureCount ?? 0),
  };
}

/**
 * 获取调度列表（转换为前端格式）
 * 防御性处理：当 response.schedules 为 undefined 时返回空数组
 */
export async function fetchSchedules(): Promise<FrontendSchedule[]> {
  const response = await listSchedules();
  const schedules = response?.schedules ?? [];
  // 确保始终是数组
  if (!Array.isArray(schedules)) {
    console.warn('[Schedules] API 返回的 schedules 不是数组:', response);
    return [];
  }
  return schedules.map(mapToSchedule);
}
