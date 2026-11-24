/// <reference types="vite/client" />

/**
 * 环境变量类型声明
 * 与 .env / .env.{mode} 中定义的 VITE_* 变量一一对应
 */
interface ImportMetaEnv {
  readonly VITE_APP_ENV: 'development' | 'test' | 'production';
  readonly VITE_APP_NAME: string;
  readonly VITE_APP_VERSION: string;
  readonly VITE_TEMPORAL_NAMESPACE: string;
  readonly VITE_DEFAULT_THEME: 'dark' | 'light' | 'system';
  readonly VITE_API_BASE_URL: string;
  readonly VITE_BASE_PATH: string;
  readonly VITE_CDN_BASE: string;
  readonly VITE_DEV_PORT: string;
  readonly VITE_ENABLE_DEVTOOLS: string;
  readonly VITE_ENABLE_SOURCEMAP: string;
  readonly VITE_LOG_API_REQUESTS: string;
  readonly VITE_USE_MOCK: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

/**
 * 运行时配置（由 /config.js 注入到 window.__APP_CONFIG__）
 * 部署到 Nginx 后可手动修改 /config.js 覆盖构建期配置
 */
interface Window {
  __APP_CONFIG__?: Partial<{
    appEnv: 'development' | 'test' | 'production';
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
  }>;
}
