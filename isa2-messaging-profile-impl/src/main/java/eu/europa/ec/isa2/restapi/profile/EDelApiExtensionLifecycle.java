package eu.europa.ec.isa2.restapi.profile;

import eu.europa.ec.isa2.restapi.profile.enums.DocumentMaturityType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;


@Schema(
        name = "x-edel-lifecycle",
        title = "The x-edel-lifecycle object",
        description = "The x-edel-lifecycle object is a specification extension defined in the API Core Profile. Its main purpose is to provide lifecycle metadata of the API such as its maturity, deprecation and sunset"
)
public class EDelApiExtensionLifecycle {

    @Schema(required = true,
            name = "maturity",
            description = "The maturity level of the API. It MUST contain one of the following values: development,supported,deprecated.",
            defaultValue = "development")
    DocumentMaturityType maturity= DocumentMaturityType.DEVELOPMENT;
    @Schema(name = "deprecated_at",
            description = "The date when the API has been deprecated. The date format MUST follow [RFC3339]")
    Date deprecatedAt;
    @Schema(name = "sunset_at",
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

    public void setSunsetAt(Date sunset_at) {
        this.sunsetAt = sunsetAt;
    }
}
