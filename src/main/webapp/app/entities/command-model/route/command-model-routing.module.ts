import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CommandModelComponent } from '../list/command-model.component';
import { CommandModelDetailComponent } from '../detail/command-model-detail.component';
import { CommandModelUpdateComponent } from '../update/command-model-update.component';
import { CommandModelRoutingResolveService } from './command-model-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const commandModelRoute: Routes = [
  {
    path: '',
    component: CommandModelComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CommandModelDetailComponent,
    resolve: {
      commandModel: CommandModelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CommandModelUpdateComponent,
    resolve: {
      commandModel: CommandModelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CommandModelUpdateComponent,
    resolve: {
      commandModel: CommandModelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(commandModelRoute)],
  exports: [RouterModule],
})
export class CommandModelRoutingModule {}
