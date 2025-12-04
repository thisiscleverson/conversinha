package com.ufrpe.poo.Api.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.ufrpe.poo.Model.Message;
import com.ufrpe.poo.services.MessageService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MessageHandler extends BaseHandler {
    private MessageService messageService;
    private ObjectMapper mapper = new ObjectMapper();

    public MessageHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.matches("/api/messages/check/*/[^/]+")) {
            this.handleGetMessageNotRead(exchange);
        } else if (path.matches("/api/messages/sent/*/[^/]+")) {
            this.handleGetSentMessages(exchange);
        } else if (path.matches("/api/messages/message/*/[^/]+")) {
            this.handleGetMessageById(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Endpoint não encontrado\"}");
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/api/messages")) {
            this.handleRegisterMessage(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Endpoint não encontrado\"}");
        }
    }

    @Override
    protected void handlePut(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.matches("/api/messages/*/[^/]+")) {
            this.handleUpdateDeliveredMessage(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"Endpoint não encontrado\"}");
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 501, "{\"message\": \"Método DELETE não implementado\"}");
    }

    private void handleGetMessageNotRead(HttpExchange exchange) throws IOException {
        String[] paths = super.getPathSegments(exchange);

        if (paths.length == 0) {
            sendResponse(exchange, 400, "{\"message\": \"Sem paths\"}");
            return;
        }

        String recipient = paths[paths.length - 1];
        List<Message> messages = messageService.getMessagesNotRead(recipient);

        if (messages.isEmpty()) {
            ObjectNode response = mapper.createObjectNode();
            response.put("message", "Sem mensagens nova no momento");
            sendResponse(exchange, 404, mapper.writeValueAsString(response));
            return;
        }

        ObjectNode response = mapper.createObjectNode();
        ArrayNode resultsArray = mapper.createArrayNode();

        for (Message message : messages) {
            ObjectNode messageNode = mapper.createObjectNode();
            messageNode.put("id", message.id);
            messageNode.put("sender", message.sender);
            messageNode.put("recipient", message.recipient);
            messageNode.put("title", message.title);
            messageNode.put("content", message.content);
            messageNode.put("is_delivered", message.isDelivered);
            messageNode.put("created_at", message.createdAt.toString());
            resultsArray.add(messageNode);
        }

        response.set("results", resultsArray);
        response.put("count", messages.size());

        sendResponse(exchange, 200, mapper.writeValueAsString(response));
    }

    private void handleGetSentMessages(HttpExchange exchange) throws IOException {
        String[] paths = super.getPathSegments(exchange);

        if (paths.length == 0) {
            sendResponse(exchange, 400, "{\"message\": \"Sem paths\"}");
            return;
        }

        String sender = paths[paths.length - 1];
        List<Message> messages = messageService.getSentMessages(sender);

        if (messages.isEmpty()) {
            ObjectNode response = mapper.createObjectNode();
            response.put("message", "Sem mensagens nova no momento");
            sendResponse(exchange, 404, mapper.writeValueAsString(response));
            return;
        }

        ObjectNode response = mapper.createObjectNode();
        ArrayNode resultsArray = mapper.createArrayNode();

        for (Message message : messages) {
            ObjectNode messageNode = mapper.createObjectNode();
            messageNode.put("id", message.id);
            messageNode.put("sender", message.sender);
            messageNode.put("recipient", message.recipient);
            messageNode.put("title", message.title);
            messageNode.put("content", message.content);
            messageNode.put("is_delivered", message.isDelivered);
            messageNode.put("created_at", message.createdAt.toString());
            resultsArray.add(messageNode);
        }

        response.set("results", resultsArray);
        response.put("count", messages.size());

        sendResponse(exchange, 200, mapper.writeValueAsString(response));
    }

    private void handleRegisterMessage(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Map<String, String> map = mapper.readValue(body, Map.class);

        String sender = map.get("sender");
        String recipient = map.get("recipient");
        String title = map.get("title");
        String content = map.get("content");

        int id = messageService.registerMessage(title, sender, recipient, content);

        if (id == -1) {
            ObjectNode response = mapper.createObjectNode();
            response.put("error", String.format("Não foi possível enviar a mensagem para: %s", recipient));
            sendResponse(exchange, 400, mapper.writeValueAsString(response));
            return;
        }

        ObjectNode response = mapper.createObjectNode();
        response.put("id", id);
        response.put("message", String.format("mensagem enviada com sucesso para: %s", recipient));
        sendResponse(exchange, 201, mapper.writeValueAsString(response));
    }

    private void handleUpdateDeliveredMessage(HttpExchange exchange) throws IOException {
        String[] paths = super.getPathSegments(exchange);
        String body = new String(exchange.getRequestBody().readAllBytes());

        Map<String, Object> map = mapper.readValue(body, Map.class);
        int idFromBody = (int) map.get("id");
        boolean isDelivered = (boolean) map.get("is_delivered");

        if (!isDelivered) {
            ObjectNode response = mapper.createObjectNode();
            response.put("error", String.format("Não foi possível atualizar o is_delivered para a mensagem de id %d", idFromBody));
            sendResponse(exchange, 400, mapper.writeValueAsString(response));
            return;
        }

        String idParam = paths[paths.length - 1];
        int idFromPath;

        try {
            idFromPath = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            ObjectNode response = mapper.createObjectNode();
            response.put("error", "ID inválido");
            response.put("message", String.format("'%s' não é um número válido", idParam));
            sendResponse(exchange, 400, mapper.writeValueAsString(response));
            return;
        }

        if (idFromPath == -1) {
            ObjectNode response = mapper.createObjectNode();
            response.put("error", "Não foi passado o id da mensagem.");
            sendResponse(exchange, 401, mapper.writeValueAsString(response));
            return;
        }

        boolean updateStatus = messageService.updatedelivereStatus(idFromPath);

        if (!updateStatus) {
            ObjectNode response = mapper.createObjectNode();
            response.put("error", String.format("Não foi possível atualizar o is_delivered para a mensagem de id %d", idFromPath));
            sendResponse(exchange, 400, mapper.writeValueAsString(response));
            return;
        }

        ObjectNode response = mapper.createObjectNode();
        response.put("message", "mensagem lida.");
        sendResponse(exchange, 200, mapper.writeValueAsString(response));
    }

    private void handleGetMessageById(HttpExchange exchange) throws IOException {
        String[] paths = super.getPathSegments(exchange);

        if (paths.length == 0) {
            sendResponse(exchange, 400, "{\"message\": \"Sem paths\"}");
            return;
        }

        String idParam = paths[paths.length - 1];

        try {
            int id = Integer.parseInt(idParam);
            Message message = messageService.getMessageById(id);

            if (message == null) {
                ObjectNode response = mapper.createObjectNode();
                response.put("message", String.format("Não foi possível buscar a mensagem de id: %d", id));
                sendResponse(exchange, 404, mapper.writeValueAsString(response));
                return;
            }

            ObjectNode response = mapper.createObjectNode();
            response.put("id", message.id);
            response.put("sender", message.sender);
            response.put("recipient", message.recipient);
            response.put("title", message.title);
            response.put("content", message.content);
            response.put("is_delivered", message.isDelivered);
            response.put("created_at", message.createdAt.toString());

            sendResponse(exchange, 200, mapper.writeValueAsString(response));
        } catch (NumberFormatException e) {
            ObjectNode response = mapper.createObjectNode();
            response.put("error", "ID inválido");
            response.put("message", String.format("'%s' não é um número válido", idParam));
            sendResponse(exchange, 400, mapper.writeValueAsString(response));
        }
    }
}