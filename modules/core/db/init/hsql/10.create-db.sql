-- begin SAMPLE_SALESPERSON
create table SAMPLE_SALESPERSON (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    PHONE varchar(255),
    LATITUDE double precision,
    LONGITUDE double precision,
    TERRITORY_ID varchar(36),
    PHOTO_ID varchar(36),
    POLYGON_COLOR varchar(7),
    --
    primary key (ID)
)^
-- end SAMPLE_SALESPERSON
-- begin SAMPLE_TERRITORY
create table SAMPLE_TERRITORY (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    POLYGON_GEOMETRY longvarchar,
    --
    primary key (ID)
)^
-- end SAMPLE_TERRITORY
-- begin SAMPLE_ORDER
create table SAMPLE_ORDER (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    AMOUNT double precision,
    DATE_ date,
    SALESPERSON_ID varchar(36),
    --
    primary key (ID)
)^
-- end SAMPLE_ORDER
