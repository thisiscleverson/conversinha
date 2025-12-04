package com.ufrpe.poo.Api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HtmlHandler implements HttpHandler {

    private static final String TEMPLATES_DIR = "site/templates";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        String htmlFile = getHtmlFileForPath(path);

        if (htmlFile != null) {
            serveHtmlFile(exchange, htmlFile);
        } else {
            send404(exchange);
        }
    }

    private String getHtmlFileForPath(String path) {
        switch (path) {
            case "/":
            case "/home":
                return "home.html";
            case "/login":
                return "login.html";
            case "/register":
                return "register.html";
            case "/send":
                return "send.html";
            case "/sent":
                return "sent.html";
            case "/view":
                return "view.html";
            default:
                if (path.endsWith(".html") && path.startsWith("/")) {
                    return path.substring(1); // Remove a barra inicial
                }
                return null;
        }
    }


    private void serveHtmlFile(HttpExchange exchange, String filename) throws IOException {
        try {
            Path filePath = Paths.get(TEMPLATES_DIR, filename);

            System.out.println("file path: " + filePath);

            if (!Files.exists(filePath)) {
                send404(exchange);
                return;
            }

            String htmlContent = Files.readString(filePath);

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, htmlContent.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(htmlContent.getBytes());
            }

            System.out.println("Servido: " + filename);

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo HTML: " + e.getMessage());
            send500(exchange);
        }
    }

    private void send404(HttpExchange exchange) throws IOException {
        String html404 = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>404 - Página não encontrada</title>
                <style>
                    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
                    h1 { color: #e74c3c; }
                    a { color: #3498db; text-decoration: none; }
                </style>
            </head>
            <body>
                <h1>404 - Página não encontrada</h1>
                <p>A página que você está procurando não existe.</p>
                <p><a href="/">Voltar para a página inicial</a></p>
            </body>
            </html>
            """;

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(404, html404.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(html404.getBytes());
        }
    }

    private void send500(HttpExchange exchange) throws IOException {
        String html500 = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>500 - Erro interno</title>
                <style>
                    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
                    h1 { color: #e74c3c; }
                </style>
            </head>
            <body>
                <h1>500 - Erro interno do servidor</h1>
                <p>Algo deu errado. Tente novamente mais tarde.</p>
            </body>
            </html>
            """;

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(500, html500.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(html500.getBytes());
        }
    }

    public static String renderTemplate(String html, java.util.Map<String, String> variables) {
        String result = html;
        for (java.util.Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, entry.getValue());
        }
        return result;
    }
}