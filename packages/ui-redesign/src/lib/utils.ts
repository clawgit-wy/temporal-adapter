/**
 * Utility functions for the Temporal UI redesign.
 */

/**
 * Format a millisecond duration into a Chinese-readable string.
 *
 * Examples:
 *   1500        -> "1秒"
 *   90000       -> "1分30秒"
 *   75000       -> "1分15秒"
 *   4500000     -> "1小时15分"
 *   7200000     -> "2小时"
 *
 * @param ms - Duration in milliseconds.
 * @returns A localized Chinese duration string.
 */
export function formatDuration(ms: number): string {
  if (ms == null || isNaN(ms) || ms < 0) return '0秒';
  if (ms < 1000) return `${Math.floor(ms)}毫秒`;

  const totalSeconds = Math.floor(ms / 1000);
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;

  if (hours > 0) {
    if (minutes > 0) return `${hours}小时${minutes}分`;
    return `${hours}小时`;
  }

  if (minutes > 0) {
    if (seconds > 0) return `${minutes}分${seconds}秒`;
    return `${minutes}分`;
  }

  return `${seconds}秒`;
}

/**
 * Format an ISO timestamp to a compact "MM-DD HH:mm:ss" string.
 *
 * @param iso - An ISO 8601 timestamp string.
 * @returns A formatted string like "08-11 14:30:05".
 */
export function formatTime(iso: string): string {
  if (!iso) return '';
  const date = new Date(iso);
  if (isNaN(date.getTime())) return '';

  const pad = (n: number) => String(n).padStart(2, '0');
  const month = pad(date.getMonth() + 1);
  const day = pad(date.getDate());
  const hours = pad(date.getHours());
  const minutes = pad(date.getMinutes());
  const seconds = pad(date.getSeconds());

  return `${month}-${day} ${hours}:${minutes}:${seconds}`;
}

/**
 * Return a set of Tailwind color classes for a workflow status.
 *
 * @param status - One of the WorkflowStatus values.
 * @returns A space-separated string of Tailwind classes (text + bg).
 */
export function getStatusColor(status: string): string {
  const colorMap: Record<string, string> = {
    running: 'text-blue-600 bg-blue-50 border-blue-200',
    completed: 'text-green-600 bg-green-50 border-green-200',
    failed: 'text-red-600 bg-red-50 border-red-200',
    timed_out: 'text-amber-600 bg-amber-50 border-amber-200',
    terminated: 'text-purple-600 bg-purple-50 border-purple-200',
    canceled: 'text-zinc-500 bg-zinc-50 border-zinc-200',
  };
  return colorMap[status] ?? 'text-gray-600 bg-gray-50 border-gray-200';
}

/**
 * Return the Chinese display text for a workflow status.
 *
 * @param status - One of the WorkflowStatus values.
 * @returns A Chinese status label.
 */
export function getStatusText(status: string): string {
  const textMap: Record<string, string> = {
    running: '运行中',
    completed: '已完成',
    failed: '已失败',
    timed_out: '已超时',
    terminated: '已终止',
    canceled: '已取消',
  };
  return textMap[status] ?? '未知';
}

/**
 * Pretty-print a JSON-serializable value.
 *
 * @param obj - Any JSON-serializable value.
 * @returns An indented (2-space) JSON string, or an empty string on failure.
 */
export function formatJSON(obj: any): string {
  if (obj === null || obj === undefined) return '';
  try {
    return JSON.stringify(obj, null, 2);
  } catch {
    return String(obj);
  }
}

/**
 * Join class names, filtering out falsy values.
 *
 * @param classes - Class name strings (may include falsy values).
 * @returns A single space-separated class string.
 */
export function cn(...classes: string[]): string {
  return classes.filter(Boolean).join(' ');
}
