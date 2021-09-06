package eu.europa.ec.isa2.restapi.profile.constants;

public class MessagingConstants {


    public static final String PATH_SEPARATOR  = "/";

    public static final String OPENAPI_REF_COMPONENT = "#/components/";
    public static final String OPENAPI_SUBPATH_SCHEMAS = "schemas/";
    public static final String OPENAPI_SUBPATH_REQUESTS = "requests/";
    public static final String OPENAPI_SUBPATH_PARAMETERS = "parameters/";
    public static final String OPENAPI_SUBPATH_HEADERS = "headers/";

    public static final String OPENAPI_REF_PATH_SCHEMAS = OPENAPI_REF_COMPONENT + OPENAPI_SUBPATH_SCHEMAS;
    public static final String OPENAPI_REF_PATH_REQUESTS = OPENAPI_REF_COMPONENT + OPENAPI_SUBPATH_REQUESTS;
    public static final String OPENAPI_REF_PATH_PARAMETERS = OPENAPI_REF_COMPONENT + OPENAPI_SUBPATH_PARAMETERS;
    public static final String OPENAPI_REF_PATH_HEADERS = OPENAPI_REF_COMPONENT + OPENAPI_SUBPATH_HEADERS;

    public static final String OPENAPI_SCHEMA_NAME_PROBLEM = "Problem";
    public static final String OPENAPI_SCHEMA_NAME_SIGNAL_MESSAGE = "SignalMessage";
    public static final String OPENAPI_SCHEMA_NAME_REFERENCE_LIST_MESSAGE = "MessageReferenceList";

    public static final String GET_METHOD = "get";
    public static final String POST_METHOD = "post";


    public static final String HEADER_REQUEST_TARGET="(Request-Target)";
    public static final String HEADER_DIGEST="Digest";

}
