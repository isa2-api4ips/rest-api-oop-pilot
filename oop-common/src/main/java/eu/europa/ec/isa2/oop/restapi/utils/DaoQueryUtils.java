package eu.europa.ec.isa2.oop.restapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class DaoQueryUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DaoQueryUtils.class);
    private static String DEF_ENCODING="UTF-8";


    static public List<String> getSortOrderList(String sortOrderString){
        return getSortOrderList(sortOrderString, DEF_ENCODING);
    }

    static public List<String> getSortOrderList(String sortOrderString, String enc){
        if (StringUtils.isBlank(sortOrderString)){
            return Collections.emptyList();
        }

        String decodeSortOrderString;
        try {
            decodeSortOrderString = URLDecoder.decode(sortOrderString, enc);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Error occurred while decoding the query filter", e);
            // ignore only for the pilot demo!!
            LOG.error("Query filter [{}] is ignored", sortOrderString);
            return Collections.emptyList();
        }

        return StringUtils.isBlank(sortOrderString)? Collections.emptyList(): Arrays.asList(decodeSortOrderString.trim().split("\\s*,\\s*"));
    }

    static public <T> T generateFilterFromJson(String queryObject, Class<T> filterClassType) {
        return generateFilterFromJson(queryObject, filterClassType, DEF_ENCODING);

    }

    static public <T> T generateFilterFromJson(String queryObject, Class<T> filterClassType, String enc) {
        if (StringUtils.isBlank(queryObject)){
            return null;
        }
        String decodedQuery;
        try {
            decodedQuery = new String(Base64.getDecoder().decode(queryObject), enc);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Error occurred while decoding the query filter", e);
            // ignore only for the pilot demo!!
            LOG.error("Query filter [{}] is ignored", queryObject);
            return null;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(decodedQuery, filterClassType);

        } catch (IOException e) {
            LOG.error("Error occurred while parsing the query filter", e);
            // ignore only for the pilot demo!!
            LOG.error("Query filter [{}] is ignored", queryObject);
        }
        return null;
    }
}
