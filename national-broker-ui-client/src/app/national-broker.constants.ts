export class NationalBrokerConstants {
  // todo do this property configurable!
  public static readonly REST_CONTEXT = '../national-broker';

  public static readonly REST_OATH_TOKEN = "/oauth/token";

  public static readonly REST_ADMIN = "/admin/v1/users"

  public static readonly REST_USER_ORGANIZATIONS        = "/dsd/v1/users/{USERNAME}/organizations";
  public static readonly REST_USER_ORGANIZATION_UPDATES = "/dsd/v1/users/{USERNAME}/organization/updates";
  public static readonly REST_USER_ORGANIZATION_MANAGE  = "/dsd-lcm/v1/users/{USERNAME}/organization";
  public static readonly REST_USER_DATASET        = "/dsd-lcm/v1/users/{USERNAME}/datasets";
  public static readonly REST_USER_DATASET_MANAGE = "/dsd-lcm/v1/users/{USERNAME}/dataset";




  public static readonly REST_USER_LOG_MESSAGES = "/dsd/v1/users/{USERNAME}/dsd-request/messages";
  public static readonly REST_USER_LOG_GET_MESSAGE = "/dsd/v1/users/{USERNAME}/dsd-request/message/{id}";
  public static readonly REST_USER_LOG_MESSAGE_DOWNLOAD = NationalBrokerConstants.REST_USER_LOG_GET_MESSAGE + "/download/{type}";

  // update this when authentication will be implemented!
  public static readonly REST_USER = 'rest/user';
  public static readonly REST_SECURITY_AUTHENTICATION = 'rest/security/authentication';
  public static readonly REST_SECURITY_USER = 'rest/security/authentication';

}
