package com.example.healthcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;

import java.util.List;

public class TimeSlotDoctorDetailAdapter extends RecyclerView.Adapter<TimeSlotDoctorDetailAdapter.TimeSlotViewHolder> {

    private List<String> timeSlots;
    private OnTimeSlotSelectedListener listener;
    private int selectedPosition = -1;

    public interface OnTimeSlotSelectedListener {
        void onTimeSlotSelected(String timeSlot, int position);
    }

    public TimeSlotDoctorDetailAdapter(List<String> timeSlots, OnTimeSlotSelectedListener listener) {
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot_doctorpage, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        holder.bind(timeSlots.get(position), position);
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public void clearSelection() {
        int previousPosition = selectedPosition;
        selectedPosition = -1;
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
    }

    class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeSlot;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeSlot = itemView.findViewById(R.id.tvTimeSlot);
        }

        public void bind(String timeSlot, int position) {
            tvTimeSlot.setText(timeSlot);

            updateSelectionState(position == selectedPosition);

            tvTimeSlot.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = position;

                if (previousPosition != -1) {
                    notifyItemChanged(previousPosition);
                }
                notifyItemChanged(position);

                if (listener != null) {
                    listener.onTimeSlotSelected(timeSlot, position);
                }
            });
        }

        private void updateSelectionState(boolean isSelected) {
            if (isSelected) {
                tvTimeSlot.setBackgroundResource(R.drawable.button_primary_background);
                tvTimeSlot.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));
            } else {
                tvTimeSlot.setBackgroundResource(R.drawable.time_slot_background);
                tvTimeSlot.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
            }
        }
    }
}