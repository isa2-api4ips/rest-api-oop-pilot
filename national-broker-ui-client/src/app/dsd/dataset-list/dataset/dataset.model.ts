import {SearchTableEntity} from "../../../common/search-table/search-table-entity.model";
import {OrganizationModel} from "../../organization-list/organization/organization.model";
import {RelationshipModel} from "./relationship.model";
import {DistributionModel} from "./distribution.model";


export interface DatasetModel extends SearchTableEntity {

  type: string;
  conformsTo: string;
  identifiers: string[];
  titles: string[];
  descriptions: string[];
  publisher: OrganizationModel;
  qualifiedRelationships: RelationshipModel[];
  distributions: DistributionModel[];

}
