export interface IModel {
  id: number;
  name?: string | null;
}

export type NewModel = Omit<IModel, 'id'> & { id: null };
