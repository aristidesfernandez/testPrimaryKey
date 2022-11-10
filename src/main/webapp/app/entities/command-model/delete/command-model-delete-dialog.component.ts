import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICommandModel } from '../command-model.model';
import { CommandModelService } from '../service/command-model.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './command-model-delete-dialog.component.html',
})
export class CommandModelDeleteDialogComponent {
  commandModel?: ICommandModel;

  constructor(protected commandModelService: CommandModelService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.commandModelService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
