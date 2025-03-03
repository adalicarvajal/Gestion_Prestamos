import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending } from '@reduxjs/toolkit';
import { cleanEntity } from 'app/shared/util/entity-utils';
import { EntityState, IQueryParams, createEntitySlice, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IAmortization, defaultValue } from 'app/shared/model/amortization.model';

const initialState: EntityState<IAmortization> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/amortizations';

// Actions

export const getEntities = createAsyncThunk(
  'amortization/fetch_entity_list',
  async ({ page, size, sort }: IQueryParams) => {
    const requestUrl = `${apiUrl}?${sort ? `page=${page}&size=${size}&sort=${sort}&` : ''}cacheBuster=${new Date().getTime()}`;
    return axios.get<IAmortization[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const getEntitiesByUserId = createAsyncThunk(
  'amortization/fetch_entity_list_by_user',
  async ({ userId, page, size, sort }: { userId: number; page: number; size: number; sort: string }) => {
    const requestUrl = `${apiUrl}/by-user/${userId}?${sort ? `page=${page}&size=${size}&sort=${sort}&` : ''}cacheBuster=${new Date().getTime()}`;
    return axios.get<IAmortization[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const getEntitiesByLoanId = createAsyncThunk(
  'amortization/fetch_entity_list_by_loan',
  async ({ loanId, page, size, sort }: { loanId: string; page?: number; size?: number; sort?: string }) => {
    const requestUrl = `${apiUrl}/by-loan/${loanId}?${sort ? `page=${page}&size=${size}&sort=${sort}&` : ''}cacheBuster=${new Date().getTime()}`;
    return axios.get<IAmortization[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const getEntitiesByCurrentUser = createAsyncThunk(
  'amortization/fetch_entity_list_by_current_user',
  async ({ page, size, sort }: { page: number; size: number; sort: string }) => {
    const requestUrl = `${apiUrl}/my-loans?${sort ? `page=${page}&size=${size}&sort=${sort}&` : ''}cacheBuster=${new Date().getTime()}`;
    return axios.get<IAmortization[]>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);


export const getEntity = createAsyncThunk(
  'amortization/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IAmortization>(requestUrl);
  },
  { serializeError: serializeAxiosError },
);

export const createEntity = createAsyncThunk(
  'amortization/create_entity',
  async (entity: IAmortization, thunkAPI) => {
    const result = await axios.post<IAmortization>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const updateEntity = createAsyncThunk(
  'amortization/update_entity',
  async (entity: IAmortization, thunkAPI) => {
    const result = await axios.put<IAmortization>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const partialUpdateEntity = createAsyncThunk(
  'amortization/partial_update_entity',
  async (entity: IAmortization, thunkAPI) => {
    const result = await axios.patch<IAmortization>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

export const deleteEntity = createAsyncThunk(
  'amortization/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IAmortization>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError },
);

// slice

export const AmortizationSlice = createEntitySlice({
  name: 'amortization',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(getEntitiesByUserId.fulfilled, (state, action) => {
        state.loading = false;
        state.entities = action.payload.data;
      })
      .addCase(getEntitiesByLoanId.fulfilled, (state, action) => {
        state.loading = false;
        state.entities = action.payload.data;
      })
      .addCase(getEntitiesByCurrentUser.fulfilled, (state, action) => {
        state.loading = false;
        state.entities = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data, headers } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
          totalItems: parseInt(headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = AmortizationSlice.actions;

// Reducer
export default AmortizationSlice.reducer;
