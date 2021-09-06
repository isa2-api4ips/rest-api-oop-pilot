package eu.europa.ec.isa2.oop.restapi.dao.utils.entities;

import javax.persistence.MappedSuperclass;

/**
 * Base type for entity
 *
 */
@MappedSuperclass
public abstract class AbstractStringEntity<T> extends AbstractBaseEntity {

    abstract public String getStringValue();

}
