package com.example.furrytrackapp.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pet implements Serializable {
    private String id;
    private String name;
    private String type;
    private String gender;
    private Date birthDate;
    private Date adoptionDate;
    private String importantInfo;
    private String imageBase64; // Хранение фото в Base64
    private Map<String, PetRecord> records;

    // Категории записей
    public static final String CATEGORY_MOOD = "mood";
    public static final String CATEGORY_ACTIVITY = "activity";
    public static final String CATEGORY_SLEEP = "sleep";
    public static final String CATEGORY_FOOD = "food";
    public static final String CATEGORY_GROOMING = "grooming";
    public static final String CATEGORY_MEDICAL = "medical";
    public static final String CATEGORY_MEASUREMENTS = "measurements";
    public static final String CATEGORY_VACCINATIONS = "vaccinations";
    public static final String CATEGORY_PHOTOS = "photos";
    public static final String CATEGORY_OTHER = "other";

    public Pet() {
        this.id = UUID.randomUUID().toString();
    }

    // Конструктор и геттеры/сеттеры
    public Pet(String id, String name, String type, String gender,
               Date birthDate, Date adoptionDate, String importantInfo,
               String imageBase64) {
        this();
        this.id = id;
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.birthDate = birthDate;
        this.adoptionDate = adoptionDate;
        this.importantInfo = importantInfo;
        this.imageBase64 = imageBase64;
    }

    public Pet(Object o, String name, String type, String gender, Date time, Date time1, String importantInfo, String petImageBase64, String id) {
    }

    // Метод для конвертации Bitmap в Base64
    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Метод для конвертации Base64 в Bitmap
    public static Bitmap base64ToBitmap(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) return null;
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    // Добавление записи о питомце
    public void addRecord(String category, PetRecord record) {
        if (records == null) {
            records = new HashMap<>();
        }
        records.put(record.getId(), record);
    }

    // Добавьте эти методы в класс Pet:

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("type", type);
        map.put("gender", gender);
        map.put("birthDate", birthDate);
        map.put("adoptionDate", adoptionDate);
        map.put("importantInfo", importantInfo);
        map.put("imageBase64", imageBase64);

        if (records != null) {
            Map<String, Object> recordsMap = new HashMap<>();
            for (Map.Entry<String, PetRecord> entry : records.entrySet()) {
                recordsMap.put(entry.getKey(), entry.getValue().toMap());
            }
            map.put("records", recordsMap);
        }

        return map;
    }

    public static Pet fromMap(Map<String, Object> map) {
        Pet pet = new Pet();
        pet.setId((String) map.get("id"));
        pet.setName((String) map.get("name"));
        pet.setType((String) map.get("type"));
        pet.setGender((String) map.get("gender"));
        pet.setBirthDate((Date) map.get("birthDate"));
        pet.setAdoptionDate((Date) map.get("adoptionDate"));
        pet.setImportantInfo((String) map.get("importantInfo"));
        pet.setImageBase64((String) map.get("imageBase64"));
        return pet;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getAdoptionDate() {
        return adoptionDate;
    }

    public void setAdoptionDate(Date adoptionDate) {
        this.adoptionDate = adoptionDate;
    }

    public String getImportantInfo() {
        return importantInfo;
    }

    public void setImportantInfo(String importantInfo) {
        this.importantInfo = importantInfo;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Map<String, PetRecord> getRecords() {
        return records;
    }

    public void setRecords(Map<String, PetRecord> records) {
        this.records = records;
    }

    public Object getBreed() {
        return null;
    }

    public void setBreed(String breed) {
    }

    public void setPrice(String price) {
    }

    public void setDescription(String description) {
    }

    public void setImage(String encodedImage) {
    }

    public int getInfo() {
        return 0;
    }

    public String getPrice() {
        return "";
    }

    public String getImage() {
        return "";
    }
}