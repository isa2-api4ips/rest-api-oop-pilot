-- This file was generated by hibernate for: [eu.europa.ec.isa2.oop.restapi.pilot:dsd-mock:1.0-SNAPSHOT].

    create table DSD_MOCK_ADDRESS (
       ID_PK bigint not null,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        ADMIN_UNIT_LEVEL varchar(255),
        FULL_ADDRESS varchar(255),
        primary key (ID_PK)
    );

    create table DSD_MOCK_DATASET (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        CONFORMS_TO varchar(255),
        DATASET_TYPE varchar(255),
        FK_ORGANIZATION_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_DIST_DATASERVICE (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        CONFORMS_TO varchar(255),
        ENDPOINT_URL varchar(255),
        IDENTIFIER varchar(255),
        TITLE varchar(255),
        FK_DISTRIBUTION_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_DIST_DESCRIPTION (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        DESCRIPTION varchar(255) not null,
        FK_DISTRIBUTION_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_DSET_DESCRIPTION (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        DESCRIPTION varchar(255) not null,
        FK_DATASET_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_DSET_DISTRIBUTION (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        ACCESS_URL varchar(255),
        CONFORMS_TO varchar(255),
        FORMAT varchar(255),
        MEDIA_TYPE varchar(255),
        FK_DATASET_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_DSET_IDENTIFIER (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        IDENTIFIER varchar(255) not null,
        FK_DATASET_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_DSET_RELATIONSHIP (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        HADROLE varchar(255),
        RELATION varchar(255),
        FK_DATASET_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_DSET_TITLE (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        TITLE varchar(255) not null,
        FK_DATASET_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_ORG_ALTLABEL (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        ALT_LABEL varchar(255),
        FK_ORGANIZATION_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_ORG_CLASSIFICATION (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        CLASSIFICATION varchar(255),
        FK_ORGANIZATION_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_ORG_PREFLABEL (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        PREF_LABEL varchar(255),
        FK_ORGANIZATION_ID bigint not null,
        primary key (ID_PK)
    );

    create table DSD_MOCK_ORGANIZATION (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        IDENTIFIER varchar(255),
        primary key (ID_PK)
    );

    create table DSD_PULL_MESSAGE (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        ACTION varchar(255),
        IDENTIFIER varchar(255),
        REF_ACTION varchar(255),
        REF_IDENTIFIER varchar(255),
        REF_SERVICE varchar(255),
        SERVICE varchar(255),
        STATUS varchar(255),
        primary key (ID_PK)
    );

    create table DSD_PULL_MESSAGE_PAYLOAD (
       ID_PK bigint generated by default as identity,
        CREATED_ON timestamp,
        LAST_UPDATED_ON timestamp,
        MIME_TPYE varchar(255),
        NAME varchar(255),
        PATH varchar(255),
        FK_PULL_MESSAGE_ID bigint not null,
        primary key (ID_PK)
    );

    alter table DSD_MOCK_DSET_IDENTIFIER 
       add constraint UK_h9dtpbfbw33enjntaro88590v unique (IDENTIFIER);

    alter table DSD_MOCK_ORGANIZATION 
       add constraint UK_af9lw528f3agiq1e569kbsutr unique (IDENTIFIER);

    alter table DSD_MOCK_ADDRESS 
       add constraint FKpc76la9b7au6krk79o7bqhpun 
       foreign key (ID_PK) 
       references DSD_MOCK_ORGANIZATION;

    alter table DSD_MOCK_DATASET 
       add constraint FKtgrsaboeppuqpee5f7clsgjf8 
       foreign key (FK_ORGANIZATION_ID) 
       references DSD_MOCK_ORGANIZATION;

    alter table DSD_MOCK_DIST_DATASERVICE 
       add constraint FKscik7hg56vb8v6t7me4db3nue 
       foreign key (FK_DISTRIBUTION_ID) 
       references DSD_MOCK_DSET_DISTRIBUTION;

    alter table DSD_MOCK_DIST_DESCRIPTION 
       add constraint FKb95sckyh1fbcatb9krrwd9d7x 
       foreign key (FK_DISTRIBUTION_ID) 
       references DSD_MOCK_DSET_DISTRIBUTION;

    alter table DSD_MOCK_DSET_DESCRIPTION 
       add constraint FKt9kly43hi9ycm20tg252mj051 
       foreign key (FK_DATASET_ID) 
       references DSD_MOCK_DATASET;

    alter table DSD_MOCK_DSET_DISTRIBUTION 
       add constraint FK4jn4sa9i5e8v53gf6is1l3nys 
       foreign key (FK_DATASET_ID) 
       references DSD_MOCK_DATASET;

    alter table DSD_MOCK_DSET_IDENTIFIER 
       add constraint FK66lmdkxff672k44isv8bdtrtk 
       foreign key (FK_DATASET_ID) 
       references DSD_MOCK_DATASET;

    alter table DSD_MOCK_DSET_RELATIONSHIP 
       add constraint FK53cg9qt3f4rhfih64xcvjvg9o 
       foreign key (FK_DATASET_ID) 
       references DSD_MOCK_DATASET;

    alter table DSD_MOCK_DSET_TITLE 
       add constraint FKkjwfuwscrfbdgqjqkb9kmx9fb 
       foreign key (FK_DATASET_ID) 
       references DSD_MOCK_DATASET;

    alter table DSD_MOCK_ORG_ALTLABEL 
       add constraint FK1pno0cil1ng6ndwqufehft95y 
       foreign key (FK_ORGANIZATION_ID) 
       references DSD_MOCK_ORGANIZATION;

    alter table DSD_MOCK_ORG_CLASSIFICATION 
       add constraint FKf5efxi95emsat5pq9unxftcxy 
       foreign key (FK_ORGANIZATION_ID) 
       references DSD_MOCK_ORGANIZATION;

    alter table DSD_MOCK_ORG_PREFLABEL 
       add constraint FKbhse1mfqyywq3wtsl4uuywitl 
       foreign key (FK_ORGANIZATION_ID) 
       references DSD_MOCK_ORGANIZATION;

    alter table DSD_PULL_MESSAGE_PAYLOAD 
       add constraint FK9xdsu63i5k1lasw3v6iltsvg 
       foreign key (FK_PULL_MESSAGE_ID) 
       references DSD_PULL_MESSAGE;
