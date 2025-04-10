package com.example.furrytrackapp.Posts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furrytrackapp.BasicPetsWind.PetActivity;
import com.example.furrytrackapp.Market.MarketActivity;
import com.example.furrytrackapp.Model.Post;
import com.example.furrytrackapp.Profile.ProfileUserActivity;
import com.example.furrytrackapp.R;
import com.example.furrytrackapp.Utils.ApiClient;
import com.example.furrytrackapp.Utils.ApiService;
import com.example.furrytrackapp.Welcome.SettingsActivity;
import com.example.furrytrackapp.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostsAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        apiService = ApiClient.getClient().create(ApiService.class);

        Toolbar toolbar = findViewById(R.id.toolbar_posts);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.posts_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostsAdapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddPost = findViewById(R.id.fab_add_post);
        fabAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(PostsActivity.this, AddPostActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_posts);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadPosts();
    }

    private void loadPosts() {
        String token = "Bearer " + Paper.book().read("token", "");

        Call<List<Post>> call = apiService.getFeed();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setPosts(response.body());
                } else {
                    Toast.makeText(PostsActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(PostsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {
        private List<Post> posts;

        public void setPosts(List<Post> posts) {
            this.posts = posts;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_item_layout, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Post post = posts.get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            return posts != null ? posts.size() : 0;
        }
    }

    private static class PostViewHolder extends RecyclerView.ViewHolder {

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Post post) {

        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_posts) {
                        return true;
                    } else if (itemId == R.id.nav_market) {
                        startActivity(new Intent(PostsActivity.this, MarketActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.nav_pets) {
                        startActivity(new Intent(PostsActivity.this, PetActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.nav_profile) {
                        startActivity(new Intent(PostsActivity.this, ProfileUserActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    return false;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutConfirmation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmation() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да", (dialog, which) -> {
                    Paper.book().destroy();
                    getSharedPreferences("UserData", MODE_PRIVATE).edit().clear().apply();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showAboutDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("О программе")
                .setMessage("НИУ ВШЭ Курсовая работа\n\nВыполнили: Замотаева Анастасия Владимировна, ФКН БПИ239\n\nЩуплова Анна Игоревна, ФКН БПИ239")
                .setPositiveButton("OK", null)
                .show();
    }
}