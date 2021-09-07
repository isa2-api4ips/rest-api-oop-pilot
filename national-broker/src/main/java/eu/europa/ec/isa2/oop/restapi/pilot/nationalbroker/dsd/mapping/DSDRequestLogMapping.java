package eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.mapping;

import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.dao.entities.DSDRequestLogEntity;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.dsd.model.DSDRequestLogRO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Mapper(componentModel = "spring")
public interface DSDRequestLogMapping {
    Logger LOG = LoggerFactory.getLogger(DSDRequestLogMapping.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Mapping(source = "requestOn", target = "requestOn", qualifiedByName = "dateToString")
    @Mapping(source = "responseOn", target = "responseOn", qualifiedByName = "dateToString")
    DSDRequestLogRO entityToRo(DSDRequestLogEntity source);

    @Mapping(source = "requestOn", target = "requestOn", qualifiedByName = "stringToDate")
    @Mapping(source = "responseOn", target = "responseOn", qualifiedByName = "stringToDate")
    DSDRequestLogEntity roToEntity(DSDRequestLogRO source);

    @Named("dateToString")
    default String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormat.format(date);
    }

    @Named("stringToDate")
    default Date stringToDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            LOG.warn("Can not parse date string [{}]", date);
        }
        return null;
    }

}


