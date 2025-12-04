package com.ufrpe.poo.Api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendResponse(exchange, 405,
                            "{\"error\": \"Método não permitido\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500,
                    "{\"error\": \"Erro interno do servidor: " + e.getMessage() + "\"}");
        }
    }

    protected abstract void handleGet(HttpExchange exchange) throws IOException;
    protected abstract void handlePost(HttpExchange exchange) throws IOException;
    protected abstract void handlePut(HttpExchange exchange) throws IOException;
    protected abstract void handleDelete(HttpExchange exchange) throws IOException;

    protected void sendResponse(HttpExchange exchange, int statusCode, String response)
            throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
            os.flush();
        }
    }

    protected String getPathParameter(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");
        return segments.length > 0 ? segments[segments.length - 1] : "";
    }

    protected Map<String, String> getQueryParams(HttpExchange exchange) {
        Map<String, String> params = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();

        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                try {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        String key = URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = URLDecoder.decode(keyValue[1], "UTF-8");
                        params.put(key, value);
                    } else if (keyValue.length == 1) {
                        String key = URLDecoder.decode(keyValue[0], "UTF-8");
                        params.put(key, "");
                    }
                } catch (UnsupportedEncodingException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return params;
    }

    protected String[] getPathSegments(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return path.split("/");
    }

    protected String getQueryParam(HttpExchange exchange, String paramName) {
        Map<String, String> params = getQueryParams(exchange);
        return params.get(paramName);
    }
}