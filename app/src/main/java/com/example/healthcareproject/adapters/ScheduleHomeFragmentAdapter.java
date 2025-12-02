package com.example.healthcareproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.healthcareproject.R;
import com.example.healthcareproject.models.ScheduleItemHomeFrag;

import java.util.List;

public class ScheduleHomeFragmentAdapter extends RecyclerView.Adapter<ScheduleHomeFragmentAdapter.ScheduleViewHolder> {

    private List<ScheduleItemHomeFrag> scheduleList;

    public ScheduleHomeFragmentAdapter(List<ScheduleItemHomeFrag> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item_layout_homescreen, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleItemHomeFrag scheduleItem = scheduleList.get(position);
        holder.bind(scheduleItem);
    }

    @Override
    public int getItemCount() {
        return scheduleList != null ? scheduleList.size() : 0;
    }

    public void updateList(List<ScheduleItemHomeFrag> newList) {
        if (scheduleList != null) {
            scheduleList.clear();
            scheduleList.addAll(newList);
            notifyDataSetChanged();
        }
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivDoctorImage;
        private TextView tvHospitalName;
        private TextView tvDoctorName;
        private TextView tvSpecialty;
        private TextView tvDate;
        private TextView tvTime;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            ivDoctorImage = itemView.findViewById(R.id.ivDoctorImage);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);

            if (tvHospitalName == null) throw new RuntimeException("tvHospitalName not found in layout");
            if (tvDoctorName == null) throw new RuntimeException("tvDoctorName not found in layout");
            if (tvSpecialty == null) throw new RuntimeException("tvSpecialty not found in layout");
            if (tvDate == null) throw new RuntimeException("tvDate not found in layout");
            if (tvTime == null) throw new RuntimeException("tvTime not found in layout");
        }

        public void bind(ScheduleItemHomeFrag scheduleItem) {
            if (scheduleItem == null) return;

            if (tvHospitalName != null) {
                tvHospitalName.setText(scheduleItem.getHospitalName() != null ?
                        scheduleItem.getHospitalName() : "Unknown Hospital");
            }

            if (tvDoctorName != null) {
                tvDoctorName.setText(scheduleItem.getDoctorName() != null ?
                        scheduleItem.getDoctorName() : "Unknown Doctor");
            }

            if (tvSpecialty != null) {
                tvSpecialty.setText(scheduleItem.getSpecialty() != null ?
                        scheduleItem.getSpecialty() : "General");
            }

            if (tvDate != null) {
                tvDate.setText(scheduleItem.getDate() != null ?
                        scheduleItem.getDate() : "No date");
            }

            if (tvTime != null) {
                tvTime.setText(scheduleItem.getTime() != null ?
                        scheduleItem.getTime() : "No time");
            }

            if (ivDoctorImage != null) {
                if (scheduleItem.getDoctorImage() != null && !scheduleItem.getDoctorImage().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(scheduleItem.getDoctorImage())
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_doctor)
                                    .error(R.drawable.ic_doctor)
                                    .centerCrop())
                            .into(ivDoctorImage);
                } else {
                    ivDoctorImage.setImageResource(R.drawable.ic_doctor);
                }
            }
        }
    }
}