EXECUTE sp_rename 'email_entity.created', 'created_at', 'COLUMN';
GO
alter table email_entity add updated_at datetime2;
GO
alter table email_entity drop column _from;
GO
alter table email_entity drop column reply_to;
GO
alter table email_entity drop column subject;
GO
alter table email_entity drop column text;
GO
alter table email_entity drop column html;
GO
drop table email_entity_attachments;
GO
drop table email_entity_bcc;
GO
drop table email_entity_cc;
GO
drop table email_entity_inline_images;
GO
drop table email_entity_tags;
GO
drop table email_entity_to;
GO
drop table email_sequence;
GO
create sequence email_sequence start with 1 increment by 50 minvalue 1
GO