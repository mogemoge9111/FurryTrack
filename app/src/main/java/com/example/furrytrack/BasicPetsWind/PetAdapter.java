package com.example.furrytrack.BasicPetsWind;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furrytrack.R;

import java.util.ArrayList;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private List<Pet> pets;
    private OnPetClickListener listener;

    public PetAdapter(List<Pet> pets, OnPetClickListener listener) {
        this.pets = pets != null ? pets : new ArrayList<>(); // Защита от null
        this.listener = listener;
    }

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = pets.get(position);
        holder.petNameTextView.setText(pet.getName());
        holder.petTypeTextView.setText(pet.getType());

        switch (pet.getType()) {
            case "Кошка":
                holder.petIconImageView.setImageResource(R.drawable.ic_cat);
                break;
            case "Собака":
                holder.petIconImageView.setImageResource(R.drawable.ic_dog);
                break;
            case "Бегемот":
                holder.petIconImageView.setImageResource(R.drawable.ic_hippo);
                break;
            default:
                holder.petIconImageView.setImageResource(R.drawable.ic_pet);
        }

        holder.itemView.setOnClickListener(v -> listener.onPetClick(pet));
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView petIconImageView;
        TextView petNameTextView;
        TextView petTypeTextView;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petIconImageView = itemView.findViewById(R.id.pet_icon_image_view);
            petNameTextView = itemView.findViewById(R.id.pet_name_text_view);
            petTypeTextView = itemView.findViewById(R.id.pet_type_text_view);
        }
    }
}