import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig(({ mode }) => {
  const isDev = mode === 'development'

  return {
    plugins: [vue()],

    resolve: {
      alias: {
        '@': resolve(__dirname, 'src')
      }
    },

    server: isDev
      ? {
          proxy: {
            '/api': {
              target: 'http://localhost:8080',
              changeOrigin: true
            }
          }
        }
      : {}
  }
})