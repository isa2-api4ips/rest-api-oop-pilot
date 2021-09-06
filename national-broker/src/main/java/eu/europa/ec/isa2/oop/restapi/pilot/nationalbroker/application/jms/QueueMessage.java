package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.jms;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class QueueMessage implements Serializable {
    private static final long serialVersionUID = -8180302325329456636L;

    protected long transactionId;

    public QueueMessage() {
    }

    public QueueMessage(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE  )
                .append("transactionId", transactionId)
                .toString();
    }
}
