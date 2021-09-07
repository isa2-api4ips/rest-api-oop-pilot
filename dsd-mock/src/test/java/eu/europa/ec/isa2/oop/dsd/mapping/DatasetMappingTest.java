package eu.europa.ec.isa2.oop.dsd.mapping;

import eu.europa.ec.isa2.oop.dsd.dao.entities.*;
import eu.europa.ec.isa2.oop.dsd.model.DatasetRO;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DatasetMappingTest {
    private DatasetMapping mapper
            = Mappers.getMapper(DatasetMapping.class);
    @Test
    public void testEntityToRoType() {
        //given
        DatasetEntity entity = new DatasetEntity();
        entity.setType("TestType");
        //when
        DatasetRO dataset = mapper.entityToRo(entity);
        //then
        assertEquals(entity.getType(), dataset.getType());
    }

    @Test
    public void testEntityToRoConformsTo() {
        //given
        DatasetEntity entity = new DatasetEntity();
        entity.setConformsTo("TestConformsTo");
        //when
        DatasetRO dataset = mapper.entityToRo(entity);
        //then
        assertEquals(entity.getConformsTo(), dataset.getConformsTo());
    }

    @Test
    public void testEntityToRoIdentifiers() {
        //given
        String[] identities = new String[]{"Identity1","Identity2"};

        DatasetEntity entity = new DatasetEntity();
        Arrays.stream(identities).forEach(ident -> entity.getIdentifiers().add(new DatasetIdentifierEntity(ident, entity)));

        //when
        DatasetRO dataset = mapper.entityToRo(entity);
        //then
        assertEquals(identities.length, dataset.getIdentifiers().size());
        for (int i=0; i< identities.length; i++) {
            assertEquals(identities[i], dataset.getIdentifiers().get(i));
        }
    }

    @Test
    public void testEntityToRoTitles() {
        //given
        String[] titles = new String[]{"Title1","Title2"};

        DatasetEntity entity = new DatasetEntity();
        Arrays.stream(titles).forEach(ident -> entity.getTitles().add(new DatasetTitleEntity(ident, entity)));

        //when
        DatasetRO dataset = mapper.entityToRo(entity);
        //then
        assertEquals(titles.length, dataset.getTitles().size());
        for (int i=0; i< titles.length; i++) {
            assertEquals(titles[i], dataset.getTitles().get(i));
        }
    }

    @Test
    public void testEntityToRoDescriptions() {
        //given
        String[] description = new String[]{"Description1","Description2"};

        DatasetEntity entity = new DatasetEntity();
        Arrays.stream(description).forEach(ident -> entity.getDescriptions().add(new DatasetDescriptionEntity(ident, entity)));

        //when
        DatasetRO dataset = mapper.entityToRo(entity);
        //then
        assertEquals(description.length, dataset.getDescriptions().size());
        for (int i=0; i< description.length; i++) {
            assertEquals(description[i], dataset.getDescriptions().get(i));
        }
    }

    @Test
    public void testEntityToRoRelationship() {
        //given
        DatasetEntity entity = new DatasetEntity();
        entity.getQualifiedRelationships().add(new DatasetRelationshipEntity("relationValue","https://toop.eu/dataset/supportedIdScheme", entity));
        //when
        DatasetRO dataset = mapper.entityToRo(entity);
        //then
        assertEquals(1, dataset.getQualifiedRelationships().size());
        assertEquals("relationValue", dataset.getQualifiedRelationships().get(0).getRelation());
        assertEquals("https://toop.eu/dataset/supportedIdScheme", dataset.getQualifiedRelationships().get(0).getHadRole());
    }

    @Test
    public void testEntityToRoDistribution() {
        //given
        DatasetEntity entity = new DatasetEntity();
        entity.getDistributions().add(new DatasetDistributionEntity("conformsTo",
                "format",
                "mediaType",
                "accessURL",
                Arrays.asList(new DistributionDescriptionEntity("descriptionValue", null)),
                Arrays.asList(new DistributionDataServiceEntity("identifier","conformsTo","title","endpointURL", null)),
                entity
        ));
        //when
        DatasetRO dataset = mapper.entityToRo(entity);
        //then
        assertEquals(1, dataset.getDistributions().size());
        assertEquals("accessURL", dataset.getDistributions().get(0).getAccessURL());
        assertEquals("conformsTo", dataset.getDistributions().get(0).getConformsTo());
        assertEquals("format", dataset.getDistributions().get(0).getFormat());
        assertEquals("mediaType", dataset.getDistributions().get(0).getMediaType());

        assertEquals(1, dataset.getDistributions().get(0).getDataServices().size());
        assertEquals("identifier", dataset.getDistributions().get(0).getDataServices().get(0).getIdentifier());
        assertEquals("conformsTo", dataset.getDistributions().get(0).getDataServices().get(0).getConformsTo());
        assertEquals("endpointURL", dataset.getDistributions().get(0).getDataServices().get(0).getEndpointURL());
        assertEquals("title", dataset.getDistributions().get(0).getDataServices().get(0).getTitle());

        assertEquals(1, dataset.getDistributions().get(0).getDescriptions().size());
        assertEquals("descriptionValue", dataset.getDistributions().get(0).getDescriptions().get(0));
    }
}