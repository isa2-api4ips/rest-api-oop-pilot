package eu.europa.ec.isa2.restapi.profile;

import eu.europa.ec.isa2.restapi.reader.utils.MessagingOpenApiUtils;
import eu.europa.ec.isa2.restapi.reader.MessagingReader;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.GroupedOpenApi;

public class GeneralOpenApi  {

    Class  messagingAPIClass;
    String groupName;

  public GeneralOpenApi(Class clazz, String group) {
      messagingAPIClass = clazz;
      groupName = group;
      /*
        super(GroupedOpenApi.builder().group(group).addOpenApiCustomiser(openApi -> {
            MessagingReader reader = new MessagingReader();
            OpenAPI result = reader.read(clazz);
            openApi.paths(result.getPaths()).
                    components(result.getComponents());
            openApi.setTags(MessagingOpenApiUtils.getGlobalTags(clazz));
        }));
       */
    }

    public Class getMessagingAPIClass() {
        return messagingAPIClass;
    }

    public void setMessagingAPIClass(Class messagingAPIClass) {
        this.messagingAPIClass = messagingAPIClass;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
