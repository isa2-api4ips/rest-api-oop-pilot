package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities;

import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Table(name = "NB_AUTH_ROLES")
public class AuthRoleEntity extends AbstractBaseEntity {

    @Id
    @GenericGenerator(name = "TB_AUTH_ROLES_SEQ", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "TB_AUTH_ROLES_SEQ")
    @Column(name = "ID_PK")
    private Long id;

    @NaturalId
    @Column(name = "ROLE_NAME")
    @Enumerated(EnumType.STRING)
    private AuthRoleType roleName;

    public AuthRoleEntity(){}

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthRoleEntity(AuthRoleType roleName){
        this.roleName = roleName;
    }
    public AuthRoleType getRoleName() {
        return roleName;
    }

    public void setRoleName(AuthRoleType roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append(super.toString())
                .append("roleName", roleName)
                .toString();
    }
}
