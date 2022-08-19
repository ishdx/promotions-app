package com.example.project483;


import android.content.Context;
import android.database.Cursor;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project483.modals.CampinModal;
import com.example.project483.service.GPSTracker;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ItemViewHolder> {
    private DatabaseHelper DBhelper;
    private ArrayList<CampinModal> camps;
    private Context context;

    public Adapter(Context context, ArrayList<CampinModal> camps , DatabaseHelper DBhelper){
        this.camps = camps;
        this.DBhelper = DBhelper;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.campaign_subscript,
                parent,
                false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        CampinModal campinModal=camps.get(position);

        boolean status=false;

        Cursor cursorOutput = DBhelper.getSubscribe();
        if(cursorOutput != null && cursorOutput.getCount() !=0) {
            cursorOutput.moveToFirst();


            do {

                if (campinModal.getId()==cursorOutput.getInt(1) && GPSTracker.currentUserId==cursorOutput.getInt(0)) {
                    status=true;
                    break;
                }

            } while (cursorOutput.moveToNext());
        }

        if (status) {
            holder.getButton().setBackgroundColor(Color.BLACK);
        }else {
            holder.getButton().setBackgroundColor(Color.parseColor("#FFCFCF"));
        }


        holder.getTextView().setText(campinModal.getTitle());
        holder.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBhelper.addSubscript(GPSTracker.currentUserId, campinModal.getId());
                holder.getButton().setBackgroundColor(Color.BLACK);
            }
        });
    };

    @Override
    public int getItemCount() {
        return camps.size(); }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView campaignTitleTv;
        private final Button subscripeButton;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            campaignTitleTv = (TextView) itemView.findViewById(R.id.camp_title);
            subscripeButton = (Button) itemView.findViewById(R.id.subscripe_button);

        }
        public TextView getTextView(){
            return campaignTitleTv;
        }
        public Button getButton(){
            return subscripeButton;
        }

    }

}
