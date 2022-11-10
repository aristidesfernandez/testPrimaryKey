import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICommandModel } from '../command-model.model';
import { CommandModelService } from '../service/command-model.service';

@Injectable({ providedIn: 'root' })
export class CommandModelRoutingResolveService implements Resolve<ICommandModel | null> {
  constructor(protected service: CommandModelService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICommandModel | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((commandModel: HttpResponse<ICommandModel>) => {
          if (commandModel.body) {
            return of(commandModel.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
