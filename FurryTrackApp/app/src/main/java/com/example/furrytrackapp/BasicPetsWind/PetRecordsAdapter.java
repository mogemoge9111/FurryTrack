package com.example.furrytrackapp.BasicPetsWind;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furrytrackapp.Model.Pet;
import com.example.furrytrackapp.Model.PetRecord;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Model.PetRecord;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PetRecordsAdapter extends RecyclerView.Adapter<PetRecordsAdapter.RecordViewHolder> {
    private List<PetRecord> records;
    private final OnRecordClickListener clickListener;

    public interface OnRecordClickListener {
        void onRecordClick(PetRecord record);
        void onRecordLongClick(PetRecord record);
    }

    public PetRecordsAdapter(List<PetRecord> records, OnRecordClickListener clickListener) {
        this.records = records;
        this.clickListener = clickListener;
    }

    public void updateRecords(List<PetRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        PetRecord record = records.get(position);
        holder.bind(record);

        holder.itemView.setOnClickListener(v -> clickListener.onRecordClick(record));
        holder.itemView.setOnLongClickListener(v -> {
            clickListener.onRecordLongClick(record);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView dateTextView;
        private final TextView categoryTextView;
        private final ImageView recordImageView;

        @SuppressLint("WrongViewCast")
        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.record_title_edit_text);
            dateTextView = itemView.findViewById(R.id.record_date_text_view);
            categoryTextView = itemView.findViewById(R.id.category_spinner);
            recordImageView = itemView.findViewById(R.id.record_image_view);
        }

        public void bind(PetRecord record) {
            titleTextView.setText(record.getTitle());

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            dateTextView.setText(sdf.format(record.getDate()));

            categoryTextView.setText(getCategoryName(record.getCategory()));
            setCategoryColor(record.getCategory());

            if (record.getImageBase64() != null && !record.getImageBase64().isEmpty()) {
                Bitmap image = Pet.base64ToBitmap(record.getImageBase64());
                recordImageView.setImageBitmap(image);
                recordImageView.setVisibility(View.VISIBLE);
            } else {
                recordImageView.setVisibility(View.GONE);
            }
        }

        private String getCategoryName(String category) {
            switch (category) {
                case Pet.CATEGORY_MOOD: return "Настроение";
                case Pet.CATEGORY_ACTIVITY: return "Активность";
                case Pet.CATEGORY_SLEEP: return "Сон";
                case Pet.CATEGORY_FOOD: return "Питание";
                case Pet.CATEGORY_GROOMING: return "Груминг";
                case Pet.CATEGORY_MEDICAL: return "Мед. уход";
                case Pet.CATEGORY_MEASUREMENTS: return "Измерения";
                case Pet.CATEGORY_VACCINATIONS: return "Прививки";
                case Pet.CATEGORY_PHOTOS: return "Фото";
                default: return "Другое";
            }
        }

        private void setCategoryColor(String category) {
            int color;
            switch (category) {
                case Pet.CATEGORY_MOOD: color = Color.parseColor("#FF9800"); break;
                case Pet.CATEGORY_ACTIVITY: color = Color.parseColor("#4CAF50"); break;
                case Pet.CATEGORY_SLEEP: color = Color.parseColor("#2196F3"); break;
                case Pet.CATEGORY_FOOD: color = Color.parseColor("#F44336"); break;
                case Pet.CATEGORY_GROOMING: color = Color.parseColor("#9C27B0"); break;
                case Pet.CATEGORY_MEDICAL: color = Color.parseColor("#607D8B"); break;
                case Pet.CATEGORY_MEASUREMENTS: color = Color.parseColor("#795548"); break;
                case Pet.CATEGORY_VACCINATIONS: color = Color.parseColor("#009688"); break;
                case Pet.CATEGORY_PHOTOS: color = Color.parseColor("#E91E63"); break;
                default: color = Color.parseColor("#9E9E9E");
            }
            categoryTextView.setBackgroundColor(color);
        }
    }
}