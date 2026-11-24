import { useState } from 'react'
import { Link } from 'react-router-dom'
import {
  ArrowLeft,
  Play,
  Dices,
  ChevronDown,
  ChevronRight,
  Plus,
  X,
  CheckCircle,
} from 'lucide-react'
import { cn } from '@/lib/utils'

const workflowTypes = [
  'OrderProcessing',
  'PaymentCallback',
  'UserRegistration',
  'DataSync',
  'NotificationPush',
]

const taskQueues = [
  'order-processing-queue',
  'payment-queue',
  'user-service-queue',
  'notification-queue',
  'data-sync-queue',
]

const delayUnits = [
  { label: '秒', value: 'seconds' },
  { label: '分', value: 'minutes' },
  { label: '时', value: 'hours' },
]

interface SearchAttribute {
  key: string
  value: string
}

const inputClass =
  'w-full rounded-lg border border-base-600 bg-base-700 px-3 py-2 text-slate-100 placeholder:text-slate-500 focus:border-accent focus:outline-none'
const labelClass = 'text-sm text-slate-400 mb-1 block'
const cardClass = 'bg-base-800 border border-base-700 rounded-xl p-5'
const sectionTitleClass = 'text-sm font-semibold text-slate-200 mb-3'

function generateUUID(): string {
  if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) {
    return crypto.randomUUID()
  }
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

export default function StartWorkflow() {
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

  const handleGenerateId = () => {
    setWorkflowId(generateUUID())
  }

  const handleAddAttribute = () => {
    setSearchAttributes((prev) => [...prev, { key: '', value: '' }])
  }

  const handleRemoveAttribute = (index: number) => {
    setSearchAttributes((prev) => prev.filter((_, i) => i !== index))
  }

  const handleAttributeChange = (
    index: number,
    field: keyof SearchAttribute,
    value: string,
  ) => {
    setSearchAttributes((prev) =>
      prev.map((item, i) => (i === index ? { ...item, [field]: value } : item)),
    )
  }

  const validateJSON = (value: string): boolean => {
    if (value.trim() === '') {
      setJsonError('')
      return true
    }
    try {
      JSON.parse(value)
      setJsonError('')
      return true
    } catch (err) {
      setJsonError('JSON 格式无效：' + (err as Error).message)
      return false
    }
  }

  const handleJsonChange = (value: string) => {
    setJsonInput(value)
    validateJSON(value)
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!validateJSON(jsonInput)) {
      return
    }
    setShowSuccess(true)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  return (
    <div className="p-6">
      {/* Header */}
      <div className="mb-6 flex items-center gap-3">
        <Link
          to="/workflows"
          className="flex h-8 w-8 items-center justify-center rounded-lg text-slate-400 hover:bg-base-700 hover:text-slate-100"
        >
          <ArrowLeft className="h-4 w-4" />
        </Link>
        <h1 className="text-2xl font-bold text-slate-100">启动工作流</h1>
      </div>

      {/* Success alert */}
      {showSuccess && (
        <div className="mx-auto mb-6 max-w-2xl">
          <div className="flex items-center justify-between rounded-xl border border-status-completed/30 bg-status-completed/10 px-4 py-3">
            <div className="flex items-center gap-2">
              <CheckCircle className="h-5 w-5 text-status-completed" />
              <span className="text-sm text-slate-200">
                工作流已启动：
                <span className="font-mono text-status-completed">
                  {workflowId || '(未设置ID)'}
                </span>
              </span>
            </div>
            <button
              type="button"
              onClick={() => setShowSuccess(false)}
              className="rounded-lg p-1 text-slate-400 hover:bg-base-700 hover:text-slate-200"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        </div>
      )}

      {/* Form */}
      <form onSubmit={handleSubmit} className="mx-auto max-w-2xl space-y-6">
        {/* 基本信息 */}
        <div className={cardClass}>
          <h2 className={sectionTitleClass}>基本信息</h2>
          <div className="space-y-4">
            <div>
              <label className={labelClass}>工作流ID</label>
              <div className="flex gap-2">
                <input
                  type="text"
                  value={workflowId}
                  onChange={(e) => setWorkflowId(e.target.value)}
                  placeholder="输入工作流ID或随机生成"
                  className={inputClass}
                />
                <button
                  type="button"
                  onClick={handleGenerateId}
                  title="随机生成"
                  className="flex flex-shrink-0 items-center gap-1.5 rounded-lg border border-base-600 bg-base-700 px-3 py-2 text-sm text-slate-300 hover:bg-base-600 hover:text-slate-100"
                >
                  <Dices className="h-4 w-4" />
                  随机生成
                </button>
              </div>
            </div>

            <div>
              <label className={labelClass}>工作流类型</label>
              <select
                value={workflowType}
                onChange={(e) => setWorkflowType(e.target.value)}
                className={inputClass}
              >
                {workflowTypes.map((type) => (
                  <option key={type} value={type} className="bg-base-800">
                    {type}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className={labelClass}>任务队列</label>
              <select
                value={taskQueue}
                onChange={(e) => setTaskQueue(e.target.value)}
                className={inputClass}
              >
                {taskQueues.map((queue) => (
                  <option key={queue} value={queue} className="bg-base-800">
                    {queue}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* 输入参数 */}
        <div className={cardClass}>
          <div className="mb-3 flex items-center justify-between">
            <h2 className={sectionTitleClass + ' mb-0'}>输入参数</h2>
            <span className="rounded bg-accent/20 px-2 py-0.5 text-xs font-medium text-accent-light">
              JSON
            </span>
          </div>
          <textarea
            value={jsonInput}
            onChange={(e) => handleJsonChange(e.target.value)}
            placeholder={'{\n  "orderId": "ORD-20260811-91734",\n  "userId": "U100238",\n  "amount": 598.00\n}'}
            rows={8}
            className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 font-mono text-sm text-slate-100 placeholder:text-slate-500 focus:border-accent focus:outline-none"
          />
          {jsonError && (
            <p className="mt-2 text-sm text-status-failed">{jsonError}</p>
          )}
        </div>

        {/* 高级选项 */}
        <div className={cardClass}>
          <button
            type="button"
            onClick={() => setShowAdvanced(!showAdvanced)}
            className="flex w-full items-center gap-2 text-sm font-semibold text-slate-200 hover:text-slate-100"
          >
            {showAdvanced ? (
              <ChevronDown className="h-4 w-4" />
            ) : (
              <ChevronRight className="h-4 w-4" />
            )}
            高级选项
          </button>

          {showAdvanced && (
            <div className="mt-4 space-y-4">
              {/* 搜索属性 */}
              <div>
                <label className={labelClass}>搜索属性</label>
                <div className="space-y-2">
                  {searchAttributes.map((attr, index) => (
                    <div key={index} className="flex items-center gap-2">
                      <input
                        type="text"
                        value={attr.key}
                        onChange={(e) =>
                          handleAttributeChange(index, 'key', e.target.value)
                        }
                        placeholder="键"
                        className={inputClass}
                      />
                      <input
                        type="text"
                        value={attr.value}
                        onChange={(e) =>
                          handleAttributeChange(index, 'value', e.target.value)
                        }
                        placeholder="值"
                        className={inputClass}
                      />
                      <button
                        type="button"
                        onClick={() => handleRemoveAttribute(index)}
                        title="删除"
                        className="flex flex-shrink-0 items-center rounded-lg p-2 text-slate-400 hover:bg-base-700 hover:text-status-failed"
                      >
                        <X className="h-4 w-4" />
                      </button>
                    </div>
                  ))}
                  <button
                    type="button"
                    onClick={handleAddAttribute}
                    className="flex items-center gap-1.5 rounded-lg border border-dashed border-base-600 px-3 py-1.5 text-sm text-slate-400 hover:border-accent hover:text-accent"
                  >
                    <Plus className="h-4 w-4" />
                    添加
                  </button>
                </div>
              </div>

              {/* 启动延迟 */}
              <div>
                <label className={labelClass}>启动延迟</label>
                <div className="flex gap-2">
                  <input
                    type="number"
                    value={delayValue}
                    onChange={(e) => setDelayValue(e.target.value)}
                    min="0"
                    placeholder="0"
                    className={inputClass}
                  />
                  <select
                    value={delayUnit}
                    onChange={(e) => setDelayUnit(e.target.value)}
                    className={cn(inputClass, 'w-28 flex-shrink-0')}
                  >
                    {delayUnits.map((unit) => (
                      <option key={unit.value} value={unit.value} className="bg-base-800">
                        {unit.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* 用户备注 */}
              <div>
                <label className={labelClass}>用户备注</label>
                <input
                  type="text"
                  value={userMemo}
                  onChange={(e) => setUserMemo(e.target.value)}
                  placeholder="用于标识此工作流运行的备注信息"
                  className={inputClass}
                />
              </div>
            </div>
          )}
        </div>

        {/* 操作按钮 */}
        <div className="flex items-center justify-end gap-3">
          <Link
            to="/workflows"
            className="rounded-lg bg-base-700 px-4 py-2 text-sm font-medium text-slate-300 transition-colors hover:bg-base-600"
          >
            取消
          </Link>
          <button
            type="submit"
            className="flex items-center gap-2 rounded-lg bg-accent px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-accent-dark"
          >
            <Play className="h-4 w-4" />
            启动
          </button>
        </div>
      </form>
    </div>
  )
}
