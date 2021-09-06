package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.filters;



public class OrganizationFilterRO {
    String queryId;
    String identifierLike;
    String organizationNameLike;
    String adminUnitLevel;

    public String getIdentifierLike() {
        return identifierLike;
    }

    public void setIdentifierLike(String identifierLike) {
        this.identifierLike = identifierLike;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getOrganizationNameLike() {
        return organizationNameLike;
    }

    public void setOrganizationNameLike(String organizationNameLike) {
        this.organizationNameLike = organizationNameLike;
    }

    public String getAdminUnitLevel() {
        return adminUnitLevel;
    }

    public void setAdminUnitLevel(String adminUnitLevel) {
        this.adminUnitLevel = adminUnitLevel;
    }
}
