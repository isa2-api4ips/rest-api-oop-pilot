package eu.europa.ec.isa2.restapi.reader.utils;

import eu.europa.ec.isa2.restapi.profile.constants.MessagingConstants;
import eu.europa.ec.isa2.restapi.profile.enums.MessagingReferenceType;
import eu.europa.ec.isa2.restapi.reader.enums.MessagingAPIDefinitionsLocation;
import org.apache.commons.lang3.StringUtils;

import java.io.StringWriter;

public class MessagingObjectPathUtils {

    public String getExternalDefinitionURI(MessagingReferenceType referenceType, String typeSubPath, String messagingAPIURL, boolean asObject) {
        StringWriter pathWriter = new StringWriter();
        // write base URL
        pathWriter.write(StringUtils.appendIfMissing(messagingAPIURL, "/"));
        // add specification part definition
        pathWriter.write(referenceType.getSpecificationPart().getName());
        // add "# path" for document or just / for object path
        pathWriter.write("/components");
        // add "# path" for document or just / for object path
        pathWriter.write(asObject ? MessagingConstants.PATH_SEPARATOR : ".json#/");
        // add type subPath as headers, parameters..
        pathWriter.write(typeSubPath);
        // add object definition
        pathWriter.write(asObject ?
                referenceType.getObjectURIDefinition() :
                referenceType.getName());

        return pathWriter.toString();
    }

    public String getDefinitionURI(MessagingAPIDefinitionsLocation messagingAPIDefinitionsLocation, MessagingReferenceType referenceType, String subPath, String messagingAPIURL) {
        String generatedURI = null;
        switch (messagingAPIDefinitionsLocation) {
            case DOCUMENT_COMPONENTS: {
                generatedURI = MessagingConstants.OPENAPI_REF_COMPONENT + subPath + referenceType.getName();
                break;
            }
            case MESSAGING_API_OBJECT: {
                generatedURI = getExternalDefinitionURI(referenceType, subPath, messagingAPIURL, true);
                break;
            }
            case MESSAGING_API_COMPONENTS: {
                generatedURI = getExternalDefinitionURI(referenceType, subPath, messagingAPIURL, false);
                break;
            }
        }
        return generatedURI;
    }


}
