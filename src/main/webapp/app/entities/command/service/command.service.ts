import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICommand, NewCommand } from '../command.model';

export type PartialUpdateCommand = Partial<ICommand> & Pick<ICommand, 'id'>;

export type EntityResponseType = HttpResponse<ICommand>;
export type EntityArrayResponseType = HttpResponse<ICommand[]>;

@Injectable({ providedIn: 'root' })
export class CommandService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/commands');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(command: NewCommand): Observable<EntityResponseType> {
    return this.http.post<ICommand>(this.resourceUrl, command, { observe: 'response' });
  }

  update(command: ICommand): Observable<EntityResponseType> {
    return this.http.put<ICommand>(`${this.resourceUrl}/${this.getCommandIdentifier(command)}`, command, { observe: 'response' });
  }

  partialUpdate(command: PartialUpdateCommand): Observable<EntityResponseType> {
    return this.http.patch<ICommand>(`${this.resourceUrl}/${this.getCommandIdentifier(command)}`, command, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICommand>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICommand[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCommandIdentifier(command: Pick<ICommand, 'id'>): number {
    return command.id;
  }

  compareCommand(o1: Pick<ICommand, 'id'> | null, o2: Pick<ICommand, 'id'> | null): boolean {
    return o1 && o2 ? this.getCommandIdentifier(o1) === this.getCommandIdentifier(o2) : o1 === o2;
  }

  addCommandToCollectionIfMissing<Type extends Pick<ICommand, 'id'>>(
    commandCollection: Type[],
    ...commandsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const commands: Type[] = commandsToCheck.filter(isPresent);
    if (commands.length > 0) {
      const commandCollectionIdentifiers = commandCollection.map(commandItem => this.getCommandIdentifier(commandItem)!);
      const commandsToAdd = commands.filter(commandItem => {
        const commandIdentifier = this.getCommandIdentifier(commandItem);
        if (commandCollectionIdentifiers.includes(commandIdentifier)) {
          return false;
        }
        commandCollectionIdentifiers.push(commandIdentifier);
        return true;
      });
      return [...commandsToAdd, ...commandCollection];
    }
    return commandCollection;
  }
}
