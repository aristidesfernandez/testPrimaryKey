import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../command-model.test-samples';

import { CommandModelFormService } from './command-model-form.service';

describe('CommandModel Form Service', () => {
  let service: CommandModelFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CommandModelFormService);
  });

  describe('Service methods', () => {
    describe('createCommandModelFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCommandModelFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            command: expect.any(Object),
            model: expect.any(Object),
          })
        );
      });

      it('passing ICommandModel should create a new form with FormGroup', () => {
        const formGroup = service.createCommandModelFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            command: expect.any(Object),
            model: expect.any(Object),
          })
        );
      });
    });

    describe('getCommandModel', () => {
      it('should return NewCommandModel for default CommandModel initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCommandModelFormGroup(sampleWithNewData);

        const commandModel = service.getCommandModel(formGroup) as any;

        expect(commandModel).toMatchObject(sampleWithNewData);
      });

      it('should return NewCommandModel for empty CommandModel initial value', () => {
        const formGroup = service.createCommandModelFormGroup();

        const commandModel = service.getCommandModel(formGroup) as any;

        expect(commandModel).toMatchObject({});
      });

      it('should return ICommandModel', () => {
        const formGroup = service.createCommandModelFormGroup(sampleWithRequiredData);

        const commandModel = service.getCommandModel(formGroup) as any;

        expect(commandModel).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICommandModel should not enable id FormControl', () => {
        const formGroup = service.createCommandModelFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCommandModel should disable id FormControl', () => {
        const formGroup = service.createCommandModelFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
