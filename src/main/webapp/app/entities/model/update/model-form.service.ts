import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IModel, NewModel } from '../model.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IModel for edit and NewModelFormGroupInput for create.
 */
type ModelFormGroupInput = IModel | PartialWithRequiredKeyOf<NewModel>;

type ModelFormDefaults = Pick<NewModel, 'id'>;

type ModelFormGroupContent = {
  id: FormControl<IModel['id'] | NewModel['id']>;
  name: FormControl<IModel['name']>;
};

export type ModelFormGroup = FormGroup<ModelFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ModelFormService {
  createModelFormGroup(model: ModelFormGroupInput = { id: null }): ModelFormGroup {
    const modelRawValue = {
      ...this.getFormDefaults(),
      ...model,
    };
    return new FormGroup<ModelFormGroupContent>({
      id: new FormControl(
        { value: modelRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(modelRawValue.name),
    });
  }

  getModel(form: ModelFormGroup): IModel | NewModel {
    return form.getRawValue() as IModel | NewModel;
  }

  resetForm(form: ModelFormGroup, model: ModelFormGroupInput): void {
    const modelRawValue = { ...this.getFormDefaults(), ...model };
    form.reset(
      {
        ...modelRawValue,
        id: { value: modelRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ModelFormDefaults {
    return {
      id: null,
    };
  }
}
