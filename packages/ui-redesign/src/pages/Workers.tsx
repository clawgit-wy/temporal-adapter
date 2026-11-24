import { useState } from 'react'
import { RefreshCw, X, Cpu, MemoryStick, Activity, Loader } from 'lucide-react'
import { fetchAllWorkers, type FrontendWorker } from '@/lib/api/workers'
import { useApi } from '@/hooks/useApi'
import type { WorkerStatus } from '@/lib/types'
import { cn, formatTime } from '@/lib/utils'

const statusConfig: Record<WorkerStatus, { label: string; dot: string; badge: string; border: string }> = {
  running: { label: '运行中', dot: 'bg-green-500', badge: 'bg-green-500/10 text-green-400', border: 'border-l-green-500' },
  unhealthy: { label: '异常', dot: 'bg-red-500', badge: 'bg-red-500/10 text-red-400', border: 'border-l-red-500' },
  drained: { label: '已排空', dot: 'bg-slate-500', badge: 'bg-slate-500/10 text-slate-400', border: 'border-l-slate-500' },
  draining: { label: '排空中', dot: 'bg-yellow-500', badge: 'bg-yellow-500/10 text-yellow-400', border: 'border-l-yellow-500' },
}

function usageColor(value: number): string {
  if (value < 50) return 'text-green-400'
  if (value < 80) return 'text-yellow-400'
  return 'text-red-400'
}

function usageBarColor(value: number): string {
  if (value < 50) return 'bg-green-500'
  if (value < 80) return 'bg-yellow-500'
  return 'bg-red-500'
}

export default function Workers() {
  const [selectedWorker, setSelectedWorker] = useState<FrontendWorker | null>(null)
  const { data: apiWorkers, loading, refetch } = useApi(() =>
    fetchAllWorkers(['HIK_SYNC_TASK_QUEUE', 'DEMO_TASK_QUEUE', 'order-task-queue']),
  )

  const workers = apiWorkers ?? []

  const stats = (() => {
    const total = workers.length
    let running = 0
    let unhealthy = 0
    let drained = 0
    let draining = 0
    for (const worker of workers) {
      switch (worker.status) {
        case 'running':
          running += 1
          break
        case 'unhealthy':
          unhealthy += 1
          break
        case 'drained':
          drained += 1
          break
        case 'draining':
          draining += 1
          break
      }
    }
    return { total, running, unhealthy, drained, draining }
  })()

  const statCards = [
    { label: '运行中', value: stats.running, dot: 'bg-green-500', border: 'border-l-green-500' },
    { label: '异常', value: stats.unhealthy, dot: 'bg-red-500', border: 'border-l-red-500' },
    { label: '已排空', value: stats.drained, dot: 'bg-slate-500', border: 'border-l-slate-500' },
    { label: '排空中', value: stats.draining, dot: 'bg-yellow-500', border: 'border-l-yellow-500' },
  ]

  return (
    <div className="space-y-6 p-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <h1 className="text-2xl font-bold text-slate-100">工作进程</h1>
          <span className="rounded-full bg-base-700 px-2.5 py-0.5 text-sm font-medium text-slate-300">
            {stats.total}
          </span>
          {loading && (
            <Loader className="h-4 w-4 animate-spin text-slate-400" />
          )}
        </div>
        <button
          type="button"
          onClick={refetch}
          className="flex items-center gap-2 rounded-lg border border-base-700 bg-base-800 px-4 py-2 text-sm text-slate-200 hover:bg-base-700"
        >
          <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          刷新
        </button>
      </div>

      {/* Stats row */}
      <div className="grid grid-cols-4 gap-4">
        {statCards.map((card) => (
          <div
            key={card.label}
            className={cn('rounded-xl border border-base-700 border-l-4 bg-base-800 p-4', card.border)}
          >
            <div className="flex items-center gap-2">
              <span className={cn('h-2 w-2 rounded-full', card.dot)} />
              <span className="text-xs text-slate-400">{card.label}</span>
            </div>
            <div className="mt-3 text-2xl font-bold text-slate-100">{card.value}</div>
          </div>
        ))}
      </div>

      {/* Worker table */}
      <div className="overflow-hidden rounded-xl border border-base-700 bg-base-800">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-base-700">
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">工作进程名称</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">状态</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">任务队列</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">构建ID</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">槽位使用</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">CPU使用率</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">内存使用率</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">轮询器数量</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">最后心跳</th>
                <th className="px-4 py-3 text-right text-xs font-medium uppercase text-slate-400">操作</th>
              </tr>
            </thead>
            <tbody>
              {workers.map((worker) => {
                const cfg = statusConfig[worker.status]
                const slotPct =
                  worker.slotsMax > 0 ? (worker.slotsUsed / worker.slotsMax) * 100 : 0
                const cpuUsage = worker.resourceInfo.cpuUsage
                const memoryUsage = worker.resourceInfo.memoryUsage
                return (
                  <tr
                    key={worker.id}
                    className="border-b border-base-700 transition-colors hover:bg-base-700/50"
                  >
                    <td className="px-4 py-3">
                      <span className="text-sm font-medium text-slate-200">{worker.name}</span>
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={cn(
                          'inline-flex items-center gap-1.5 rounded-full px-2 py-0.5 text-xs',
                          cfg.badge,
                        )}
                      >
                        <span className={cn('h-1.5 w-1.5 rounded-full', cfg.dot)} />
                        {cfg.label}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-300">{worker.taskQueue}</td>
                    <td className="px-4 py-3">
                      <span className="font-mono text-xs text-slate-400">{worker.buildId}</span>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2">
                        <span className="text-xs text-slate-300">
                          {worker.slotsUsed}/{worker.slotsMax}
                        </span>
                        <div className="h-1.5 w-20 rounded-full bg-base-700">
                          <div
                            className="h-full rounded-full bg-accent"
                            style={{ width: `${slotPct}%` }}
                          />
                        </div>
                      </div>
                    </td>
                    <td className="px-4 py-3">
                      <span className={cn('text-sm font-medium', usageColor(cpuUsage))}>
                        {cpuUsage}%
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={cn('text-sm font-medium', usageColor(memoryUsage))}>
                        {memoryUsage}%
                      </span>
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-300">{worker.pollerCount}</td>
                    <td className="px-4 py-3">
                      <span className="font-mono text-xs text-slate-400">
                        {formatTime(worker.lastHeartbeat)}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <button
                        type="button"
                        onClick={() => setSelectedWorker(worker)}
                        className="text-sm text-accent hover:text-accent-light"
                      >
                        查看详情
                      </button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* Worker detail drawer */}
      {selectedWorker && (
        <>
          <div
            className="fixed inset-0 z-20 bg-black/50"
            onClick={() => setSelectedWorker(null)}
          />
          <div className="fixed right-0 top-0 z-30 h-full w-96 overflow-y-auto border-l border-base-700 bg-base-800">
            {/* Header */}
            <div className="flex items-start justify-between border-b border-base-700 p-4">
              <div className="space-y-2">
                <div className="flex items-center gap-2">
                  <Activity className="h-4 w-4 text-accent" />
                  <h2 className="text-base font-semibold text-slate-100">{selectedWorker.name}</h2>
                </div>
                <span
                  className={cn(
                    'inline-flex items-center gap-1.5 rounded-full px-2 py-0.5 text-xs',
                    statusConfig[selectedWorker.status].badge,
                  )}
                >
                  <span
                    className={cn(
                      'h-1.5 w-1.5 rounded-full',
                      statusConfig[selectedWorker.status].dot,
                    )}
                  />
                  {statusConfig[selectedWorker.status].label}
                </span>
              </div>
              <button
                type="button"
                onClick={() => setSelectedWorker(null)}
                className="rounded-lg p-1.5 text-slate-400 hover:bg-base-700 hover:text-slate-200"
              >
                <X className="h-4 w-4" />
              </button>
            </div>

            {/* Basic info */}
            <div className="space-y-3 border-b border-base-700 p-4">
              <div className="text-xs font-medium uppercase text-slate-400">基础信息</div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">Identity</span>
                <span className="font-mono text-slate-300">{selectedWorker.identity}</span>
              </div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">构建ID</span>
                <span className="font-mono text-slate-300">{selectedWorker.buildId}</span>
              </div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">版本</span>
                <span className="text-slate-300">{selectedWorker.version}</span>
              </div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">启动时间</span>
                <span className="font-mono text-slate-300">
                  {formatTime(selectedWorker.startTime)}
                </span>
              </div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">最后心跳</span>
                <span className="font-mono text-slate-300">
                  {formatTime(selectedWorker.lastHeartbeat)}
                </span>
              </div>
            </div>

            {/* Resource usage */}
            <div className="space-y-4 border-b border-base-700 p-4">
              <div className="text-xs font-medium uppercase text-slate-400">资源使用</div>
              <div>
                <div className="mb-1.5 flex items-center justify-between">
                  <span className="flex items-center gap-1.5 text-sm text-slate-300">
                    <Cpu className="h-3.5 w-3.5" />
                    CPU
                  </span>
                  <span className={cn('text-sm font-medium', usageColor(selectedWorker.resourceInfo.cpuUsage))}>
                    {selectedWorker.resourceInfo.cpuUsage}%
                  </span>
                </div>
                <div className="h-1.5 rounded-full bg-base-700">
                  <div
                    className={cn('h-full rounded-full', usageBarColor(selectedWorker.resourceInfo.cpuUsage))}
                    style={{ width: `${selectedWorker.resourceInfo.cpuUsage}%` }}
                  />
                </div>
              </div>
              <div>
                <div className="mb-1.5 flex items-center justify-between">
                  <span className="flex items-center gap-1.5 text-sm text-slate-300">
                    <MemoryStick className="h-3.5 w-3.5" />
                    内存
                  </span>
                  <span className={cn('text-sm font-medium', usageColor(selectedWorker.resourceInfo.memoryUsage))}>
                    {selectedWorker.resourceInfo.memoryUsage}%
                  </span>
                </div>
                <div className="h-1.5 rounded-full bg-base-700">
                  <div
                    className={cn('h-full rounded-full', usageBarColor(selectedWorker.resourceInfo.memoryUsage))}
                    style={{ width: `${selectedWorker.resourceInfo.memoryUsage}%` }}
                  />
                </div>
              </div>
            </div>

            {/* Slot usage */}
            <div className="space-y-3 border-b border-base-700 p-4">
              <div className="text-xs font-medium uppercase text-slate-400">槽位使用</div>
              <div>
                <div className="mb-1.5 flex items-center justify-between text-sm">
                  <span className="text-slate-300">
                    {selectedWorker.slotsUsed} / {selectedWorker.slotsMax}
                  </span>
                  <span className="text-slate-400">
                    {selectedWorker.slotsMax > 0
                      ? Math.round((selectedWorker.slotsUsed / selectedWorker.slotsMax) * 100)
                      : 0}
                    %
                  </span>
                </div>
                <div className="h-1.5 rounded-full bg-base-700">
                  <div
                    className="h-full rounded-full bg-accent"
                    style={{
                      width: `${selectedWorker.slotsMax > 0 ? (selectedWorker.slotsUsed / selectedWorker.slotsMax) * 100 : 0}%`,
                    }}
                  />
                </div>
              </div>
            </div>

            {/* Task queue & pollers */}
            <div className="space-y-3 p-4">
              <div className="text-xs font-medium uppercase text-slate-400">队列与轮询</div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">任务队列</span>
                <span className="text-slate-300">{selectedWorker.taskQueue}</span>
              </div>
              <div className="flex items-center justify-between text-sm">
                <span className="text-slate-400">轮询器数量</span>
                <span className="text-slate-300">{selectedWorker.pollerCount}</span>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  )
}
