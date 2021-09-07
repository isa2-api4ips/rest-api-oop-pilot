import {SearchTableEntity} from "../../../common/search-table/search-table-entity.model";

export interface DsdRequestMessageModel extends SearchTableEntity {

  headers: Map<string, string>;
  body: string;

}
