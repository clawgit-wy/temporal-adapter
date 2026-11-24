import { useState, useRef, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { Search, Play, MoreVertical, Square, Ban, RotateCcw, X, Loader, RefreshCw } from 'lucide-react'
import { fetchWorkflows, terminateWorkflow, cancelWorkflow, type FrontendWorkflow } from '@/lib/api/workflows'
import { useApi } from '@/hooks/useApi'
import StatusBadge from '@/components/StatusBadge'
import StartWorkflowModal from '@/components/StartWorkflowModal'
import type { WorkflowStatus } from '@/lib/types'
import { cn, formatTime, formatDuration } from '@/lib/utils'

type FilterStatus = 'all' | WorkflowStatus

const filterTabs: { label: string; value: FilterStatus }[] = [
  { label: '全部', value: 'all' },
  { label: '运行中', value: 'running' },
  { label: '已完成', value: 'completed' },
  { label: '失败', value: 'failed' },
  { label: '超时', value: 'timed_out' },
  { label: '已取消', value: 'canceled' },
]

export default function WorkflowList() {
  const [activeFilter, setActiveFilter] = useState<FilterStatus>('all')
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedIds, setSelectedIds] = useState<string[]>([])
  const [openMenuId, setOpenMenuId] = useState<string | null>(null)
  const [showStartModal, setShowStartModal] = useState(false)
  const [actionMsg, setActionMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null)
  const selectAllRef = useRef<HTMLInputElement>(null)

  const { data: apiWorkflows, loading, refetch } = useApi(() => fetchWorkflows())

  const workflows = apiWorkflows ?? []

  const handleTerminate = async (wf: FrontendWorkflow) => {
    setOpenMenuId(null)
    try {
      await terminateWorkflow(wf.workflowId, wf.runId, '用户手动终止')
      setActionMsg({ type: 'success', text: `工作流 ${wf.workflowId} 已终止` })
      setTimeout(() => setActionMsg(null), 3000)
      refetch()
    } catch (err) {
      setActionMsg({ type: 'error', text: `终止失败: ${(err as Error).message}` })
      setTimeout(() => setActionMsg(null), 3000)
    }
  }

  const handleCancel = async (wf: FrontendWorkflow) => {
    setOpenMenuId(null)
    try {
      await cancelWorkflow(wf.workflowId, wf.runId)
      setActionMsg({ type: 'success', text: `工作流 ${wf.workflowId} 取消请求已发送` })
      setTimeout(() => setActionMsg(null), 3000)
      refetch()
    } catch (err) {
      setActionMsg({ type: 'error', text: `取消失败: ${(err as Error).message}` })
      setTimeout(() => setActionMsg(null), 3000)
    }
  }

  const handleReset = (wf: FrontendWorkflow) => {
    setOpenMenuId(null)
    setActionMsg({ type: 'success', text: `重置功能暂未接入 Temporal API` })
    setTimeout(() => setActionMsg(null), 3000)
  }

  const filteredWorkflows = workflows.filter((wf) => {
    const matchesStatus = activeFilter === 'all' || wf.status === activeFilter
    const query = searchQuery.trim().toLowerCase()
    const matchesSearch =
      query === '' ||
      wf.workflowId.toLowerCase().includes(query) ||
      wf.name.toLowerCase().includes(query)
    return matchesStatus && matchesSearch
  })

  const allFilteredSelected =
    filteredWorkflows.length > 0 &&
    filteredWorkflows.every((wf) => selectedIds.includes(wf.id))
  const someFilteredSelected = filteredWorkflows.some((wf) =>
    selectedIds.includes(wf.id),
  )

  useEffect(() => {
    if (selectAllRef.current) {
      selectAllRef.current.indeterminate =
        someFilteredSelected && !allFilteredSelected
    }
  }, [someFilteredSelected, allFilteredSelected])

  const toggleSelect = (id: string) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id],
    )
  }

  const toggleSelectAll = () => {
    if (allFilteredSelected) {
      setSelectedIds((prev) =>
        prev.filter((id) => !filteredWorkflows.some((wf) => wf.id === id)),
      )
    } else {
      setSelectedIds((prev) => [
        ...new Set([...prev, ...filteredWorkflows.map((wf) => wf.id)]),
      ])
    }
  }

  const clearSelection = () => setSelectedIds([])

  return (
    <div className="space-y-6 p-6">
      {actionMsg && (
        <div className={cn(
          'flex items-center gap-2 rounded-lg border px-4 py-3',
          actionMsg.type === 'success'
            ? 'border-status-completed/30 bg-status-completed/10'
            : 'border-status-failed/30 bg-status-failed/10',
        )}>
          <span className="text-sm text-slate-200">{actionMsg.text}</span>
        </div>
      )}
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <h1 className="text-2xl font-bold text-slate-100">工作流列表</h1>
          <span className="rounded-full bg-base-700 px-2.5 py-0.5 text-sm font-medium text-slate-300">
            {filteredWorkflows.length}
          </span>
          {loading && (
            <Loader className="h-4 w-4 animate-spin text-slate-400" />
          )}
        </div>
        <div className="flex items-center gap-2">
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
            onClick={() => setShowStartModal(true)}
            className="flex items-center gap-2 rounded-lg bg-accent px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-accent-dark"
          >
            <Play className="h-4 w-4" />
            启动工作流
          </button>
        </div>
      </div>

      {/* Filter bar */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-1">
          {filterTabs.map((tab) => (
            <button
              key={tab.value}
              type="button"
              onClick={() => setActiveFilter(tab.value)}
              className={cn(
                'rounded-lg px-3 py-1.5 text-sm transition-colors',
                activeFilter === tab.value
                  ? 'bg-accent text-white'
                  : 'text-slate-400 hover:bg-base-700',
              )}
            >
              {tab.label}
            </button>
          ))}
        </div>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="搜索工作流ID或名称..."
            className="w-72 rounded-lg border border-base-600 bg-base-700 py-2 pl-9 pr-3 text-sm text-slate-200 placeholder:text-slate-500 focus:border-accent focus:outline-none"
          />
        </div>
      </div>

      {/* Workflow table */}
      <div className="overflow-hidden rounded-xl border border-base-700 bg-base-800">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-base-700">
                <th className="w-12 px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">
                  <input
                    ref={selectAllRef}
                    type="checkbox"
                    checked={allFilteredSelected}
                    onChange={toggleSelectAll}
                    className="h-4 w-4 cursor-pointer rounded border-base-600 bg-base-900 text-accent focus:ring-accent"
                  />
                </th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">工作流名称</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">工作流ID</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">状态</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">工作流类型</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">任务队列</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">开始时间</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-slate-400">持续时间</th>
                <th className="w-20 px-4 py-3 text-right text-xs font-medium uppercase text-slate-400">操作</th>
              </tr>
            </thead>
            <tbody>
              {filteredWorkflows.map((wf) => {
                const isSelected = selectedIds.includes(wf.id)
                return (
                  <tr
                    key={wf.id}
                    className={cn(
                      'border-b border-base-700 transition-colors hover:bg-base-700/50',
                      isSelected ? 'bg-accent/10' : '',
                    )}
                  >
                    <td className="px-4 py-3">
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => toggleSelect(wf.id)}
                        className="h-4 w-4 cursor-pointer rounded border-base-600 bg-base-900 text-accent focus:ring-accent"
                      />
                    </td>
                    <td className="px-4 py-3">
                      <Link
                        to={`/workflows/${wf.id}`}
                        className="text-sm font-medium text-slate-200 hover:text-accent"
                      >
                        {wf.name}
                      </Link>
                    </td>
                    <td className="px-4 py-3">
                      <span className="font-mono text-xs text-slate-400">{wf.workflowId}</span>
                    </td>
                    <td className="px-4 py-3">
                      <StatusBadge status={wf.status} size="sm" />
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-300">{wf.workflowType}</td>
                    <td className="px-4 py-3 text-sm text-slate-300">{wf.taskQueue}</td>
                    <td className="px-4 py-3">
                      <span className="font-mono text-xs text-slate-400">{formatTime(wf.startTime)}</span>
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-300">{formatDuration(wf.duration)}</td>
                    <td className="px-4 py-3 text-right">
                      <div className="relative inline-block">
                        <button
                          type="button"
                          onClick={() => setOpenMenuId(openMenuId === wf.id ? null : wf.id)}
                          className="rounded-lg p-1.5 text-slate-400 hover:bg-base-700 hover:text-slate-200"
                        >
                          <MoreVertical className="h-4 w-4" />
                        </button>
                        {openMenuId === wf.id && (
                          <>
                            <div
                              className="fixed inset-0 z-10"
                              onClick={() => setOpenMenuId(null)}
                            />
                            <div className="absolute right-0 top-full z-20 mt-1 w-32 rounded-lg border border-base-600 bg-base-800 py-1 shadow-xl">
                              <button
                                type="button"
                                onClick={() => handleTerminate(wf)}
                                className="flex w-full items-center gap-2 px-3 py-2 text-sm text-slate-300 hover:bg-base-700"
                              >
                                <Square className="h-4 w-4" />
                                终止
                              </button>
                              <button
                                type="button"
                                onClick={() => handleCancel(wf)}
                                className="flex w-full items-center gap-2 px-3 py-2 text-sm text-slate-300 hover:bg-base-700"
                              >
                                <Ban className="h-4 w-4" />
                                取消
                              </button>
                              <button
                                type="button"
                                onClick={() => handleReset(wf)}
                                className="flex w-full items-center gap-2 px-3 py-2 text-sm text-slate-300 hover:bg-base-700"
                              >
                                <RotateCcw className="h-4 w-4" />
                                重置
                              </button>
                            </div>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                )
              })}
              {filteredWorkflows.length === 0 && (
                <tr>
                  <td colSpan={9} className="px-4 py-12 text-center text-sm text-slate-500">
                    没有匹配的工作流
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Batch operation bar */}
      {selectedIds.length > 0 && (
        <div className="slide-up fixed bottom-6 left-1/2 z-50 -translate-x-1/2">
          <div className="flex items-center gap-3 rounded-xl border border-base-600 bg-base-800 px-4 py-3 shadow-2xl">
            <span className="text-sm text-slate-300">
              已选 <span className="font-semibold text-accent">{selectedIds.length}</span> 项
            </span>
            <div className="h-5 w-px bg-base-600" />
            <button
              type="button"
              className="flex items-center gap-1.5 rounded-lg bg-base-700 px-3 py-1.5 text-sm text-slate-300 hover:bg-base-600"
            >
              <Square className="h-3.5 w-3.5" />
              批量终止
            </button>
            <button
              type="button"
              className="flex items-center gap-1.5 rounded-lg bg-base-700 px-3 py-1.5 text-sm text-slate-300 hover:bg-base-600"
            >
              <Ban className="h-3.5 w-3.5" />
              批量取消
            </button>
            <button
              type="button"
              className="flex items-center gap-1.5 rounded-lg bg-base-700 px-3 py-1.5 text-sm text-slate-300 hover:bg-base-600"
            >
              <RotateCcw className="h-3.5 w-3.5" />
              批量重置
            </button>
            <div className="h-5 w-px bg-base-600" />
            <button
              type="button"
              onClick={clearSelection}
              className="flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-sm text-slate-400 hover:bg-base-700 hover:text-slate-200"
            >
              <X className="h-3.5 w-3.5" />
              清除选择
            </button>
          </div>
        </div>
      )}

      {showStartModal && <StartWorkflowModal onClose={() => setShowStartModal(false)} />}
    </div>
  )
}
