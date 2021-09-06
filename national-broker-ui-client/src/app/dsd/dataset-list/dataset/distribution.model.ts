import {DataServiceModel} from "./dataservice.model";

export interface DistributionModel {
  descriptions: string[];
  conformsTo: string;
  format: string;
  mediaType: string;
  accessURL: string;
  dataServices: DataServiceModel[];
}
