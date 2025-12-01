package com.example.mp_lab_9.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mp_lab_9.data.model.ShoppingList;
import com.example.mp_lab_9.R;
import com.example.mp_lab_9.util.DateUtils;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    public interface OnListClickListener {
        void onListClick(ShoppingList shoppingList);
        void onListLongClick(ShoppingList shoppingList);
    }

    private final List<ShoppingList> shoppingLists;
    private final OnListClickListener listener;

    public ShoppingListAdapter(List<ShoppingList> shoppingLists, OnListClickListener listener) {
        this.shoppingLists = shoppingLists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(shoppingLists.get(position));
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull ViewGroup parent, OnListClickListener listener) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shopping_list, parent, false));

            itemView.setOnClickListener(v ->
                    listener.onListClick(((ShoppingListAdapter.ViewHolder) v.getTag()).currentList));

            itemView.setOnLongClickListener(v -> {
                listener.onListLongClick(((ShoppingListAdapter.ViewHolder) v.getTag()).currentList);
                return true;
            });

            itemView.setTag(this);
        }

        private ShoppingList currentList;

        public void bind(ShoppingList list) {
            currentList = list;
            ((android.widget.TextView) itemView.findViewById(R.id.textViewListName))
                    .setText(list.getName());
            ((android.widget.TextView) itemView.findViewById(R.id.textViewDate))
                    .setText(DateUtils.formatDate(list.getCreatedAt()));
        }
    }
}