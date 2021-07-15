-- create sequence
create table email_sequence (next_val bigint)
insert into email_sequence (next_val)
SELECT
  ISNULL(MAX(id),0) + 1
FROM email_entity;
GO

-- remove identity constraint from email_entity so that hibernate can set the id
alter table email_entity_attachments drop constraint FKl5kwwv5ab57us9aq6kmf68jk3;
alter table email_entity_bcc drop constraint FKnjngqktdgllaoe7uvi6likw8o;
alter table email_entity_cc drop constraint FKi7kmdg6945n5spcixblarm5tf;
alter table email_entity_inline_images drop constraint FKnq93nttfymqh9lsqbec5pc3dl;
alter table email_entity_tags drop constraint FKgjhn91uok9ftucrubmtck5eh1;
alter table email_entity_to drop constraint FKryk06nppew24at1013wdfl53r;
GO

declare @table_name nvarchar(256)
declare @col_name nvarchar(256)
declare @Command  nvarchar(1000)
set @table_name = N'email_entity'
set @col_name = N'id'
select @Command = 'ALTER TABLE ' + @table_name + ' drop constraint ' + d.name
 from sys.key_constraints d
 where type = 'PK' AND OBJECT_NAME(parent_object_id) = N'email_entity'
execute (@Command);
GO

alter table email_entity add tmp_id bigint;
GO
update email_entity set tmp_id=id;
GO
alter table email_entity drop column id;
GO
alter table email_entity add id bigint;
GO
update email_entity set id=tmp_id;
GO
alter table email_entity drop column tmp_id;
GO