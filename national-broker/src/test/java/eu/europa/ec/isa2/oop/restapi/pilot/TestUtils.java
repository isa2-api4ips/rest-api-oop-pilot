package eu.europa.ec.isa2.oop.restapi.pilot;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import eu.europa.ec.isa2.oop.restapi.pilot.nationalbroker.application.property.NationalBrokerPropertyMetaDataManager;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;

public class TestUtils {

    @Test
    public void testStringSeparation() {
        String db_url = "jdbc:h2:tcp://localhost:13011/~/testDatabase;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=13011;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        if (StringUtils.contains(db_url, "AUTO_SERVER_PORT")) {
            String[] strArray = StringUtils.split(db_url, ';');
            System.out.println(Arrays.asList(strArray));
            System.out.println(strArray[2]);

            Optional<String> auto_server_port = Arrays.stream(strArray).filter(str -> str.startsWith("AUTO_SERVER_PORT")).findFirst();
            String dbServerPort = auto_server_port.get().split("=")[1];
            System.out.println(dbServerPort);
        }
    }

    public void testPropertyFileLoad() {
        InputStream nationalBrokerPropertiesStream = this.getClass().getResourceAsStream(NationalBrokerPropertyMetaDataManager.NATIONAL_BROKER_PROPERTIES_FILE);

    }


    @Test
    public void testJWTDecodingWithNimbusDs_HS256() throws ParseException, JOSEException {

        String signatureAlg = "HS256";
        String sharedSignKey = "eUqZe4Wqw4zh4DVdS9VVwARCv8JWnwUy";
        String JWT_IDToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2xvY2FsaG9zdDo3MDAyL2Nhcy9vYXV0aDIiLCJzdWIiOiJEU0RfVVNFUjEiLCJjbGllbnRfaWQiOiJuYXRpb25hbC1icm9rZXItZGVtbyIsImF1ZCI6Imh0dHBzOi8vbG9jYWxob3N0OjcwMDIvY2FzL29hdXRoMi9hdXRob3JpemUiLCJleHAiOjE4OTM1MzUyMDB9.aisYMBIodACA6RhCyWwA9QjpB4RoS0vadJjHgJvdtHY";

        JWSObject jwsObject = JWSObject.parse(JWT_IDToken);
        JWSHeader header = jwsObject.getHeader();
        Payload payload = jwsObject.getPayload();

        System.out.println("HEADER:" + header);
        System.out.println("PAYLOAD:" + payload);

        JWSVerifier jwsVerifier = new MACVerifier(sharedSignKey.getBytes());
        System.out.println("Result of verification:" + jwsObject.verify(jwsVerifier));

        System.out.println("Header: included params:" + header.getIncludedParams());
        System.out.println("Header: Algorithm:" + header.getAlgorithm());
        System.out.println("Header: Content type:" + header.getContentType());
        System.out.println("Header: Type:" + header.getType());

        JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(payload.toJSONObject());
        System.out.println("Issuer:" + jwtClaimsSet.getIssuer());
        System.out.println("Subject:" + jwtClaimsSet.getSubject());
        System.out.println("Client ID:" + jwtClaimsSet.getStringClaim("client_id"));
        System.out.println("Audience List:" + jwtClaimsSet.getAudience());
        System.out.println("Expiration:" + jwtClaimsSet.getExpirationTime());
    }


    @Test
    public void testJWTDecodingWithNimbusDs_RS256() throws ParseException, JOSEException {
        String signatureAlg = "RS256";

        String RS256_PrivateKey_JWK = "" +
                "{" +
                "\"alg\":\"RS256\"," +
                "\"d\":\"QkdwVWX6rX6IGbzTSUVVaCNOiEbHgnrruk_zS-IvPpMAGiI_zEObY84uL5R43GpatxX9GK7RwiorwYnHHqb_FuHMjuLkGiET0C8ggJAxvBdFY44D8niZ7UwssccuiENvZ5OKP_ZQIQnmD6kcyuqWmjTxeFPA0ddsSAfL4tjDLLsVCSr5s2C13GvcZChS6o40_o2UGDxftLjdzXD8uw93eidVS7mHThG55NobS2Ueys1m7-whp-YkbA2D8z8jMI3fXoHLB6faG13cEPFwcBthkHLBrDQEYFmCED0CdDXhDSewiailHtyMkIoSHqrxDFLVtYMneFwOAX4LJh8Z4Fa-e9VfcQYSS4zhQe29CPkuRqiUeLSHgrMd7r_lLDeTCbZGcra-Xa3AN5rJsC0o3sy26mHL4C7l09j4Sl-9v6boh6pL0JCatoTpw7o23wQJbvWZro1XQcWhtLAouMWWfCFSOmrvMUsr0_jbxMyt86vorFA1uluZEv5ymHMjabtOtCNqGozRCQZHLBeyCZ4nTVISHOp7JLqSiG0juUwSQ2pKyP0S-gtkx81b4fqsmNr9ExA95Taw3ILbtWRywMzexPDgV_z8TNXJC9PY0wPW2X1uy4aHKAenbvcsxF6dJqB31MUg0BOcMhQ8zjM5QCfy5E7OQ3OrKjQMNHKallkgy3LNVRk\"," +
                "\"dp\":\"X0b03IuFsIZBFqpQnl91wR-sBLvoxEMSUYnSYwXGaXyf-hnMg97rGni2INpgJ5u3ZnY0T1JCd_ZUhTJUnHs79lje1a-RUSkYFNbBJKoj1NJb6MqCy5epgIhcCDTfhpEbGKr0wHP8vWUtgozvatg_pTzuqgQAmvhOuHmduavq_cCdTp-Ti1odQFGVKegEdMvEwwUf9nZxIgpW0Fcc6mkJxxBN9fgOoBj9DsDaT1GedagpHRm38x5X4KZa6KV5JQz27j3tPPGPvvJNCL91gJuqzuN-jVbMjxjhzMz232HMtiq7bdQX71A91hZ1eXMcJY45ExjK2_z2C9IpGktN1u4INQ\"," +
                "\"dq\":\"v-XCjXirtrXSK3gNxFvmTzmmHtqQDRWltPQ7pLGBvzmzXKJMcfTIfpv23_h4WIkWi1HxTqgMEnAu9Ojzlz_e-jtLWH3VpvPNcPMeWtehufDu11f3WzlIFr6H7AMWzMdVqxYk5j1H-bTMJIZyGy2U1n7qC1n4P0XjMkPfuTyhdKQa318riseGT-FRI6XWTFqVBgq-K9HyYlwXeiMpJga9fVSdVBd9GpIkdIlgfCoG66kN3pI2Sv2ryt5Pk3Z2C_X1zK0CAWhUNIxdH7jNejIE32oLiEKsVIVDn8BgvJi-uuT6Sbp5dVYP9oxNJdpCryHlgjFKBB5KhGIOV9459Up-WQ\"," +
                "\"e\":\"AQAB\"," +
                "\"ext\":true," +
                "\"key_ops\":[\"sign\"]," +
                "\"kty\":\"RSA\"," +
                "\"n\":\"yz0-smqLmO6GdNUmIhW5aEJXnTiZejLlpSq6E43KjR2o36I2HF5oEePW779re7q7lLWc3bZBtg8UUTSPt6FYv-X6IIOBpOveycHCLYNGzKgFcqoR3XM0CYkkYfRlo4R0cDinZhC7_8LB9GInOnOFqogN6OKW-lrzU6MrX8QWFNU0rSFZ57m6VmTgZXA3hEG0ujFqw4XMthi3h966JpuEVaDmZc-b9HEMSlXlZBAyToSnQLlXuKgUSweNWwo3ayZoFNsMXFZJZ2NF3xEIA4sXN6K9nITX0vw506iFWpNhbG2F8sEnnIG3B_FrLpz36XrdZMmNwoj_MrKQWVXBq_Uuh89qVek4y-66ZM7fKPBz3Q6e_qRi0qTJYXUa0QNDD-IxiKNh2aQYwl_l5o5AWDMskWMnSHnJGDmmjBq5INZTwaKq5rh30RfZd07uy3sIEN6EtkPQyxFvspmx-7mzWfwltHQAFeG65DqG3cs1X4FwCxabSmq1L2IZDiD1ez9cglpYusyxjMmVy382akXeP7HHODNVi76RKqn4RgPXjz7_qgIzkjnR2m7ffaAyt7gfphuFTWn9LLIIMehJs4rlpRo9S22WtZpx881tkSANGW1m4d2hqPA2jqpp7VN-AU1OkPbB2CbS-I8ol3HrHbniAlDuiRgX-8CmwVgeTOwDKEamm1s\"," +
                "\"p\":\"-rW6WMU1Y0VGK-d9BBd6TSVIONHSvA51Ls92pROln17tlo8wGcXlAIMUWOUZ0d48Fz8JXoc-iZ5mH7ACdGTRSh4bp8BtNTTdlh4yz8kogfxZKzc7sw8J49sLNaSuhpMVgCebohoUM5PQ9k0xO4KLOrJvspFI5_pX88BWC6b-ZKT7shjQeye-UuqIabe-rWHuL1egE061sLnDtmIxzjBvVuA_39P47gys19SC_itvbwQJgqu0kDZiYHpPtf9m1T3wXkjZhpOGt5kB-_nQmHcFTzsd9eZSqOned8EM2hQlqR6AvEzHcNc_J8gJ3db76nr1rFnbAUCM3dtAx9y5nT3Okw\"," +
                "\"q\":\"z4cXrJulpZ0g5oYcjMeE3WgW6j5FUfSUqFLEG1hJXySMH4mfDVu9_wNwz6HzSfhiQpprbYWmH-3Btlos1o-hAvq0gXS_wVEWjpdWto9ADVOOiPlMhU5uP0zhvkX5wX8-XauFdisoKkOBKB8EkslXVrzSiM9Yr-ToMB6xsH2gcSsmU0ma0W5XKavyuQONbYuf-YKqEydZyA3_smJqe-qsZWkGQDSFryJek-KroAQ0HmX5UMQHz8qiAZa7I0tsepuOejjuJTn18PjfRA7omNoS9N1AEEgR6GjVB6rOKTXpnivUFAJ_VGNLb9UYn9jVhYWFlksM_MUI8iAbr6No72M1GQ\"," +
                "\"qi\":\"Ok7I1bN738b2lsbjs9ATuC0Zmvox7vRPoDxY-IU7QSpqcE6YhUYKs7q6-K-OV4j6rbeB20fz7oE_tyeO8W3167A0A7rQa4OhxWnNYnihwPQxJ0Jujhfv1xi4ThyvvVSdJqmAR7Qod7PgprEQ3A7HPtF2yF8Y4NQ5wAFSocE_J_8oMuFsBLdSJ890Viqr4h_JHIkLiZtaNOWZZvseDr5SvD_GEoutvA9LQi2cyfDVgN6fIGm0ixmKQ67PKgJObSjJE1U4xMtU-nny45yePRm1ZTRR76Bn9BylfJlD0ksBr1Ke47UzWtFxFOjZ3HiKytDo7VaLBtrtfZs_HaE9xnE2Cg\"" +
                "}";
        RSAKey rsaPrivateKey = RSAKey.parse(JSONObjectUtils.parse(RS256_PrivateKey_JWK));

        String RS256_PublicKey_JWK = "" +
                "{" +
                "\"alg\":\"RS256\"," +
                "\"e\":\"AQAB\"," +
                "\"ext\":true," +
                "\"key_ops\":[\"verify\"]," +
                "\"kty\":\"RSA\"," +
                "\"n\":\"yz0-smqLmO6GdNUmIhW5aEJXnTiZejLlpSq6E43KjR2o36I2HF5oEePW779re7q7lLWc3bZBtg8UUTSPt6FYv-X6IIOBpOveycHCLYNGzKgFcqoR3XM0CYkkYfRlo4R0cDinZhC7_8LB9GInOnOFqogN6OKW-lrzU6MrX8QWFNU0rSFZ57m6VmTgZXA3hEG0ujFqw4XMthi3h966JpuEVaDmZc-b9HEMSlXlZBAyToSnQLlXuKgUSweNWwo3ayZoFNsMXFZJZ2NF3xEIA4sXN6K9nITX0vw506iFWpNhbG2F8sEnnIG3B_FrLpz36XrdZMmNwoj_MrKQWVXBq_Uuh89qVek4y-66ZM7fKPBz3Q6e_qRi0qTJYXUa0QNDD-IxiKNh2aQYwl_l5o5AWDMskWMnSHnJGDmmjBq5INZTwaKq5rh30RfZd07uy3sIEN6EtkPQyxFvspmx-7mzWfwltHQAFeG65DqG3cs1X4FwCxabSmq1L2IZDiD1ez9cglpYusyxjMmVy382akXeP7HHODNVi76RKqn4RgPXjz7_qgIzkjnR2m7ffaAyt7gfphuFTWn9LLIIMehJs4rlpRo9S22WtZpx881tkSANGW1m4d2hqPA2jqpp7VN-AU1OkPbB2CbS-I8ol3HrHbniAlDuiRgX-8CmwVgeTOwDKEamm1s\"" +
                "}";
        RSAKey rsaPublicKey = RSAKey.parse(RS256_PublicKey_JWK);

        String JWT_IDToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2xvY2FsaG9zdDo3MDAyL2Nhcy9vYXV0aDIiLCJzdWIiOiJEU0RfVVNFUjEiLCJjbGllbnRfaWQiOiJuYXRpb25hbC1icm9rZXItZGVtbyIsImF1ZCI6Imh0dHBzOi8vbG9jYWxob3N0OjcwMDIvY2FzL29hdXRoMi9hdXRob3JpemUiLCJleHAiOjE4OTM1MzUyMDB9.pUiJMXIS8VpMhT7xp8rClwIAWtyXkXiwpMeOCYBK7VmaXtebvcWGlNFsXcsvW2IllUPs4IUVVL_K_tqSuK7YHWiGIqOPtIa7QoLKfe_ue3bG1U78RaLEip2K8N7116jNVvJYGWzAeaaT-mbBPqbm-BrMc5xtDRDMbLOFMTG5SzqGJ9WsrzRE9qZ13HvHgqaWRA61Xx-afZ7y4Mw_g9Tl5sH1NtynQD14y8ovYVTzcebkvms_05iPf22EHf56-rzfezyjOkbO2g-rBwNgroVjJ6TcU4jdO4nZSAob_e9XfMT0R7Dms_bVKBLu65blmUzxSz1eTItnwnItM9QekdBLF8SOaf0ktROlo-MCQET73vnBmEve8F6V9MG7GGuQB1Q4TtwAxCZ847GSrz3J7itTIHRIlrFxgaBNzM3Ii2_PNjxzFzc9iTpB91HlW8lShKw0njyj64TlheDLh1TPAERvpOIuUgoD5IS4lFocTr3xh1mAis_wqPERPORrus8wl3pRW05vfUBXfHrO4ljhxRgBfLznl2tsoRqggJkkTBvDAd9L9RbOlAFUdYPYHgKGOCZO3_vR5c8p02RE52NrbOTYMf1Tr4j5fgPiquUq5XKejQPpl2emx3z-oKXt-gWUFfar5ctmr6HxRt87M4PF-bHuie_NaOPNiX-wkWcNEQ6NohY";

        JWSObject jwsObject = JWSObject.parse(JWT_IDToken);
        JWSHeader header = jwsObject.getHeader();
        Payload payload = jwsObject.getPayload();
        Base64URL jwsObjectSignature = jwsObject.getSignature();

        System.out.println("HEADER:" + header);
        System.out.println("PAYLOAD:" + payload);

        JWSVerifier verifier = new RSASSAVerifier(rsaPublicKey);
        System.out.println("Result of verification:" + jwsObject.verify(verifier));

        System.out.println("Header: included params:" + header.getIncludedParams());
        System.out.println("Header: Algorithm:" + header.getAlgorithm());
        System.out.println("Header: Content type:" + header.getContentType());
        System.out.println("Header: Type:" + header.getType());

        JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(payload.toJSONObject());
        System.out.println("Issuer:" + jwtClaimsSet.getIssuer());
        System.out.println("Subject:" + jwtClaimsSet.getSubject());
        System.out.println("Client ID:" + jwtClaimsSet.getStringClaim("client_id"));
        System.out.println("Audience List:" + jwtClaimsSet.getAudience());
        System.out.println("Expiration:" + jwtClaimsSet.getExpirationTime());
        System.out.println("////////////////////////////////////////////");
        System.out.println("Signature:" + jwsObjectSignature);

    }


}
