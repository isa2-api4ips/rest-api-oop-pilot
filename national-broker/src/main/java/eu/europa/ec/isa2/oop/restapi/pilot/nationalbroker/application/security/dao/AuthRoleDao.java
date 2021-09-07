package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.dao;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.dao.BasicDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities.AuthRoleEntity;
import org.springframework.stereotype.Repository;

/**
 * Dao operations for {@link AuthRoleEntity} entity
 *
 * @author Arun Raj
 * @since 1.0
 */
@Repository
public class AuthRoleDao extends BasicDao<AuthRoleEntity> {

//    /**
//     * @param typeOfT The entity class this DAO provides access to
//     */
//    public AuthRoleDao(Class<AuthRole> typeOfT) {
//        super(typeOfT);
//    }

    public AuthRoleDao(){super();}
}
