import { Link } from 'react-router-dom'
import { LayoutDashboard, List, Cpu, Calendar, Play, ArrowLeft, Book } from 'lucide-react'

const sections = [
  {
    icon: LayoutDashboard,
    title: '全局总览',
    path: '/',
    description: '展示 Temporal 集群中所有工作流的整体运行状况。',
    features: [
      '统计卡片：显示运行中、已完成、失败、超时等工作流数量',
      '24小时成功率趋势图：按小时展示工作流成功率变化',
      '最近工作流列表：展示最近启动的 5 个工作流，可点击查看详情',
      '右上角刷新按钮：手动刷新所有数据',
    ],
  },
  {
    icon: List,
    title: '工作流列表',
    path: '/workflows',
    description: '查看和管理所有工作流执行实例。',
    features: [
      '状态筛选：按全部、运行中、已完成、失败、超时、已取消筛选',
      '搜索功能：通过工作流 ID 或名称搜索',
      '启动工作流：点击右上角"启动工作流"按钮，填写表单启动新工作流',
      '操作菜单：每行右侧点击三点菜单可终止、取消或重置工作流',
      '批量操作：勾选多个工作流后底部出现批量操作栏',
      '点击工作流名称可跳转至详情页',
    ],
  },
  {
    icon: Book,
    title: '工作流详情',
    path: '/workflows/:id',
    description: '查看单个工作流的详细执行信息。',
    features: [
      '基本信息：工作流 ID、运行 ID、类型、任务队列、开始/结束时间',
      '执行统计：持续时间、活动总数、第三方调用次数、重试次数',
      '执行时间线：以时间线形式展示每个 Activity 的执行状态',
      '活动详情：点击时间线节点查看输入输出、第三方交互、日志',
      '操作按钮：顶部可发送信号、发送更新、重置、取消、终止工作流',
      '底部折叠区：查看工作流输入和结果数据',
    ],
  },
  {
    icon: Cpu,
    title: '工作进程',
    path: '/workers',
    description: '监控 Temporal Worker 的运行状态。',
    features: [
      '统计卡片：显示运行中、异常、已排空、排空中的 Worker 数量',
      'Worker 列表：展示每个 Worker 的名称、状态、任务队列、构建 ID',
      '资源监控：CPU 使用率、内存使用率、槽位使用情况',
      '点击"查看详情"打开侧边栏，查看 Worker 的完整信息',
      '注意：当 TaskQueue API 无 poller 数据时，显示任务队列占位信息',
    ],
  },
  {
    icon: Calendar,
    title: '计划任务',
    path: '/schedules',
    description: '管理 Temporal Schedule（定时调度工作流）。',
    features: [
      '统计卡片：活跃计划数、已暂停数、总执行次数',
      '计划列表：展示名称、状态、工作流类型、Cron 表达式',
      'Cron 中文描述：自动将 Cron 表达式转换为中文可读格式',
      '暂停/恢复：点击操作栏的暂停或恢复按钮切换计划状态',
      '创建计划：点击"创建计划"按钮（功能开发中，当前通过 CLI 创建）',
    ],
  },
  {
    icon: Play,
    title: '启动工作流',
    path: '/workflows（点击启动按钮）',
    description: '通过表单启动新的工作流执行。',
    features: [
      '基本信息：填写工作流 ID（可随机生成）、选择工作流类型和任务队列',
      '输入参数：以 JSON 格式填写工作流输入参数',
      '高级选项：设置搜索属性、启动延迟、用户备注',
      '当前可用工作流类型：DemoWorkflow、HikSyncWorkflow',
      '当前可用任务队列：DEMO_TASK_QUEUE、HIK_SYNC_TASK_QUEUE',
    ],
  },
]

const apiInfo = [
  { label: 'API 基础路径', value: '/api/v1' },
  { label: 'Temporal Server', value: 'localhost:7233 (gRPC)' },
  { label: 'UI Server', value: 'localhost:8080 (HTTP)' },
  { label: '前端开发端口', value: 'localhost:5173/5174/5175 (Vite)' },
  { label: 'Mock API (mokapi)', value: 'localhost:19090' },
  { label: 'Demo Worker', value: 'localhost:18093' },
]

export default function Help() {
  return (
    <div className="overflow-auto p-6">
      {/* Header */}
      <div className="mb-6 flex items-center gap-3">
        <Link
          to="/"
          className="flex h-8 w-8 items-center justify-center rounded-lg text-slate-400 hover:bg-base-700 hover:text-slate-100"
        >
          <ArrowLeft className="h-4 w-4" />
        </Link>
        <div>
          <h1 className="text-2xl font-bold text-slate-100">使用说明</h1>
          <p className="mt-1 text-sm text-slate-400">Temporal 工作流监控平台 - 功能介绍与操作指南</p>
        </div>
      </div>

      {/* API 配置信息 */}
      <div className="mb-6 rounded-xl border border-base-700 bg-base-800 p-5">
        <h2 className="mb-4 text-base font-semibold text-slate-100">服务配置信息</h2>
        <div className="grid grid-cols-2 gap-3">
          {apiInfo.map((item) => (
            <div key={item.label} className="flex items-center justify-between rounded-lg bg-base-700/50 px-3 py-2">
              <span className="text-sm text-slate-400">{item.label}</span>
              <span className="font-mono text-sm text-slate-200">{item.value}</span>
            </div>
          ))}
        </div>
      </div>

      {/* 功能模块 */}
      <div className="space-y-4">
        {sections.map((section, index) => {
          const Icon = section.icon
          return (
            <div key={index} className="rounded-xl border border-base-700 bg-base-800 p-5">
              <div className="mb-3 flex items-center gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-accent/10 text-accent">
                  <Icon className="h-5 w-5" />
                </div>
                <div>
                  <h3 className="text-base font-semibold text-slate-100">{section.title}</h3>
                  <p className="text-xs text-slate-500">路径: {section.path}</p>
                </div>
              </div>
              <p className="mb-3 text-sm text-slate-400">{section.description}</p>
              <ul className="space-y-1.5">
                {section.features.map((feature, i) => (
                  <li key={i} className="flex items-start gap-2 text-sm text-slate-300">
                    <span className="mt-1.5 h-1 w-1 flex-shrink-0 rounded-full bg-accent" />
                    {feature}
                  </li>
                ))}
              </ul>
            </div>
          )
        })}
      </div>

      {/* 快速验证步骤 */}
      <div className="mt-6 rounded-xl border border-base-700 bg-base-800 p-5">
        <h2 className="mb-4 text-base font-semibold text-slate-100">快速验证步骤</h2>
        <ol className="space-y-3">
          <li className="flex gap-3 text-sm text-slate-300">
            <span className="flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-accent/20 text-xs font-bold text-accent">1</span>
            <span>打开<span className="text-accent">全局总览</span>页面，确认统计卡片显示正确的工作流数量</span>
          </li>
          <li className="flex gap-3 text-sm text-slate-300">
            <span className="flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-accent/20 text-xs font-bold text-accent">2</span>
            <span>进入<span className="text-accent">工作流列表</span>，点击右上角"启动工作流"按钮创建新工作流</span>
          </li>
          <li className="flex gap-3 text-sm text-slate-300">
            <span className="flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-accent/20 text-xs font-bold text-accent">3</span>
            <span>在工作流列表中点击工作流名称，进入<span className="text-accent">详情页</span>查看执行时间线</span>
          </li>
          <li className="flex gap-3 text-sm text-slate-300">
            <span className="flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-accent/20 text-xs font-bold text-accent">4</span>
            <span>在详情页点击"终止"或"取消"按钮测试操作功能</span>
          </li>
          <li className="flex gap-3 text-sm text-slate-300">
            <span className="flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-accent/20 text-xs font-bold text-accent">5</span>
            <span>进入<span className="text-accent">工作进程</span>页面，查看 Worker 状态和任务队列信息</span>
          </li>
          <li className="flex gap-3 text-sm text-slate-300">
            <span className="flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-full bg-accent/20 text-xs font-bold text-accent">6</span>
            <span>进入<span className="text-accent">计划任务</span>页面，查看调度列表</span>
          </li>
        </ol>
      </div>
    </div>
  )
}
