package eu.europa.ec.isa2.oop.dsd.model;




import java.util.ArrayList;
import java.util.List;

public class OrganizationRO extends BaseRO {

    String identifier;
    List<String> prefLabels;
    List<String> altLabels;
    List<String> classifications;
    AddressRO addressRO;

    public OrganizationRO() {
    }

    public OrganizationRO(String identifier, List<String> prefLabels, List<String> altLabels, List<String> classifications, AddressRO addressRO) {
        this.identifier = identifier;
        getPrefLabels().addAll(prefLabels);
        getAltLabels().addAll(altLabels);
        getClassifications().addAll(classifications);
        this.addressRO = addressRO;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public AddressRO getAddress() {
        return addressRO;
    }

    public void setAddress(AddressRO addressRO) {
        this.addressRO = addressRO;
    }

    public List<String> getPrefLabels() {
        if (prefLabels == null){
            prefLabels = new ArrayList<>();
        }
        return prefLabels;
    }


    public List<String> getAltLabels() {
        if (altLabels == null){
            altLabels = new ArrayList<>();
        }
        return altLabels;
    }


    public List<String> getClassifications() {
        if (classifications == null){
            classifications = new ArrayList<>();
        }
        return classifications;
    }

}
