import { ICommand } from 'app/entities/command/command.model';
import { IModel } from 'app/entities/model/model.model';

export interface ICommandModel {
  id: number;
  command?: Pick<ICommand, 'id'> | null;
  model?: Pick<IModel, 'id'> | null;
}

export type NewCommandModel = Omit<ICommandModel, 'id'> & { id: null };
