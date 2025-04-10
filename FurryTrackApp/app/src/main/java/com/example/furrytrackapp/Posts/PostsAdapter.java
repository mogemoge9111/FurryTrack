package com.example.furrytrackapp.Posts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furrytrackapp.Model.Post;
import com.example.furrytrackapp.R;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> posts;

    public PostsAdapter(List<Post> posts) {
        this.posts = posts;
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts = newPosts;
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

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postDescription, postHashtags;
        ImageView postImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.post_title);
            postDescription = itemView.findViewById(R.id.post_description);
            postHashtags = itemView.findViewById(R.id.post_hashtags);
            postImage = itemView.findViewById(R.id.post_image);
        }

        public void bind(Post post) {
            postTitle.setText(post.getTitle());
            postDescription.setText(post.getDescription());
            postHashtags.setText(post.getHashtags());

            if (post.getImageBase64() != null && !post.getImageBase64().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(post.getImageBase64(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    postImage.setImageBitmap(decodedByte);
                    postImage.setVisibility(View.VISIBLE);
                } catch (IllegalArgumentException e) {
                    postImage.setVisibility(View.GONE);
                }
            } else {
                postImage.setVisibility(View.GONE);
            }
        }
    }
}