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

    private final List<Product> products;
    private final OnProductClickListener listener;
    private final boolean showDeleteButton;

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
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBoxPurchased;
        private final TextView textViewProductName;
        private final TextView textViewQuantity;
        private final TextView textViewPrice;
        private final ImageButton buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxPurchased = itemView.findViewById(R.id.checkBoxPurchased);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(Product product) {
            textViewProductName.setText(product.getName());

            // Убрал явное преобразование для количества
            textViewQuantity.setText(String.valueOf(product.getQuantity()));

            checkBoxPurchased.setChecked(product.isPurchased());
            updateTextAppearance(product.isPurchased());

            buttonDelete.setVisibility(showDeleteButton ? View.VISIBLE : View.GONE);

            if (showDeleteButton) {
                buttonDelete.setOnClickListener(v -> listener.onProductDeleteClick(product));
            }

            itemView.setOnClickListener(v -> listener.onProductClick(product));
            itemView.setOnLongClickListener(v -> {
                listener.onProductLongClick(product);
                return true;
            });
        }

        private void updateTextAppearance(boolean isPurchased) {
            int nameColor, quantityColor, priceColor;

            if (isPurchased) {
                textViewProductName.setPaintFlags(
                        textViewProductName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                );
                nameColor = R.color.colorTextHint;
                quantityColor = R.color.colorTextHint;
                priceColor = R.color.colorTextHint;
            } else {
                textViewProductName.setPaintFlags(
                        textViewProductName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
                );
                nameColor = R.color.colorTextPrimary;
                quantityColor = R.color.colorTextSecondary;
                priceColor = R.color.success;
            }

            textViewProductName.setTextColor(getColor(nameColor));
            textViewQuantity.setTextColor(getColor(quantityColor));
            textViewPrice.setTextColor(getColor(priceColor));
        }

        private int getColor(int colorResId) {
            return ContextCompat.getColor(itemView.getContext(), colorResId);
        }
    }
}