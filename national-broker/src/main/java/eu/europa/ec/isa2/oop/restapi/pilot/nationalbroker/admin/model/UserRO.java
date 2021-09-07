package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.admin.model;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.BaseRO;

import java.util.HashSet;
import java.util.Set;

public class UserRO extends BaseRO {

    private String username;
    private Set<String> userAuthRoles;
    private String name;
    private String email;
    private Boolean active;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getUserAuthRoles() {
        if( userAuthRoles ==null){
            userAuthRoles = new HashSet<>();
        }
        return userAuthRoles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
