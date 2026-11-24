import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { Plus, Pause, Play, RefreshCw, Clock, Loader, X, Check, AlertCircle } from 'lucide-react'
import { fetchSchedules, createSchedule, patchSchedule, type FrontendSchedule } from '@/lib/api/schedules'
import { useApi } from '@/hooks/useApi'
import { formatTime, cn } from '@/lib/utils'

const weekMap: Record<string, string> = {
  '0': '周日',
  '1': '周一',
  '2': '周二',
  '3': '周三',
  '4': '周四',
  '5': '周五',
  '6': '周六',
  '7': '周日',
}

// Convert a common cron expression to a Chinese-readable description.
function cronToChinese(cron: string): string {
  const parts = cron.trim().split(/\s+/)
  if (parts.length !== 5) return cron
  const [min, hour, day, month, week] = parts

  const pad = (n: string) => n.padStart(2, '0')
  const fmt = (h: string, m: string) => `${pad(h)}:${pad(m)}`

  if (min.startsWith('*/') && hour === '*' && day === '*' && month === '*' && week === '*') {
    return `每${min.slice(2)}分钟`
  }
  if (min === '0' && hour === '*' && day === '*' && month === '*' && week === '*') {
    return '每小时'
  }
  if (min === '0' && hour.startsWith('*/') && day === '*' && month === '*' && week === '*') {
    return `每${hour.slice(2)}小时`
  }
  if (min !== '*' && hour.includes(',') && !hour.includes('/') && day === '*' && month === '*' && week === '*') {
    const times = hour.split(',').map((h) => fmt(h.trim(), min))
    return `每天 ${times.join(', ')}`
  }
  if (week !== '*' && weekMap[week] && min !== '*' && hour !== '*' && !hour.includes('/') && !hour.includes(',')) {
    return `每${weekMap[week]} ${fmt(hour, min)}`
  }
  if (day !== '*' && month === '*' && week === '*' && min !== '*' && hour !== '*' && !hour.includes('/') && !hour.includes(',')) {
    return `每月${day}日 ${fmt(hour, min)}`
  }
  if (min !== '*' && hour !== '*' && !hour.includes('/') && !hour.includes(',') && day === '*' && month === '*' && week === '*') {
    return `每天 ${fmt(hour, min)}`
  }
  return cron
}

// 工作流类型与任务队列可选项（与 StartWorkflow 保持一致）
const WORKFLOW_TYPES = [
  { value: 'DemoWorkflow', label: 'DemoWorkflow' },
  { value: 'HikSyncWorkflow', label: 'HikSyncWorkflow（海康同步）' },
]

const TASK_QUEUES = [
  { value: 'DEMO_TASK_QUEUE', label: 'DEMO_TASK_QUEUE' },
  { value: 'HIK_SYNC_TASK_QUEUE', label: 'HIK_SYNC_TASK_QUEUE' },
]

// 常用 Cron 预设
const CRON_PRESETS = [
  { label: '每天 09:00', value: '0 9 * * *' },
  { label: '每天 00:00', value: '0 0 * * *' },
  { label: '每小时', value: '0 * * * *' },
  { label: '每 30 分钟', value: '*/30 * * * *' },
  { label: '每周一 09:00', value: '0 9 * * 1' },
  { label: '每月 1 日 09:00', value: '0 9 1 * *' },
]

export default function Schedules() {
  const { data: apiSchedules, loading, error, refetch } = useApi(() => fetchSchedules())
  const [schedules, setSchedules] = useState<FrontendSchedule[]>([])
  const [createModalOpen, setCreateModalOpen] = useState(false)
  const [actionMsg, setActionMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  useEffect(() => {
    setSchedules(apiSchedules ?? [])
  }, [apiSchedules])

  const showMsg = (type: 'success' | 'error', text: string) => {
    setActionMsg({ type, text })
    setTimeout(() => setActionMsg(null), 3000)
  }

  const handleCreate = () => {
    setCreateModalOpen(true)
  }

  const handleCreateSuccess = (scheduleId: string) => {
    setCreateModalOpen(false)
    showMsg('success', `计划任务 ${scheduleId} 创建成功`)
    refetch()
  }

  const stats = (() => {
    const total = schedules.length
    let active = 0
    let paused = 0
    let totalRuns = 0
    let totalFailures = 0
    for (const schedule of schedules) {
      if (schedule.status === 'active') {
        active += 1
      } else {
        paused += 1
      }
      totalRuns += schedule.runCount
      totalFailures += schedule.failureCount
    }
    return { total, active, paused, totalRuns, totalFailures }
  })()

  const toggleStatus = async (schedule: FrontendSchedule) => {
    const action = schedule.status === 'active' ? 'pause' : 'unpause'
    const label = action === 'pause' ? '暂停' : '恢复'
    try {
      await patchSchedule(schedule.id, action)
      // 乐观更新
      setSchedules((prev) =>
        prev.map((s) =>
          s.id === schedule.id
            ? { ...s, status: s.status === 'active' ? 'paused' : 'active' }
            : s,
        ),
      )
      showMsg('success', `计划 ${schedule.id} 已${label}`)
    } catch (err) {
      showMsg('error', `${label}失败: ${(err as Error).message}`)
    }
  }

  return (
    <div className="space-y-6 p-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <h1 className="text-2xl font-bold text-slate-100">计划任务</h1>
          <span className="rounded-full bg-base-700 px-2.5 py-0.5 text-sm font-medium text-slate-300">
            {schedules.length}
          </span>
          {loading && (
            <Loader className="h-4 w-4 animate-spin text-slate-400" />
          )}
        </div>
        <div className="flex items-center gap-3">
          <button
            type="button"
            onClick={refetch}
            className="flex items-center gap-2 rounded-lg border border-base-700 bg-base-800 px-4 py-2 text-sm text-slate-200 hover:bg-base-700"
          >
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
            刷新
          </button>
          <button
            type="button"
            onClick={handleCreate}
            className="flex items-center gap-2 rounded-lg bg-accent px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-accent-dark"
          >
            <Plus className="h-4 w-4" />
            创建计划
          </button>
        </div>
      </div>

      {/* 操作反馈消息 */}
      {actionMsg && (
        <div className={cn(
          'flex items-center gap-2 rounded-lg border px-4 py-3',
          actionMsg.type === 'success'
            ? 'border-status-completed/30 bg-status-completed/10'
            : 'border-status-failed/30 bg-status-failed/10'
        )}>
          {actionMsg.type === 'success'
            ? <Check className="h-4 w-4 flex-shrink-0 text-status-completed" />
            : <AlertCircle className="h-4 w-4 flex-shrink-0 text-status-failed" />}
          <span className="text-sm text-slate-200">{actionMsg.text}</span>
        </div>
      )}

      {/* 错误提示 */}
      {error && (
        <div className="flex items-center gap-2 rounded-lg border border-status-failed/30 bg-status-failed/10 px-4 py-3">
          <AlertCircle className="h-4 w-4 flex-shrink-0 text-status-failed" />
          <span className="text-sm text-slate-200">
            API 请求失败：{error}。请确认 Temporal UI Server (端口 8080) 已启动。
          </span>
        </div>
      )}

      {/* Stats row */}
      <div className="grid grid-cols-3 gap-4">
        <div className="bg-base-800 border border-base-700 rounded-xl p-4">
          <div className="flex items-center gap-2">
            <span className="h-2 w-2 rounded-full bg-status-completed" />
            <span className="text-sm text-slate-400">活跃计划</span>
          </div>
          <div className="mt-2 text-2xl font-bold text-slate-100">{stats.active}</div>
        </div>
        <div className="bg-base-800 border border-base-700 rounded-xl p-4">
          <div className="flex items-center gap-2">
            <span className="h-2 w-2 rounded-full bg-status-running" />
            <span className="text-sm text-slate-400">已暂停</span>
          </div>
          <div className="mt-2 text-2xl font-bold text-slate-100">{stats.paused}</div>
        </div>
        <div className="bg-base-800 border border-base-700 rounded-xl p-4">
          <div className="flex items-center gap-2">
            <span className="h-2 w-2 rounded-full bg-blue-500" />
            <span className="text-sm text-slate-400">总执行次数</span>
          </div>
          <div className="mt-2 text-2xl font-bold text-slate-100">{stats.totalRuns}</div>
        </div>
      </div>

      {/* Schedule table */}
      <div className="overflow-hidden rounded-xl border border-base-700 bg-base-800">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-base-700">
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">计划名称</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">状态</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">工作流类型</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">任务队列</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">Cron表达式</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">上次执行</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">下次执行</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">执行次数</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">失败次数</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">操作</th>
              </tr>
            </thead>
            <tbody>
              {schedules.map((schedule) => (
                <tr
                  key={schedule.id}
                  className="border-b border-base-700 transition-colors hover:bg-base-700/50"
                >
                  <td className="px-4 py-3">
                    <Link
                      to={`/schedules/${schedule.id}`}
                      className="text-sm font-medium text-slate-200 hover:text-accent"
                    >
                      {schedule.name}
                    </Link>
                  </td>
                  <td className="px-4 py-3">
                    {schedule.status === 'active' ? (
                      <span className="rounded-full bg-green-500/15 px-2 py-0.5 text-xs text-green-400">活跃</span>
                    ) : (
                      <span className="rounded-full bg-amber-500/15 px-2 py-0.5 text-xs text-amber-400">已暂停</span>
                    )}
                  </td>
                  <td className="px-4 py-3 text-sm text-slate-300">{schedule.workflowType}</td>
                  <td className="px-4 py-3 text-sm text-slate-300">{schedule.taskQueue}</td>
                  <td className="px-4 py-3">
                    <div className="font-mono text-xs text-slate-300">{schedule.cronExpression || '-'}</div>
                    {schedule.cronExpression && (
                      <div className="text-xs text-slate-500">{cronToChinese(schedule.cronExpression)}</div>
                    )}
                  </td>
                  <td className="px-4 py-3">
                    <span className="font-mono text-xs text-slate-400">
                      {formatTime(schedule.lastRunTime) || '-'}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-1">
                      <Clock className="h-3 w-3 text-slate-500" />
                      <span className="font-mono text-xs text-slate-400">
                        {formatTime(schedule.nextRunTime) || '-'}
                      </span>
                    </div>
                  </td>
                  <td className="px-4 py-3 text-sm text-slate-300">{schedule.runCount}</td>
                  <td className="px-4 py-3">
                    <span
                      className={cn(
                        'text-sm',
                        schedule.failureCount > 0 ? 'text-red-400' : 'text-slate-300',
                      )}
                    >
                      {schedule.failureCount}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-2">
                      <button
                        type="button"
                        onClick={() => toggleStatus(schedule)}
                        className={cn(
                          'flex items-center gap-1 rounded-lg px-2.5 py-1 text-xs transition-colors',
                          schedule.status === 'active'
                            ? 'text-amber-400 hover:bg-amber-500/10'
                            : 'text-green-400 hover:bg-green-500/10',
                        )}
                      >
                        {schedule.status === 'active' ? (
                          <>
                            <Pause className="h-3.5 w-3.5" />
                            暂停
                          </>
                        ) : (
                          <>
                            <Play className="h-3.5 w-3.5" />
                            恢复
                          </>
                        )}
                      </button>
                      <Link
                        to={`/schedules/${schedule.id}`}
                        className="text-xs text-accent hover:text-accent-light"
                      >
                        查看详情
                      </Link>
                    </div>
                  </td>
                </tr>
              ))}
              {schedules.length === 0 && !loading && (
                <tr>
                  <td colSpan={10} className="px-4 py-12 text-center text-sm text-slate-500">
                    暂无计划任务，点击右上角"创建计划"按钮新建
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* 创建计划 Modal */}
      {createModalOpen && (
        <CreateScheduleModal
          onClose={() => setCreateModalOpen(false)}
          onSuccess={handleCreateSuccess}
          onError={(msg) => {
            setCreateModalOpen(false)
            showMsg('error', msg)
          }}
        />
      )}
    </div>
  )
}

/**
 * 创建计划任务 Modal
 */
function CreateScheduleModal({
  onClose,
  onSuccess,
  onError,
}: {
  onClose: () => void
  onSuccess: (scheduleId: string) => void
  onError: (msg: string) => void
}) {
  const [scheduleId, setScheduleId] = useState('')
  const [workflowType, setWorkflowType] = useState(WORKFLOW_TYPES[0].value)
  const [taskQueue, setTaskQueue] = useState(TASK_QUEUES[0].value)
  const [cronExpression, setCronExpression] = useState(CRON_PRESETS[0].value)
  const [input, setInput] = useState('')
  const [notes, setNotes] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)

  const handleRandomId = () => {
    setScheduleId(`schedule-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 6)}`)
  }

  const validate = (): string | null => {
    if (!scheduleId.trim()) return '请填写计划 ID'
    if (!workflowType.trim()) return '请选择工作流类型'
    if (!taskQueue.trim()) return '请选择任务队列'
    if (!cronExpression.trim()) return '请填写 Cron 表达式'
    // 简单校验 Cron：5 段
    const parts = cronExpression.trim().split(/\s+/)
    if (parts.length !== 5) return 'Cron 表达式格式错误，应为 5 段（分 时 日 月 周）'
    if (input.trim()) {
      try {
        JSON.parse(input)
      } catch {
        return '输入参数必须是合法 JSON'
      }
    }
    return null
  }

  const handleSubmit = async () => {
    const err = validate()
    if (err) {
      setFormError(err)
      return
    }
    setFormError(null)
    setSubmitting(true)
    try {
      await createSchedule({
        scheduleId: scheduleId.trim(),
        workflowType,
        taskQueue,
        cronExpression: cronExpression.trim(),
        input: input.trim() || undefined,
        notes: notes.trim() || undefined,
      })
      onSuccess(scheduleId.trim())
    } catch (e) {
      const msg = (e as Error).message || '未知错误'
      setFormError(msg)
      onError(`创建计划失败: ${msg}`)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="w-full max-w-lg rounded-xl border border-base-600 bg-base-800 p-5 shadow-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-base font-semibold text-slate-100">创建计划任务</h3>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg p-1 text-slate-400 hover:bg-base-700 hover:text-slate-200"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
        <p className="mb-4 text-xs text-slate-400">
          创建一个 Temporal Schedule，按 Cron 表达式定时启动工作流。
        </p>

        <div className="space-y-4">
          {/* 计划 ID */}
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-300">
              计划 ID <span className="text-status-failed">*</span>
            </label>
            <div className="flex gap-2">
              <input
                type="text"
                value={scheduleId}
                onChange={(e) => { setScheduleId(e.target.value); setFormError(null) }}
                placeholder="例如：daily-sync-001"
                className="flex-1 rounded-lg border border-base-600 bg-base-900 px-3 py-2 text-sm text-slate-100 placeholder-slate-500 outline-none focus:border-accent"
              />
              <button
                type="button"
                onClick={handleRandomId}
                className="rounded-lg border border-base-600 px-3 py-2 text-xs text-slate-300 hover:bg-base-700"
              >
                随机
              </button>
            </div>
          </div>

          {/* 工作流类型 */}
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-300">
              工作流类型 <span className="text-status-failed">*</span>
            </label>
            <select
              value={workflowType}
              onChange={(e) => setWorkflowType(e.target.value)}
              className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 text-sm text-slate-100 outline-none focus:border-accent"
            >
              {WORKFLOW_TYPES.map((w) => (
                <option key={w.value} value={w.value}>{w.label}</option>
              ))}
            </select>
          </div>

          {/* 任务队列 */}
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-300">
              任务队列 <span className="text-status-failed">*</span>
            </label>
            <select
              value={taskQueue}
              onChange={(e) => setTaskQueue(e.target.value)}
              className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 text-sm text-slate-100 outline-none focus:border-accent"
            >
              {TASK_QUEUES.map((q) => (
                <option key={q.value} value={q.value}>{q.label}</option>
              ))}
            </select>
          </div>

          {/* Cron 表达式 */}
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-300">
              Cron 表达式 <span className="text-status-failed">*</span>
            </label>
            <input
              type="text"
              value={cronExpression}
              onChange={(e) => { setCronExpression(e.target.value); setFormError(null) }}
              placeholder="分 时 日 月 周（如：0 9 * * *）"
              className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 font-mono text-sm text-slate-100 placeholder-slate-500 outline-none focus:border-accent"
            />
            <div className="mt-2 flex flex-wrap gap-1.5">
              {CRON_PRESETS.map((p) => (
                <button
                  key={p.value}
                  type="button"
                  onClick={() => setCronExpression(p.value)}
                  className={cn(
                    'rounded-full px-2.5 py-0.5 text-xs transition-colors',
                    cronExpression === p.value
                      ? 'bg-accent/20 text-accent-light'
                      : 'bg-base-700 text-slate-400 hover:bg-base-600 hover:text-slate-200'
                  )}
                >
                  {p.label}
                </button>
              ))}
            </div>
            {cronExpression && cronToChinese(cronExpression) !== cronExpression && (
              <p className="mt-1 text-xs text-slate-500">说明：{cronToChinese(cronExpression)}</p>
            )}
          </div>

          {/* 输入参数 */}
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-300">输入参数（JSON，可选）</label>
            <textarea
              value={input}
              onChange={(e) => { setInput(e.target.value); setFormError(null) }}
              placeholder='例如：{"deviceId": "12345"}'
              rows={3}
              className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 font-mono text-sm text-slate-100 placeholder-slate-500 outline-none focus:border-accent"
            />
          </div>

          {/* 备注 */}
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-300">备注（可选）</label>
            <input
              type="text"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="计划用途说明"
              className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 text-sm text-slate-100 placeholder-slate-500 outline-none focus:border-accent"
            />
          </div>

          {formError && (
            <div className="flex items-center gap-2 rounded-lg bg-status-failed/10 px-3 py-2 text-xs text-status-failed">
              <AlertCircle className="h-3.5 w-3.5" />
              {formError}
            </div>
          )}
        </div>

        <div className="mt-5 flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            disabled={submitting}
            className="rounded-lg px-3 py-1.5 text-sm text-slate-400 hover:bg-base-700 hover:text-slate-200 disabled:opacity-50"
          >
            取消
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            disabled={submitting}
            className="flex items-center gap-2 rounded-lg bg-accent px-4 py-1.5 text-sm text-white hover:bg-accent-dark disabled:opacity-50"
          >
            {submitting && <Loader className="h-3.5 w-3.5 animate-spin" />}
            {submitting ? '创建中...' : '创建'}
          </button>
        </div>
      </div>
    </div>
  )
}
