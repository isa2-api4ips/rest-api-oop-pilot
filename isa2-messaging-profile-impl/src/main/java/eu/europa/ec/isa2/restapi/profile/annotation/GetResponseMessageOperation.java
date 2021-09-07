package eu.europa.ec.isa2.restapi.profile.annotation;


import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("GET")
public @interface GetResponseMessageOperation {
    String service() default "";
    String action() default "";
    String messageIdParamName() default "messageId";
    String responseService() default "";
    String responseAction() default "";
    String responseMessageIdParamName() default "rMessageId";
    String responseTitle() default "";
    String responseDescription() default "";
    MultipartPayload[] responsePayloads()  default {};
    SecurityRequirement[] securityRequirements()  default {};


    /**
     * Tags can be used for logical grouping of operations by resources or any other qualifier.
     *
     * @return the list of tags associated with this operation
     **/
    String[] tags() default {};

    /**
     * Provides a brief description of this operation. Should be 120 characters or less for proper visibility in Swagger-UI.
     *
     * @return a summary of this operation
     **/
    String summary() default "";

    /**
     * A verbose description of the operation.
     *
     * @return a description of this operation
     **/
    String description() default "";

    /**
     * Additional external documentation for this operation.
     *
     * @return additional documentation about this operation
     **/
    ExternalDocumentation externalDocs() default @ExternalDocumentation();

    /**
     * The operationId is used by third-party tools to uniquely identify this operation.
     *
     * @return the ID of this operation
     **/
    String operationId() default "";





}
