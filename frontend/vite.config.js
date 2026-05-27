import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 后端端口配置
const BACKEND_PORT = 8080

// 获取命令行参数中的端口，用于区分用户端和管理员端
const args = process.argv
const portArgIndex = args.findIndex(arg => arg === '--port')
const port = portArgIndex !== -1 ? parseInt(args[portArgIndex + 1]) : 5173

export default defineConfig({
  plugins: [vue()],
  define: {
    // 将端口信息注入到应用中
    __APP_PORT__: port
  },
  server: {
    host: '0.0.0.0', // 允许外部访问
    port: port,
    strictPort: true, // 端口被占用时直接失败，不自动切换
    proxy: {
      '/api': {
        target: `http://localhost:${BACKEND_PORT}`,
        changeOrigin: true,
        secure: false
      }
    }
  },
  // 构建配置
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    rollupOptions: {
      output: {
        manualChunks: {
          vue: ['vue', 'vue-router', 'pinia'],
          axios: ['axios']
        }
      }
    }
  }
})