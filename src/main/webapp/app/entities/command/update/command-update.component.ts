import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { CommandFormService, CommandFormGroup } from './command-form.service';
import { ICommand } from '../command.model';
import { CommandService } from '../service/command.service';

@Component({
  selector: 'jhi-command-update',
  templateUrl: './command-update.component.html',
})
export class CommandUpdateComponent implements OnInit {
  isSaving = false;
  command: ICommand | null = null;

  editForm: CommandFormGroup = this.commandFormService.createCommandFormGroup();

  constructor(
    protected commandService: CommandService,
    protected commandFormService: CommandFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ command }) => {
      this.command = command;
      if (command) {
        this.updateForm(command);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const command = this.commandFormService.getCommand(this.editForm);
    if (command.id !== null) {
      this.subscribeToSaveResponse(this.commandService.update(command));
    } else {
      this.subscribeToSaveResponse(this.commandService.create(command));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICommand>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(command: ICommand): void {
    this.command = command;
    this.commandFormService.resetForm(this.editForm, command);
  }
}
