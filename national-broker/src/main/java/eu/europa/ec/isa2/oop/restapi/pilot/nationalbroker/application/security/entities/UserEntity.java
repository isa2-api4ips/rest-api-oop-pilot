package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities;

import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.OrganizationEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "NB_USER")
@NamedQueries({
        @NamedQuery(name = "User.findUserByName", query = "SELECT d FROM UserEntity d WHERE d.username=:USER_NAME"),
        @NamedQuery(name = "User.findUserByIdpId", query = "SELECT d FROM UserEntity d WHERE d.user_IDP_ID=:USER_IDP_ID")
})
public class UserEntity extends AbstractBaseEntity {
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_IDP_ID = "USER_IDP_ID";

    @Id
    @GenericGenerator(name = "TB_USER_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "TB_USER_SEQ")
    @Column(name = "ID_PK")
    private Long id;
    @NaturalId
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "NAME")
    private String name;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "ACTIVE")
    private Boolean active;
    @Column(name = "USER_IDP_ID")
    private String user_IDP_ID;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "NB_USER_ROLE",
            joinColumns = @JoinColumn(name = "FK_USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "FK_ROLE_ID")
    )
    private Set<AuthRoleEntity> userAuthRoles;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "NB_USER_ROA_DATACONSUMER",
            joinColumns = @JoinColumn(name = "FK_USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "FK_ROA_DATACONSUMER_ID")
    )

    private Set<OrganizationEntity> organizations;

    public UserEntity() {
    }

    public UserEntity(String userName) {
        this.username = userName;
    }

    /**
     * @return the primary key of the entity
     */
    @Override
    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
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

    public String getUser_IDP_ID() {
        return user_IDP_ID;
    }

    public void setUser_IDP_ID(String user_IDP_ID) {
        this.user_IDP_ID = user_IDP_ID;
    }

    public Set<AuthRoleEntity> getUserAuthRoles() {
        return userAuthRoles;
    }

    public void setUserAuthRoles(Set<AuthRoleEntity> userAuthRoles) {
        this.userAuthRoles = userAuthRoles;
    }

    public void addUserRole(AuthRoleEntity userRole) {
        if (userAuthRoles == null) {
            userAuthRoles = new HashSet<>();
        }
        userAuthRoles.add(userRole);
    }

    public Set<OrganizationEntity> getOrganizations() {
        return organizations;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append(super.toString())
                .append("id", id)
                .append("userName", username)
                .append("userAuthRoles", userAuthRoles)
                //               .append("userROADataConsumers", userROADataConsumers)
                .toString();
    }


}
