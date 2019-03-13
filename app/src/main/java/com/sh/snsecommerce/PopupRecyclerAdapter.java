package com.sh.snsecommerce;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PopupRecyclerAdapter extends RecyclerView.Adapter<PopupRecyclerAdapter.PopupRecyclerViewHolder> {
    PopupRecyclerViewHolder holder;
    ArrayList<Calendar> calendars;
    Context mContext;

    PopupRecyclerAdapter(ArrayList<Calendar> calendars, Context mContext) {
        this.calendars = calendars;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PopupRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_calendar, parent, false);

        return new PopupRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopupRecyclerViewHolder holder, final int position) {
        Log.e("Popup Recycler", (position + 1) + " : " + calendars.get(position).getContent());
        holder.tv_popup_num.setText(Integer.toString(position + 1));
        holder.tv_popup_content.setText(calendars.get(position).getContent());

        holder.btn_cal_remove.setOnClickListener(new View.OnClickListener() {       //일정 삭제 버튼
            @Override
            public void onClick(View v) {
                removeCalendar(calendars.get(position).getId());

                //RecyclerView Update
                calendars.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, calendars.size());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return calendars.size();
    }

    static class PopupRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tv_popup_num;
        TextView tv_popup_content;
        Button btn_cal_remove;

        public PopupRecyclerViewHolder(View itemView) {
            super(itemView);
            tv_popup_num = itemView.findViewById(R.id.tv_cal_num);
            tv_popup_content = itemView.findViewById(R.id.tv_cal_content);
            btn_cal_remove = itemView.findViewById(R.id.btn_cal_remove);
        }
    }

    public void removeCalendar(final String removeId) {                 //Firestore에서 일정 삭제
        FirebaseFirestore.getInstance().collection("calendars")
                .document(removeId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "일정을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "일정을 삭제하지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
