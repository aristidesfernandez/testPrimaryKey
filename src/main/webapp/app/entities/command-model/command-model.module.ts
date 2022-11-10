import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CommandModelComponent } from './list/command-model.component';
import { CommandModelDetailComponent } from './detail/command-model-detail.component';
import { CommandModelUpdateComponent } from './update/command-model-update.component';
import { CommandModelDeleteDialogComponent } from './delete/command-model-delete-dialog.component';
import { CommandModelRoutingModule } from './route/command-model-routing.module';

@NgModule({
  imports: [SharedModule, CommandModelRoutingModule],
  declarations: [CommandModelComponent, CommandModelDetailComponent, CommandModelUpdateComponent, CommandModelDeleteDialogComponent],
})
export class CommandModelModule {}
