import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICommandModel } from '../command-model.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../command-model.test-samples';

import { CommandModelService } from './command-model.service';

const requireRestSample: ICommandModel = {
  ...sampleWithRequiredData,
};

describe('CommandModel Service', () => {
  let service: CommandModelService;
  let httpMock: HttpTestingController;
  let expectedResult: ICommandModel | ICommandModel[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CommandModelService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a CommandModel', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const commandModel = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(commandModel).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CommandModel', () => {
      const commandModel = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(commandModel).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CommandModel', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CommandModel', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CommandModel', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCommandModelToCollectionIfMissing', () => {
      it('should add a CommandModel to an empty array', () => {
        const commandModel: ICommandModel = sampleWithRequiredData;
        expectedResult = service.addCommandModelToCollectionIfMissing([], commandModel);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(commandModel);
      });

      it('should not add a CommandModel to an array that contains it', () => {
        const commandModel: ICommandModel = sampleWithRequiredData;
        const commandModelCollection: ICommandModel[] = [
          {
            ...commandModel,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCommandModelToCollectionIfMissing(commandModelCollection, commandModel);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CommandModel to an array that doesn't contain it", () => {
        const commandModel: ICommandModel = sampleWithRequiredData;
        const commandModelCollection: ICommandModel[] = [sampleWithPartialData];
        expectedResult = service.addCommandModelToCollectionIfMissing(commandModelCollection, commandModel);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(commandModel);
      });

      it('should add only unique CommandModel to an array', () => {
        const commandModelArray: ICommandModel[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const commandModelCollection: ICommandModel[] = [sampleWithRequiredData];
        expectedResult = service.addCommandModelToCollectionIfMissing(commandModelCollection, ...commandModelArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const commandModel: ICommandModel = sampleWithRequiredData;
        const commandModel2: ICommandModel = sampleWithPartialData;
        expectedResult = service.addCommandModelToCollectionIfMissing([], commandModel, commandModel2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(commandModel);
        expect(expectedResult).toContain(commandModel2);
      });

      it('should accept null and undefined values', () => {
        const commandModel: ICommandModel = sampleWithRequiredData;
        expectedResult = service.addCommandModelToCollectionIfMissing([], null, commandModel, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(commandModel);
      });

      it('should return initial array if no CommandModel is added', () => {
        const commandModelCollection: ICommandModel[] = [sampleWithRequiredData];
        expectedResult = service.addCommandModelToCollectionIfMissing(commandModelCollection, undefined, null);
        expect(expectedResult).toEqual(commandModelCollection);
      });
    });

    describe('compareCommandModel', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCommandModel(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCommandModel(entity1, entity2);
        const compareResult2 = service.compareCommandModel(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCommandModel(entity1, entity2);
        const compareResult2 = service.compareCommandModel(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCommandModel(entity1, entity2);
        const compareResult2 = service.compareCommandModel(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
