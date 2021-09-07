package eu.europa.ec.isa2.oop.dsd.dao;

import eu.europa.ec.isa2.oop.dsd.dao.entities.*;
import eu.europa.ec.isa2.oop.dsd.mapping.OrganizationMapping;
import eu.europa.ec.isa2.oop.dsd.model.OrganizationRO;
import eu.europa.ec.isa2.oop.dsd.model.OrganizationSearchResult;
import eu.europa.ec.isa2.oop.restapi.utils.EntityCollectionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Dao operations for {@link OrganizationEntity} entity
 *
 * @author Joze Rihtarsic
 * @since 1.0
 */
@Repository
public class OrganizationDao extends BasicDao<OrganizationEntity> {

    private OrganizationMapping mapper
            = Mappers.getMapper(OrganizationMapping.class);


    @Transactional
    public OrganizationSearchResult getAllOrganizations(int page, int pageSize) {

        OrganizationSearchResult sg = new OrganizationSearchResult();
        sg.setPage(page < 0 ? 0 : page);

        long iCnt = getDataListCount(null);
        if (pageSize < 0) { // if page size iz -1 return all results and set pageSize to maxCount
            pageSize = (int) iCnt;
        }
        sg.setPageSize(pageSize);
        sg.setCount(iCnt);


        if (iCnt > 0) {
            int iStartIndex = pageSize<0?-1:page * pageSize;
            if (iStartIndex >= iCnt && page > 0){
                page = page -1;
                sg.setPage(page); // go back for a page
                iStartIndex = pageSize<0?-1:page * pageSize;
            }



            List<OrganizationEntity> lst = getDataList(iStartIndex, pageSize, null, null, null);

            for (OrganizationEntity d : lst) {
                OrganizationRO dro = mapper.entityToRo(d);
                dro.setIndex( iStartIndex++);
                sg.getServiceEntities().add(dro);

            }
        }
        return sg;
    }

    @Transactional
    public OrganizationRO updateOrganizations(OrganizationRO organizationRO) {
        TypedQuery<OrganizationEntity> query = memEManager.createNamedQuery(
                "OrganizationEntity.getByIdentifier",OrganizationEntity.class);
        query.setParameter("identifier", organizationRO.getIdentifier());

        List<OrganizationEntity> entities = query.getResultList();
        if (entities.isEmpty()) {
            // throw error
            return null;
        }
        OrganizationEntity entity = entities.get(0);

        // update value
        if (organizationRO.getAddress() ==null) {
            entity.setAddress(null);
        }else {
            if (entity.getAddress() ==null ) {
                entity.setAddress(new AddressEntity());
            }
            entity.getAddress().setFullAddress(organizationRO.getAddress().getFullAddress());
            entity.getAddress().setAdminUnitLevel(organizationRO.getAddress().getAdminUnitLevel());
        }

        OrganizationEntity finalEntity = entity;
        List<AltLabelEntity> updatedAltLabelEntities
                = EntityCollectionUtils.updateLabelList(entity.getAltLabels(),
                organizationRO.getAltLabels(), label -> new AltLabelEntity(label, finalEntity));
        entity.getAltLabels().clear();
        entity.getAltLabels().addAll(updatedAltLabelEntities);


        List<PrefLabelEntity> updatedPrefLabelEntities
                = EntityCollectionUtils.updateLabelList(entity.getPrefLabels(),
                organizationRO.getPrefLabels(), label -> new PrefLabelEntity(label, finalEntity));
        entity.getPrefLabels().clear();
        entity.getPrefLabels().addAll(updatedPrefLabelEntities);

        List<ClassificationEntity> updatedClassificationsEntities
                = EntityCollectionUtils.updateLabelList(entity.getClassifications(),
                organizationRO.getPrefLabels(), label -> new ClassificationEntity(label, finalEntity));
        entity.getClassifications().clear();
        entity.getClassifications().addAll(updatedClassificationsEntities);
        entity = memEManager.merge(entity);

        return mapper.entityToRo(entity);
    }
}
