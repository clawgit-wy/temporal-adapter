import { cn } from '@/lib/utils'

interface StatusBadgeProps {
  status: string
  size?: 'sm' | 'md'
}

const statusMap: Record<string, { color: string; text: string }> = {
  running: { color: 'bg-status-running', text: '运行中' },
  completed: { color: 'bg-status-completed', text: '已完成' },
  failed: { color: 'bg-status-failed', text: '失败' },
  timed_out: { color: 'bg-status-timeout', text: '超时' },
  canceled: { color: 'bg-status-canceled', text: '已取消' },
  terminated: { color: 'bg-status-terminated', text: '已终止' },
}

export default function StatusBadge({ status, size = 'md' }: StatusBadgeProps) {
  const config = statusMap[status] ?? { color: 'bg-slate-500', text: status }

  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5',
        size === 'sm' ? 'text-xs' : 'text-sm'
      )}
    >
      <span
        className={cn(
          'rounded-full',
          config.color,
          size === 'sm' ? 'h-1.5 w-1.5' : 'h-2 w-2'
        )}
      />
      <span className="text-slate-300">{config.text}</span>
    </span>
  )
}
