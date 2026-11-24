import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { Loader, CheckCircle2, XCircle, Clock, RefreshCw, AlertCircle } from 'lucide-react'
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts'
import StatCard from '@/components/StatCard'
import StatusBadge from '@/components/StatusBadge'
import { fetchWorkflows, type FrontendWorkflow } from '@/lib/api'
import type { WorkflowStats, TrendPoint } from '@/lib/types'
import { useApi } from '@/hooks/useApi'
import { formatTime, formatDuration } from '@/lib/utils'

function computeStatsFromWorkflows(workflows: FrontendWorkflow[]): WorkflowStats {
  const counts: Record<string, number> = {}
  const total = workflows.length
  for (const wf of workflows) {
    counts[wf.status] = (counts[wf.status] || 0) + 1
  }
  const closed = total - (counts.running || 0)
  const successRate =
    closed > 0 ? Math.round(((counts.completed || 0) / closed) * 1000) / 10 : 0
  return {
    total,
    running: counts.running || 0,
    completed: counts.completed || 0,
    failed: counts.failed || 0,
    timed_out: counts.timed_out || 0,
    terminated: counts.terminated || 0,
    canceled: counts.canceled || 0,
    successRate,
    totalActivities: 0,
    thirdPartyCalls: 0,
    avgDuration: 0,
  }
}

/**
 * 根据工作流列表计算最近24小时的趋势数据
 */
function computeTrendData(workflows: FrontendWorkflow[]): TrendPoint[] {
  const now = Date.now()
  const buckets: Record<string, { total: number; success: number; failed: number }> = {}
  for (let i = 23; i >= 0; i--) {
    const hour = new Date(now - i * 3600_000)
    const key = hour.getHours().toString().padStart(2, '0') + ':00'
    buckets[key] = { total: 0, success: 0, failed: 0 }
  }

  for (const wf of workflows) {
    if (!wf.startTime) continue
    const startMs = new Date(wf.startTime).getTime()
    if (isNaN(startMs)) continue
    const hoursAgo = Math.floor((now - startMs) / 3600_000)
    if (hoursAgo < 0 || hoursAgo >= 24) continue
    const bucketKey = new Date(now - hoursAgo * 3600_000).getHours().toString().padStart(2, '0') + ':00'
    if (!buckets[bucketKey]) continue
    buckets[bucketKey].total += 1
    if (wf.status === 'completed') buckets[bucketKey].success += 1
    if (wf.status === 'failed' || wf.status === 'timed_out' || wf.status === 'terminated') {
      buckets[bucketKey].failed += 1
    }
  }

  return Object.entries(buckets).map(([hour, data]) => ({
    hour,
    total: data.total,
    success: data.success,
    failed: data.failed,
    successRate: data.total > 0 ? Math.round((data.success / data.total) * 1000) / 10 : 100,
  }))
}

export default function Dashboard() {
  const [currentTime, setCurrentTime] = useState(new Date())

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000)
    return () => clearInterval(timer)
  }, [])

  const { data: workflowsData, loading, error: workflowsError, refetch: refetchWorkflows } = useApi(() => fetchWorkflows())

  const stats = computeStatsFromWorkflows(workflowsData ?? [])
  const trendData = computeTrendData(workflowsData ?? [])
  const recentWorkflows = (workflowsData ?? [])
    .slice()
    .sort((a, b) => {
      const ta = a.startTime ? new Date(a.startTime).getTime() : 0
      const tb = b.startTime ? new Date(b.startTime).getTime() : 0
      return tb - ta
    })
    .slice(0, 5)

  const handleRefresh = () => {
    setCurrentTime(new Date())
    refetchWorkflows()
  }

  const hasError = workflowsError

  return (
    <div className="bg-base-900 p-6">
      {/* Header */}
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">全局总览</h1>
          <p className="mt-1 text-sm text-slate-400">{formatTime(currentTime.toISOString())}</p>
        </div>
        <button
          type="button"
          onClick={handleRefresh}
          className="flex items-center gap-2 rounded-lg border border-base-700 bg-base-800 px-4 py-2 text-sm text-slate-200 hover:bg-base-700"
        >
          <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          刷新
        </button>
      </div>

      {/* Error banner */}
      {hasError && (
        <div className="mb-6 flex items-center gap-2 rounded-lg border border-status-failed/30 bg-status-failed/10 px-4 py-3">
          <AlertCircle className="h-4 w-4 flex-shrink-0 text-status-failed" />
          <span className="text-sm text-slate-200">
            API 请求失败：{workflowsError!}。请确认 Temporal UI Server (端口 8080) 已启动。
          </span>
        </div>
      )}

      {/* Stats cards */}
      <div className="mb-6 grid grid-cols-4 gap-4">
        <StatCard title="运行中" value={stats.running} icon={Loader} color="bg-status-running" />
        <StatCard title="已完成" value={stats.completed} icon={CheckCircle2} color="bg-status-completed" />
        <StatCard title="失败" value={stats.failed} icon={XCircle} color="bg-status-failed" />
        <StatCard title="超时" value={stats.timed_out} icon={Clock} color="bg-status-timeout" />
      </div>

      {/* Success rate trend chart */}
      <div className="mb-6 rounded-xl border border-base-700 bg-base-800 p-5">
        <h2 className="mb-4 text-base font-semibold text-slate-100">24小时成功率趋势</h2>
        <ResponsiveContainer width="100%" height={300}>
          <AreaChart data={trendData}>
            <defs>
              <linearGradient id="successGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#22c55e" stopOpacity={0.4} />
                <stop offset="100%" stopColor="#22c55e" stopOpacity={0} />
              </linearGradient>
            </defs>
            <XAxis dataKey="hour" stroke="#64748b" fontSize={12} />
            <YAxis domain={[0, 100]} stroke="#64748b" fontSize={12} tickFormatter={(v) => `${v}%`} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#0f172a',
                border: '1px solid #1e293b',
                borderRadius: '8px',
                color: '#e2e8f0',
              }}
              formatter={(value) => [`${value}%`, '成功率']}
            />
            <Area
              type="monotone"
              dataKey="successRate"
              stroke="#22c55e"
              strokeWidth={2}
              fill="url(#successGradient)"
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>

      {/* Recent workflow list */}
      <div className="rounded-xl border border-base-700 bg-base-800 p-5">
        <h2 className="mb-4 text-base font-semibold text-slate-100">最近工作流</h2>
        <div className="overflow-x-auto">
          {recentWorkflows.length === 0 ? (
            <div className="flex items-center justify-center py-12 text-sm text-slate-500">
              暂无工作流
            </div>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-base-700 text-left text-xs text-slate-400">
                  <th className="pb-3 font-medium">工作流名称</th>
                  <th className="pb-3 font-medium">状态</th>
                  <th className="pb-3 font-medium">工作流类型</th>
                  <th className="pb-3 font-medium">开始时间</th>
                  <th className="pb-3 font-medium">持续时间</th>
                  <th className="pb-3 font-medium">操作</th>
                </tr>
              </thead>
              <tbody>
                {recentWorkflows.map((workflow) => (
                  <tr key={workflow.id} className="border-b border-base-700/50 hover:bg-base-700">
                    <td className="py-3 text-sm text-slate-200">{workflow.name}</td>
                    <td className="py-3">
                      <StatusBadge status={workflow.status} />
                    </td>
                    <td className="py-3 text-sm text-slate-400">{workflow.workflowType}</td>
                    <td className="py-3 text-sm text-slate-400">{formatTime(workflow.startTime)}</td>
                    <td className="py-3 text-sm text-slate-400">{formatDuration(workflow.duration)}</td>
                    <td className="py-3">
                      <Link
                        to={`/workflows/${workflow.id}`}
                        className="text-sm text-accent hover:text-accent-light"
                      >
                        查看详情
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  )
}
