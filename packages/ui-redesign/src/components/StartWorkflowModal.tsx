import { useState } from 'react'
import { Play, Dices, ChevronDown, ChevronRight, Plus, X, CheckCircle, Loader } from 'lucide-react'
import { cn } from '@/lib/utils'
import { startWorkflow } from '@/lib/api/workflows'

const workflowTypes = [
  'DemoWorkflow',
  'HikSyncWorkflow',
]

const taskQueues = [
  'DEMO_TASK_QUEUE',
  'HIK_SYNC_TASK_QUEUE',
]

const delayUnits = [
  { label: '秒', value: 'seconds' },
  { label: '分', value: 'minutes' },
  { label: '时', value: 'hours' },
]

interface SearchAttribute { key: string; value: string }

const inputClass = 'w-full rounded-lg border border-base-600 bg-base-700 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-500 focus:border-accent focus:outline-none'
const labelClass = 'text-sm text-slate-400 mb-1 block'

function generateUUID(): string {
  if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) return crypto.randomUUID()
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0
    return (c === 'x' ? r : (r & 0x3) | 0x8).toString(16)
  })
}

export default function StartWorkflowModal({ onClose }: { onClose: () => void }) {
  const [workflowId, setWorkflowId] = useState('')
  const [workflowType, setWorkflowType] = useState(workflowTypes[0])
  const [taskQueue, setTaskQueue] = useState(taskQueues[0])
  const [jsonInput, setJsonInput] = useState('')
  const [searchAttributes, setSearchAttributes] = useState<SearchAttribute[]>([
    { key: '', value: '' },
    { key: '', value: '' },
  ])
  const [delayValue, setDelayValue] = useState('')
  const [delayUnit, setDelayUnit] = useState('seconds')
  const [userMemo, setUserMemo] = useState('')
  const [showAdvanced, setShowAdvanced] = useState(false)
  const [showSuccess, setShowSuccess] = useState(false)
  const [jsonError, setJsonError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')
  const [runId, setRunId] = useState('')

  const validateJSON = (value: string): boolean => {
    if (value.trim() === '') { setJsonError(''); return true }
    try { JSON.parse(value); setJsonError(''); return true }
    catch (err) { setJsonError('JSON 格式无效：' + (err as Error).message); return false }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!validateJSON(jsonInput)) return
    setSubmitting(true)
    setErrorMsg('')
    try {
      const result = await startWorkflow({
        workflowId: workflowId || generateUUID(),
        workflowType,
        taskQueue,
        input: jsonInput.trim() || undefined,
      })
      setRunId(result.runId)
      setShowSuccess(true)
      setTimeout(() => { setShowSuccess(false); onClose() }, 2000)
    } catch (err) {
      setErrorMsg((err as Error).message || '启动失败')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto bg-black/50 p-4 pt-12" onClick={onClose}>
      <div className="w-full max-w-2xl rounded-xl border border-base-700 bg-base-800 shadow-2xl" onClick={(e) => e.stopPropagation()}>
        {/* Modal header */}
        <div className="flex items-center justify-between border-b border-base-700 px-5 py-3">
          <h2 className="text-base font-semibold text-slate-100">启动工作流</h2>
          <button onClick={onClose} className="rounded-lg p-1 text-slate-400 hover:bg-base-700 hover:text-slate-200">
            <X className="h-4 w-4" />
          </button>
        </div>

        {showSuccess && (
          <div className="mx-5 mt-4 flex items-center gap-2 rounded-lg border border-status-completed/30 bg-status-completed/10 px-4 py-2.5">
            <CheckCircle className="h-4 w-4 text-status-completed" />
            <span className="text-sm text-slate-200">工作流已启动，RunID：<span className="font-mono text-status-completed">{runId}</span></span>
          </div>
        )}

        {errorMsg && (
          <div className="mx-5 mt-4 flex items-center gap-2 rounded-lg border border-status-failed/30 bg-status-failed/10 px-4 py-2.5">
            <X className="h-4 w-4 text-status-failed" />
            <span className="text-sm text-slate-200">启动失败：<span className="font-mono text-status-failed">{errorMsg}</span></span>
          </div>
        )}

        {/* Form */}
        <form onSubmit={handleSubmit} className="max-h-[70vh] space-y-4 overflow-y-auto p-5">
          {/* 基本信息 */}
          <div className="rounded-lg border border-base-700 bg-base-700/30 p-4">
            <h3 className="mb-3 text-sm font-semibold text-slate-200">基本信息</h3>
            <div className="space-y-3">
              <div>
                <label className={labelClass}>工作流ID</label>
                <div className="flex gap-2">
                  <input type="text" value={workflowId} onChange={(e) => setWorkflowId(e.target.value)}
                    placeholder="输入工作流ID或随机生成" className={inputClass} />
                  <button type="button" onClick={() => setWorkflowId(generateUUID())} title="随机生成"
                    className="flex flex-shrink-0 items-center gap-1.5 rounded-lg border border-base-600 bg-base-700 px-3 py-2 text-sm text-slate-300 hover:bg-base-600">
                    <Dices className="h-4 w-4" /> 随机生成
                  </button>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className={labelClass}>工作流类型</label>
                  <select value={workflowType} onChange={(e) => setWorkflowType(e.target.value)} className={inputClass}>
                    {workflowTypes.map((t) => <option key={t} value={t} className="bg-base-800">{t}</option>)}
                  </select>
                </div>
                <div>
                  <label className={labelClass}>任务队列</label>
                  <select value={taskQueue} onChange={(e) => setTaskQueue(e.target.value)} className={inputClass}>
                    {taskQueues.map((q) => <option key={q} value={q} className="bg-base-800">{q}</option>)}
                  </select>
                </div>
              </div>
            </div>
          </div>

          {/* 输入参数 */}
          <div className="rounded-lg border border-base-700 bg-base-700/30 p-4">
            <div className="mb-2 flex items-center justify-between">
              <h3 className="text-sm font-semibold text-slate-200">输入参数</h3>
              <span className="rounded bg-accent/20 px-2 py-0.5 text-xs font-medium text-accent-light">JSON</span>
            </div>
            <textarea value={jsonInput} onChange={(e) => { setJsonInput(e.target.value); validateJSON(e.target.value) }}
              placeholder={'{\n  "orderId": "ORD-20260811-91734",\n  "userId": "U100238",\n  "amount": 598.00\n}'}
              rows={6} className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 font-mono text-sm text-slate-100 placeholder:text-slate-500 focus:border-accent focus:outline-none" />
            {jsonError && <p className="mt-2 text-sm text-status-failed">{jsonError}</p>}
          </div>

          {/* 高级选项 */}
          <div className="rounded-lg border border-base-700 bg-base-700/30 p-4">
            <button type="button" onClick={() => setShowAdvanced(!showAdvanced)}
              className="flex w-full items-center gap-2 text-sm font-semibold text-slate-200">
              {showAdvanced ? <ChevronDown className="h-4 w-4" /> : <ChevronRight className="h-4 w-4" />}
              高级选项
            </button>
            {showAdvanced && (
              <div className="mt-3 space-y-3">
                <div>
                  <label className={labelClass}>搜索属性</label>
                  <div className="space-y-2">
                    {searchAttributes.map((attr, index) => (
                      <div key={index} className="flex items-center gap-2">
                        <input type="text" value={attr.key} onChange={(e) => setSearchAttributes((prev) => prev.map((item, i) => i === index ? { ...item, key: e.target.value } : item))}
                          placeholder="键" className={inputClass} />
                        <input type="text" value={attr.value} onChange={(e) => setSearchAttributes((prev) => prev.map((item, i) => i === index ? { ...item, value: e.target.value } : item))}
                          placeholder="值" className={inputClass} />
                        <button type="button" onClick={() => setSearchAttributes((prev) => prev.filter((_, i) => i !== index))}
                          className="flex flex-shrink-0 rounded-lg p-2 text-slate-400 hover:bg-base-700 hover:text-status-failed">
                          <X className="h-4 w-4" />
                        </button>
                      </div>
                    ))}
                    <button type="button" onClick={() => setSearchAttributes((prev) => [...prev, { key: '', value: '' }])}
                      className="flex items-center gap-1.5 rounded-lg border border-dashed border-base-600 px-3 py-1.5 text-sm text-slate-400 hover:border-accent hover:text-accent">
                      <Plus className="h-4 w-4" /> 添加
                    </button>
                  </div>
                </div>
                <div>
                  <label className={labelClass}>启动延迟</label>
                  <div className="flex gap-2">
                    <input type="number" value={delayValue} onChange={(e) => setDelayValue(e.target.value)} min="0" placeholder="0" className={inputClass} />
                    <select value={delayUnit} onChange={(e) => setDelayUnit(e.target.value)} className={cn(inputClass, 'w-24 flex-shrink-0')}>
                      {delayUnits.map((u) => <option key={u.value} value={u.value} className="bg-base-800">{u.label}</option>)}
                    </select>
                  </div>
                </div>
                <div>
                  <label className={labelClass}>用户备注</label>
                  <input type="text" value={userMemo} onChange={(e) => setUserMemo(e.target.value)} placeholder="用于标识此工作流运行的备注信息" className={inputClass} />
                </div>
              </div>
            )}
          </div>

          {/* 操作按钮 */}
          <div className="flex items-center justify-end gap-3 pt-2">
            <button type="button" onClick={onClose} className="rounded-lg bg-base-700 px-4 py-2 text-sm font-medium text-slate-300 hover:bg-base-600">取消</button>
            <button type="submit" disabled={submitting} className="flex items-center gap-2 rounded-lg bg-accent px-4 py-2 text-sm font-medium text-white hover:bg-accent-dark disabled:opacity-50 disabled:cursor-not-allowed">
              {submitting ? <Loader className="h-4 w-4 animate-spin" /> : <Play className="h-4 w-4" />}
              {submitting ? '启动中...' : '启动'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
