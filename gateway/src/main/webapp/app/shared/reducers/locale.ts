import { TranslatorContext } from 'react-jhipster';

import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import dayjs from 'dayjs';

const initialState = {
  currentLocale: '',
  lastChange: TranslatorContext.context.lastChange,
  loadedKeys: [] as string[],
};

export type LocaleState = Readonly<typeof initialState>;

const registerTranslations = async (locale: string) => {
  const translations = await import(`../../../i18n/${locale}/${locale}.js`);
  TranslatorContext.registerTranslations(locale, translations.default);
};

export const setLocale = createAsyncThunk('locale/setLocale', async (locale: string, thunkAPI: any) => {
  const { loadedKeys } = thunkAPI.getState().locale;
  if (!loadedKeys.includes(locale)) {
    await registerTranslations(locale);
    thunkAPI.dispatch(loaded({ keys: [locale] }));
  }
  thunkAPI.dispatch(updateLocale(locale));
  return locale;
});

export const LocaleSlice = createSlice({
  name: 'locale',
  initialState,
  reducers: {
    updateLocale(state, action) {
      const currentLocale = action.payload;
      if (state.currentLocale !== currentLocale) {
        dayjs.locale(currentLocale);
        TranslatorContext.setLocale(currentLocale);
      }
      state.currentLocale = currentLocale;
    },
    loaded(state, action) {
      const { keys } = action.payload;
      if (keys) {
        state.loadedKeys = state.loadedKeys.concat(keys);
      }
      state.lastChange = TranslatorContext.context.lastChange;
    },
  },
});

export const { updateLocale, loaded } = LocaleSlice.actions;

// Reducer
export default LocaleSlice.reducer;
