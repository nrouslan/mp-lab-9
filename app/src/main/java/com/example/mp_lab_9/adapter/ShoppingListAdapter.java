package com.example.mp_lab_9.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

    private List<ShoppingList> shoppingLists;
    private OnListClickListener listener;

    public ShoppingListAdapter(List<ShoppingList> shoppingLists, OnListClickListener listener) {
        this.shoppingLists = shoppingLists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingList list = shoppingLists.get(position);
        holder.bind(list, listener);
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    public void updateData(List<ShoppingList> newLists) {
        this.shoppingLists.clear();
        this.shoppingLists.addAll(newLists);
        notifyDataSetChanged();
    }

    public void addItem(ShoppingList list) {
        shoppingLists.add(0, list);
        notifyItemInserted(0);
    }

    public void updateItem(ShoppingList list) {
        int position = getItemPosition(list.getId());
        if (position != -1) {
            shoppingLists.set(position, list);
            notifyItemChanged(position);
        }
    }

    public void removeItem(int listId) {
        int position = getItemPosition(listId);
        if (position != -1) {
            shoppingLists.remove(position);
            notifyItemRemoved(position);
        }
    }

    private int getItemPosition(int listId) {
        for (int i = 0; i < shoppingLists.size(); i++) {
            if (shoppingLists.get(i).getId() == listId) {
                return i;
            }
        }
        return -1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewListName;
        private TextView textViewDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewListName = itemView.findViewById(R.id.textViewListName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }

        public void bind(ShoppingList list, OnListClickListener listener) {
            // Установка названия списка
            textViewListName.setText(list.getName());

            // Форматирование даты
            String formattedDate = DateUtils.formatDate(list.getCreatedAt());
            textViewDate.setText(formattedDate);

            // Обработчики кликов
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onListClick(list);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onListLongClick(list);
                }
                return true;
            });
        }
    }
}