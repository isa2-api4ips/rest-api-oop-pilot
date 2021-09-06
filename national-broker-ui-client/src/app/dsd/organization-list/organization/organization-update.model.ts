export interface OrganizationUpdate {
  dsdStatus?:string;
  dsdMessage?:string;
  updateRequestId?:string;
  updateResponseId?:string;
  updateRequestOn?:string;
  updateConfirmedOn?:string;
  username:string;
  organizationIdentifier?:string;
  service:string;
  action:string;
  entityType:string;
}
