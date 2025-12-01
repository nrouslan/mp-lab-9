package com.example.mp_lab_9.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mp_lab_9.data.model.Product;
import com.example.mp_lab_9.R;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onProductLongClick(Product product);
        void onProductDeleteClick(Product product);
    }

    private List<Product> products;
    private OnProductClickListener listener;
    private boolean showDeleteButton;

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this(products, listener, true);
    }

    public ProductAdapter(List<Product> products, OnProductClickListener listener, boolean showDeleteButton) {
        this.products = products;
        this.listener = listener;
        this.showDeleteButton = showDeleteButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener, showDeleteButton);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateData(List<Product> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }

    public void addItem(Product product) {
        products.add(product);
        notifyItemInserted(products.size() - 1);
    }

    public void updateItem(Product product) {
        int position = getItemPosition(product.getId());
        if (position != -1) {
            products.set(position, product);
            notifyItemChanged(position);
        }
    }

    public void removeItem(int productId) {
        int position = getItemPosition(productId);
        if (position != -1) {
            products.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void toggleProduct(int productId) {
        int position = getItemPosition(productId);
        if (position != -1) {
            Product product = products.get(position);
            product.togglePurchased();
            notifyItemChanged(position);
        }
    }

    private int getItemPosition(int productId) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == productId) {
                return i;
            }
        }
        return -1;
    }

    public List<Product> getProducts() {
        return products;
    }

    public int getPurchasedCount() {
        int count = 0;
        for (Product product : products) {
            if (product.isPurchased()) {
                count++;
            }
        }
        return count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBoxPurchased;
        private TextView textViewProductName;
        private TextView textViewQuantity;
        private TextView textViewPrice;
        private ImageButton buttonDelete;
        private View categoryIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxPurchased = itemView.findViewById(R.id.checkBoxPurchased);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            categoryIndicator = itemView.findViewById(R.id.categoryIndicator);
        }

        public void bind(Product product, OnProductClickListener listener, boolean showDeleteButton) {
            // Установка названия товара
            textViewProductName.setText(product.getName());

            // Установка количества
            String quantityText = itemView.getContext().getString(
                    R.string.quantity) + ": " + product.getFormattedQuantity();
            textViewQuantity.setText(quantityText);

            // Установка цены (если есть)
            if (product.getPrice() > 0) {
                textViewPrice.setText(product.getFormattedPrice());
                textViewPrice.setVisibility(View.VISIBLE);
            } else {
                textViewPrice.setVisibility(View.GONE);
            }

            // Настройка чекбокса - только отображение, без обработчиков
            checkBoxPurchased.setChecked(product.isPurchased());
            updateTextAppearance(product.isPurchased());

            // Настройка видимости кнопки удаления
            if (showDeleteButton) {
                buttonDelete.setVisibility(View.VISIBLE);
                buttonDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onProductDeleteClick(product);
                    }
                });
            } else {
                buttonDelete.setVisibility(View.GONE);
            }

            // Обработчики кликов на весь элемент
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onProductLongClick(product);
                    return true;
                }
                return false;
            });
        }

        private void updateTextAppearance(boolean isPurchased) {
            if (isPurchased) {
                textViewProductName.setPaintFlags(textViewProductName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textViewProductName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorTextHint));
                textViewQuantity.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorTextHint));
                textViewPrice.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorTextHint));
            } else {
                textViewProductName.setPaintFlags(textViewProductName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                textViewProductName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorTextPrimary));
                textViewQuantity.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorTextSecondary));
                textViewPrice.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.success));
            }
        }
    }
}