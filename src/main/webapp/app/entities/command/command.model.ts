export interface ICommand {
  id: number;
  code?: string | null;
}

export type NewCommand = Omit<ICommand, 'id'> & { id: null };
