import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tsconfigPaths from 'vite-tsconfig-paths'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // 加载对应 mode 的 .env 文件（.env, .env.{mode}, .env.{mode}.local）
  const env = loadEnv(mode, process.cwd(), '')

  // 从环境变量读取配置
  const devPort = parseInt(env.VITE_DEV_PORT || '5173', 10)
  const enableSourcemap = env.VITE_ENABLE_SOURCEMAP !== 'false'
  const basePath = env.VITE_BASE_PATH || '/'
  const cdnBase = env.VITE_CDN_BASE || ''
  const devTemporalServer = env.DEV_TEMPORAL_UI_SERVER || 'http://localhost:8280'
  const testTemporalServer = env.TEST_TEMPORAL_UI_SERVER || devTemporalServer

  // 根据模式选择代理目标
  // - development: 代理到本地 Temporal UI Server
  // - test: 代理到测试环境
  // - production: 通常不使用 Vite 代理（由 Nginx 处理）
  const proxyTarget = mode === 'test' ? testTemporalServer : devTemporalServer

  // 通用代理配置
  const proxyConfig = {
    '/api/v1': {
      target: proxyTarget,
      changeOrigin: true,
      // 修复 CSRF Cookie：官方镜像设置 Secure 标志，HTTP 下浏览器不会存储
      // 这里在代理层剥离 Secure，使开发环境正常工作
      onProxyRes: (proxyRes: any) => {
        const setCookie = proxyRes.headers['set-cookie']
        if (setCookie) {
          proxyRes.headers['set-cookie'] = setCookie.map((cookie: string) =>
            cookie.replace(/;\s*Secure/gi, '').replace(/;\s*SameSite=Strict/gi, '; SameSite=Lax')
          )
        }
      },
    },
  }

  return {
    // 部署根路径（影响所有静态资源 URL）
    base: basePath,

    // 构建配置
    build: {
      // sourcemap: true / false / 'hidden'
      // 'hidden' 会生成 sourcemap 但不在 bundle 中引用（用于线上错误监控）
      sourcemap: enableSourcemap ? (mode === 'production' ? 'hidden' : true) : false,
      // 生产环境 chunk 大小警告阈值
      chunkSizeWarningLimit: 1000,
      // 滚动 chunk 策略
      rollupOptions: {
        output: {
          // 静态资源 CDN 前缀
          assetFileNames: cdnBase
            ? `${cdnBase.replace(/\/$/, '')}/assets/[name]-[hash][extname]`
            : 'assets/[name]-[hash][extname]',
          // 将第三方依赖拆分到独立 chunk，便于长期缓存
          manualChunks: {
            'react-vendor': ['react', 'react-dom', 'react-router-dom'],
            'charts-vendor': ['recharts'],
            'icons-vendor': ['lucide-react'],
          },
        },
      },
    },

    plugins: [
      react({
        babel: {
          plugins: [
            'react-dev-locator',
          ],
        },
      }),
      tsconfigPaths(),
    ],

    // 开发服务器配置
    server: {
      port: devPort,
      // 仅在 development / test 模式下启用代理
      proxy: mode === 'production' ? undefined : proxyConfig,
      // 允许任意 host 访问（便于 ngrok / 局域网调试）
      host: true,
    },

    // 预览服务器配置（用于 pnpm preview 验证生产构建）
    preview: {
      port: devPort,
      proxy: proxyConfig,
    },

    // 定义全局常量（在代码中可通过 import.meta.env.VITE_* 访问）
    // Vite 已自动注入 VITE_ 前缀变量，这里无需重复 define
  }
})
