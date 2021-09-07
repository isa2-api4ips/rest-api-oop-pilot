package eu.europa.ec.isa2.oop.dsd.model;


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

    @Schema(name = "organizationIdentifier", title = "Dataset publisher identifier/location", description = "Dataset publisher identifier/location", example = "100122")
    String organizationIdentifier;

    @Schema(name = "country", title = "ISO 3166 country parameter", description = "ISO 3166 country parameter", example = "BE")
    String country;

    @Schema(name = "datasetType", title = "Dataset type", description = "Dataset type", example = "CRIMINAL_CASE")
    String datasetType;

    @Schema(name = "limit", title = "Result count limit",
            description = "The number of resources of a collection to be returned from a request. The limit MUST be a positive integer",
            defaultValue = "50", example = "50")
    Integer limit = new Integer(50);
    @Schema(name = "offset", title = "Offset of results",
            description = "The offset the response should start providing resources of the collection. It MUST be a positive integer",
            defaultValue = "0", example = "5")
    Integer offset = new Integer(0);;
    @Schema(name = "sort", title = "Sort the records",
            description = " Used to express the sorting order of resources in a collection. It MUST follow the following regular expression: (-|+)<field-name> (',' (-|+)<field-name>)*",
            defaultValue = "+organizationIdentifier", example = "+organizationIdentifier,-country")
    String sort;

    @Schema(name = "queryId", title = "Identifier of the query",
            description = " Used to express the query id",
            defaultValue = "urn:toop:dsd:ebxml-regrem:queries:ByDatasetTypeAndLocation", example = "urn:toop:dsd:ebxml-regrem:queries:ByDatasetTypeAndLocation")
    String queryId;

    public DatasetSearchParameters() {
    }
    public DatasetSearchParameters(String country, String organizationIdentifier, Integer limit, Integer offset, String sort) {
        this.organizationIdentifier = organizationIdentifier;
        this.country = country;
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

    public String getOrganizationIdentifier() {
        return organizationIdentifier;
    }

    public void setOrganizationIdentifier(String organizationIdentifier) {
        this.organizationIdentifier = organizationIdentifier;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getDatasetType() {
        return datasetType;
    }

    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
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
