import { ArrowUp, ArrowDown, type LucideIcon } from 'lucide-react'
import { cn } from '@/lib/utils'

interface StatCardProps {
  title: string
  value: number | string
  icon: LucideIcon
  color: string
  trend?: number
}

export default function StatCard({ title, value, icon: Icon, color, trend }: StatCardProps) {
  return (
    <div className="bg-base-800 border border-base-700 rounded-xl p-5">
      <div className="flex items-center justify-between">
        <div className={cn('flex h-10 w-10 items-center justify-center rounded-full', color)}>
          <Icon className="h-5 w-5 text-white" />
        </div>
        {trend !== undefined && (
          <span
            className={cn(
              'flex items-center gap-0.5 text-xs font-medium',
              trend >= 0 ? 'text-status-completed' : 'text-status-failed'
            )}
          >
            {trend >= 0 ? <ArrowUp className="h-3 w-3" /> : <ArrowDown className="h-3 w-3" />}
            {Math.abs(trend)}%
          </span>
        )}
      </div>
      <div className="mt-4">
        <div className="text-2xl font-bold text-slate-100">{value}</div>
        <div className="mt-1 text-xs text-slate-400">{title}</div>
      </div>
    </div>
  )
}
