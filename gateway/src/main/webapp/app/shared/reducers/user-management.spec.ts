import { beforeEach, describe, expect, it, vi } from 'vitest';

import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';

import userManagement, { getUsers } from 'app/shared/reducers/user-management';

describe('User management reducer tests', () => {
  const initialState = {
    users: [],
    errorMessage: null,
  };

  describe('Common', () => {
    it('should return the initial state', () => {
      expect(userManagement(undefined, { type: 'unknown' })).toEqual({ ...initialState });
    });
  });

  describe('Failures', () => {
    it('should set state to failed and put an error message in errorMessage', () => {
      expect(
        userManagement(undefined, {
          type: getUsers.rejected.type,
          payload: 'something happened',
          error: { message: 'error happened' },
        }),
      ).toEqual({ ...initialState, errorMessage: 'error happened' });
    });
  });

  describe('Success', () => {
    it('should update state according to a successful fetch users request', () => {
      const payload = { data: 'some handsome users' };
      const toTest = userManagement(undefined, { type: getUsers.fulfilled.type, payload });
      expect(toTest).toMatchObject({
        users: payload.data,
      });
    });
  });

  describe('Actions', () => {
    const resolvedObject = { value: 'whatever' };
    const getState = vi.fn();
    const dispatch = vi.fn();
    const extra = {};
    beforeEach(() => {
      configureStore({
        reducer: (state = [], action) => [...state, action],
      });
      axios.get = vi.fn().mockResolvedValue(resolvedObject);
    });

    it('dispatches FETCH_USERS_PENDING and FETCH_USERS_FULFILLED actions', async () => {
      const arg = {};

      const result = await getUsers(arg)(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getUsers.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(getUsers.fulfilled.match(result)).toBe(true);
    });

    it('dispatches FETCH_USERS_PENDING and FETCH_USERS_FULFILLED actions with pagination options', async () => {
      const arg = { page: 1, size: 20, sort: 'id,desc' };

      const result = await getUsers(arg)(dispatch, getState, extra);

      expect(dispatch).toHaveBeenCalledWith(
        expect.objectContaining({
          type: getUsers.pending.type,
          meta: expect.objectContaining({ requestStatus: 'pending' }),
        }),
      );
      expect(getUsers.fulfilled.match(result)).toBe(true);
    });
  });
});
