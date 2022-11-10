import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICommandModel, NewCommandModel } from '../command-model.model';

export type PartialUpdateCommandModel = Partial<ICommandModel> & Pick<ICommandModel, 'id'>;

export type EntityResponseType = HttpResponse<ICommandModel>;
export type EntityArrayResponseType = HttpResponse<ICommandModel[]>;

@Injectable({ providedIn: 'root' })
export class CommandModelService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/command-models');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(commandModel: NewCommandModel): Observable<EntityResponseType> {
    return this.http.post<ICommandModel>(this.resourceUrl, commandModel, { observe: 'response' });
  }

  update(commandModel: ICommandModel): Observable<EntityResponseType> {
    return this.http.put<ICommandModel>(`${this.resourceUrl}/${this.getCommandModelIdentifier(commandModel)}`, commandModel, {
      observe: 'response',
    });
  }

  partialUpdate(commandModel: PartialUpdateCommandModel): Observable<EntityResponseType> {
    return this.http.patch<ICommandModel>(`${this.resourceUrl}/${this.getCommandModelIdentifier(commandModel)}`, commandModel, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICommandModel>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICommandModel[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCommandModelIdentifier(commandModel: Pick<ICommandModel, 'id'>): number {
    return commandModel.id;
  }

  compareCommandModel(o1: Pick<ICommandModel, 'id'> | null, o2: Pick<ICommandModel, 'id'> | null): boolean {
    return o1 && o2 ? this.getCommandModelIdentifier(o1) === this.getCommandModelIdentifier(o2) : o1 === o2;
  }

  addCommandModelToCollectionIfMissing<Type extends Pick<ICommandModel, 'id'>>(
    commandModelCollection: Type[],
    ...commandModelsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const commandModels: Type[] = commandModelsToCheck.filter(isPresent);
    if (commandModels.length > 0) {
      const commandModelCollectionIdentifiers = commandModelCollection.map(
        commandModelItem => this.getCommandModelIdentifier(commandModelItem)!
      );
      const commandModelsToAdd = commandModels.filter(commandModelItem => {
        const commandModelIdentifier = this.getCommandModelIdentifier(commandModelItem);
        if (commandModelCollectionIdentifiers.includes(commandModelIdentifier)) {
          return false;
        }
        commandModelCollectionIdentifiers.push(commandModelIdentifier);
        return true;
      });
      return [...commandModelsToAdd, ...commandModelCollection];
    }
    return commandModelCollection;
  }
}
