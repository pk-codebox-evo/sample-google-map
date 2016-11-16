alter table CUBAMAPEXAMPLE_SALES_PERSON add column LATITUDE double precision ;
alter table CUBAMAPEXAMPLE_SALES_PERSON add column LONGITUDE double precision ;
alter table CUBAMAPEXAMPLE_SALES_PERSON drop column POINT_GEOMETRY cascade ;
