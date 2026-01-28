package com.industrial.iot_platform.simulation;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IotSimulator {
    private static final String BASE_URL = "http://localhost:8080";
    private static final int DEVICE_COUNT = 10;       // How many machines?
    private static final int MESSAGES_PER_DEVICE = 20; // How many messages each sends?

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("STARTING INDUSTRIAL IOT SIMULATION");
        System.out.println("-----------------------------------");

        ExecutorService executor = Executors.newFixedThreadPool(DEVICE_COUNT);

        for (int i = 1; i <= DEVICE_COUNT; i++) {
            int deviceId = i;
            executor.submit(() -> simulateDevice("Simulated-Machine-" + deviceId));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
        System.out.println("SIMULATION COMPLETE");
    }

    private static void simulateDevice(String deviceName) {
        try {
            // 1. Register
            String registerJson = String.format(Locale.US, """
                {
                    "name": "%s",
                    "type": "SIMULATOR",
                    "firmwareVersion": "v1.0"
                }
                """, deviceName);

            String authResponse = post("/auth/register", registerJson, null);
            JsonNode authNode = mapper.readTree(authResponse);
            String id = authNode.get("deviceId").asText();
            String apiSecret = authNode.get("apiSecret").asText();

            System.out.println("Registered: " + deviceName);

            // 2. LOGIN
            String loginJson = String.format(Locale.US, """
                {
                    "deviceId": "%s",
                    "apiSecret": "%s"
                }
                """, id, apiSecret);

            String tokenResponse = post("/auth/login", loginJson, null);
            String token = mapper.readTree(tokenResponse).get("token").asText();

            // 3. SEND TELEMETRY LOOP
            for (int j = 0; j < MESSAGES_PER_DEVICE; j++) {
                // Randomize data: 90% normal, 10% critical (overheating)
                double temp = 60.0 + (random.nextDouble() * 50.0);
                if (random.nextDouble() > 0.9) temp += 50.0; // Critical spike

                String telemetryJson = String.format(Locale.US, """
                    {
                        "sensorType": "TEMPERATURE",
                        "value": %.2f,
                        "unit": "CELSIUS"
                    }
                    """, temp);

                post("/telemetry", telemetryJson, token);

                // Sleep randomly between 0.5s and 2s
                Thread.sleep(500 + random.nextInt(1500));
            }
            System.out.println("🏁 " + deviceName + " finished sending data.");
        } catch (Exception e) {
            System.err.println("Error in " + deviceName + ": " + e.getMessage());
        }
    }

    // Helper method to send HTTP Requests
    private static String post(String endpoint, String json, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("HTTP Error " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }
}