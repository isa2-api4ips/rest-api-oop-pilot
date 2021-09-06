package eu.europa.ec.isa2.restapi.jws;

import org.junit.Assert;
import org.junit.Test;

public class JADESSignatureTest {

    JADESSignature testInstance = new JADESSignature(null, null);
    @Test
    public void normalizeHeader() {

        String contentType = "multipart/mixed; \n\r\t  boundary=\"----=_Part_19_613600381.1625898442006\"  ";
        String result =testInstance.normalizeHeader(contentType);

        Assert.assertEquals("multipart/mixed; boundary=\"----=_Part_19_613600381.1625898442006\"", result);

    }
}