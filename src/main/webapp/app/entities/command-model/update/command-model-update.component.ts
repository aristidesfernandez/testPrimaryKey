import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CommandModelFormService, CommandModelFormGroup } from './command-model-form.service';
import { ICommandModel } from '../command-model.model';
import { CommandModelService } from '../service/command-model.service';
import { ICommand } from 'app/entities/command/command.model';
import { CommandService } from 'app/entities/command/service/command.service';
import { IModel } from 'app/entities/model/model.model';
import { ModelService } from 'app/entities/model/service/model.service';

@Component({
  selector: 'jhi-command-model-update',
  templateUrl: './command-model-update.component.html',
})
export class CommandModelUpdateComponent implements OnInit {
  isSaving = false;
  commandModel: ICommandModel | null = null;

  commandsSharedCollection: ICommand[] = [];
  modelsSharedCollection: IModel[] = [];

  editForm: CommandModelFormGroup = this.commandModelFormService.createCommandModelFormGroup();

  constructor(
    protected commandModelService: CommandModelService,
    protected commandModelFormService: CommandModelFormService,
    protected commandService: CommandService,
    protected modelService: ModelService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCommand = (o1: ICommand | null, o2: ICommand | null): boolean => this.commandService.compareCommand(o1, o2);

  compareModel = (o1: IModel | null, o2: IModel | null): boolean => this.modelService.compareModel(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ commandModel }) => {
      this.commandModel = commandModel;
      if (commandModel) {
        this.updateForm(commandModel);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const commandModel = this.commandModelFormService.getCommandModel(this.editForm);
    if (commandModel.id !== null) {
      this.subscribeToSaveResponse(this.commandModelService.update(commandModel));
    } else {
      this.subscribeToSaveResponse(this.commandModelService.create(commandModel));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICommandModel>>): void {
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

  protected updateForm(commandModel: ICommandModel): void {
    this.commandModel = commandModel;
    this.commandModelFormService.resetForm(this.editForm, commandModel);

    this.commandsSharedCollection = this.commandService.addCommandToCollectionIfMissing<ICommand>(
      this.commandsSharedCollection,
      commandModel.command
    );
    this.modelsSharedCollection = this.modelService.addModelToCollectionIfMissing<IModel>(this.modelsSharedCollection, commandModel.model);
  }

  protected loadRelationshipsOptions(): void {
    this.commandService
      .query()
      .pipe(map((res: HttpResponse<ICommand[]>) => res.body ?? []))
      .pipe(
        map((commands: ICommand[]) => this.commandService.addCommandToCollectionIfMissing<ICommand>(commands, this.commandModel?.command))
      )
      .subscribe((commands: ICommand[]) => (this.commandsSharedCollection = commands));

    this.modelService
      .query()
      .pipe(map((res: HttpResponse<IModel[]>) => res.body ?? []))
      .pipe(map((models: IModel[]) => this.modelService.addModelToCollectionIfMissing<IModel>(models, this.commandModel?.model)))
      .subscribe((models: IModel[]) => (this.modelsSharedCollection = models));
  }
}
