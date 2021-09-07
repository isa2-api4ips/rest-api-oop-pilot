package eu.europa.ec.isa2.restapi.profile;

public class MessagingOpenApi {
    Class messagingAPIClass;
    String groupName;

    public MessagingOpenApi(Class clazz, String group) {
        messagingAPIClass = clazz;
        groupName = group;
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
