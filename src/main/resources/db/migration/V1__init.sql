create table email_entity (
  id bigint identity not null,
  created datetime2,
  _from varchar(255),
  html varchar(MAX),
  reply_to varchar(255),
  subject varchar(255),
  text varchar(MAX),
  status varchar(255),
  status_message varchar(255),
  primary key (id)
)
create table email_entity_attachments (
  email_entity_id bigint not null,
  content varchar(MAX),
  content_type varchar(255),
  id bigint,
  name varchar(255)
)
create table email_entity_bcc (
  email_entity_id bigint not null,
  bcc varchar(255)
)
create table email_entity_cc (
  email_entity_id bigint not null,
  cc varchar(255)
)
create table email_entity_inline_images (
  email_entity_id bigint not null,
  content varchar(MAX),
  content_type varchar(255),
  id bigint,
  name varchar(255)
)
create table email_entity_tags (
  email_entity_id bigint not null,
  name varchar(255),
  value varchar(255)
)
create table email_entity_to (
  email_entity_id bigint not null,
  _to varchar(255)
)
alter table email_entity_attachments add constraint FKl5kwwv5ab57us9aq6kmf68jk3 foreign key (email_entity_id) references email_entity
alter table email_entity_bcc add constraint FKnjngqktdgllaoe7uvi6likw8o foreign key (email_entity_id) references email_entity
alter table email_entity_cc add constraint FKi7kmdg6945n5spcixblarm5tf foreign key (email_entity_id) references email_entity
alter table email_entity_inline_images add constraint FKnq93nttfymqh9lsqbec5pc3dl foreign key (email_entity_id) references email_entity
alter table email_entity_tags add constraint FKgjhn91uok9ftucrubmtck5eh1 foreign key (email_entity_id) references email_entity
alter table email_entity_to add constraint FKryk06nppew24at1013wdfl53r foreign key (email_entity_id) references email_entity
