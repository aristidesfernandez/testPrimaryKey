import { ICommand, NewCommand } from './command.model';

export const sampleWithRequiredData: ICommand = {
  id: 51722,
};

export const sampleWithPartialData: ICommand = {
  id: 95434,
};

export const sampleWithFullData: ICommand = {
  id: 18116,
  code: 'Bacon Camiseta',
};

export const sampleWithNewData: NewCommand = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
