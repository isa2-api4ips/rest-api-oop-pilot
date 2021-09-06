package eu.europa.ec.isa2.restapi.profile.enums;

public enum APIProblemType {
    // defined in messaging AI
    MESSAGE_ACCEPTED("MessageAccepted","message-accepted",	202, false, false,"Message Accepted","https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md#message-accepted", "Sent when the message is properly validated. It may include a status monitor that can provide the user with an estimate of when the request will be fulfilled (see [RFC7231])", null),
    VALIDATION_FAILED("ValidationFailed",	"message-validation-failed",400, true, false,"Validation Failed", "https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md#message-validation-failed", "Sent when the message fails the validation process", null),
    INVALID_MESSAGE_ID("InvalidMessageId","invalid-message-id",	400, true, false,"Invalid or Duplicate Message ID", "https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md#invalid-message-id", "Sent when the MessageId is not valid", null),
    INVALID_SIGNATURE("InvalidSignature",	"invalid-message-signature",400, true, false,"Invalid Message Signature", "https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md#invalid-message-signature", "Sent when the message signature cannot be verified", null),
    INVALID_ADDRESSING("InvalidAddressing","invalid-addressing",400, true, false,"Invalid Addressing", "https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md#invalid-addressing", "Sent when the Original Sender or Final Recipient(s) cannot be resolved", null),
    INVALID_MESSAGE_FORMAT("InvalidMessageFormat","invalid-format",400, true,false,"Invalid Message Format",  "https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md#invalid-format", "Sent when the message format does not adhere to the specification", null),
    NO_RECIPIENT("NoFinalRecipient","no-final-recipient",400, true, true,"No final recipient configured for the pulling user", "https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md#pull/no-final-recipient", "Sent when the server cannot resolve/match the pulling user to a final recipient", null),
    NO_MESSAGE("NoMessageFound","no-message-found",404, true, true,"No Message Found",	 "https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md#pull/no-message-found", "Sent when no message is found that maps to the pull request", null),
    UNAUTHORIZED("Unauthorized","unauthorized", 401, true, true,"Unauthorized", "https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md#pull/unauthorized", "Sent when the pull request is unauthorized", null),
    MESSAGE_READY("MessageReady","message-ready",201, false, true,"Message Response is ready", "https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md#message-ready", "An HTTP Request following [RFC7807] MUST be sent when a message response is ready to be retrieve", null),
    // added by implementation
    INTERNAL_SERVER_ERROR("InternalServerError","internal-server-error",500, true, false,"Internal Server Error", "https://github.com/isa2-api4ips/rest-api-profile/blob/main/messaging-api-specification/signal.md/internal-server-error", "The server encountered an unexpected condition that prevented it from fulfilling the request", null);

    String name;
    String fileName;
    String title;
    String type;
    Integer status;
    String detail;
    String instance;
    boolean isError;
    boolean isPullSpecific;

    APIProblemType(String name,String fileName, Integer status, boolean isError,boolean isPullSpecific, String title, String type, String detail, String instance) {
        this.name = name;
        this.fileName = fileName;
        this.title = title;
        this.type = type;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.isError = isError;
        this.isPullSpecific = isPullSpecific;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public String getInstance() {
        return instance;
    }

    public boolean isError() {
        return isError;
    }

    public boolean isPullSpecific() {
        return isPullSpecific;
    }
}
