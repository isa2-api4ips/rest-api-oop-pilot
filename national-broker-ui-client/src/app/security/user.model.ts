import {Authority} from "./authority.model";
import {NationalBrokerToken} from "./token.mode";

export interface RestApiUser {
  id: number;
  username: string;
  name?: string;
  email?: string;
  active?: boolean;
  userAuthRoles?: Array<String>;
  authorities: Array<Authority>;
  defaultPasswordUsed: boolean;
  idToken?:NationalBrokerToken;
}
