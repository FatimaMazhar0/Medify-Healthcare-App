package com.example.healthcareproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.adapters.ChatAdapter;
import com.example.healthcareproject.models.ChatMessage;
import com.example.healthcareproject.network.AIHealthService;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private EditText messageInput;
    private ImageButton sendButton;
    private RecyclerView chatRecyclerView;
    private ProgressBar loadingIndicator;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private AIHealthService aiHealthService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupAIService();
        addWelcomeMessage();
        setupSendButton();
    }

    private void initViews(View view) {
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
    }

    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupAIService() {
        aiHealthService = new AIHealthService();
    }

    private void addWelcomeMessage() {
        ChatMessage welcomeMessage = new ChatMessage(
                "Hello! I'm your AI Health Assistant. I'm here to help answer your health-related questions and provide general health information. How can I assist you today?\n\n" +
                        "Note: I provide general information only and am not a replacement for professional medical advice.",
                false,
                System.currentTimeMillis()
        );
        chatMessages.add(welcomeMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(v -> sendMessage());

        messageInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();

        if (message.isEmpty()) {
            return;
        }

        ChatMessage userMessage = new ChatMessage(message, true, System.currentTimeMillis());
        chatMessages.add(userMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);

        messageInput.setText("");

        showLoading(true);

        aiHealthService.getHealthConsultation(message, new AIHealthService.AIResponseCallback() {
            @Override
            public void onSuccess(String response) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);

                        ChatMessage aiMessage = new ChatMessage(response, false, System.currentTimeMillis());
                        chatMessages.add(aiMessage);
                        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                        chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();

                        ChatMessage errorMessage = new ChatMessage(
                                "I apologize, but I'm having trouble connecting right now. Please try again in a moment.",
                                false,
                                System.currentTimeMillis()
                        );
                        chatMessages.add(errorMessage);
                        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    });
                }
            }
        });
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (sendButton != null) {
            sendButton.setEnabled(!show);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}