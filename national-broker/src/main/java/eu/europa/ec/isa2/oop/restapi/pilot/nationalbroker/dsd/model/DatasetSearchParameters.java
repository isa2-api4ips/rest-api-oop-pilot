package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model;


import eu.europa.ec.isa2.restapi.profile.constants.JSONConstants;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(

        name = "DatasetQuery",
        title = "Dataset query request",
        description = "Object contains search parameters to retrieve organization list",
        id = "https://joinup.ec.europa.eu/collection/api4dt/solution/.../dsd/dataset.query.schema.json",
        schema = JSONConstants.SCHEMA_V202012,
        requiredProperties = {"country"},
        type = "object"

)
public class DatasetSearchParameters {
    @Schema(name = "country", title = "ISO 3166 country parameter", description = "ISO 3166 country parameter", example = "BE")
    String country;
    @Schema(name = "name", title = "Partial country name", description = "Parameter is used for name search", example = "Company Name")
    String name;
    @Schema(name = "limit", title = "Result count limit",
            description = "The number of resources of a collection to be returned from a request. The limit MUST be a positive integer",
            defaultValue = "100", example = "100")
    Integer limit = new Integer(100);
    @Schema(name = "offset", title = "Offset of results",
            description = "The offset the response should start providing resources of the collection. It MUST be a positive integer",
            defaultValue = "0", example = "5")
    Integer offset = new Integer(0);;
    @Schema(name = "sort", title = "Sort the records",
            description = " Used to express the sorting order of resources in a collection. It MUST follow the following regular expression: (-|+)<field-name> (',' (-|+)<field-name>)*",
            defaultValue = "0", example = "+name,-country")
    String sort;

    public DatasetSearchParameters() {}
    public DatasetSearchParameters(String country, String name, Integer limit, Integer offset, String sort) {
        this.country = country;
        this.name = name;
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
