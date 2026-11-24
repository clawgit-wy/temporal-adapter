/**
 * 运行时环境配置
 *
 * 设计思路：
 * 1. 构建期注入：通过 Vite 的 import.meta.env 读取 .env 文件中的 VITE_* 变量
 * 2. 运行时覆盖：支持从 window.__APP_CONFIG__ 加载运行时配置
 *    （用于 Nginx 部署场景，无需重新构建即可切换环境）
 *
 * 优先级：window.__APP_CONFIG__ > import.meta.env > 默认值
 *
 * Nginx 部署示例：
 *   通过 njs 或 sub_filter 注入 window.__APP_CONFIG__ = { apiBaseUrl: '/api/v1', ... }
 *   或在 public/config.js 中预定义（构建后可手动修改）
 */

type AppEnv = 'development' | 'test' | 'production';

interface RuntimeConfig {
  appEnv: AppEnv;
  appName: string;
  appVersion: string;
  namespace: string;
  defaultTheme: 'dark' | 'light' | 'system';
  apiBaseUrl: string;
  basePath: string;
  cdnBase: string;
  enableDevtools: boolean;
  enableSourcemap: boolean | 'hidden';
  logApiRequests: boolean;
  useMock: boolean;
}

/**
 * 安全读取 import.meta.env 中的字符串值
 */
function readEnvString(key: string, fallback: string): string {
  const val = import.meta.env[key];
  return typeof val === 'string' && val.length > 0 ? val : fallback;
}

function readEnvBool(key: string, fallback: boolean): boolean {
  const val = import.meta.env[key];
  if (val === undefined || val === null || val === '') return fallback;
  return String(val).toLowerCase() === 'true';
}

/**
 * 从 window.__APP_CONFIG__ 读取运行时配置（Nginx 部署用）
 */
function readRuntimeOverride(): Partial<RuntimeConfig> {
  if (typeof window === 'undefined') return {};
  const override = (window as any).__APP_CONFIG__;
  if (!override || typeof override !== 'object') return {};
  return override;
}

const runtimeOverride = readRuntimeOverride();

export const appConfig: RuntimeConfig = {
  appEnv: (runtimeOverride.appEnv as AppEnv) || (readEnvString('VITE_APP_ENV', 'development') as AppEnv),
  appName: runtimeOverride.appName || readEnvString('VITE_APP_NAME', 'Temporal UI Redesign'),
  appVersion: runtimeOverride.appVersion || readEnvString('VITE_APP_VERSION', '0.0.0'),
  namespace: runtimeOverride.namespace || readEnvString('VITE_TEMPORAL_NAMESPACE', 'default'),
  defaultTheme: runtimeOverride.defaultTheme || readEnvString('VITE_DEFAULT_THEME', 'dark') as 'dark' | 'light' | 'system',
  apiBaseUrl: runtimeOverride.apiBaseUrl || readEnvString('VITE_API_BASE_URL', '/api/v1'),
  basePath: runtimeOverride.basePath || readEnvString('VITE_BASE_PATH', '/'),
  cdnBase: runtimeOverride.cdnBase !== undefined ? runtimeOverride.cdnBase : readEnvString('VITE_CDN_BASE', ''),
  enableDevtools: runtimeOverride.enableDevtools ?? readEnvBool('VITE_ENABLE_DEVTOOLS', true),
  enableSourcemap: runtimeOverride.enableSourcemap ?? (readEnvBool('VITE_ENABLE_SOURCEMAP', true) ? true : false),
  logApiRequests: runtimeOverride.logApiRequests ?? readEnvBool('VITE_LOG_API_REQUESTS', false),
  useMock: runtimeOverride.useMock ?? readEnvBool('VITE_USE_MOCK', false),
};

/**
 * 是否为开发环境
 */
export const isDev = appConfig.appEnv === 'development';

/**
 * 是否为测试环境
 */
export const isTest = appConfig.appEnv === 'test';

/**
 * 是否为生产环境
 */
export const isProd = appConfig.appEnv === 'production';

/**
 * 调试日志：仅在 logApiRequests 开启时输出
 */
export function debugLog(...args: any[]): void {
  if (appConfig.logApiRequests) {
    // eslint-disable-next-line no-console
    console.log('[App]', ...args);
  }
}
