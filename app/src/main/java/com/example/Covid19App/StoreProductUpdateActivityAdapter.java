package com.example.Covid19App;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StoreProductUpdateActivityAdapter extends RecyclerView.Adapter<StoreProductUpdateActivityAdapter.MyViewHolder> {

    ArrayList<ProductClass> recyclerProducts;
    Onclick onclick;
    Context context;

    public interface Onclick{
        void onEvent( ProductClass productClass, int pos, String event);
    }

    public StoreProductUpdateActivityAdapter( Context context, ArrayList<ProductClass> recyclerProducts, Onclick onclick) {
        this.context = context;
        this.recyclerProducts = recyclerProducts;
        this.onclick = onclick;
    }


    @NonNull
    @Override
    public StoreProductUpdateActivityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.seller_product_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreProductUpdateActivityAdapter.MyViewHolder holder, final int position) {

        final ProductClass productClass = recyclerProducts.get(position);

        if(productClass.getProductName() != null){

            holder.productName.setText(productClass.getProductName());
            holder.productPrice.setText( String.valueOf(productClass.getPrice()));
            holder.productUnits.setText( String.valueOf(productClass.getUnits()));
            holder.productUnitType.setText(productClass.getUnitType());

        }

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to delete " +productClass.getProductName()+ " item?")
                        .setTitle("Delete Operation:")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String delete_event = "delete";
                                recyclerProducts.remove(position);
                                onclick.onEvent( productClass, position, delete_event);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Do something when cancelled
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        holder.sellerProductLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String event = "update";
                onclick.onEvent( productClass, position, event);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recyclerProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout sellerProductLayout;

        TextView productName, productPrice, productUnits, productUnitType;
        TextView productCurrency;
        ImageView deleteIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            sellerProductLayout = itemView.findViewById(R.id.sellerProductLayout);

            productName = itemView.findViewById(R.id.sellerRecyclerProductName);
            productPrice = itemView.findViewById(R.id.sellerRecyclerProductPrice);
            productUnits = itemView.findViewById(R.id.sellerRecyclerProductUnits);
            productUnitType = itemView.findViewById(R.id.sellerRecyclerProductUnitType);

            productCurrency = itemView.findViewById(R.id.sellerRupeeSymbol);

            deleteIcon = itemView.findViewById(R.id.sellerRecyclerDeleteIcon);
        }
    }
}
