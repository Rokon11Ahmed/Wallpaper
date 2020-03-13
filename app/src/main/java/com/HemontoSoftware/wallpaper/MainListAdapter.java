package com.HemontoSoftware.wallpaper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import static com.HemontoSoftware.wallpaper.MyUtils.counter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MainViewHolder> {
    private Context context;
    private ArrayList<String> urls;
    private ArrayList<String> name;

    public MainListAdapter(Context context, ArrayList<String> urls, ArrayList<String> name ) {
        this.context =context;
        this.urls = urls;
        this.name = name;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, final int position) {
        holder.setData(urls.get(position));
        holder.name.setText(name.get(position));
        holder.clicklayoutID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url =urls.get(position);
                counter++;
                Intent intent = new Intent(context, PreviewActivity.class);
                intent.putExtra("image_url", url);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        private ConstraintLayout clicklayoutID;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_list);
            name = itemView.findViewById(R.id.textView2);
            clicklayoutID = itemView.findViewById(R.id.constraintLayoutID);
        }

        public void setData(String url){
            Picasso.get().load(url).fit().centerCrop().into(imageView);
        }


    }


}

