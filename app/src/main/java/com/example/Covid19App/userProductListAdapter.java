package com.example.Covid19App;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class userProductListAdapter extends RecyclerView.Adapter<userProductListAdapter.MyViewHolder> {

    Context context;
    ArrayList<ProductClass> dbProducts;
    UserProductOnclick userProductOnclick;




    int msgCount = 0;

    public interface UserProductOnclick{
        void onEvent( ProductClass productClass, int pos,String productName, float userUnits, float userPrice, String unitType, String event);
    }

    public userProductListAdapter(Context context, ArrayList<ProductClass> dbProducts, UserProductOnclick userProductOnclick) {
        this.context = context;
        this.dbProducts = dbProducts;
        this.userProductOnclick = userProductOnclick;
    }


    @NonNull
    @Override
    public userProductListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_product_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final userProductListAdapter.MyViewHolder holder, final int position) {

        final ProductClass productClass = dbProducts.get(position);

        if(holder.name.toString() != productClass.getProductName())
        {
            holder.userPrice.setText("0");
            holder.userUnit.setText("0");

            if(msgCount > 1 )
            {
                Log.d(TAG, "inside toast if");
                Toast.makeText(context, "The product list has been changed by the seller & hence some selected items have been reset!", Toast.LENGTH_SHORT).show();
            }

            if(position == dbProducts.size()-1) {
                Log.d(TAG, "inside msg increment");
                msgCount += 1;
            }
        }

        if(productClass.getProductName() != null){

            holder.name.setText(productClass.getProductName());
            holder.prodPrice.setText( String.valueOf(productClass.getPrice()));
            holder.prodUnit.setText( String.valueOf(productClass.getUnits()));
            holder.prodUnitType.setText(productClass.getUnitType());
            holder.userType.setText(productClass.getUnitType());

        }


        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String event = "Add";

                String productName = holder.name.getText().toString();
                String userType = holder.userType.getText().toString();
                Float userUnits = Float.parseFloat(holder.userUnit.getText().toString());
                Float userPrice = Float.parseFloat(holder.userPrice.getText().toString());

                float productPrice = productClass.price;
                float availableUnits = productClass.quantity;

                if(userType.equals("Units") || userType.equals("Unit") || userType.equals("Piece") || userType.equals("Pieces"))
                {
                    if(userUnits + 1 <= availableUnits)
                    {
                        if(userUnits == 0)
                        {
                            userUnits += 1;
                            userPrice = userPrice + productPrice;
                        }
                        else if(userUnits == 1 || userUnits ==2)
                        {
                            userUnits += 1;
                            userPrice = userPrice + (2 * productPrice);
                        }
                        else
                        {
                            Toast.makeText(context, "Limit Reached!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "Items cant exceed available items", Toast.LENGTH_SHORT).show();
                    }

                }
                else if(userType.equals("Kg"))
                {
                    if(userUnits + 0.25 <= availableUnits && userUnits + 0.25 <= 3)
                    {
                        if(userUnits < 1)
                        {
                            userUnits += (float) 0.25;
                            userPrice = userPrice + (float) (productPrice * 0.25);
                        }
                        else
                        {
                            userUnits += (float) 0.25;
                            userPrice = userPrice + (float) (2 * productPrice * 0.25);
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "Items ordered greater than available items or exceeds 3 Kg limit!", Toast.LENGTH_SHORT).show();
                    }
                }

                holder.userUnit.setText(String.valueOf(userUnits));
                holder.userPrice.setText(String.valueOf(userPrice));

                userProductOnclick.onEvent( productClass, position, productName, userUnits, userPrice, userType, event);
            }
        });

        holder.subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String event = "Sub";

                String productName = holder.name.getText().toString();
                String userType = holder.userType.getText().toString();
                Float userUnits = Float.parseFloat(holder.userUnit.getText().toString());
                Float userPrice = Float.parseFloat(holder.userPrice.getText().toString());

                float productPrice = productClass.price;

                if(userType.equals("Units") || userType.equals("Unit") || userType.equals("Piece") || userType.equals("Pieces"))
                {
                    if(userUnits - 1 >= 0)
                    {
                        if(userUnits == 1)
                        {
                            userUnits -= 1;
                            userPrice = userPrice - productPrice;
                        }
                        else if(userUnits == 2 || userUnits == 3)
                        {
                            userUnits -= 1;
                            userPrice = userPrice - (2*productPrice);
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "Can't go lower than zero!", Toast.LENGTH_SHORT).show();
                    }

                }
                else if(userType.equals("Kg"))
                {
                    if(userUnits - 0.25 >= 0)
                    {
                        if(userUnits <= 1)
                        {
                            userUnits -= (float) 0.25;
                            userPrice = userPrice - (float) (productPrice * 0.25);
                        }
                        else
                        {
                            userUnits -= (float) 0.25;
                            userPrice = userPrice - (float) (2 * productPrice * 0.25);
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "Item count can't go lower than zero", Toast.LENGTH_SHORT).show();
                    }
                }

                holder.userUnit.setText(String.valueOf(userUnits));
                holder.userPrice.setText(String.valueOf(userPrice));

                userProductOnclick.onEvent(productClass, position, productName, userUnits, userPrice, userType, event);
            }
        });

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
