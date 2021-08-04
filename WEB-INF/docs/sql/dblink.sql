update importacionfacturadetalle d set costopromediogs = l.costopromediogs
FROM dblink('dbname=gtsa_070721 port=5433 host=127.0.0.1 user=postgres password=postgres',
'select id, costopromediogs from importacionfacturadetalle') 
as l(id bigint, costopromediogs double precision)
where d.id = l.id
