package eu.europa.ec.isa2.oop.dsd.dao;

import eu.europa.ec.isa2.oop.dsd.property.DsdMockPropertyMetaDataManager;
import eu.europa.ec.isa2.oop.restapi.dao.utils.entities.AbstractBaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A basic DAO implementation providing the standard CRUD operations,
 *
 * @author Arun Raj
 * @since 1.0
 */

public abstract class BasicDao<E extends AbstractBaseEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(BasicDao.class);
/*
    private final Class<T> typeOfT;

    @PersistenceContext(unitName = NATIONAL_BROKER_PERSISTENCE_UNIT)
    protected EntityManager memEManager;

    /**
     * @param typeOfT The entity class this DAO provides access to
     * /
    public BasicDao(final Class<T> typeOfT) {
        this.typeOfT = typeOfT;
    }

    public <T> T findById(Class<T> typeOfT, long id) {
        return memEManager.find(typeOfT, id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void create(final T entity) {
        memEManager.persist(entity);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void delete(final T entity) {
        memEManager.remove(memEManager.merge(entity));
    }

    public T read(final int id) {
        return memEManager.find(this.typeOfT, id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateAll(final Collection<T> update) {
        for (final T t : update) {
            this.update(t);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteAll(final Collection<T> delete) {
        for (final T t : delete) {
            this.delete(t);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void update(final T entity) {
        memEManager.merge(entity);
    }
*/

    protected String defaultSortMethod;

    @PersistenceContext(unitName = DsdMockPropertyMetaDataManager.DSD_MOCK_PERSISTENCE_UNIT)
    protected EntityManager memEManager;

    private final Class<E> entityClass;

    public BasicDao() {
        entityClass = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), BasicDao.class);
    }

    public E find(Object primaryKey) {
        return memEManager.find(entityClass, primaryKey);
    }

    /**
     * save or update database entity
     *
     * @param entity
     */
    @javax.transaction.Transactional
    public void persistFlushDetach(E entity) {
        memEManager.persist(entity);
        memEManager.flush();
        memEManager.detach(entity);
    }

    /**
     * save or update detached database entity
     *
     * @param entity
     */
    @javax.transaction.Transactional
    public void update(E entity) {
        memEManager.merge(entity);
        memEManager.flush();
        memEManager.detach(entity);
    }


    /**
     * Removes Entity by given primary key
     *
     * @return true if entity existed before and was removed in this call.
     * False if entity does not exist, so nothing was changed
     */
    @javax.transaction.Transactional
    public boolean removeById(Object primaryKey) {
        // Do not use query delete else envers will not work!!
        E val = find(primaryKey);
        if (val!= null) {
            memEManager.remove(val);
            return true;
        } return false;
    }


    /**
     * Clear the persistence context, causing all managed entities to become detached.
     * Changes made to entities that have not been flushed to the database will not be persisted.
     * <p>
     * Main purpose is to clear cache for unit testing
     */
    public void clearPersistenceContext() {
        memEManager.clear();
    }

    /**
     * Method generates CriteriaQuery for search or count. If filter property value should match multiple values eg: column in (:list)
     * than filter method must end with List and returned value must be list. If property is comparable (decimal, int, date)
     * if filter method ends with From, than predicate greaterThanOrEqualTo is set to quer. If Method end To, than
     * predicate cb.lessThan is set. If filter property  has null value, than filter parameter is ignored.
     *
     * @param searchParams
     * @param forCount
     * @param sortField
     * @param sortOrder
     * @return
     */
    protected <D> CriteriaQuery createSearchCriteria(Object searchParams, Class<D> filterType,
                                                     boolean forCount, String sortField, String sortOrder) {
        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery cq = forCount ? cb.createQuery(Long.class) : cb.createQuery(entityClass);
        Root<E> om = cq.from(filterType == null ? entityClass : filterType);
        if (forCount) {
            cq.select(cb.count(om));
        } else if (sortField != null) {
            if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
                cq.orderBy(cb.asc(om.get(sortField)));
            } else {
                cq.orderBy(cb.desc(om.get(sortField)));
            }
        } else {
            if (!StringUtils.isBlank(defaultSortMethod)) {
                cq.orderBy(cb.desc(om.get(defaultSortMethod)));
            }
        }


        // set order by
        if (searchParams != null) {
            List<Predicate> lstPredicate = createPredicates(searchParams, om, cb);
            if (!lstPredicate.isEmpty()) {
                Predicate[] tblPredicate = lstPredicate.stream().toArray(Predicate[]::new);
                cq.where(cb.and(tblPredicate));
            }
        }
        return cq;
    }


    public <T> List<T> getDataList(Class<T> type, String namedQuery,
                                   Map<String, Object> params) {
        TypedQuery<T> q = memEManager.createQuery(namedQuery, type);
        params.forEach((param, value) -> {
            q.setParameter(param, value);
        });
        return q.getResultList();
    }

    /**
     * Method crates list of predicates for Query
     *
     * @param searchParams - filter object
     * @param om
     * @param cb
     * @return
     */
    protected List<Predicate> createPredicates(Object searchParams, Root<E> om, CriteriaBuilder cb) {
        List<Predicate> lstPredicate = new ArrayList<>();

        Class cls = searchParams.getClass();
        Method[] methodList = cls.getMethods();
        for (Method m : methodList) {
            // only getters (public, starts with get, no arguments)
            String mName = m.getName();

            if (Modifier.isPublic(m.getModifiers()) && m.getParameterCount() == 0
                    && !m.getReturnType().equals(Void.TYPE)
                    && !mName.equalsIgnoreCase("getClass")
                    && (mName.startsWith("get") || mName.startsWith("is"))) {
                String fieldName = mName.substring(mName.startsWith("get") ? 3 : 2);
                // get return parameter
                Object searchValue;
                try {
                    searchValue = m.invoke(searchParams, new Object[]{});
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    LOG.error("Error setting retrieveing search parameters", ex);
                    continue;
                }

                if (searchValue == null) {
                    continue;
                }

                if (fieldName.endsWith("List") && searchValue instanceof List) {
                    Path path = getPath(om, fieldName, "List");
                    if (!((List) searchValue).isEmpty()) {
                        lstPredicate.add(path.in(
                                ((List) searchValue).toArray()));
                    } else {
                        lstPredicate.add(path.isNull());
                    }
                } else {
                    try {
                        cls.getMethod("set" + fieldName, new Class[]{m.getReturnType()});
                    } catch (NoSuchMethodException | SecurityException ex) {
                        // method does not have setter // ignore other methods
                        LOG.error("Field '" + fieldName + "' does not have a setter!", ex);
                        continue;
                    }

                    if (fieldName.endsWith("From") && searchValue instanceof Comparable) {
                        lstPredicate.add(cb.greaterThanOrEqualTo(
                                getPath(om, fieldName, "From"),
                                (Comparable) searchValue));
                    } else if (fieldName.endsWith("To") && searchValue instanceof Comparable) {
                        lstPredicate.add(cb.lessThan(getPath(om, fieldName, "To"),
                                (Comparable) searchValue));
                    } else if (searchValue instanceof String && fieldName.endsWith("Like")) {
                        if (!((String) searchValue).isEmpty()) {
                            // like search is also case insensitive
                            String searchPhraze = ((String) searchValue).toLowerCase().trim();
                            lstPredicate.add(cb.like(cb.lower(getPath(om, fieldName, "Like")), "%" + searchPhraze + "%"));
                        }
                    } else if (searchValue instanceof String) {
                        if (!((String) searchValue).isEmpty()) {
                            lstPredicate.add(cb.equal(getPath(om, fieldName), searchValue));
                        }
                    } else if (searchValue instanceof BigInteger) {
                        lstPredicate.add(cb.equal(getPath(om, fieldName), searchValue));
                    } else if (searchValue instanceof Long) {
                        lstPredicate.add(cb.equal(getPath(om, fieldName), searchValue));
                    } else {
                        LOG.info("Unknown search value type {} for method {}! Parameter is ignored!",
                                searchValue, fieldName);
                    }
                }

            }
        }
        return lstPredicate;
    }


    public Path getPath(Root<E> om, String fieldName) {
        return getPath(om, fieldName, null);
    }

    public Path getPath(Root<E> om, String fieldName, String trimRight) {
        String fn = StringUtils.uncapitalize(fieldName);
        if (!StringUtils.isBlank(trimRight)) {
            fn = fn.substring(0, fn.lastIndexOf(trimRight));
        }
        return om.get(fn);
    }

    /**
     * Method returns paginated entity list with give pagination parameters and filters.
     * Filter methods must match object methods. If property value should match multiple values eg: column in (:list)
     * than filter method must end with List and returned value must be list. If property is comparable (decimal, int, date)
     * if filter method ends with From, than predicate greaterThanOrEqualTo is set to quer. If Method end To, than
     * predicate cb.lessThan is setted. If filter property  has null value, than filter parameter is ignored.
     *
     * @param startingAt
     * @param maxResultCnt
     * @param sortField
     * @param sortOrder
     * @param filters
     * @return List
     */

    public List<E> getDataList(int startingAt, int maxResultCnt,
                               String sortField,
                               String sortOrder, Object filters) {

        return getDataList(startingAt, maxResultCnt, sortField, sortOrder,
                filters, null);
    }

    /**
     * Method returns paginated entity list with give pagination parameters and filters.
     * Filter methods must match object methods. If property value should match multiple values eg: column in (:list)
     * than filter method must end with List and returned value must be list. If property is comparable (decimal, int, date)
     * if filter method ends with From, than predicate greaterThanOrEqualTo is set to quer. If Method end To, than
     * predicate cb.lessThan is setted. If filter property  has null value, than filter parameter is ignored.
     *
     * @param startingAt
     * @param maxResultCnt
     * @param sortField
     * @param sortOrder
     * @param filters
     * @param filterType
     * @return List
     */
    public <D> List<E> getDataList(int startingAt,
                                   int maxResultCnt,
                                   String sortField,
                                   String sortOrder, Object filters, Class<D> filterType) {

        List<E> lstResult;
        try {
            CriteriaQuery<E> cq = createSearchCriteria(filters, filterType,
                    false, sortField,
                    sortOrder);
            TypedQuery<E> q = memEManager.createQuery(cq);
            if (maxResultCnt > 0) {
                q.setMaxResults(maxResultCnt);
            }
            if (startingAt > 0) {
                q.setFirstResult(startingAt);
            }
            lstResult = q.getResultList();
        } catch (NoResultException ex) {
            LOG.warn("No result for '" + filterType.getName() + "' does not have a setter!", ex);
            lstResult = new ArrayList<>();
        }

        return lstResult;
    }

    /**
     * Method returns filtered list count.
     * Filter methods must match object methods. If property value should match multiple values eg: column in (:list)
     * than filter method must end with List and returned value must be list. If property is comparable (decimal, int, date)
     * if filter method ends with From, than predicate greaterThanOrEqualTo is set to quer. If Method end To, than
     * predicate cb.lessThan is setted. If filter property  has null value, than filter parameter is ignored.
     *
     * @param filters
     * @return
     */
    public long getDataListCount(Object filters) {

        CriteriaQuery<Long> cqCount = createSearchCriteria(filters, null, true,
                null,
                null);
        return memEManager.createQuery(cqCount).getSingleResult();
    }

}
