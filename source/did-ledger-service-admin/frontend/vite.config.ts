import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    host: '0.0.0.0',
    proxy: {
      // /lss/admin/v1 → http://localhost:8098/lss/admin/v1
      '/lss/admin/v1': {
        target: 'http://localhost:8098',
        changeOrigin: true,
      },
    },
  },
});
