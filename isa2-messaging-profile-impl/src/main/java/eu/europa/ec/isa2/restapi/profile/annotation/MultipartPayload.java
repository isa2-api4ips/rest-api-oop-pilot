package eu.europa.ec.isa2.restapi.profile.annotation;


import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MultipartPayload {
    String name();
    String contentType();
    Class  instance();
    String example();


}
