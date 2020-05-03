package com.example.Covid19App;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class userProductListAdapter extends RecyclerView.Adapter<userProductListAdapter.MyViewHolder> {

    Context context;
    ArrayList<ProductClass> dbProducts;

    public userProductListAdapter(Context context, ArrayList<ProductClass> dbProducts) {
        this.context = context;
        this.dbProducts = dbProducts;
    }


    @NonNull
    @Override
    public userProductListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_product_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userProductListAdapter.MyViewHolder holder, int position) {

        final ProductClass productClass = dbProducts.get(position);

        if(productClass.getProductName() != null){

            holder.name.setText(productClass.getProductName());
            holder.prodPrice.setText( String.valueOf(productClass.getPrice()));
            holder.prodUnit.setText( String.valueOf(productClass.getUnits()));
            holder.prodUnitType.setText(productClass.getUnitType());
            holder.userType.setText(productClass.getUnitType());

        }

    }

    @Override
    public int getItemCount() {
        return dbProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, prodPrice, prodUnit, prodUnitType, userPrice, userUnit, userType, addBtn, subBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userRecyclerProductName);
            prodPrice = itemView.findViewById(R.id.userRecyclerProductPrice);
            prodUnit = itemView.findViewById(R.id.userRecyclerProductUnits);
            prodUnitType = itemView.findViewById(R.id.userRecyclerProductUnitType);
            userPrice = itemView.findViewById(R.id.userRecyclerProductPrice2);
            userUnit = itemView.findViewById(R.id.userPrice);
            userType = itemView.findViewById(R.id.userProductType2);
            addBtn = itemView.findViewById(R.id.userPlusBtn);
            subBtn = itemView.findViewById(R.id.userMinusBtn);

        }
    }
}
