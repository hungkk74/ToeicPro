import { beforeEach, describe, expect, it, vi } from 'vitest';

import axios from 'axios';

import administration, {
  getConfigurations,
  getEnv,
  getGatewayRoutes,
  getLoggers,
  getSystemHealth,
  getSystemMetrics,
  getSystemThreadDump,
  setLoggers,
} from './administration.reducer';

describe('Administration reducer tests', () => {
  function isEmpty(element): boolean {
    if (Array.isArray(element)) {
      return element.length === 0;
    }
    return Object.keys(element).length === 0;
  }

  function testInitialState(state) {
    expect(state).toMatchObject({
      loading: false,
      errorMessage: null,
      totalItems: 0,
    });
    expect(isEmpty(state.gateway.routes)).toBe(true);
    expect(isEmpty(state.logs.loggers)).toBe(true);
    expect(isEmpty(state.threadDump)).toBe(true);
  }

  function testMultipleTypes(types, payload, testFunction, error?) {
    types.forEach(e => {
      testFunction(administration(undefined, { type: e, payload, error }));
    });
  }

  describe('Common', () => {
    it('should return the initial state', () => {
      testInitialState(administration(undefined, { type: '' }));
    });
  });

  describe('Requests', () => {
    it('should set state to loading', () => {
      testMultipleTypes(
        [
          getGatewayRoutes.pending.type,
          getLoggers.pending.type,
          getSystemHealth.pending.type,
          getSystemMetrics.pending.type,
          getSystemThreadDump.pending.type,
          getConfigurations.pending.type,
          getEnv.pending.type,
        ],
        {},
        state => {
          expect(state).toMatchObject({
            errorMessage: null,
            loading: true,
          });
        },
      );
    });
  });

  describe('Failures', () => {
    it('should set state to failed and put an error message in errorMessage', () => {
      testMultipleTypes(
        [
          getGatewayRoutes.rejected.type,
          getLoggers.rejected.type,
          getSystemHealth.rejected.type,
          getSystemMetrics.rejected.type,
          getSystemThreadDump.rejected.type,
          getConfigurations.rejected.type,
          getEnv.rejected.type,
        ],
        'something happened',
        state => {
          expect(state).toMatchObject({
            loading: false,
            errorMessage: 'error',
          });
        },
        {
          message: 'error',
        },
      );
    });
  });

  describe('Success', () => {
    it('should update state according to a successful fetch gateway routes request', () => {
      const payload = { data: [] };
      const toTest = administration(undefined, { type: getGatewayRoutes.fulfilled.type, payload });

      expect(toTest).toMatchObject({
        loading: false,
        gateway: { routes: payload.data },
      });
    });
    it('should update state according to a successful fetch logs request', () => {
      const payload = {
        data: {
          loggers: {
            main: {
              effectiveLevel: 'WARN',
            },
          },
        },
      };
      const toTest = administration(undefined, { type: getLoggers.fulfilled.type, payload });

      expect(toTest).toMatchObject({
        loading: false,
        logs: payload.data,
      });
    });

    it('should update state according to a successful fetch health request', () => {
      const payload = { data: { status: 'UP' } };
      const toTest = administration(undefined, { type: getSystemHealth.fulfilled.type, payload });

      expect(toTest).toMatchObject({
        loading: false,
        health: payload.data,
      });
    });

    it('should update state according to a successful fetch metrics request', () => {
      const payload = { data: { version: '3.1.3', gauges: {} } };
      const toTest = administration(undefined, { type: getSystemMetrics.fulfilled.type, payload });

      expect(toTest).toMatchObject({
        loading: false,
        metrics: payload.data,
      });
    });

    it('should update state according to a successful fetch thread dump request', () => {
      const payload = { data: [{ threadName: 'hz.gateway.cached.thread-6', threadId: 9266 }] };
      const toTest = administration(undefined, { type: getSystemThreadDump.fulfilled.type, payload });

      expect(toTest).toMatchObject({
        loading: false,
        threadDump: payload.data,
      });
    });

    it('should update state according to a successful fetch configurations request', () => {
      const payload = { data: { contexts: { jhipster: { beans: {} } } } };
      const toTest = administration(undefined, { type: getConfigurations.fulfilled.type, payload });

      expect(toTest).toMatchObject({
        loading: false,
        configuration: {
          configProps: payload.data,
          env: {},
        },
      });
    });

    it('should update state according to a successful fetch env request', () => {
      const payload = { data: { activeProfiles: ['api-docs', 'dev'] } };
      const toTest = administration(undefined, { type: getEnv.fulfilled.type, payload });

      expect(toTest).toMatchObject({
        loading: false,
        configuration: {
          configProps: {},
          env: payload.data,
        },
      });
    });
  });
  describe('Actions', () => {
    const resolvedObject = { value: 'whatever' };
    const getState = vi.fn();
    const dispatch = vi.fn();
    const extra = {};
    beforeEach(() => {
      axios.get = vi.fn().mockResolvedValue(resolvedObject);
      axios.post = vi.fn().mockResolvedValue(resolvedObject);
    });
    it('dispatches FETCH_GATEWAY_ROUTE_PENDING and FETCH_GATEWAY_ROUTE_FULFILLED actions', async () => {
      const result = await getGatewayRoutes()(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getGatewayRoutes.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(getGatewayRoutes.fulfilled.match(result)).toBe(true);
      expect(result.payload).toBe(resolvedObject);
    });
    it('dispatches FETCH_HEALTH_PENDING and FETCH_HEALTH_FULFILLED actions', async () => {
      const result = await getSystemHealth()(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getSystemHealth.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(getSystemHealth.fulfilled.match(result)).toBe(true);
      expect(result.payload).toBe(resolvedObject);
    });
    it('dispatches FETCH_METRICS_PENDING and FETCH_METRICS_FULFILLED actions', async () => {
      const result = await getSystemMetrics()(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getSystemMetrics.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(getSystemMetrics.fulfilled.match(result)).toBe(true);
    });
    it('dispatches FETCH_THREAD_DUMP_PENDING and FETCH_THREAD_DUMP_FULFILLED actions', async () => {
      const result = await getSystemThreadDump()(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getSystemThreadDump.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(getSystemThreadDump.fulfilled.match(result)).toBe(true);
    });
    it('dispatches FETCH_LOGS_PENDING and FETCH_LOGS_FULFILLED actions', async () => {
      const result = await getLoggers()(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getLoggers.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(getLoggers.fulfilled.match(result)).toBe(true);
    });
    it('dispatches FETCH_LOGS_CHANGE_LEVEL_PENDING and FETCH_LOGS_CHANGE_LEVEL_FULFILLED actions', async () => {
      const result = await setLoggers({ name: 'ROOT', configuredLevel: 'DEBUG' })(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: setLoggers.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(setLoggers.fulfilled.match(result)).toBe(true);
    });
    it('dispatches FETCH_CONFIGURATIONS_PENDING and FETCH_CONFIGURATIONS_FULFILLED actions', async () => {
      const result = await getConfigurations()(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getConfigurations.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(getConfigurations.fulfilled.match(result)).toBe(true);
    });
    it('dispatches FETCH_ENV_PENDING and FETCH_ENV_FULFILLED actions', async () => {
      const result = await getEnv()(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getEnv.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(getEnv.fulfilled.match(result)).toBe(true);
    });
  });
});
