package eu.europa.ec.isa2.restapi.profile;


import eu.europa.ec.isa2.restapi.profile.enums.DocumentMaturityType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

@Schema(
        name = "x-edelivery",
        title = "eDelivery profile extension",
        description = "eDelivery profile extensions: publisher and lifecycle"
)
public class EDeliveryAPIExtension {
    @Schema(
            name = "publisher",
            title = "eDelivery publisher extension",
            description = "to support a common requirement of API repositories, the OpenAPI Document MUST contain the info.x-edelivery.publisher "
    )
    EDelApiExtensionPublisher publisher;
    @Schema(
            name = "lifecycle",
            title = "lifecycle metadata of the API",
            description = "APIs conformant to this profile MUST publish information about their maturity level. To provide lifecycle metadata of the API such as its maturity, deprecation and sunset, the OpenAPI Document MUST contain the info.x-edelivery.lifecycle object"
    )
    EDelApiExtensionLifecycle lifecycle;

    public EDelApiExtensionPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(EDelApiExtensionPublisher publisher) {
        this.publisher = publisher;
    }

    public EDelApiExtensionLifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(EDelApiExtensionLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }


    public static class EDelApiExtensionPublisher implements Serializable {
        @Schema(name = "name",
                description = "The name of the publisher")
        String name;
        @Schema(name = "URL",
                description = "The URL pointing to a web page providing information about the publisher")
        URL url;

        public EDelApiExtensionPublisher(String name, URL url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public URL getUrl() {
            return url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }
    }

    public static class EDelApiExtensionLifecycle {

        @Schema(required = true,
                name = "maturity",
                description = "The maturity level of the API. It MUST contain one of the following values: development,supported,deprecated.",
                defaultValue = "development")
        DocumentMaturityType maturity = DocumentMaturityType.DEVELOPMENT;
        @Schema(name = "deprecatedAt",
                description = "The date when the API has been deprecated. The date format MUST follow [RFC3339]")
        Date deprecatedAt;
        @Schema(name = "sunsetAt",
                description = "The date when the API will be sunset. The date format MUST follow [RFC3339]")
        Date sunsetAt;


        public DocumentMaturityType getMaturity() {
            return maturity;
        }

        public void setMaturity(DocumentMaturityType maturity) {
            this.maturity = maturity;
        }

        public Date getDeprecatedAt() {
            return deprecatedAt;
        }

        public void setDeprecatedAt(Date deprecatedAt) {
            this.deprecatedAt = deprecatedAt;
        }

        public Date getSunsetAt() {
            return sunsetAt;
        }

        public void setSunsetAt(Date sunsetAt) {
            this.sunsetAt = sunsetAt;
        }
    }
}
