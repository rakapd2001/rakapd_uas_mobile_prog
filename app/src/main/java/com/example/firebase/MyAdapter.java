package com.example.firebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemViewHolder> {
    private List<ItemList> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ItemList item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MyAdapter(List<ItemList> itemList) {
        this.itemList = itemList;
    }

    public void updateData(List<ItemList> newItemList) {
        this.itemList = newItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemList item = itemList.get(position);
        holder.judul.setText(item.getJudul());
        holder.kategori.setText(item.getKategori());
        holder.keterangan.setText(item.getKeterangan());
        // Mengatur ukuran gambar lebih kecil dengan Glide
        Glide.with(holder.imageView.getContext())
                .load(item.getImageUrl())
                .override(100, 100) // ukuran gambar lebih kecil
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView judul;
        TextView kategori;
        TextView keterangan;
        ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.textViewJudul);
            kategori = itemView.findViewById(R.id.textViewKategori);
            keterangan = itemView.findViewById(R.id.textViewKeterangan);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
