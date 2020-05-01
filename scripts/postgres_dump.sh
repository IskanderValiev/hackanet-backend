now="$(date)"
docker exec -t -u postgres postgres pg_dump hackanet_db > ~/postgres-dumps/"${now}".sql
