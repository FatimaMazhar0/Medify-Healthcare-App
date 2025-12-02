package com.example.healthcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcareproject.R;

import java.util.List;

public class DateDoctorDetailAdapter extends RecyclerView.Adapter<DateDoctorDetailAdapter.DateViewHolder> {

    private List<String> dates;
    private OnDateSelectedListener listener;
    private int selectedPosition = 0;

    public interface OnDateSelectedListener {
        void onDateSelected(String date, int position);
    }

    public DateDoctorDetailAdapter(List<String> dates, OnDateSelectedListener listener) {
        this.dates = dates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date_doctorpage, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        holder.bind(dates.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        TextView tvDay;
        TextView tvDate;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.dateContainer);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(String dateStr, int position) {
            String[] parts = dateStr.split(" ");
            if (parts.length >= 2) {
                tvDay.setText(parts[0]);
                tvDate.setText(parts[1]);
            }

            updateSelectionState(position == selectedPosition);

            container.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = position;

                notifyItemChanged(previousPosition);
                notifyItemChanged(position);

                if (listener != null) {
                    listener.onDateSelected(dateStr, position);
                }
            });
        }

        private void updateSelectionState(boolean isSelected) {
            if (isSelected) {
                container.setBackgroundResource(R.drawable.date_button_selected_background);
                tvDay.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));
                tvDate.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));
            } else {
                container.setBackgroundResource(R.drawable.date_button_background);
                tvDay.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
                tvDate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
            }
        }
    }
}