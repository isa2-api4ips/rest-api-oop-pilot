
INSERT INTO NB_AUTH_ROLES(ID_PK, ROLE_NAME,  CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 'ROLE_SUPER_USER', NOW(), NOW()),
(2, 'ROLE_UPDATE_DSD', NOW(), NOW());


INSERT INTO NB_USER (ID_PK, USERNAME, NAME, EMAIL, ACTIVE, USER_IDP_ID, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 'ADMIN', 'Admin User','admin.organization@isa2.eu', 1, 'auth0|60d0851e0eb20b006998bebc', NOW(), NOW()),
(2, 'USER_DSD', 'Dsd User','dsd.organization@isa2.eu', 1, 'auth0|60aaec1b802b8800686a5c94', NOW(), NOW());

INSERT INTO NB_USER_ROLE (FK_USER_ID, FK_ROLE_ID) VALUES
(1, 1),
(1, 2),
(2, 2);


INSERT INTO NB_DSD_ORGANIZATION  (ID_PK, IDENTIFIER, DSD_STATUS, DSD_MESSAGE, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, '100001', 'OK', NULL, NOW(), NOW()),
(2, '110001', 'OK', NULL, NOW(), NOW()),
(3, '111001', 'OK', NULL, NOW(), NOW()),
(4, '111002', 'OK', NULL, NOW(), NOW());

INSERT INTO NB_DSD_ORG_ALTLABEL (ID_PK, FK_ORGANIZATION_ID, ALT_LABEL, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1, 'Federal Ministry of Education, will provide information regarding German university diplomas issues by any German university',NOW(), NOW()),
(2, 2, 'State Ministry of Education, will provide information regarding diplomas issued by universities located in Bavaria',NOW(), NOW()),
(3, 3, 'Technical university of Munchen will provide information regarding diplomas issued by this university',NOW(), NOW()),
(4, 4, 'Munchen univeristy of Applied Sciences will provide information regarding diplomas issued by this university',NOW(), NOW());


INSERT INTO NB_DSD_ORG_PREFLABEL (ID_PK, FK_ORGANIZATION_ID, PREF_LABEL, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1, 'Federal Ministry of Education and Research',NOW(), NOW()),
(2, 2, 'Bavarian State Ministry for Education, Culture, Science and Arts',NOW(), NOW()),
(3, 3, 'Technische Universitat Munchen',NOW(), NOW()),
(4, 4, 'Munich University of Applied Sciences',NOW(), NOW());

INSERT INTO NB_DSD_ORG_CLASSIFICATION (ID_PK, FK_ORGANIZATION_ID, CLASSIFICATION, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 1, 'level-0',NOW(), NOW()),
(2, 2, 'level-1',NOW(), NOW()),
(3, 3, 'level-5',NOW(), NOW()),
(4, 4, 'level-5',NOW(), NOW());


INSERT INTO NB_DSD_ADDRESS (ID_PK, ADMIN_UNIT_LEVEL, FULL_ADDRESS, CREATED_ON, LAST_UPDATED_ON) VALUES
(1, 'DE', 'Heinemannstrasse 2, 53175, Bonn', NOW(), NOW()),
(2, 'DE', 'Salvatorstrasse 2, Munchen, 80333',NOW(), NOW()),
(3, 'DE', 'Arcisstrasse 21, 80333 Munchen',NOW(), NOW()),
(4, 'DE', 'Lothstrasse 34, 80335 Munchen',NOW(), NOW());
