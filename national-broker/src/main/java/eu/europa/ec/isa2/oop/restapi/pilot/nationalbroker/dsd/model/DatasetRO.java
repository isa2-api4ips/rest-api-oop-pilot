package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.BaseRO;

import java.util.ArrayList;
import java.util.List;

public class DatasetRO extends BaseRO {


    String type;
    String conformsTo;
    List<String> identifiers;
    List<String> titles;
    List<String> descriptions;
    OrganizationRO publisher;
    List<RelationshipRO> qualifiedRelationships;
    List<DistributionRO> distributions;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(String conformsTo) {
        this.conformsTo = conformsTo;
    }

    public List<String> getIdentifiers() {
        if (identifiers==null) {
            identifiers = new ArrayList<>();
        }
        return identifiers;
    }

    public List<String> getTitles() {
        if (titles == null) {
            titles = new ArrayList<>();
        }
        return titles;
    }

    public List<String> getDescriptions() {
        if (descriptions==null) {
            descriptions = new ArrayList<>();
        }
        return descriptions;
    }

    public OrganizationRO getPublisher() {
        return publisher;
    }

    public void setPublisher(OrganizationRO publisher) {
        this.publisher = publisher;
    }

    public List<RelationshipRO> getQualifiedRelationships() {
        if (qualifiedRelationships == null) {
            qualifiedRelationships = new ArrayList<>();
        }
        return qualifiedRelationships;
    }

    public List<DistributionRO> getDistributions() {
        if (distributions == null) {
            distributions = new ArrayList<>();
        }
        return distributions;
    }

}
