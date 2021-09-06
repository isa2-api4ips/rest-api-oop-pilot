import {SearchTableEntity} from "../../../common/search-table/search-table-entity.model";
import {DsdRequestMessageModel} from "./dsd-request-message.model";

export interface DsdRequestModel extends SearchTableEntity {

  dsdMessage: string;
  role: string;
  messageId: string;
  responseMessageId: string;
  requestOn: string;
  responseOn: string;
  requestStoragePath: string;
  responseStoragePath: string;
  username: string;
  dsdStatus: string;
  service: string;
  action: string;
  httpPath: string;
  httpMethod: string;

  requestMessage?: DsdRequestMessageModel;
  responseMessage?: DsdRequestMessageModel;
}
