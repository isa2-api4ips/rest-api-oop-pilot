export interface SearchTableValidationResult{
  validOperation: boolean;
  stringMessage?: string;

  listId?: Array<number>;
  listDeleteNotPermitedId?: Array<number>;
}
