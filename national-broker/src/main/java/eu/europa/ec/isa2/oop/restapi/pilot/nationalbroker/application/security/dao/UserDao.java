package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.dao;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.admin.mapping.UserMapping;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.admin.model.UserRO;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.dao.BasicDao;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.model.ServiceResult;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities.UserEntity;
import org.hibernate.Hibernate;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

import static eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities.UserEntity.USER_IDP_ID;
import static eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.security.entities.UserEntity.USER_NAME;

/**
 * Dao operations for {@link UserEntity} entity
 *
 * @author Arun Raj
 * @since 1.0
 */
@Repository
public class UserDao extends BasicDao<UserEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(UserDao.class);
    private UserMapping mapper
            = Mappers.getMapper(UserMapping.class);

    public UserDao() {
        super();
    }

    public UserEntity findUserByName(String userName) {
        TypedQuery<UserEntity> findUserByNameQuery = memEManager.createNamedQuery("User.findUserByName", UserEntity.class);
        findUserByNameQuery.setParameter(USER_NAME, userName);
        try {
            return findUserByNameQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public UserEntity findUserByIDP_ID(String userIdpId) {
        TypedQuery<UserEntity> findUserByNameQuery = memEManager.createNamedQuery("User.findUserByIdpId", UserEntity.class);
        findUserByNameQuery.setParameter(USER_IDP_ID, userIdpId);
        UserEntity entity = null;
        try {
            entity = findUserByNameQuery.getSingleResult();
            // force initialize UserAuthRoles: Do not set EAGER by default - load it when needed!
            Hibernate.initialize(entity.getUserAuthRoles());
        } catch (NoResultException e) {
            LOG.warn("No user with IDP: [{}]", userIdpId);
        }
        return entity;
    }


    @Transactional
    public ServiceResult<UserRO> getAllUsers(int page, int pageSize) {

        ServiceResult<UserRO> usersResult = new ServiceResult<>();
        usersResult.setPage(page < 0 ? 0 : page);

        long iCnt = getDataListCount(null);
        if (pageSize < 0) { // if page size iz -1 return all results and set pageSize to maxCount
            pageSize = (int) iCnt;
        }
        usersResult.setPageSize(pageSize);
        usersResult.setCount(iCnt);


        if (iCnt > 0) {
            int iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            if (iStartIndex >= iCnt && page > 0) {
                page = page - 1;
                usersResult.setPage(page); // go back for a page
                iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            }


            List<UserEntity> lst = getDataList(iStartIndex, pageSize, null, null, null);

            for (UserEntity d : lst) {
                UserRO dro = mapper.entityToRo(d);
                dro.setIndex(iStartIndex++);
                usersResult.getServiceEntities().add(dro);

            }
        }
        return usersResult;
    }
}
