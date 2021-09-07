import {SearchTableEntity} from "../../../common/search-table/search-table-entity.model";
import {OrganizationAddress} from "./organization-address.model";
import {OrganizationUpdate} from "./organization-update.model";

export interface OrganizationModel extends SearchTableEntity {
  identifier: string;
  prefLabels: string[];
  altLabels: string[];
  classifications: string[];
  address: OrganizationAddress;
  dsdStatus?: string;
  dsdMessage?: string;
  updateRequestId?: string;
  updateResponseId?: string;
  updateRequestOn?: string;
  updateConfirmedOn?: string;
  updateRequests?: OrganizationUpdate[]

}
