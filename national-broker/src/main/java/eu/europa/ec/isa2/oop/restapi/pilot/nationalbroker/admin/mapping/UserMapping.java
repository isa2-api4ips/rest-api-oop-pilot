package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.admin.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.admin.model.UserRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities.AuthRoleEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities.AuthRoleType;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities.UserEntity;
import org.mapstruct.Mapper;

import java.util.Set;


@Mapper(componentModel = "spring")
public interface UserMapping {
    UserRO entityToRo(UserEntity source);
    UserEntity roToEntity(UserRO source);

    Set<String> mapToStringSet(Set<AuthRoleEntity> value);
    Set<AuthRoleEntity> mapToEnumSet(Set<String> value);


    default String mapToString(AuthRoleEntity value) {
        return (value ==null)? null:value.getRoleName().name();
    };
    default  AuthRoleEntity mapToEnum(String value){
        AuthRoleEntity authRoleEntity = new AuthRoleEntity();
        authRoleEntity.setRoleName(AuthRoleType.valueOf(value));
        return authRoleEntity;
    };

}


