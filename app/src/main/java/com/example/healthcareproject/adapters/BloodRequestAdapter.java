package com.example.healthcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;
import com.example.healthcareproject.models.BloodRequest;
import com.example.healthcareproject.utils.SessionManager;

import java.util.List;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.RequestViewHolder> {

    private List<BloodRequest> requestList;
    private OnRequestActionListener actionListener;
    private SessionManager sessionManager;

    public interface OnRequestActionListener {
        void onApprove(BloodRequest request);
        void onReject(BloodRequest request);
    }

    public BloodRequestAdapter(List<BloodRequest> requestList, OnRequestActionListener listener) {
        this.requestList = requestList;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_request, parent, false);
        sessionManager = new SessionManager(parent.getContext());
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        BloodRequest request = requestList.get(position);
        String currentUserId = sessionManager.getUserId();

        if (request.getPatientId().equals(currentUserId)) {
            holder.tvRequestInfo.setText("Request to " + request.getDonorName());
        } else {
            holder.tvRequestInfo.setText("Request from " + request.getPatientName());
        }

        holder.tvBloodGroupRequest.setText("Blood Group: " + request.getBloodGroup());
        holder.tvHospital.setText("Hospital: " + request.getHospitalName());
        holder.tvUrgency.setText("Urgency: " + request.getUrgency());

        if (request.getMessage() != null && !request.getMessage().isEmpty()) {
            holder.tvMessage.setText("Message: " + request.getMessage());
            holder.tvMessage.setVisibility(View.VISIBLE);
        } else {
            holder.tvMessage.setVisibility(View.GONE);
        }

        holder.tvRequestStatus.setText(request.getStatus().toUpperCase());
        switch (request.getStatus().toLowerCase()) {
            case "pending":
                holder.tvRequestStatus.setBackgroundResource(R.drawable.status_pending_bg);
                break;
            case "approved":
                holder.tvRequestStatus.setBackgroundResource(R.drawable.status_approved_bg);
                break;
            case "rejected":
                holder.tvRequestStatus.setBackgroundResource(R.drawable.status_rejected_bg);
                break;
            case "completed":
                holder.tvRequestStatus.setBackgroundResource(R.drawable.status_completed_bg);
                break;
        }

        if (request.getStatus().equals("pending") && request.getDonorId().equals(currentUserId)) {
            holder.layoutActionButtons.setVisibility(View.VISIBLE);
            holder.btnApprove.setOnClickListener(v -> actionListener.onApprove(request));
            holder.btnReject.setOnClickListener(v -> actionListener.onReject(request));
        } else {
            holder.layoutActionButtons.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvRequestInfo, tvRequestStatus, tvBloodGroupRequest, tvHospital, tvUrgency, tvMessage;
        LinearLayout layoutActionButtons;
        Button btnApprove, btnReject;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRequestInfo = itemView.findViewById(R.id.tvRequestInfo);
            tvRequestStatus = itemView.findViewById(R.id.tvRequestStatus);
            tvBloodGroupRequest = itemView.findViewById(R.id.tvBloodGroupRequest);
            tvHospital = itemView.findViewById(R.id.tvHospital);
            tvUrgency = itemView.findViewById(R.id.tvUrgency);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            layoutActionButtons = itemView.findViewById(R.id.layoutActionButtons);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}