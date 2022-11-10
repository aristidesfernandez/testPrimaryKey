import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICommandModel } from '../command-model.model';

@Component({
  selector: 'jhi-command-model-detail',
  templateUrl: './command-model-detail.component.html',
})
export class CommandModelDetailComponent implements OnInit {
  commandModel: ICommandModel | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ commandModel }) => {
      this.commandModel = commandModel;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
