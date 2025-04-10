package com.example.furrytrackapp.Model;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PetRecord {
    private String id;
    private String category;
    private String title;
    private String description;
    private String imageBase64; // Для фото в записях
    private Date date;
    private Map<String, String> measurements; // Для категории измерений

    public PetRecord(String id, String category, String title,
                     String description, String imageBase64) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.imageBase64 = imageBase64;
        this.date = new Date();
        this.measurements = new HashMap<>();
    }

    public PetRecord() {

    }

    public static Bitmap base64ToBitmap(String imageBase64) {
        return null;
    }

    public static String bitmapToBase64(Bitmap recordImageBitmap) {
        return "";
    }

    // Добавьте эти методы в класс PetRecord:

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("category", category);
        map.put("title", title);
        map.put("description", description);
        map.put("imageBase64", imageBase64);
        map.put("date", date);
        map.put("measurements", measurements);
        return map;
    }

    public static PetRecord fromMap(Map<String, Object> map) {
        PetRecord record = new PetRecord(
                (String) map.get("id"),
                (String) map.get("category"),
                (String) map.get("title"),
                (String) map.get("description"),
                (String) map.get("imageBase64")
        );
        record.setDate((Date) map.get("date"));
        if (map.get("measurements") != null) {
            record.setMeasurements((Map<String, String>) map.get("measurements"));
        }
        return record;
    }

    // Добавление измерения (вес, рост и т.д.)
    public void addMeasurement(String key, String value) {
        measurements.put(key, value);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, String> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Map<String, String> measurements) {
        this.measurements = measurements;
    }

    public void setPetId(String petId) {
    }
}