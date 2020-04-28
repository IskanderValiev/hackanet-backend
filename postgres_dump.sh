docker exec -it postgres bash
dump_file_name-$(date + '%Y-%m-%d')
pg_dump hackanet > ~/postgres-dumps/${dump_file_name}
