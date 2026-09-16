import { beforeEach, describe, expect, it, vi } from 'vitest';
import { TranslatorContext } from 'react-jhipster';

import locale, { loaded, setLocale, updateLocale } from 'app/shared/reducers/locale';

vi.mock('../../../i18n/en/en.js', () => ({ default: { key: 'value' } }));

const defaultLocale = 'en';
const dispatch = vi.fn();
const extra = {};

describe('Locale reducer tests', () => {
  it('should return the initial state', () => {
    const localeState = locale(undefined, { type: '' });
    expect(localeState).toMatchObject({
      currentLocale: '',
    });
  });

  it('should correctly set the first time locale', () => {
    const localeState = locale(undefined, updateLocale(defaultLocale));
    expect(localeState).toMatchObject({
      currentLocale: defaultLocale,
    });
    expect(TranslatorContext.context.locale).toEqual(defaultLocale);
  });

  it('should correctly detect update in current locale state', () => {
    TranslatorContext.setLocale(defaultLocale);
    expect(TranslatorContext.context.locale).toEqual(defaultLocale);
    const localeState = locale(
      {
        currentLocale: defaultLocale,
        lastChange: Date.now(),
        loadedKeys: [],
      },
      updateLocale('es'),
    );
    expect(localeState).toMatchObject({
      currentLocale: 'es',
    });
    expect(TranslatorContext.context.locale).toEqual('es');
  });

  describe('setLocale reducer', () => {
    beforeEach(() => {
      dispatch.mockClear();
    });

    describe('with default language loaded', () => {
      it('dispatches updateLocale action for default locale', async () => {
        TranslatorContext.setDefaultLocale(defaultLocale);
        const getState = vi.fn(() => ({ locale: { loadedKeys: [defaultLocale] } }));
        const result = await setLocale(defaultLocale)(dispatch, getState, extra);
        expect(dispatch).toHaveBeenCalledWith(
          expect.objectContaining({
            type: setLocale.pending.type,
            meta: expect.objectContaining({ requestStatus: 'pending' }),
          }),
        );
        expect(dispatch).not.toHaveBeenCalledWith(expect.objectContaining({ type: loaded.type }));
        expect(dispatch).toHaveBeenCalledWith(updateLocale(defaultLocale));
        expect(setLocale.fulfilled.match(result)).toBe(true);
      });
    });

    describe('with no language loaded', () => {
      it('dispatches loaded and updateLocale action for default locale', async () => {
        TranslatorContext.setDefaultLocale(defaultLocale);
        const getState = vi.fn(() => ({ locale: { loadedKeys: [] } }));
        const result = await setLocale(defaultLocale)(dispatch, getState, extra);
        expect(dispatch).toHaveBeenCalledWith(loaded({ keys: [defaultLocale] }));
        expect(dispatch).toHaveBeenCalledWith(updateLocale(defaultLocale));
        expect(setLocale.fulfilled.match(result)).toBe(true);
      });
    });
  });

  describe('loaded reducer', () => {
    let initialState;

    beforeEach(() => {
      initialState = { currentLocale: defaultLocale, lastChange: 0, loadedKeys: [] };
    });

    it("and empty parameter, doesn't add anything", () => {
      const localeState = locale(initialState, loaded({}));
      expect(localeState).toMatchObject({ currentLocale: defaultLocale, loadedKeys: [] });
    });

    it('and keys parameter, adds to loadedKeys', () => {
      const localeState = locale(initialState, loaded({ keys: ['foo'] }));
      expect(localeState).toMatchObject({ currentLocale: defaultLocale, loadedKeys: ['foo'] });
    });
  });
});
