import {defineConfig, loadEnv} from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig(({mode}) => {
  // eslint-disable-next-line no-undef
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react()],
    server: {
      proxy: {
        // 프론트엔드에서 '/api'로 시작하는 요청을 백엔드(8080)로 전달
        '/api': {
          target: env.VITE_API_TARGET || 'http://localhost:8080',
          changeOrigin: true,
          secure: false,
        }
      }
    },
    test: {
      globals: true,          // describe, it, expect 등을 import 없이 사용
      environment: 'jsdom',   // 브라우저 환경 시뮬레이션
      setupFiles: './src/setupTests.js', // 초기 설정 파일 지정
      css: true,
    },
  }
})