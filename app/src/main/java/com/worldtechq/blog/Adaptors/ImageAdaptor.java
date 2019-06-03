package com.worldtechq.blog.Adaptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.worldtechq.blog.Helper.Upload;
import com.worldtechq.blog.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdaptor extends RecyclerView.Adapter<ImageAdaptor.ImageViewHolder> {
    //to give the reference of current activity
    private Context mcontext;
    ///to create the list of uploaded files
    private List<Upload> mlist;

    private OnItemClickListeners mlisteners;

    //constructor for context reference
    public ImageAdaptor(Context context, List<Upload> uploads) {

        mcontext = context;
        mlist = uploads;


    }

    //creating the view for recycler view item by inflate the view layout file into recycler view.
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    //bind the data into viewholder
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.cardView.setAnimation(AnimationUtils.loadAnimation(mcontext, R.anim.faded_animation));
        //get the positions of images which is fetched from server
        Upload uploadcurrent = mlist.get(position);
        //fetching name of the image in text view(binding the fetched name into text view)
        holder.textViewname.setText(uploadcurrent.getMname());
        //fetching image in image view.(binding the fetched images into imageview)
        Glide.with(mcontext).load(uploadcurrent.getMurl()).into(holder.imageView1);

    }


    //to return the size of the list
    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView textViewname;
        public ImageView imageView1;
        CardView cardView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewname = itemView.findViewById(R.id.tv1);
            imageView1 = itemView.findViewById(R.id.imagev);
            cardView = itemView.findViewById(R.id.cardy);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {

            if (mlisteners != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mlisteners.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("select action");
            MenuItem dowhatever = menu.add(Menu.NONE, 1, 1, "Download");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            dowhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mlisteners != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            mlisteners.onWhatIWant(position);
                            return true;
                        case 2:
                            mlisteners.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListeners {

        void onItemClick(int position);

        void onWhatIWant(int position);

        void onDeleteClick(int position);
    }

    public void setonItemClickListener(OnItemClickListeners listeners) {
        mlisteners = listeners;
    }
}
