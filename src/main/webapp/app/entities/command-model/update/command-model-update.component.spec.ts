import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CommandModelFormService } from './command-model-form.service';
import { CommandModelService } from '../service/command-model.service';
import { ICommandModel } from '../command-model.model';
import { ICommand } from 'app/entities/command/command.model';
import { CommandService } from 'app/entities/command/service/command.service';
import { IModel } from 'app/entities/model/model.model';
import { ModelService } from 'app/entities/model/service/model.service';

import { CommandModelUpdateComponent } from './command-model-update.component';

describe('CommandModel Management Update Component', () => {
  let comp: CommandModelUpdateComponent;
  let fixture: ComponentFixture<CommandModelUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let commandModelFormService: CommandModelFormService;
  let commandModelService: CommandModelService;
  let commandService: CommandService;
  let modelService: ModelService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CommandModelUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CommandModelUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CommandModelUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    commandModelFormService = TestBed.inject(CommandModelFormService);
    commandModelService = TestBed.inject(CommandModelService);
    commandService = TestBed.inject(CommandService);
    modelService = TestBed.inject(ModelService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Command query and add missing value', () => {
      const commandModel: ICommandModel = { id: 456 };
      const command: ICommand = { id: 89832 };
      commandModel.command = command;

      const commandCollection: ICommand[] = [{ id: 30854 }];
      jest.spyOn(commandService, 'query').mockReturnValue(of(new HttpResponse({ body: commandCollection })));
      const additionalCommands = [command];
      const expectedCollection: ICommand[] = [...additionalCommands, ...commandCollection];
      jest.spyOn(commandService, 'addCommandToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commandModel });
      comp.ngOnInit();

      expect(commandService.query).toHaveBeenCalled();
      expect(commandService.addCommandToCollectionIfMissing).toHaveBeenCalledWith(
        commandCollection,
        ...additionalCommands.map(expect.objectContaining)
      );
      expect(comp.commandsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Model query and add missing value', () => {
      const commandModel: ICommandModel = { id: 456 };
      const model: IModel = { id: 84731 };
      commandModel.model = model;

      const modelCollection: IModel[] = [{ id: 22080 }];
      jest.spyOn(modelService, 'query').mockReturnValue(of(new HttpResponse({ body: modelCollection })));
      const additionalModels = [model];
      const expectedCollection: IModel[] = [...additionalModels, ...modelCollection];
      jest.spyOn(modelService, 'addModelToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commandModel });
      comp.ngOnInit();

      expect(modelService.query).toHaveBeenCalled();
      expect(modelService.addModelToCollectionIfMissing).toHaveBeenCalledWith(
        modelCollection,
        ...additionalModels.map(expect.objectContaining)
      );
      expect(comp.modelsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const commandModel: ICommandModel = { id: 456 };
      const command: ICommand = { id: 28926 };
      commandModel.command = command;
      const model: IModel = { id: 25178 };
      commandModel.model = model;

      activatedRoute.data = of({ commandModel });
      comp.ngOnInit();

      expect(comp.commandsSharedCollection).toContain(command);
      expect(comp.modelsSharedCollection).toContain(model);
      expect(comp.commandModel).toEqual(commandModel);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICommandModel>>();
      const commandModel = { id: 123 };
      jest.spyOn(commandModelFormService, 'getCommandModel').mockReturnValue(commandModel);
      jest.spyOn(commandModelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commandModel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: commandModel }));
      saveSubject.complete();

      // THEN
      expect(commandModelFormService.getCommandModel).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(commandModelService.update).toHaveBeenCalledWith(expect.objectContaining(commandModel));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICommandModel>>();
      const commandModel = { id: 123 };
      jest.spyOn(commandModelFormService, 'getCommandModel').mockReturnValue({ id: null });
      jest.spyOn(commandModelService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commandModel: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: commandModel }));
      saveSubject.complete();

      // THEN
      expect(commandModelFormService.getCommandModel).toHaveBeenCalled();
      expect(commandModelService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICommandModel>>();
      const commandModel = { id: 123 };
      jest.spyOn(commandModelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commandModel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(commandModelService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCommand', () => {
      it('Should forward to commandService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(commandService, 'compareCommand');
        comp.compareCommand(entity, entity2);
        expect(commandService.compareCommand).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareModel', () => {
      it('Should forward to modelService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(modelService, 'compareModel');
        comp.compareModel(entity, entity2);
        expect(modelService.compareModel).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
