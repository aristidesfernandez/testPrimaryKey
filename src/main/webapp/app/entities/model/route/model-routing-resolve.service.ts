import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IModel } from '../model.model';
import { ModelService } from '../service/model.service';

@Injectable({ providedIn: 'root' })
export class ModelRoutingResolveService implements Resolve<IModel | null> {
  constructor(protected service: ModelService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IModel | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((model: HttpResponse<IModel>) => {
          if (model.body) {
            return of(model.body);
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
