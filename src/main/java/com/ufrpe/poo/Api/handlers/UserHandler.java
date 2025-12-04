package com.ufrpe.poo.Api.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufrpe.poo.Model.User;
import com.ufrpe.poo.services.UserService;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UserHandler extends BaseHandler {

    private UserService userService;
    private ObjectMapper mapper = new ObjectMapper();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case "/api/users/search":
                handleSearchUsers(exchange);
                break;
            case "/api/users/login":
                handleLogin(exchange);
                break;
            default:
                sendResponse(
                    exchange,
                    404,
                    "{\"error\": \"Endpoint não encontrado\"}"
                );
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/api/users")) {
            handleCreateUser(exchange);
        } else {
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


    private void handleSearchUsers(HttpExchange exchange) throws IOException {
        Map<String, String> params = getQueryParams(exchange);
        String searchTerm = params.get("q");

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            sendResponse(
                exchange,
                400,
                "{\"error\": \"Parâmetro 'q' é obrigatório para busca\"}"
            );
            return;
        }

        List<User> users = userService.searchUsers(searchTerm);

        if (users == null || users.isEmpty()) {
            sendResponse(
                exchange,
                404,
                String.format("{\"message\": \"Nenhum usuário encontrado para '%s'\"}", searchTerm)
            );
            return;
        }

        StringBuilder json = new StringBuilder("{\"results\": [");

        for (User user : users) {
            json.append(String.format(
                "{\"username\": \"%s\"},",
                 user.getUsername()
            ));
        }

        json.deleteCharAt(json.length() - 1); // Remove última vírgula
        json.append(String.format("], \"count\": %d}", users.size()));

        sendResponse(exchange, 200, json.toString());
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        Map<String, String> params = getQueryParams(exchange);

        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            sendResponse(
                exchange,
                400,
                "{\"error\": \"Parâmetros username e password são obrigatórios\"}"
            );
            return;
        }

        User user = userService.getAccessUser(username, password);

        if (user == null) {
            sendResponse(
                exchange,
                401,
                "{\"error\": \"Credenciais inválidas\"}"
            );
            return;
        }

        String response = String.format(
            "{\"username\": \"%s\", \"message\": \"Login realizado com sucesso\"}",
            user.getUsername()
        );
        sendResponse(exchange, 200, response);
    }

    private void handleCreateUser(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());


        Map<String, String> map = mapper.readValue(body, Map.class);

        String username = map.get("username");
        String password = map.get("password");

        if (username == null || password == null) {
            sendResponse(
                    exchange,
                    400,
                    "{\"error\": \"Username e password são obrigatórios\"}"
            );
            return;
        }

        User newUser = userService.createUser(username, password);

        if (newUser == null) {
            sendResponse(
                exchange,
                400,
                "{\"error\": \"Não foi possível criar o usuário\"}"
            );
            return;
        }

        String response = String.format(
            "{\"username\": \"%s\", \"message\": \"Usuário criado com sucesso\"}",
             newUser.getUsername()
        );
        sendResponse(exchange, 201, response);
    }

}