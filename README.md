
# Conversinha

## Banco de dado - Docker 

```shell
docker run -d --network=host --name=conversinha\
  -e POSTGRES_USER=conversinha \
  -e POSTGRES_PASSWORD=123 \
  -e POSTGRES_DB=conversinhadb \
  -v pgdata:/var/lib/postgresql/data \
  postgres:17
```

## Endpoint 

- create user: htttp://localhost:8080/api/users
- login: http://localhost:3030/api/users/login?username={username}&password={password}
- get messages not read: http://localhost:3030/api/messages/check/{username}
- send message: http://localhost:3030/api/messages
- get sent message: http://localhost:3030/api/messages/sent/
- read message: http://localhost:3030/api/messages/message/3

## HTML handle

- **/**
- **/home**
- **/login**
- **/register**
- **/send**
- **/sent**
- **/view**