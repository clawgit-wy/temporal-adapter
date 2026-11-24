/**
 * 运行时配置文件（Nginx 部署后可手动修改）
 *
 * 此文件在 index.html 中通过 <script src="/config.js"></script> 加载，
 * 在应用主入口之前执行，注入 window.__APP_CONFIG__。
 *
 * 部署时根据环境（开发/测试/生产）调整下列值即可，无需重新构建前端。
 *
 * 注意：此文件不参与构建，直接由 Nginx 静态托管。
 */
window.__APP_CONFIG__ = {
  // 环境标识：development | test | production
  appEnv: 'production',

  // 应用元信息
  appName: 'Temporal UI Redesign',
  appVersion: '0.1.0',

  // Temporal 命名空间
  namespace: 'default',

  // 主题默认值
  defaultTheme: 'dark',

  // API 基础路径（前端请求路径前缀，Nginx 反向代理到 Temporal UI Server）
  apiBaseUrl: '/api/v1',

  // 部署根路径（如挂在子路径下需调整）
  basePath: '/',

  // 静态资源 CDN 前缀（无 CDN 留空）
  cdnBase: '',

  // 调试开关
  enableDevtools: false,
  enableSourcemap: false,
  logApiRequests: false,
  useMock: false,
};
