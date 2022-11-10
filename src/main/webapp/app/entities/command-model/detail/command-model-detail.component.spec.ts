import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CommandModelDetailComponent } from './command-model-detail.component';

describe('CommandModel Management Detail Component', () => {
  let comp: CommandModelDetailComponent;
  let fixture: ComponentFixture<CommandModelDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CommandModelDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ commandModel: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CommandModelDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CommandModelDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load commandModel on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.commandModel).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
