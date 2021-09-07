package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.oauth2;

public class OAuthException extends Exception{
    private OAuthErrorResponse.OAuthErrorCode errorCode;
    private String errorDescription;

    OAuthException(){};

    public OAuthException(OAuthErrorResponse.OAuthErrorCode errorCode, String errorDescription){
        super(errorDescription);
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }
    public OAuthException(OAuthErrorResponse.OAuthErrorCode errorCode, String errorDescription, Exception e){
        super(errorDescription, e);
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public OAuthErrorResponse.OAuthErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

}
