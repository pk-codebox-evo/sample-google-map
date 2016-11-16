-- begin CUBAMAPEXAMPLE_SALES_PERSON
create table CUBAMAPEXAMPLE_SALES_PERSON (
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
-- end CUBAMAPEXAMPLE_SALES_PERSON
-- begin CUBAMAPEXAMPLE_SALES_TERRITORY
create table CUBAMAPEXAMPLE_SALES_TERRITORY (
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
-- end CUBAMAPEXAMPLE_SALES_TERRITORY
-- begin CUBAMAPEXAMPLE_SALES_ORDER
create table CUBAMAPEXAMPLE_SALES_ORDER (
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
    SALES_PERSON_ID varchar(36),
    --
    primary key (ID)
)^
-- end CUBAMAPEXAMPLE_SALES_ORDER
