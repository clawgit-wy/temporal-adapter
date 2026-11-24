import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import {
  ArrowLeft, Signal, Send, RotateCcw, Square, Ban,
  Clock, Activity as ActivityIcon, Globe, Terminal,
  ChevronDown, ChevronRight, Zap, Loader, X, Check, AlertCircle
} from 'lucide-react'
import StatusBadge from '@/components/StatusBadge'
import JsonViewer from '@/components/JsonViewer'
import { getWorkflow, getWorkflowHistory, terminateWorkflow, cancelWorkflow, signalWorkflow, updateWorkflow, type FrontendWorkflow } from '@/lib/api/workflows'
import { mapWorkflowStatus } from '@/lib/api/types'
import type { Activity } from '@/lib/types'
import { useApi } from '@/hooks/useApi'
import { formatTime, formatDuration, cn } from '@/lib/utils'

const methodColors: Record<string, string> = {
  GET: 'bg-green-500/20 text-green-400',
  POST: 'bg-blue-500/20 text-blue-400',
  PUT: 'bg-yellow-500/20 text-yellow-400',
  DELETE: 'bg-red-500/20 text-red-400',
  PATCH: 'bg-purple-500/20 text-purple-400',
}

const statusDotColors: Record<string, string> = {
  running: 'bg-status-running pulse-ring',
  completed: 'bg-status-completed',
  failed: 'bg-status-failed',
  timed_out: 'bg-status-timeout',
  canceled: 'bg-status-canceled',
  terminated: 'bg-status-terminated',
}

type TabKey = 'io' | 'thirdparty' | 'logs'

export default function WorkflowDetail() {
  const { id } = useParams<{ id: string }>()
  const { data: apiWorkflow, loading, refetch } = useApi(() => getWorkflow(id || ''), [id])
  const { data: historyData } = useApi(() => getWorkflowHistory(id || ''), [id])

  const workflow: FrontendWorkflow | undefined = (() => {
    if (apiWorkflow?.workflowExecutionInfo) {
      const info = apiWorkflow.workflowExecutionInfo
      const startTime = info.startTime || ''
      const closeTime = info.closeTime || ''
      let duration = 0
      if (startTime) {
        const end = closeTime ? new Date(closeTime).getTime() : Date.now()
        duration = end - new Date(startTime).getTime()
      }

      // 从历史事件中解析 activities
      const activities: Activity[] = []
      const rawEvents = historyData?.history?.events ?? []
      let activityIdx = 0
      for (const evt of rawEvents) {
        const eventType = evt.eventType || ''
        // ActivityTaskScheduled / ActivityTaskStarted / ActivityTaskCompleted
        if (eventType.includes('ActivityTask')) {
          const isCompleted = eventType === 'EVENT_TYPE_ACTIVITY_TASK_COMPLETED'
          const isFailed = eventType === 'EVENT_TYPE_ACTIVITY_TASK_FAILED'
          const isStarted = eventType === 'EVENT_TYPE_ACTIVITY_TASK_STARTED'
          const isScheduled = eventType === 'EVENT_TYPE_ACTIVITY_TASK_SCHEDULED'
          if (isScheduled || isStarted) {
            const attrs = isScheduled ? evt.activityTaskScheduledEventAttributes : evt.activityTaskStartedEventAttributes
            activities.push({
              id: `activity-${activityIdx}`,
              activityType: attrs?.activityType?.name ?? `Activity ${activityIdx}`,
              status: isStarted ? 'running' : 'running',
              startTime: evt.eventTime || '',
              endTime: '',
              duration: 0,
              attempt: attrs?.attempt ? Number(attrs.attempt) : 1,
              input: attrs?.input ?? {},
              output: {},
              isThirdParty: false,
            })
            activityIdx++
          }
          if (isCompleted) {
            const attrs = evt.activityTaskCompletedEventAttributes
            const lastAct = activities[activities.length - 1]
            if (lastAct) {
              lastAct.status = 'completed'
              lastAct.endTime = evt.eventTime || ''
              if (lastAct.startTime) {
                lastAct.duration = new Date(lastAct.endTime).getTime() - new Date(lastAct.startTime).getTime()
              }
              lastAct.output = attrs?.result ?? {}
            }
          }
          if (isFailed) {
            const attrs = evt.activityTaskFailedEventAttributes
            const lastAct = activities[activities.length - 1]
            if (lastAct) {
              lastAct.status = 'failed'
              lastAct.endTime = evt.eventTime || ''
              if (lastAct.startTime) {
                lastAct.duration = new Date(lastAct.endTime).getTime() - new Date(lastAct.startTime).getTime()
              }
            }
          }
        }
      }

      return {
        id: info.execution?.workflowId || '',
        workflowId: info.execution?.workflowId || '',
        runId: info.execution?.runId || '',
        name: info.type?.name ?? 'Unknown',
        status: mapWorkflowStatus(info.status),
        workflowType: info.type?.name ?? 'Unknown',
        taskQueue: info.taskQueue ?? '',
        startTime,
        closeTime,
        duration,
        input: {},
        result: {},
        activities,
        searchAttributes: info.searchAttributes?.indexedFields ?? {},
      }
    }
    return undefined
  })()

  const [selectedActivityId, setSelectedActivityId] = useState<string | null>(null)
  const [activeTab, setActiveTab] = useState<TabKey>('io')
  const [showInput, setShowInput] = useState(true)
  const [showResult, setShowResult] = useState(false)
  const [actionMsg, setActionMsg] = useState<{type: 'success'|'error', text: string} | null>(null)
  const [actionLoading, setActionLoading] = useState(false)
  const [signalModalOpen, setSignalModalOpen] = useState(false)
  const [updateModalOpen, setUpdateModalOpen] = useState(false)

  const showActionMsg = (type: 'success'|'error', text: string) => {
    setActionMsg({ type, text })
    setTimeout(() => setActionMsg(null), 3000)
  }

  const handleSignalSuccess = (signalName: string) => {
    setSignalModalOpen(false)
    showActionMsg('success', `信号 ${signalName} 已发送至工作流`)
    refetch()
  }

  const handleSignalError = (msg: string) => {
    setSignalModalOpen(false)
    showActionMsg('error', msg)
  }

  const handleUpdateSuccess = (updateType: string) => {
    setUpdateModalOpen(false)
    showActionMsg('success', `更新 ${updateType} 已提交（结果可能异步返回）`)
    refetch()
  }

  const handleUpdateError = (msg: string) => {
    setUpdateModalOpen(false)
    showActionMsg('error', msg)
  }

  const handleTerminate = async () => {
    if (!workflow) return
    setActionLoading(true)
    try {
      await terminateWorkflow(workflow.workflowId, workflow.runId, '用户手动终止')
      showActionMsg('success', `工作流 ${workflow.workflowId} 已终止`)
      refetch()
    } catch (err) {
      showActionMsg('error', `终止失败: ${(err as Error).message}`)
    } finally {
      setActionLoading(false)
    }
  }

  const handleCancel = async () => {
    if (!workflow) return
    setActionLoading(true)
    try {
      await cancelWorkflow(workflow.workflowId, workflow.runId)
      showActionMsg('success', `工作流 ${workflow.workflowId} 取消请求已发送`)
      refetch()
    } catch (err) {
      showActionMsg('error', `取消失败: ${(err as Error).message}`)
    } finally {
      setActionLoading(false)
    }
  }

  const handleReset = () => {
    showActionMsg('success', '重置功能暂未接入 Temporal API')
  }

  if (!workflow) {
    if (loading) {
      return (
        <div className="flex h-full items-center justify-center">
          <Loader className="h-8 w-8 animate-spin text-slate-400" />
        </div>
      )
    }
    return (
      <div className="flex h-full items-center justify-center">
        <div className="text-center">
          <p className="text-xl text-slate-400">工作流未找到</p>
          <Link to="/workflows" className="mt-4 inline-block text-accent hover:text-accent-light">
            返回工作流列表
          </Link>
        </div>
      </div>
    )
  }

  const selectedActivity = workflow.activities.find(a => a.id === selectedActivityId)
  const thirdPartyCount = workflow.activities.filter(a => a.isThirdParty).length
  const retryCount = workflow.activities.reduce((sum, a) => sum + Math.max(0, a.attempt - 1), 0)

  const handleActivityClick = (activity: Activity) => {
    setSelectedActivityId(activity.id)
    setActiveTab(activity.isThirdParty ? 'thirdparty' : 'io')
  }

  return (
    <div className="flex h-full flex-col">
      {/* 操作反馈消息 */}
      {actionMsg && (
        <div className={cn(
          'flex items-center gap-2 px-6 py-2 text-sm',
          actionMsg.type === 'success'
            ? 'bg-status-completed/10 text-status-completed'
            : 'bg-status-failed/10 text-status-failed'
        )}>
          {actionMsg.text}
        </div>
      )}

      {/* 顶部操作栏 */}
      <div className="sticky top-0 z-20 flex items-center justify-between border-b border-base-700 bg-base-800 px-6 py-3">
        <div className="flex items-center gap-3">
          <Link
            to="/workflows"
            className="flex h-8 w-8 items-center justify-center rounded-lg text-slate-400 hover:bg-base-700 hover:text-slate-100"
          >
            <ArrowLeft className="h-4 w-4" />
          </Link>
          <h1 className="text-lg font-semibold text-slate-100">{workflow.name}</h1>
          <StatusBadge status={workflow.status} size="sm" />
        </div>
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => setSignalModalOpen(true)}
            disabled={!workflow || actionLoading}
            className="flex items-center gap-1.5 rounded-lg bg-base-700 px-3 py-1.5 text-sm text-slate-300 hover:bg-base-600 disabled:opacity-50"
          >
            <Signal className="h-3.5 w-3.5" /> 发送信号
          </button>
          <button
            type="button"
            onClick={() => setUpdateModalOpen(true)}
            disabled={!workflow || actionLoading}
            className="flex items-center gap-1.5 rounded-lg bg-base-700 px-3 py-1.5 text-sm text-slate-300 hover:bg-base-600 disabled:opacity-50"
          >
            <Send className="h-3.5 w-3.5" /> 发送更新
          </button>
          <button
            onClick={handleReset}
            disabled={actionLoading}
            className="flex items-center gap-1.5 rounded-lg bg-base-700 px-3 py-1.5 text-sm text-slate-300 hover:bg-base-600 disabled:opacity-50"
          >
            <RotateCcw className="h-3.5 w-3.5" /> 重置
          </button>
          <button
            onClick={handleCancel}
            disabled={actionLoading}
            className="flex items-center gap-1.5 rounded-lg bg-base-700 px-3 py-1.5 text-sm text-slate-300 hover:bg-base-600 disabled:opacity-50"
          >
            <Ban className="h-3.5 w-3.5" /> 取消
          </button>
          <button
            onClick={handleTerminate}
            disabled={actionLoading}
            className="flex items-center gap-1.5 rounded-lg bg-status-failed/20 px-3 py-1.5 text-sm text-status-failed hover:bg-status-failed/30 disabled:opacity-50"
          >
            {actionLoading ? <Loader className="h-3.5 w-3.5 animate-spin" /> : <Square className="h-3.5 w-3.5" />} 终止
          </button>
        </div>
      </div>

      {/* 工作流信息卡片 */}
      <div className="grid grid-cols-3 gap-4 p-6 pb-0">
        <div className="rounded-xl border border-base-700 bg-base-800 p-4">
          <h3 className="mb-3 text-xs font-medium uppercase tracking-wider text-slate-400">基本信息</h3>
          <dl className="space-y-2 text-sm">
            <div className="flex justify-between">
              <dt className="text-slate-500">工作流ID</dt>
              <dd className="font-mono text-slate-300">{workflow.workflowId}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-slate-500">运行ID</dt>
              <dd className="font-mono text-xs text-slate-400">{workflow.runId.slice(0, 16)}...</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-slate-500">工作流类型</dt>
              <dd className="text-slate-300">{workflow.workflowType}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-slate-500">任务队列</dt>
              <dd className="text-slate-300">{workflow.taskQueue}</dd>
            </div>
            <div className="flex justify-between">
              <dt className="text-slate-500">开始时间</dt>
              <dd className="text-slate-300">{formatTime(workflow.startTime)}</dd>
            </div>
            {workflow.closeTime && (
              <div className="flex justify-between">
                <dt className="text-slate-500">结束时间</dt>
                <dd className="text-slate-300">{formatTime(workflow.closeTime)}</dd>
              </div>
            )}
          </dl>
        </div>

        <div className="rounded-xl border border-base-700 bg-base-800 p-4">
          <h3 className="mb-3 text-xs font-medium uppercase tracking-wider text-slate-400">执行统计</h3>
          <div className="grid grid-cols-2 gap-3">
            <div className="rounded-lg bg-base-700/50 p-3">
              <div className="flex items-center gap-1.5 text-xs text-slate-400">
                <Clock className="h-3 w-3" /> 持续时间
              </div>
              <p className="mt-1 text-lg font-semibold text-slate-100">{formatDuration(workflow.duration)}</p>
            </div>
            <div className="rounded-lg bg-base-700/50 p-3">
              <div className="flex items-center gap-1.5 text-xs text-slate-400">
                <ActivityIcon className="h-3 w-3" /> 活动总数
              </div>
              <p className="mt-1 text-lg font-semibold text-slate-100">{workflow.activities.length}</p>
            </div>
            <div className="rounded-lg bg-base-700/50 p-3">
              <div className="flex items-center gap-1.5 text-xs text-slate-400">
                <Globe className="h-3 w-3" /> 第三方调用
              </div>
              <p className="mt-1 text-lg font-semibold text-accent-light">{thirdPartyCount}</p>
            </div>
            <div className="rounded-lg bg-base-700/50 p-3">
              <div className="flex items-center gap-1.5 text-xs text-slate-400">
                <RotateCcw className="h-3 w-3" /> 重试次数
              </div>
              <p className="mt-1 text-lg font-semibold text-status-timeout">{retryCount}</p>
            </div>
          </div>
        </div>

        <div className="rounded-xl border border-base-700 bg-base-800 p-4">
          <h3 className="mb-3 text-xs font-medium uppercase tracking-wider text-slate-400">搜索属性</h3>
          <dl className="space-y-2 text-sm">
            {Object.entries(workflow.searchAttributes).map(([key, value]) => (
              <div key={key} className="flex justify-between">
                <dt className="text-slate-500">{key}</dt>
                <dd className="text-slate-300">{String(value)}</dd>
              </div>
            ))}
          </dl>
        </div>
      </div>

      {/* 主内容区：时间线 + 活动详情 */}
      <div className="flex flex-1 gap-4 overflow-hidden p-6">
        {/* 左侧时间线 */}
        <div className="w-1/2 overflow-auto rounded-xl border border-base-700 bg-base-800 p-4">
          <h3 className="mb-4 text-sm font-semibold text-slate-100">执行时间线</h3>
          <div className="relative">
            {workflow.activities.length === 0 ? (
              <div className="flex items-center justify-center py-12 text-sm text-slate-500">
                暂无活动数据
              </div>
            ) : (
            workflow.activities.map((activity, index) => (
              <div key={activity.id} className="relative flex gap-3 pb-4">
                {/* 竖线 */}
                {index < workflow.activities.length - 1 && (
                  <div className="absolute left-[7px] top-5 h-full w-px bg-base-600" />
                )}
                {/* 节点 */}
                <button
                  onClick={() => handleActivityClick(activity)}
                  className={cn(
                    'mt-1 h-3.5 w-3.5 flex-shrink-0 rounded-full border-2 border-base-800 transition-transform hover:scale-125',
                    statusDotColors[activity.status] || 'bg-slate-500',
                    selectedActivityId === activity.id && 'ring-2 ring-accent ring-offset-2 ring-offset-base-800'
                  )}
                />
                {/* 内容 */}
                <button
                  onClick={() => handleActivityClick(activity)}
                  className={cn(
                    'flex-1 rounded-lg p-2 text-left transition-colors',
                    selectedActivityId === activity.id ? 'bg-base-700' : 'hover:bg-base-700/50'
                  )}
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className="text-sm font-medium text-slate-200">{activity.activityType}</span>
                      {activity.isThirdParty && (
                        <span className="flex items-center gap-1 rounded bg-accent/20 px-1.5 py-0.5 text-xs text-accent-light">
                          <Globe className="h-2.5 w-2.5" /> 第三方
                        </span>
                      )}
                      {activity.attempt > 1 && (
                        <span className="rounded bg-status-timeout/20 px-1.5 py-0.5 text-xs text-status-timeout">
                          重试 {activity.attempt}次
                        </span>
                      )}
                    </div>
                    <span className="text-xs text-slate-500">{formatDuration(activity.duration)}</span>
                  </div>
                  <p className="mt-1 text-xs text-slate-500">{formatTime(activity.startTime)}</p>
                </button>
              </div>
            ))
            )}
          </div>
        </div>

        {/* 右侧活动详情 */}
        <div className="flex-1 overflow-auto rounded-xl border border-base-700 bg-base-800 p-4">
          {!selectedActivity ? (
            <div className="flex h-full items-center justify-center">
              <div className="text-center">
                <ActivityIcon className="mx-auto h-10 w-10 text-slate-600" />
                <p className="mt-3 text-sm text-slate-500">点击时间线节点查看详情</p>
              </div>
            </div>
          ) : (
            <div className="slide-up">
              {/* 活动标题 */}
              <div className="mb-4 flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <h3 className="text-sm font-semibold text-slate-100">{selectedActivity.activityType}</h3>
                  <StatusBadge status={selectedActivity.status} size="sm" />
                </div>
                <span className="text-xs text-slate-500">
                  {formatTime(selectedActivity.startTime)} · {formatDuration(selectedActivity.duration)}
                </span>
              </div>

              {/* 标签导航 */}
              <div className="mb-4 flex gap-4 border-b border-base-700">
                <button
                  onClick={() => setActiveTab('io')}
                  className={cn(
                    'pb-2 text-sm transition-colors',
                    activeTab === 'io'
                      ? 'border-b-2 border-accent text-accent'
                      : 'text-slate-400 hover:text-slate-200'
                  )}
                >
                  输入输出
                </button>
                {selectedActivity.isThirdParty && (
                  <button
                    onClick={() => setActiveTab('thirdparty')}
                    className={cn(
                      'pb-2 text-sm transition-colors',
                      activeTab === 'thirdparty'
                        ? 'border-b-2 border-accent text-accent'
                        : 'text-slate-400 hover:text-slate-200'
                    )}
                  >
                    第三方交互
                  </button>
                )}
                <button
                  onClick={() => setActiveTab('logs')}
                  className={cn(
                    'pb-2 text-sm transition-colors',
                    activeTab === 'logs'
                      ? 'border-b-2 border-accent text-accent'
                      : 'text-slate-400 hover:text-slate-200'
                  )}
                >
                  日志
                </button>
              </div>

              {/* 标签内容 */}
              {activeTab === 'io' && (
                <div className="space-y-4">
                  <div>
                    <h4 className="mb-2 text-xs font-medium uppercase tracking-wider text-slate-400">输入数据</h4>
                    <div className="rounded-lg bg-base-900 p-3">
                      <JsonViewer data={selectedActivity.input} maxHeight="200px" />
                    </div>
                  </div>
                  <div>
                    <h4 className="mb-2 text-xs font-medium uppercase tracking-wider text-slate-400">输出结果</h4>
                    <div className="rounded-lg bg-base-900 p-3">
                      {Object.keys(selectedActivity.output).length > 0 ? (
                        <JsonViewer data={selectedActivity.output} maxHeight="200px" />
                      ) : (
                        <p className="text-sm text-slate-500">暂无输出数据</p>
                      )}
                    </div>
                  </div>
                </div>
              )}

              {activeTab === 'thirdparty' && selectedActivity.thirdPartyInfo && (
                <div className="space-y-3">
                  {/* 接口信息 */}
                  <div className="rounded-lg bg-base-900 p-3">
                    <div className="mb-2 flex items-center gap-2">
                      <span className={cn('rounded px-2 py-0.5 text-xs font-bold', methodColors[selectedActivity.thirdPartyInfo.method] || 'bg-slate-500/20 text-slate-400')}>
                        {selectedActivity.thirdPartyInfo.method}
                      </span>
                      <code className="flex-1 text-sm text-slate-300">{selectedActivity.thirdPartyInfo.endpoint}</code>
                    </div>
                    <div className="flex gap-4 text-xs">
                      <span className="text-slate-400">
                        状态码: <span className={selectedActivity.thirdPartyInfo.statusCode < 400 ? 'text-status-completed' : 'text-status-failed'}>
                          {selectedActivity.thirdPartyInfo.statusCode}
                        </span>
                      </span>
                      <span className="text-slate-400">
                        响应时间: <span className="text-slate-200">{selectedActivity.thirdPartyInfo.responseTime}ms</span>
                      </span>
                    </div>
                  </div>

                  {/* 请求体 */}
                  <div>
                    <h4 className="mb-2 text-xs font-medium uppercase tracking-wider text-slate-400">请求体</h4>
                    <div className="rounded-lg bg-base-900 p-3">
                      <JsonViewer data={selectedActivity.thirdPartyInfo.requestBody} maxHeight="150px" />
                    </div>
                  </div>

                  {/* 响应体 */}
                  <div>
                    <h4 className="mb-2 text-xs font-medium uppercase tracking-wider text-slate-400">响应体</h4>
                    <div className="rounded-lg bg-base-900 p-3">
                      <JsonViewer data={selectedActivity.thirdPartyInfo.responseBody} maxHeight="150px" />
                    </div>
                  </div>
                </div>
              )}

              {activeTab === 'logs' && (
                <div className="space-y-1 font-mono text-xs">
                  <div className="flex gap-2 rounded bg-base-900 px-2 py-1">
                    <span className="text-slate-500">{formatTime(selectedActivity.startTime)}</span>
                    <span className="text-slate-400">[INFO]</span>
                    <span className="text-slate-300">活动 {selectedActivity.activityType} 开始执行</span>
                  </div>
                  {selectedActivity.attempt > 1 && (
                    <div className="flex gap-2 rounded bg-status-timeout/10 px-2 py-1">
                      <span className="text-slate-500">{formatTime(selectedActivity.startTime)}</span>
                      <span className="text-status-timeout">[WARN]</span>
                      <span className="text-slate-300">第 {selectedActivity.attempt} 次重试</span>
                    </div>
                  )}
                  {selectedActivity.isThirdParty && selectedActivity.thirdPartyInfo && (
                    <>
                      <div className="flex gap-2 rounded bg-base-900 px-2 py-1">
                        <span className="text-slate-500">{formatTime(selectedActivity.startTime)}</span>
                        <span className="text-blue-400">[HTTP]</span>
                        <span className="text-slate-300">
                          {selectedActivity.thirdPartyInfo.method} {selectedActivity.thirdPartyInfo.endpoint}
                        </span>
                      </div>
                      <div className="flex gap-2 rounded bg-base-900 px-2 py-1">
                        <span className="text-slate-500">{formatTime(selectedActivity.startTime)}</span>
                        <span className="text-blue-400">[HTTP]</span>
                        <span className={selectedActivity.thirdPartyInfo.statusCode < 400 ? 'text-status-completed' : 'text-status-failed'}>
                          响应 {selectedActivity.thirdPartyInfo.statusCode} ({selectedActivity.thirdPartyInfo.responseTime}ms)
                        </span>
                      </div>
                    </>
                  )}
                  {selectedActivity.status === 'completed' && (
                    <div className="flex gap-2 rounded bg-status-completed/10 px-2 py-1">
                      <span className="text-slate-500">{formatTime(selectedActivity.endTime)}</span>
                      <span className="text-status-completed">[INFO]</span>
                      <span className="text-slate-300">活动执行完成</span>
                    </div>
                  )}
                  {selectedActivity.status === 'failed' && (
                    <div className="flex gap-2 rounded bg-status-failed/10 px-2 py-1">
                      <span className="text-slate-500">{formatTime(selectedActivity.endTime)}</span>
                      <span className="text-status-failed">[ERROR]</span>
                      <span className="text-slate-300">活动执行失败</span>
                    </div>
                  )}
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* 底部输入输出折叠区 */}
      <div className="border-t border-base-700 bg-base-800">
        <button
          onClick={() => setShowInput(!showInput)}
          className="flex w-full items-center justify-between px-6 py-2 text-sm text-slate-300 hover:bg-base-700/50"
        >
          <span className="flex items-center gap-2">
            {showInput ? <ChevronDown className="h-4 w-4" /> : <ChevronRight className="h-4 w-4" />}
            工作流输入
          </span>
          <Zap className="h-3.5 w-3.5 text-slate-500" />
        </button>
        {showInput && (
          <div className="border-t border-base-700 px-6 py-3">
            <JsonViewer data={workflow.input} maxHeight="200px" />
          </div>
        )}

        {Object.keys(workflow.result).length > 0 && (
          <>
            <button
              onClick={() => setShowResult(!showResult)}
              className="flex w-full items-center justify-between px-6 py-2 text-sm text-slate-300 hover:bg-base-700/50"
            >
              <span className="flex items-center gap-2">
                {showResult ? <ChevronDown className="h-4 w-4" /> : <ChevronRight className="h-4 w-4" />}
                工作流结果
              </span>
              <Terminal className="h-3.5 w-3.5 text-slate-500" />
            </button>
            {showResult && (
              <div className="border-t border-base-700 px-6 py-3">
                <JsonViewer data={workflow.result} maxHeight="200px" />
              </div>
            )}
          </>
        )}
      </div>

      {/* 发送信号 Modal */}
      {signalModalOpen && workflow && (
        <SignalModal
          workflowId={workflow.workflowId}
          runId={workflow.runId}
          onClose={() => setSignalModalOpen(false)}
          onSuccess={handleSignalSuccess}
          onError={handleSignalError}
        />
      )}

      {/* 发送更新 Modal */}
      {updateModalOpen && workflow && (
        <UpdateModal
          workflowId={workflow.workflowId}
          runId={workflow.runId}
          onClose={() => setUpdateModalOpen(false)}
          onSuccess={handleUpdateSuccess}
          onError={handleUpdateError}
        />
      )}
    </div>
  )
}

/**
 * 发送信号 Modal
 */
function SignalModal({
  workflowId,
  runId,
  onClose,
  onSuccess,
  onError,
}: {
  workflowId: string
  runId: string
  onClose: () => void
  onSuccess: (signalName: string) => void
  onError: (msg: string) => void
}) {
  const [signalName, setSignalName] = useState('')
  const [input, setInput] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)

  const handleSubmit = async () => {
    if (!signalName.trim()) {
      setFormError('请填写信号名称')
      return
    }
    if (input.trim()) {
      try { JSON.parse(input) } catch {
        setFormError('输入参数必须是合法 JSON')
        return
      }
    }
    setFormError(null)
    setSubmitting(true)
    try {
      await signalWorkflow(workflowId, runId, signalName.trim(), input.trim() || undefined)
      onSuccess(signalName.trim())
    } catch (e) {
      const msg = (e as Error).message || '未知错误'
      setFormError(msg)
      onError(`发送信号失败: ${msg}`)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="w-full max-w-md rounded-xl border border-base-600 bg-base-800 p-5 shadow-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="flex items-center gap-2 text-base font-semibold text-slate-100">
            <Signal className="h-4 w-4 text-accent" />
            发送信号
          </h3>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg p-1 text-slate-400 hover:bg-base-700 hover:text-slate-200"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
        <p className="mb-4 text-xs text-slate-400">
          向工作流 <span className="font-mono text-slate-300">{workflowId.slice(0, 16)}...</span> 发送 Signal。
          信号会立即触发工作流中对应的 signal handler。
        </p>
        <div className="space-y-4">
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-300">
              信号名称 <span className="text-status-failed">*</span>
            </label>
            <input
              type="text"
              value={signalName}
              onChange={(e) => { setSignalName(e.target.value); setFormError(null) }}
              placeholder="例如：cancelSync、updateConfig"
              className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 font-mono text-sm text-slate-100 placeholder-slate-500 outline-none focus:border-accent"
            />
          </div>
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-300">输入参数（JSON，可选）</label>
            <textarea
              value={input}
              onChange={(e) => { setInput(e.target.value); setFormError(null) }}
              placeholder='例如：{"reason": "manual"}'
              rows={3}
              className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 font-mono text-sm text-slate-100 placeholder-slate-500 outline-none focus:border-accent"
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
            {submitting ? <Loader className="h-3.5 w-3.5 animate-spin" /> : <Send className="h-3.5 w-3.5" />}
            {submitting ? '发送中...' : '发送'}
          </button>
        </div>
      </div>
    </div>
  )
}

/**
 * 发送更新 Modal
 */
function UpdateModal({
  workflowId,
  runId,
  onClose,
  onSuccess,
  onError,
}: {
  workflowId: string
  runId: string
  onClose: () => void
  onSuccess: (updateType: string) => void
  onError: (msg: string) => void
}) {
  const [updateType, setUpdateType] = useState('')
  const [input, setInput] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)

  const handleSubmit = async () => {
    if (!updateType.trim()) {
      setFormError('请填写更新类型')
      return
    }
    if (input.trim()) {
      try { JSON.parse(input) } catch {
        setFormError('输入参数必须是合法 JSON')
        return
      }
    }
    setFormError(null)
    setSubmitting(true)
    try {
      await updateWorkflow(workflowId, runId, updateType.trim(), input.trim() || undefined)
      onSuccess(updateType.trim())
    } catch (e) {
      const msg = (e as Error).message || '未知错误'
      setFormError(msg)
      onError(`发送更新失败: ${msg}`)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="w-full max-w-md rounded-xl border border-base-600 bg-base-800 p-5 shadow-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="flex items-center gap-2 text-base font-semibold text-slate-100">
            <Send className="h-4 w-4 text-accent" />
            发送更新
          </h3>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg p-1 text-slate-400 hover:bg-base-700 hover:text-slate-200"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
        <p className="mb-4 text-xs text-slate-400">
          向工作流 <span className="font-mono text-slate-300">{workflowId.slice(0, 16)}...</span> 发送 Update。
          更新会等待工作流的 update handler 完成后返回结果。
        </p>
        <div className="space-y-4">
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-300">
              更新类型 <span className="text-status-failed">*</span>
            </label>
            <input
              type="text"
              value={updateType}
              onChange={(e) => { setUpdateType(e.target.value); setFormError(null) }}
              placeholder="例如：updatePriority、pauseActivity"
              className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 font-mono text-sm text-slate-100 placeholder-slate-500 outline-none focus:border-accent"
            />
          </div>
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-300">输入参数（JSON，可选）</label>
            <textarea
              value={input}
              onChange={(e) => { setInput(e.target.value); setFormError(null) }}
              placeholder='例如：{"priority": 5}'
              rows={3}
              className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 font-mono text-sm text-slate-100 placeholder-slate-500 outline-none focus:border-accent"
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
            {submitting ? <Loader className="h-3.5 w-3.5 animate-spin" /> : <Send className="h-3.5 w-3.5" />}
            {submitting ? '提交中...' : '提交'}
          </button>
        </div>
      </div>
    </div>
  )
}
