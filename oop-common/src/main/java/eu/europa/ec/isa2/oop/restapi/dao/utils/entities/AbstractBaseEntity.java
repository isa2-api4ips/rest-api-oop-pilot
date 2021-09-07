package eu.europa.ec.isa2.oop.restapi.dao.utils.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Base type for entity
 *
 */
@MappedSuperclass
public abstract class AbstractBaseEntity implements Serializable {


    /**
     * Date of the creation.
     */
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    protected Calendar creationDate;

    /**
     * Date of the last update.
     */
    @Column(name = "LAST_UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    protected Calendar lastUpdateDate;

    abstract public Object getId();

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public Calendar getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Calendar lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @PrePersist
    private void prePersist(){
        creationDate = Calendar.getInstance();
        lastUpdateDate = Calendar.getInstance();
    }

    @PreUpdate
    public void preUpdateFunction(){
        lastUpdateDate = Calendar.getInstance();
    }

    @Override
    public boolean equals(final Object other) {
        //noinspection NonFinalFieldReferenceInEquals
        return ((other != null) &&
                this.getClass().equals(other.getClass())
        );
    }

}
