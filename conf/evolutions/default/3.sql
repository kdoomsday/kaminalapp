# --- !Ups
alter table clientes add column cuenta char(20);

ALTER TABLE clientes DROP CONSTRAINT clientes_nombre_key;

# --- !Downs

alter table clientes drop column cuenta;

ALTER TABLE clientes ADD CONSTRAINT clientes_nombre_key UNIQUE(nombre);
