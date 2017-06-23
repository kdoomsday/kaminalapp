# --- !Ups
alter table clientes add column cuenta char(20);

# --- !Downs

alter table clientes drop column cuenta;
