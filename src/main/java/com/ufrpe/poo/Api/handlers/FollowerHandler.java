package com.ufrpe.poo.Api.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.ufrpe.poo.services.FollowerService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FollowerHandler extends BaseHandler {
    private FollowerService followerService;
    private ObjectMapper mapper = new ObjectMapper();

    public FollowerHandler(FollowerService followerService) {
        this.followerService = followerService;
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if(path.matches("/api/followers/[^/]+")) {
            this.handleGetFollowerUser(exchange);
        }else{
            sendResponse(exchange, 404, "{\"error\": \"Endpoint não encontrado\"}");
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case "/api/followers":
                this.handleFollowerUser(exchange);
                break;
            case "/api/followers/unfollowers":
                this.handleUnFollowerUser(exchange);
                break;
            default:
                sendResponse(exchange, 404, "{\"error\": \"Endpoint não encontrado\"}");
        }
    }

    @Override
    protected void handlePut(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 501, "{\"message\": \"Método PUT não implementado\"}");
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 501, "{\"message\": \"Método DELETE não implementado\"}");
    }


    private void handleGetFollowerUser(HttpExchange exchange) throws IOException {
        String[] paths = super.getPathSegments(exchange);

        if(paths.length == 0) {
            sendResponse(exchange, 404, "{\"message\": \"Sem paths\"}");
            return;
        }

        if(paths.length < 3) {
            sendResponse(exchange, 404, "{\"message\": \"Sem usename\"}");
            return;
        }

        String username = paths[paths.length - 1];

        List<String> followers = followerService.getFollowers(username);

        if (followers.isEmpty()) {
            sendResponse(
                exchange,
                404,
                String.format("{\"message\": \"Nao foi possivel encontrado os seguidores para '%s'\"}", username)
            );
            return;
        }

        StringBuilder json = new StringBuilder("{\"results\": [");

        for (String u : followers) {
            json.append(String.format(
                    "{\"username\": \"%s\"},",
                    u
            ));
        }

        json.deleteCharAt(json.length() - 1); // Remove última vírgula
        json.append(String.format("], \"count\": %d}", followers.size()));

        sendResponse(exchange, 200, json.toString());

    }

    private void handleFollowerUser(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());

        Map<String, String> map = mapper.readValue(body, Map.class);

        String user = map.get("user");
        String follower = map.get("follower");

        if (user == null || follower == null) {
            sendResponse(
                    exchange,
                    400,
                    "{\"error\": \"user e followor são obrigatórios\"}"
            );
            return;
        }

        boolean success = followerService.followUser(user, follower);

        if (!success) {
            String response = String.format(
                    "{\"error\": \"Não foi possível seguir o usuário: %s\"}",
                    follower
            );

            sendResponse(
                exchange,
                400,
                response
            );

            return;
        }

        String response = String.format(
                "{\"message\": \"%s\" esta seguindo \"%s\"}",
                user,
                follower
        );

        sendResponse(exchange, 201, response);
    }

    private void handleUnFollowerUser(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());

        String[] parts = body.replace("{", "").replace("}", "").split(",");
        String user = null;
        String follower = null;

        for (String part : parts) {
            String[] keyValue = part.split(":");

            if (keyValue.length == 2) {
                String key = keyValue[0].replace("\"", "").trim();
                String value = keyValue[1].replace("\"", "").trim();

                switch (key.trim()){
                    case "user":
                        user = value;
                        break;
                    case "follower":
                        follower = value;
                        break;
                }
            }
        }

        if (user == null || follower == null) {
            sendResponse(
                    exchange,
                    400,
                    "{\"error\": \"user e followor são obrigatórios\"}"
            );
            return;
        }

        boolean success = followerService.unfollowUser(user, follower);

        if (!success) {
            String response = String.format(
                "{\"error\": \"Não foi possível fazer a operacao de unfollower com os usuário: %s e %s\"}",
                user,
                follower
            );

            sendResponse(
                exchange,
                400,
                response
            );

            return;
        }

        String response = String.format(
                "{\"message\": \"%s  deixou de seguir  %s\"}",
                user,
                follower
        );

        sendResponse(exchange, 201, response);
    }
}
