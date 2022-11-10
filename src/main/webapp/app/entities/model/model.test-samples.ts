import { IModel, NewModel } from './model.model';

export const sampleWithRequiredData: IModel = {
  id: 76257,
};

export const sampleWithPartialData: IModel = {
  id: 93142,
  name: 'unleash',
};

export const sampleWithFullData: IModel = {
  id: 90822,
  name: 'mano Joyería extend',
};

export const sampleWithNewData: NewModel = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
