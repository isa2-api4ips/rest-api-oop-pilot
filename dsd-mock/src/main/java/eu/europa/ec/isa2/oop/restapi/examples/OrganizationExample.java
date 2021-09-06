package eu.europa.ec.isa2.oop.restapi.examples;

public class OrganizationExample {

    public static final String ORGANIZATION_SEARCH="{" +
            "  \"country\": \"BE\"," +
            "  \"name\": \"Company name\"," +
            "  \"limit\": 100," +
            "  \"offset\": 5," +
            "  \"sort\": \"+name,-country\"" +
            "}";

    public static final String ORGANIZATION_UPDATE="{" +
            "    \"identifier\" : \"Identifier1\"," +
            "    \"prefLabels\" : [ \"prefLabel1\",\"prefLabel2\",\"prefLabel3\" ]," +
            "    \"altLabels\" : [ \"altLabel\" ]," +
            "    \"classifications\" : [ \"classification\" ]" +
            "  }";


    public static final String ORGANIZATION_RESULT ="------=_Part_10_464896862.1623328165093" +
            "Content-Disposition: name=\"searchResult\"; filename=\"search.json\"" +
            "Content-Type: application/json" +
            "Edel-Payload-Sig: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c" +
            "" +
            "{" +
            "  \"serviceEntities\" : [ {" +
            "    \"status\" : 0," +
            "    \"index\" : 0," +
            "    \"identifier\" : \"Identifier1\"," +
            "    \"prefLabels\" : [ \"prefLabel\" ]," +
            "    \"altLabels\" : [ \"altLabel\" ]," +
            "    \"classifications\" : [ \"classification\" ]" +
            "  }, {" +
            "    \"status\" : 0," +
            "    \"index\" : 1," +
            "    \"identifier\" : \"Identifier2\"," +
            "    \"prefLabels\" : [ \"prefLabel\" ]," +
            "    \"altLabels\" : [ \"altLabel\" ]," +
            "    \"classifications\" : [ \"classification\" ]" +
            "  } ]" +
            "}" +
            "" +
            "------=_Part_10_464896862.1623328165093--";
}
