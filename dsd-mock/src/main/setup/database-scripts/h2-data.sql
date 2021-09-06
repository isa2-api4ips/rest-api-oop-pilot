-----------------------------
-- Organization
-----------------------------
INSERT INTO DSD_MOCK_ORGANIZATION  (ID_PK, IDENTIFIER, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, '100001', NOW(), NOW()),
(2, '110001', NOW(), NOW()),
(3, '111001', NOW(), NOW()),
(4, '111002', NOW(), NOW());

INSERT INTO DSD_MOCK_ORG_ALTLABEL (ID_PK, FK_ORGANIZATION_ID, ALT_LABEL, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1, 'Federal Ministry of Education, will provide information regarding German university diplomas issues by any German university@en',NOW(), NOW()),
(2, 1, 'Bundesbildungsministerium, informiert von allen deutschen Hochschulen zu Fragen des deutschen Hochschulabschlusses@de',NOW(), NOW()),
(3, 2, 'State Ministry of Education, will provide information regarding diplomas issued by universities located in Bavaria',NOW(), NOW()),
(4, 3, 'Technical university of Munchen will provide information regarding diplomas issued by this university',NOW(), NOW()),
(5, 4, 'Munchen univeristy of Applied Sciences will provide information regarding diplomas issued by this university',NOW(), NOW());


INSERT INTO DSD_MOCK_ORG_PREFLABEL (ID_PK, FK_ORGANIZATION_ID, PREF_LABEL, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1, 'Federal Ministry of Education and Research@en',NOW(), NOW()),
(2, 1, 'Bundesministerium für Bildung und Forschung@de',NOW(), NOW()),
(3, 2, 'Bavarian State Ministry for Education, Culture, Science and Arts',NOW(), NOW()),
(4, 3, 'Technische Universitat Munchen',NOW(), NOW()),
(5, 4, 'Munich University of Applied Sciences',NOW(), NOW());


INSERT INTO DSD_MOCK_ORG_CLASSIFICATION (ID_PK, FK_ORGANIZATION_ID, CLASSIFICATION, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1, 'level-0',NOW(), NOW()),
(2, 2, 'level-1',NOW(), NOW()),
(3, 3, 'level-5',NOW(), NOW()),
(4, 4, 'level-5',NOW(), NOW());

INSERT INTO DSD_MOCK_ADDRESS (ID_PK, ADMIN_UNIT_LEVEL, FULL_ADDRESS, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 'DE', 'Heinemannstrasse 2, 53175, Bonn', NOW(), NOW()),
(2, 'DE', 'Salvatorstrasse 2, Munchen, 80333',NOW(), NOW()),
(3, 'DE', 'Arcisstrasse 21, 80333 Munchen',NOW(), NOW()),
(4, 'DE', 'Lothstrasse 34, 80335 Munchen',NOW(), NOW());

-----------------------------
-- DATASET
-----------------------------
INSERT INTO DSD_MOCK_DATASET (ID_PK,  FK_ORGANIZATION_ID, CONFORMS_TO, DATASET_TYPE, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 3, 'https://semantic-repository.toop.eu/ontology/higher-education/bachelor-degree', 'BACHELOR_DEGREE', NOW(), NOW()),
(2, 3, 'https://semantic-repository.toop.eu/ontology/higher-education/master-degree', 'MASTER_DEGREE', NOW(), NOW()),
(3, 4, 'https://semantic-repository.toop.eu/ontology/higher-education/doctorate-degree', 'DOCTORATE_DEGREE', NOW(), NOW()),
(4, 1, 'https://semantic-repository.toop.eu/ontology/higher-education/state-ministry', 'STATE_MINISTRY', NOW(), NOW()),
(5, 2, 'https://semantic-repository.toop.eu/ontology/higher-education/accredited-higher-education-institution', 'ACCREDITED_HIGHER_EDUCATION_INSTITUTION', NOW(), NOW())
;

INSERT INTO DSD_MOCK_DSET_DESCRIPTION  (ID_PK, FK_DATASET_ID, DESCRIPTION, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1, 'A dataset about the bachelor degree', NOW(), NOW()),
(2, 2, 'A dataset about the master degree', NOW(), NOW()),
(3, 3, 'A dataset about the doctorate degree', NOW(), NOW()),
(4, 4, 'A dataset about the state Ministries responsible for higher education', NOW(), NOW()),
(5, 5, 'A dataset about accredited higher education institution', NOW(), NOW()),
;

INSERT INTO DSD_MOCK_DSET_TITLE  (ID_PK,FK_DATASET_ID, TITLE, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1,  'Education bachelor degree registry', NOW(), NOW() ),
(2, 2,  'Education master degree registry', NOW(), NOW() ),
(3, 3,  'Education doctorate degree registry', NOW(), NOW()),
(4, 4,  'Registry of the State Ministries responsible for higher education', NOW(), NOW()),
(5, 5,  'Accredited higher education institution registry', NOW(), NOW()),
;

INSERT INTO DSD_MOCK_DSET_IDENTIFIER  (ID_PK,FK_DATASET_ID, IDENTIFIER, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1,  'HE238918371', NOW(), NOW()),
(2, 1,  'AE238918371', NOW(), NOW()),
(3, 1,  'BE238918371', NOW(), NOW()),
(4, 2,  'HE238918372', NOW(), NOW()),
(5, 3,  'HE238918373', NOW(), NOW()),
(6, 4,  'DE000001000', NOW(), NOW()),
(7, 5,  'DE000001001', NOW(), NOW()),
;

INSERT INTO DSD_MOCK_DSET_RELATIONSHIP (ID_PK, FK_DATASET_ID, HADROLE, RELATION, CREATED_ON,LAST_UPDATED_ON) VALUES
(1, 1,  'https://toop.eu/dataset/supportedIdScheme', 'urn:oasis:names:tc:ebcore:partyid-type:iso6523:9999:0000000001', NOW(), NOW()),
(2, 2,  'https://toop.eu/dataset/supportedIdScheme', 'urn:oasis:names:tc:ebcore:partyid-type:iso6523:9999:0000000002', NOW(), NOW()),
(3, 3,  'https://toop.eu/dataset/supportedIdScheme', 'urn:oasis:names:tc:ebcore:partyid-type:iso6523:9999:0000000003', NOW(), NOW()),
(4, 4,  'https://toop.eu/dataset/supportedIdScheme', 'urn:oasis:names:tc:ebcore:partyid-type:iso6523:9999:0000000004', NOW(), NOW()),
(5, 5,  'https://toop.eu/dataset/supportedIdScheme', 'urn:oasis:names:tc:ebcore:partyid-type:iso6523:9999:0000000005', NOW(), NOW()),
;

INSERT INTO DSD_MOCK_DSET_DISTRIBUTION ( ID_PK,FK_DATASET_ID,ACCESS_URL,CONFORMS_TO,FORMAT,MEDIA_TYPE,CREATED_ON,LAST_UPDATED_ON) VALUES
(1, 1,  'https://smp.bonn.toop.eu/9999::0000000001/services/BACHELOR_DEGREE', 'RegRepv4-EDMv2','UNSTRUCTURED','application/pdf', NOW(), NOW()),
(2, 1,  'https://smp.bonn.toop.eu/9999::0000000001/services/BACHELOR_DEGREE', 'RegRepv4-EDMv2','STRUCTURED','application/xml', NOW(), NOW()),
(3, 2,  'https://smp.bonn.toop.eu/9999::0000000002/services/MASTER_DEGREE', 'RegRepv4-EDMv2','UNSTRUCTURED','application/pdf', NOW(), NOW()),
(4, 2,  'https://smp.bonn.toop.eu/9999::0000000002/services/MASTER_DEGREE', 'RegRepv4-EDMv2','STRUCTURED','application/xml', NOW(), NOW()),
(5, 3,  'https://smp.bonn.toop.eu/9999::0000000003/services/DOCTORATE_DEGREE', 'RegRepv4-EDMv2','UNSTRUCTURED','application/pdf', NOW(), NOW()),
(6, 3,  'https://smp.bonn.toop.eu/9999::0000000003/services/DOCTORATE_DEGREE', 'RegRepv4-EDMv2','STRUCTURED','application/xml', NOW(), NOW()),
(7, 4,  'https://smp.bonn.toop.eu/9999::0000000003/services/STATE_MINISTRY', 'RegRepv4-EDMv2','STRUCTURED','application/xml', NOW(), NOW()),
(8, 5,  'https://smp.bonn.toop.eu/9999::0000000003/services/ACCREDITED_HIGHER_EDUCATION_INSTITUTION', 'RegRepv4-EDMv2','STRUCTURED','application/xml', NOW(), NOW());


INSERT INTO DSD_MOCK_DIST_DESCRIPTION  (ID_PK, FK_DISTRIBUTION_ID, DESCRIPTION, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1, 'This is a PDF distribution of the bachelor registry', NOW(), NOW()),
(2, 2, 'This is a XML distribution of the bachelor registry', NOW(), NOW()),
(3, 3, 'This is a PDF distribution of the master registry', NOW(), NOW()),
(4, 4, 'This is a XML distribution of the master registry', NOW(), NOW()),
(5, 5, 'This is a PDF distribution of the doctorate registry', NOW(), NOW()),
(6, 6, 'This is a XML distribution of the doctorate registry', NOW(), NOW()),
(7, 7, 'This is a XML distribution of the registry', NOW(), NOW()),
(8, 8, 'This is a XML distribution of the registry', NOW(), NOW());
;

INSERT INTO DSD_MOCK_DIST_DATASERVICE ( ID_PK, FK_DISTRIBUTION_ID, IDENTIFIER, TITLE, ENDPOINT_URL, CONFORMS_TO, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1, 'toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-2::Request##urn:eu.toop.request.bachelor-degree::2.0', 'Access Service Title','https://smp.bonn.toop.eu/9999::0000000001/services/BACHELOR_DEGREE?type=pdf',  '', NOW(), NOW() ),
(2, 2, 'toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-2::Request##urn:eu.toop.request.bachelor-degree::2.0', 'Access Service Title','https://smp.bonn.toop.eu/9999::0000000001/services/BACHELOR_DEGREE?type=xml',  '', NOW(), NOW() ),
(3, 3, 'toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-2::Request##urn:eu.toop.request.master-degree::2.0'   , 'Access Service Title','https://smp.bonn.toop.eu/9999::0000000002/services/MASTER_DEGREE?type=pdf',    '', NOW(), NOW() ),
(4, 4, 'toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-2::Request##urn:eu.toop.request.master-degree::2.0'   , 'Access Service Title','https://smp.bonn.toop.eu/9999::0000000002/services/MASTER_DEGREE?type=xml',    '', NOW(), NOW() ),
(5, 5, 'toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-2::Request##urn:eu.toop.request.doctorate-degree::2.0', 'Access Service Title','https://smp.bonn.toop.eu/9999::0000000003/services/DOCTORATE_DEGREE?type=pdf', '', NOW(), NOW() ),
(6, 6, 'toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-2::Request##urn:eu.toop.request.doctorate-degree::2.0', 'Access Service Title','https://smp.bonn.toop.eu/9999::0000000004/services/DOCTORATE_DEGREE?type=xml', '', NOW(), NOW() ),
(7, 7, 'toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-2::Request##urn:eu.toop.request.state-ministry::2.0', 'Access Service Title','https://smp.bonn.toop.eu/9999::0000000005/services/state-ministry?type=xml', '', NOW(), NOW() ),
(8, 8, 'toop-doctypeid-qns::urn:eu:toop:ns:dataexchange-2::Request##urn:eu.toop.request.accredited-higher-education-institution::2.0', 'Access Service Title','https://smp.bonn.toop.eu/9999::0000000006/services/ACCREDITED_HIGHER_EDUCATION_INSTITUTION?type=xml', '', NOW(), NOW() );
