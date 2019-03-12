package com.sh.snsecommerce;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    RecyclerViewHolder holder;
    ArrayList<Comment> comments;
    Context mContext;

    RecyclerAdapter(ArrayList<Comment> comments, Context mContext) {
        this.comments = comments;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        this.holder = holder;

        holder.iv_image.setImageResource(R.drawable.img_mino);
        /*if(comments.get(position).getImageUrl() == null) {
            holder.iv_image.setImageResource(R.drawable.img_mino);
        }
        else {
            LoadImageAsyncTask loadImageAsyncTask = new LoadImageAsyncTask();
            loadImageAsyncTask.execute(comments.get(position).getImageUrl());
        }*/
        /*if(comments.get(position).getBitmap() == null) {
            holder.iv_image.setImageResource(R.drawable.img_mino);
        }
        else {
            holder.iv_image.setImageBitmap(comments.get(position).getBitmap());
        }*/

        holder.tv_name.setText(comments.get(position).getName());
        holder.tv_comment.setText(comments.get(position).getComment());
        holder.tv_date.setText(comments.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_name;
        TextView tv_comment;
        TextView tv_date;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }

    /*private class LoadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {          //HTTP로 이미지 불러오기
        Bitmap bitmap;
        @Override
        protected Bitmap doInBackground(String... imgURLs) {
            try {
                URL imgUrl = new URL(imgURLs[0]);
                HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream inputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap bm) {
            holder.iv_image.setImageBitmap(bitmap);
        }
    }*/
}
