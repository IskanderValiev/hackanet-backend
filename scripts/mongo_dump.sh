now="$(date)"
docker exec -i mongo mongodump --uri="mongodb://localhost:27017" -o ~/mongo-dumps/"${now}"
