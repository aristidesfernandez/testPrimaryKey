import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'command',
        data: { pageTitle: 'testPrimaryKeyApp.command.home.title' },
        loadChildren: () => import('./command/command.module').then(m => m.CommandModule),
      },
      {
        path: 'model',
        data: { pageTitle: 'testPrimaryKeyApp.model.home.title' },
        loadChildren: () => import('./model/model.module').then(m => m.ModelModule),
      },
      {
        path: 'command-model',
        data: { pageTitle: 'testPrimaryKeyApp.commandModel.home.title' },
        loadChildren: () => import('./command-model/command-model.module').then(m => m.CommandModelModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
