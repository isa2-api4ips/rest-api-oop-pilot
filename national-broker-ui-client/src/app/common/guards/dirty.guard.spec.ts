import {inject, TestBed} from '@angular/core/testing';

import {DirtyGuard} from './dirty.guard';

describe('DirtyGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DirtyGuard]
    });
  });

  it('should ...', inject([DirtyGuard], (guard: DirtyGuard) => {
    expect(guard).toBeTruthy();
  }));
});
