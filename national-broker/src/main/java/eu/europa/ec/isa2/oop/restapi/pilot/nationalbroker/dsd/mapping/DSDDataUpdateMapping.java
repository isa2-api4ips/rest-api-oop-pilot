package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.DSDDataUpdateEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.OrganizationEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DSDDataUpdateRO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Mapper(componentModel = "spring")
public interface DSDDataUpdateMapping {
    Logger LOG = LoggerFactory.getLogger(DSDDataUpdateMapping.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


    @Mapping(source = "organization", target = "organizationIdentifier", qualifiedByName = "organizationToString")
    @Mapping(source = "updateRequestOn", target = "updateRequestOn", qualifiedByName = "dateToString")
    @Mapping(source = "updateConfirmedOn", target = "updateConfirmedOn", qualifiedByName = "dateToString")
    DSDDataUpdateRO entityToRo(DSDDataUpdateEntity source);
    @Mapping(source = "updateRequestOn", target = "updateRequestOn", qualifiedByName = "stringToDate")
    @Mapping(source = "updateConfirmedOn", target = "updateConfirmedOn", qualifiedByName = "stringToDate")
    DSDDataUpdateEntity roToEntity(DSDDataUpdateRO source);

    @Named("organizationToString")
    default String organizationToString(OrganizationEntity organization){
        return organization!=null?organization.getIdentifier():null;

    }
    @Named("dateToString")
    default String dateToString(Date date){
        if (date == null){
            return null;
        }
        return  dateFormat.format(date);
    }

    @Named("stringToDate")
    default Date stringToDate(String date){
        if (date == null){
            return null;
        }
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            LOG.warn("Can not parse date string [{}]",date);
        }
        return null;
    }

}


