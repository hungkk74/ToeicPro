import path from 'node:path';
import { URL, fileURLToPath } from 'node:url';

import react from '@vitejs/plugin-react';
import { defineConfig, normalizePath } from 'vite';
import { viteStaticCopy } from 'vite-plugin-static-copy';

const { getAbsoluteFSPath } = await import('swagger-ui-dist');
const swaggerUiPath = getAbsoluteFSPath();
const webappDir = fileURLToPath(new URL('./src/main/webapp/', import.meta.url));

const development = process.env.NODE_ENV !== 'production';

const config = defineConfig({
  plugins: [
    react(),
    viteStaticCopy({
      targets: [
        {
          src: [
            `${normalizePath(swaggerUiPath)}/*.{js,css,png}`,
            `!${normalizePath(swaggerUiPath)}/**/index.html`,
          ],
          dest: 'swagger-ui',
        },
        {
          src: normalizePath(fileURLToPath(new URL('./dist/axios.min.js', import.meta.resolve('axios/package.json')))),
          dest: 'swagger-ui',
        },
        {
          src: normalizePath(fileURLToPath(new URL('./src/main/webapp/swagger-ui/index.html', import.meta.url))),
          dest: 'swagger-ui',
        },
      ],
    }),
  ],
  root: fileURLToPath(new URL('./src/main/webapp/', import.meta.url)),
  publicDir: fileURLToPath(new URL('./target/classes/static/public', import.meta.url)),
  cacheDir: fileURLToPath(new URL('./target/.vite-cache', import.meta.url)),
  build: {
    emptyOutDir: true,
    outDir: fileURLToPath(new URL('./target/classes/static/', import.meta.url)),
    rollupOptions: {
      input: {
        app: fileURLToPath(new URL('./src/main/webapp/index.html', import.meta.url)),
      },
    },
  },
  resolve: {
    tsconfigPaths: true,
  },
  define: {
    DEVELOPMENT: JSON.stringify(development),
    // APP_VERSION is passed as an environment variable from the Gradle / Maven build tasks.
    VERSION: JSON.stringify(process.env.APP_VERSION || 'DEV'),
    // If this URL is left empty (""), then it will be relative to the current context.
    // If you use an API server, in `prod` mode, you will need to enable CORS
    // (see the `jhipster.cors` common JHipster property in the `application-*.yml` configurations)
    SERVER_API_URL: '""',
    // react-jhipster requires LOG_LEVEL config.
    'process.env.LOG_LEVEL': JSON.stringify(development ? 'info' : 'error'),
  },
  server: {
    host: true,
    port: 9000,
    proxy: Object.fromEntries(
      ['/api', '/services', '/management', '/v3/api-docs', '/h2-console', '/oauth2', '/login'].map(res => [
        res,
        {
          target: 'http://localhost:8080',
        },
      ]),
    ),
  },
});

// jhipster-needle-add-vite-config - JHipster will add custom config

export default config;
