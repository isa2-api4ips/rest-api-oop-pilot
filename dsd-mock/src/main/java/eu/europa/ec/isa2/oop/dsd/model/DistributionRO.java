package eu.europa.ec.isa2.oop.dsd.model;

import java.util.ArrayList;
import java.util.List;

public class DistributionRO {

    List<String> descriptions;
    String conformsTo;
    String format;
    String mediaType;
    String accessURL;
    List<DataServiceRO> dataServices;


    public List<String> getDescriptions() {
        if (descriptions==null) {
            descriptions = new ArrayList<>();
        }
        return descriptions;
    }


    public String getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(String conformsTo) {
        this.conformsTo = conformsTo;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getAccessURL() {
        return accessURL;
    }

    public void setAccessURL(String accessURL) {
        this.accessURL = accessURL;
    }

    public List<DataServiceRO> getDataServices() {
        if (dataServices==null) {
            dataServices = new ArrayList<>();
        }
        return dataServices;
    }


}
