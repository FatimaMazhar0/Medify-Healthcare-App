package com.example.healthcareproject.network;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;
import com.example.healthcareproject.BuildConfig;

public class AIHealthService {

    // Groq API Configuration
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_API_KEY = BuildConfig.GROQ_API_KEY;

    // Available Groq LLaMA Models:
    // "llama-3.1-8b-instant" - Fast and efficient (Recommended)
    // "llama-3.1-70b-versatile" - More powerful, slightly slower
    // "llama3-8b-8192" - Good balance
    // "llama3-70b-8192" - Most capable
    private static final String GROQ_MODEL = "llama-3.1-8b-instant";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public AIHealthService() {
        // Configure OkHttpClient with appropriate timeouts for Groq (usually very fast)
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface AIResponseCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    /**
     * Main method to get health consultation from Groq's LLaMA model
     */
    public void getHealthConsultation(String userMessage, AIResponseCallback callback) {
        executorService.execute(() -> {
            try {
                String response = callGroqAPI(userMessage);
                mainHandler.post(() -> callback.onSuccess(response));
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                if (errorMessage == null) {
                    errorMessage = "Unknown error occurred";
                }
                final String finalError = errorMessage;
                mainHandler.post(() -> callback.onError(finalError));
            }
        });
    }

    /**
     * Calls Groq API with LLaMA model
     */
    private String callGroqAPI(String userMessage) throws IOException, JSONException {
        // Create comprehensive system prompt for health consultation
        String systemPrompt = "You are a knowledgeable, empathetic, and professional AI health assistant. " +
                "Your role is to:\n" +
                "1. Provide accurate, helpful health information in clear, easy-to-understand language\n" +
                "2. Be compassionate and supportive while discussing health concerns\n" +
                "3. Always clarify that you provide general information only and are NOT a replacement for professional medical advice\n" +
                "4. For serious symptoms or emergencies, strongly encourage consulting healthcare professionals immediately\n" +
                "5. Provide practical, actionable advice when appropriate\n" +
                "6. Be honest about limitations and uncertainties\n" +
                "7. Use a warm, professional tone that puts patients at ease\n\n" +
                "Remember: Patient safety is paramount. When in doubt, recommend professional consultation.";

        // Build request body for Groq API
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", GROQ_MODEL);
        requestBody.put("temperature", 0.7); // Balanced creativity and accuracy
        requestBody.put("max_tokens", 1024); // Adequate for detailed responses
        requestBody.put("top_p", 0.9); // Good diversity in responses
        requestBody.put("stream", false); // Get complete response at once

        // Build messages array
        JSONArray messages = new JSONArray();

        // Add system message
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.put(systemMessage);

        // Add user message
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.put(userMsg);

        requestBody.put("messages", messages);

        // Create HTTP request
        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(GROQ_API_URL)
                .addHeader("Authorization", "Bearer " + GROQ_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // Execute request and handle response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";

                // Parse error for better user feedback
                if (response.code() == 401) {
                    throw new IOException("Invalid API key. Please check your Groq API key.");
                } else if (response.code() == 429) {
                    throw new IOException("Rate limit exceeded. Please wait a moment and try again.");
                } else if (response.code() == 500) {
                    throw new IOException("Groq server error. Please try again in a moment.");
                } else {
                    throw new IOException("API Error (" + response.code() + "): " + errorBody);
                }
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            // Extract the AI's response text
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                String content = message.getString("content");

                // Validate response is not empty
                if (content == null || content.trim().isEmpty()) {
                    throw new IOException("Received empty response from AI");
                }

                return content.trim();
            }

            throw new IOException("No response generated from AI model");
        }
    }

    /**
     * Optional: Get consultation with conversation history for context
     * This maintains context across multiple messages
     */
    public void getHealthConsultationWithHistory(String userMessage,
                                                 JSONArray conversationHistory,
                                                 AIResponseCallback callback) {
        executorService.execute(() -> {
            try {
                String response = callGroqAPIWithHistory(userMessage, conversationHistory);
                mainHandler.post(() -> callback.onSuccess(response));
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                if (errorMessage == null) {
                    errorMessage = "Unknown error occurred";
                }
                final String finalError = errorMessage;
                mainHandler.post(() -> callback.onError(finalError));
            }
        });
    }

    private String callGroqAPIWithHistory(String userMessage, JSONArray conversationHistory)
            throws IOException, JSONException {

        String systemPrompt = "You are a knowledgeable, empathetic, and professional AI health assistant. " +
                "Provide helpful health information while being clear that you're not a replacement " +
                "for professional medical advice.";

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", GROQ_MODEL);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1024);
        requestBody.put("top_p", 0.9);

        JSONArray messages = new JSONArray();

        // Add system message
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.put(systemMessage);

        // Add conversation history
        if (conversationHistory != null && conversationHistory.length() > 0) {
            for (int i = 0; i < conversationHistory.length(); i++) {
                messages.put(conversationHistory.getJSONObject(i));
            }
        }

        // Add current user message
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.put(userMsg);

        requestBody.put("messages", messages);

        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(GROQ_API_URL)
                .addHeader("Authorization", "Bearer " + GROQ_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("API call failed (" + response.code() + "): " + errorBody);
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                return message.getString("content").trim();
            }

            throw new IOException("No response generated");
        }
    }

    /**
     * Cleanup method - call this when done with the service
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}