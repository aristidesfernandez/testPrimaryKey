import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICommand } from '../command.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../command.test-samples';

import { CommandService } from './command.service';

const requireRestSample: ICommand = {
  ...sampleWithRequiredData,
};

describe('Command Service', () => {
  let service: CommandService;
  let httpMock: HttpTestingController;
  let expectedResult: ICommand | ICommand[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CommandService);
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

    it('should create a Command', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const command = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(command).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Command', () => {
      const command = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(command).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Command', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Command', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Command', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCommandToCollectionIfMissing', () => {
      it('should add a Command to an empty array', () => {
        const command: ICommand = sampleWithRequiredData;
        expectedResult = service.addCommandToCollectionIfMissing([], command);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(command);
      });

      it('should not add a Command to an array that contains it', () => {
        const command: ICommand = sampleWithRequiredData;
        const commandCollection: ICommand[] = [
          {
            ...command,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCommandToCollectionIfMissing(commandCollection, command);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Command to an array that doesn't contain it", () => {
        const command: ICommand = sampleWithRequiredData;
        const commandCollection: ICommand[] = [sampleWithPartialData];
        expectedResult = service.addCommandToCollectionIfMissing(commandCollection, command);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(command);
      });

      it('should add only unique Command to an array', () => {
        const commandArray: ICommand[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const commandCollection: ICommand[] = [sampleWithRequiredData];
        expectedResult = service.addCommandToCollectionIfMissing(commandCollection, ...commandArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const command: ICommand = sampleWithRequiredData;
        const command2: ICommand = sampleWithPartialData;
        expectedResult = service.addCommandToCollectionIfMissing([], command, command2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(command);
        expect(expectedResult).toContain(command2);
      });

      it('should accept null and undefined values', () => {
        const command: ICommand = sampleWithRequiredData;
        expectedResult = service.addCommandToCollectionIfMissing([], null, command, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(command);
      });

      it('should return initial array if no Command is added', () => {
        const commandCollection: ICommand[] = [sampleWithRequiredData];
        expectedResult = service.addCommandToCollectionIfMissing(commandCollection, undefined, null);
        expect(expectedResult).toEqual(commandCollection);
      });
    });

    describe('compareCommand', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCommand(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCommand(entity1, entity2);
        const compareResult2 = service.compareCommand(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCommand(entity1, entity2);
        const compareResult2 = service.compareCommand(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCommand(entity1, entity2);
        const compareResult2 = service.compareCommand(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
