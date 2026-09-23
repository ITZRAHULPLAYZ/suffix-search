import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Server {

    private static SuffixArray currentSa = null;
    private static SearchEngine currentEngine = null;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new StaticHandler());
        server.createContext("/api/search", new SearchHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String path = t.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }
            try {
                byte[] response = Files.readAllBytes(Paths.get("web" + path));
                t.sendResponseHeaders(200, response.length);
                OutputStream os = t.getResponseBody();
                os.write(response);
                os.close();
            } catch (Exception e) {
                String response = "404 Not Found";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                String body = new java.io.BufferedReader(new java.io.InputStreamReader(t.getRequestBody()))
                        .lines().collect(Collectors.joining("\n"));
                
                Map<String, String> params = parseJson(body);
                String text = params.get("text");
                String pattern = params.get("pattern");

                if (text == null || pattern == null || text.isEmpty() || pattern.isEmpty()) {
                    sendJsonResponse(t, 400, "{\"error\": \"Text and pattern are required\"}");
                    return;
                }

                if (currentSa == null || !currentSa.getText().equals(text)) {
                    currentSa = new SuffixArray(text);
                    currentEngine = new SearchEngine(currentSa);
                }

                SearchResult result = currentEngine.search(pattern);
                
                StringBuilder positionsJson = new StringBuilder("[");
                for (int i = 0; i < result.getPositions().size(); i++) {
                    positionsJson.append(result.getPositions().get(i));
                    if (i < result.getPositions().size() - 1) positionsJson.append(",");
                }
                positionsJson.append("]");

                String jsonResponse = String.format(
                        "{\"pattern\": \"%s\", \"count\": %d, \"time\": \"%s\", \"positions\": %s}",
                        escapeJson(result.getPattern()),
                        result.getCount(),
                        result.getElapsedFormatted(),
                        positionsJson.toString()
                );

                sendJsonResponse(t, 200, jsonResponse);
            } else {
                sendJsonResponse(t, 405, "{\"error\": \"Method not allowed\"}");
            }
        }

        private void sendJsonResponse(HttpExchange t, int statusCode, String response) throws IOException {
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private Map<String, String> parseJson(String json) {
            Map<String, String> map = new HashMap<>();
            json = json.trim();
            if (json.startsWith("{") && json.endsWith("}")) {
                json = json.substring(1, json.length() - 1);
                String[] pairs = json.split("\",\"");
                for (String pair : pairs) {
                    String[] kv = pair.split("\":\"");
                    if (kv.length == 2) {
                        String key = kv[0].replaceAll("\"", "").trim();
                        String value = kv[1].replaceAll("\"", "").trim();
                        value = value.replace("\\n", "\n").replace("\\r", "");
                        map.put(key, value);
                    }
                }
            }
            return map;
        }

        private String escapeJson(String s) {
            return s.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}
