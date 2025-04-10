package com.example.furrytrackapp.Posts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import com.example.furrytrackapp.Model.Post;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Utils.ApiClient;
import com.example.furrytrackapp.Utils.ApiService;
import com.example.furrytrackapp.Utils.PostRequest;
import com.example.furrytrackapp.Utils.PostResponse;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_READ_STORAGE = 101;

    private EditText postTitle, postDescription, postHashtags;
    private ImageView postImage;
    private Button btnAddImage, btnPublish;
    private Bitmap selectedImageBitmap;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        apiService = ApiClient.getClient().create(ApiService.class);

        postTitle = findViewById(R.id.post_title_input);
        postDescription = findViewById(R.id.post_description_input);
        postHashtags = findViewById(R.id.post_hashtags_input);
        postImage = findViewById(R.id.post_image_preview);
        btnAddImage = findViewById(R.id.btn_add_image);
        btnPublish = findViewById(R.id.btn_publish);

        btnAddImage.setOnClickListener(v -> checkPermissionsAndOpenImageChooser());
        btnPublish.setOnClickListener(v -> publishPost());
    }

    private void checkPermissionsAndOpenImageChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        } else {
            openFileChooser();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImageBitmap = BitmapFactory.decodeStream(imageStream);
                postImage.setImageBitmap(selectedImageBitmap);
                postImage.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void publishPost() {
        String title = postTitle.getText().toString().trim();
        String description = postDescription.getText().toString().trim();
        String hashtags = postHashtags.getText().toString().trim();
        String imageBase64 = null;

        if (selectedImageBitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description) && imageBase64 == null) {
            Toast.makeText(this, "Добавьте хотя бы текст или изображение", Toast.LENGTH_SHORT).show();
            return;
        }

        savePostToServer(title, description, hashtags, imageBase64);
    }

    private void savePostToServer(String title, String description, String hashtags, String imageBase64) {
        String token = Paper.book().read("token", "");

        PostRequest postRequest = new PostRequest();
        postRequest.setTitle(title);
        postRequest.setDescription(description);
        postRequest.setHashtags(hashtags);
        postRequest.setImage(imageBase64);

        Call<PostResponse> call = apiService.createPost("Bearer " + token, postRequest);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddPostActivity.this, "Пост опубликован", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddPostActivity.this, "Ошибка: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(AddPostActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Нужно разрешение для выбора изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }
}