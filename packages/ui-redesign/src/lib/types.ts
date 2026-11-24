/**
 * 共享类型定义
 * 从 mock/data.ts 迁移，供 API 层和页面组件使用
 */

// 工作流状态（小写形式，前端统一使用）
export type WorkflowStatus =
  | 'running'
  | 'completed'
  | 'failed'
  | 'timed_out'
  | 'terminated'
  | 'canceled';

// Worker 状态
export type WorkerStatus = 'running' | 'unhealthy' | 'drained' | 'draining';

// 第三方调用信息
export interface ThirdPartyInfo {
  endpoint: string;
  method: string;
  statusCode: number;
  responseTime: number;
  requestBody: Record<string, any>;
  responseBody: Record<string, any>;
}

// Activity（活动）类型
export interface Activity {
  id: string;
  activityType: string;
  status: WorkflowStatus;
  startTime: string;
  endTime: string;
  duration: number;
  attempt: number;
  input: Record<string, any>;
  output: Record<string, any>;
  isThirdParty: boolean;
  thirdPartyInfo?: ThirdPartyInfo;
}

// 工作流统计
export interface WorkflowStats {
  total: number;
  running: number;
  completed: number;
  failed: number;
  timed_out: number;
  terminated: number;
  canceled: number;
  successRate: number;
  totalActivities: number;
  thirdPartyCalls: number;
  avgDuration: number;
}

// 趋势数据点
export interface TrendPoint {
  hour: string;
  successRate: number;
  total: number;
  success: number;
  failed: number;
}
