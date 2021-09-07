package eu.europa.ec.isa2.oop.restapi.utils;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.AltLabelEntity;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class EntityCollectionUtilsTest {

    @Test
    public void testUpdateLabelList() {
        // given
        List<AltLabelEntity> altLabelEntities = Arrays.asList(new AltLabelEntity("label1", null),new AltLabelEntity("label2", null));
        List<String> newLabelList = Arrays.asList("label1","label3");

        // when
        List<AltLabelEntity> updatedAltLabelEntities       = EntityCollectionUtils.updateLabelList(altLabelEntities,
                newLabelList, label -> new AltLabelEntity(label, null));
        //then
        assertEquals(2, updatedAltLabelEntities.size());
        assertEquals("label1", updatedAltLabelEntities.get(0).getStringValue());
        assertEquals("label3", updatedAltLabelEntities.get(1).getStringValue());
    }

    @Test
    public void testLabelExistTrue() {
        // given
        List<AltLabelEntity> altLabelEntities = Arrays.asList(new AltLabelEntity("label1", null),new AltLabelEntity("label2", null));
        // when
        boolean result = EntityCollectionUtils.labelExist(altLabelEntities, "label1");
        // then
        assertTrue(result);

    }
    @Test
    public void testLabelExistFalse() {
        // given
        List<AltLabelEntity> altLabelEntities = Arrays.asList(new AltLabelEntity("label1", null),new AltLabelEntity("label2", null));
        // when
        boolean result = EntityCollectionUtils.labelExist(altLabelEntities, "label3");
        // then
        assertFalse(result);

    }
}