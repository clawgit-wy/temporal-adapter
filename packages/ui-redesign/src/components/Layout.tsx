import { useState, type ReactNode } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { LayoutDashboard, List, Activity, Settings, ChevronDown, Cpu, Calendar, Server, Moon, Sun, Monitor, X, Check, BookOpen } from 'lucide-react'
import { cn } from '@/lib/utils'
import { useTheme, type ThemeMode } from '@/hooks/useTheme'

const navItems = [
  { to: '/', label: '全局总览', icon: LayoutDashboard },
  { to: '/workflows', label: '工作流列表', icon: List },
  { to: '/workers', label: '工作进程', icon: Cpu },
  { to: '/schedules', label: '计划任务', icon: Calendar },
  { to: '/help', label: '使用说明', icon: BookOpen },
]

const CODEC_STORAGE_KEY = 'temporal-codec-server'

function getStoredCodecServer(): string {
  return localStorage.getItem(CODEC_STORAGE_KEY) || ''
}

export default function Layout({ children }: { children: ReactNode }) {
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const { mode, setThemeMode } = useTheme()
  const [settingsOpen, setSettingsOpen] = useState(false)
  const [codecModalOpen, setCodecModalOpen] = useState(false)
  const [codecUrl, setCodecUrl] = useState(getStoredCodecServer)
  const [codecMsg, setCodecMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null)
  const [feedbackMsg, setFeedbackMsg] = useState<string | null>(null)

  const isActive = (to: string) => {
    if (to === '/') return pathname === '/'
    if (to === '/workflows') return pathname === '/workflows'
    return pathname.startsWith(to)
  }

  const handleSaveCodec = () => {
    const trimmed = codecUrl.trim()
    try {
      if (trimmed) {
        // 验证 URL 格式
        const u = new URL(trimmed)
        localStorage.setItem(CODEC_STORAGE_KEY, u.toString())
      } else {
        localStorage.removeItem(CODEC_STORAGE_KEY)
      }
      setCodecMsg({ type: 'success', text: '编解码服务器配置已保存并生效' })
      // 触发 storage 事件以通知其他组件
      window.dispatchEvent(new StorageEvent('storage', { key: CODEC_STORAGE_KEY }))
      setTimeout(() => {
        setCodecMsg(null)
        setCodecModalOpen(false)
      }, 1200)
    } catch {
      setCodecMsg({ type: 'error', text: 'URL 格式无效，请输入完整地址（如 http://localhost:8081）' })
    }
  }

  const showFeedback = (text: string) => {
    setFeedbackMsg(text)
    setTimeout(() => setFeedbackMsg(null), 2000)
  }

  const handleThemeChange = (next: ThemeMode) => {
    setThemeMode(next)
    const labelMap = { dark: '深色主题', light: '浅色主题', system: '跟随系统' }
    showFeedback(`已切换至${labelMap[next]}`)
    setSettingsOpen(false)
  }

  return (
    <div className="flex h-screen">
      <aside className="w-60 bg-base-800 border-r border-base-700 flex flex-col">
        {/* Logo */}
        <div className="p-4 border-b border-base-700">
          <div className="flex items-center gap-2.5">
            <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-accent/10 text-accent">
              <Activity className="h-5 w-5" />
            </div>
            <div>
              <div className="text-base font-semibold text-slate-100">Temporal</div>
              <div className="text-xs text-slate-500">工作流监控平台</div>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-3 space-y-1">
          {navItems.map(({ to, label, icon: Icon }) => {
            const active = isActive(to)
            return (
              <Link
                key={to}
                to={to}
                className={cn(
                  'flex items-center gap-3 rounded-lg px-3 py-2 text-sm transition-colors',
                  active
                    ? 'bg-base-700 text-accent'
                    : 'text-slate-400 hover:bg-base-700 hover:text-slate-200'
                )}
              >
                <Icon className="h-4 w-4" />
                {label}
              </Link>
            )
          })}
        </nav>

        {/* Namespace selector + settings */}
        <div className="relative p-3 border-t border-base-700">
          <div className="flex items-center gap-2">
            <button
              type="button"
              className="flex flex-1 items-center justify-between rounded-lg bg-base-700 px-3 py-2 text-sm text-slate-200 hover:bg-base-600"
            >
              <span className="flex items-center gap-2">
                <span className="h-2 w-2 rounded-full bg-accent" />
                default
              </span>
              <ChevronDown className="h-4 w-4 text-slate-400" />
            </button>
            <button
              type="button"
              aria-label="设置"
              onClick={() => setSettingsOpen(!settingsOpen)}
              className="flex h-9 w-9 items-center justify-center rounded-lg text-slate-400 hover:bg-base-700 hover:text-slate-200"
            >
              <Settings className="h-4 w-4" />
            </button>
          </div>

          {/* Settings dropdown */}
          {settingsOpen && (
            <>
              <div className="fixed inset-0 z-10" onClick={() => setSettingsOpen(false)} />
              <div className="absolute bottom-full right-3 mb-2 z-20 w-60 rounded-xl border border-base-600 bg-base-800 py-1.5 shadow-2xl">
                <div className="px-3 py-1.5 text-xs font-medium uppercase tracking-wider text-slate-500">
                  设置
                </div>
                <button
                  type="button"
                  onClick={() => {
                    setSettingsOpen(false)
                    setCodecModalOpen(true)
                  }}
                  className="flex w-full items-center gap-2.5 px-3 py-2 text-sm text-slate-300 hover:bg-base-700"
                >
                  <Server className="h-4 w-4 text-slate-400" />
                  编解码服务器配置
                  {getStoredCodecServer() && (
                    <span className="ml-auto rounded bg-accent/20 px-1.5 py-0.5 text-xs text-accent-light">已配置</span>
                  )}
                </button>
                <button
                  type="button"
                  onClick={() => handleThemeChange('dark')}
                  className="flex w-full items-center gap-2.5 px-3 py-2 text-sm text-slate-300 hover:bg-base-700"
                >
                  <Moon className="h-4 w-4 text-slate-400" />
                  深色主题
                  {mode === 'dark' && (
                    <span className="ml-auto rounded bg-accent/20 px-1.5 py-0.5 text-xs text-accent-light">当前</span>
                  )}
                </button>
                <button
                  type="button"
                  onClick={() => handleThemeChange('light')}
                  className="flex w-full items-center gap-2.5 px-3 py-2 text-sm text-slate-300 hover:bg-base-700"
                >
                  <Sun className="h-4 w-4 text-slate-400" />
                  浅色主题
                  {mode === 'light' && (
                    <span className="ml-auto rounded bg-accent/20 px-1.5 py-0.5 text-xs text-accent-light">当前</span>
                  )}
                </button>
                <button
                  type="button"
                  onClick={() => handleThemeChange('system')}
                  className="flex w-full items-center gap-2.5 px-3 py-2 text-sm text-slate-300 hover:bg-base-700"
                >
                  <Monitor className="h-4 w-4 text-slate-400" />
                  跟随系统
                  {mode === 'system' && (
                    <span className="ml-auto rounded bg-accent/20 px-1.5 py-0.5 text-xs text-accent-light">当前</span>
                  )}
                </button>
                <div className="my-1 border-t border-base-700" />
                <button
                  type="button"
                  onClick={() => {
                    setSettingsOpen(false)
                    navigate('/help')
                  }}
                  className="flex w-full items-center gap-2.5 px-3 py-2 text-sm text-slate-300 hover:bg-base-700"
                >
                  <BookOpen className="h-4 w-4 text-slate-400" />
                  使用说明
                </button>
              </div>
            </>
          )}
        </div>
      </aside>

      <main className="flex-1 bg-base-900 overflow-auto">
        {children}
        {/* 全局反馈消息 */}
        {feedbackMsg && (
          <div className="fixed bottom-4 left-1/2 z-50 -translate-x-1/2 rounded-lg border border-accent/30 bg-base-800 px-4 py-2 text-sm text-slate-100 shadow-xl">
            {feedbackMsg}
          </div>
        )}
      </main>

      {/* 编解码服务器配置 Modal */}
      {codecModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="w-full max-w-md rounded-xl border border-base-600 bg-base-800 p-5 shadow-2xl">
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-base font-semibold text-slate-100">编解码服务器配置</h3>
              <button
                type="button"
                onClick={() => { setCodecModalOpen(false); setCodecMsg(null) }}
                className="rounded-lg p-1 text-slate-400 hover:bg-base-700 hover:text-slate-200"
              >
                <X className="h-4 w-4" />
              </button>
            </div>
            <p className="mb-3 text-xs text-slate-400">
              用于解码工作流执行历史中加密的 Payload 数据。配置后将在查看工作流详情时自动应用。
            </p>
            <label className="mb-1 block text-xs font-medium text-slate-300">服务器地址</label>
            <input
              type="text"
              value={codecUrl}
              onChange={(e) => { setCodecUrl(e.target.value); setCodecMsg(null) }}
              placeholder="例如：http://localhost:8081"
              className="w-full rounded-lg border border-base-600 bg-base-900 px-3 py-2 text-sm text-slate-100 placeholder-slate-500 outline-none focus:border-accent"
            />
            {codecMsg && (
              <div className={cn(
                'mt-3 flex items-center gap-2 rounded-lg px-3 py-2 text-xs',
                codecMsg.type === 'success'
                  ? 'bg-status-completed/10 text-status-completed'
                  : 'bg-status-failed/10 text-status-failed'
              )}>
                {codecMsg.type === 'success' ? <Check className="h-3.5 w-3.5" /> : <X className="h-3.5 w-3.5" />}
                {codecMsg.text}
              </div>
            )}
            <div className="mt-4 flex justify-end gap-2">
              <button
                type="button"
                onClick={() => { setCodecModalOpen(false); setCodecMsg(null); setCodecUrl(getStoredCodecServer()) }}
                className="rounded-lg px-3 py-1.5 text-sm text-slate-400 hover:bg-base-700 hover:text-slate-200"
              >
                取消
              </button>
              <button
                type="button"
                onClick={handleSaveCodec}
                className="rounded-lg bg-accent px-4 py-1.5 text-sm text-white hover:bg-accent-dark"
              >
                保存
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
