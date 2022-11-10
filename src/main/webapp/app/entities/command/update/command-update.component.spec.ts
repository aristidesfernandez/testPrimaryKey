import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CommandFormService } from './command-form.service';
import { CommandService } from '../service/command.service';
import { ICommand } from '../command.model';

import { CommandUpdateComponent } from './command-update.component';

describe('Command Management Update Component', () => {
  let comp: CommandUpdateComponent;
  let fixture: ComponentFixture<CommandUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let commandFormService: CommandFormService;
  let commandService: CommandService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CommandUpdateComponent],
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
      .overrideTemplate(CommandUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CommandUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    commandFormService = TestBed.inject(CommandFormService);
    commandService = TestBed.inject(CommandService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const command: ICommand = { id: 456 };

      activatedRoute.data = of({ command });
      comp.ngOnInit();

      expect(comp.command).toEqual(command);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICommand>>();
      const command = { id: 123 };
      jest.spyOn(commandFormService, 'getCommand').mockReturnValue(command);
      jest.spyOn(commandService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ command });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: command }));
      saveSubject.complete();

      // THEN
      expect(commandFormService.getCommand).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(commandService.update).toHaveBeenCalledWith(expect.objectContaining(command));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICommand>>();
      const command = { id: 123 };
      jest.spyOn(commandFormService, 'getCommand').mockReturnValue({ id: null });
      jest.spyOn(commandService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ command: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: command }));
      saveSubject.complete();

      // THEN
      expect(commandFormService.getCommand).toHaveBeenCalled();
      expect(commandService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICommand>>();
      const command = { id: 123 };
      jest.spyOn(commandService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ command });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(commandService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
