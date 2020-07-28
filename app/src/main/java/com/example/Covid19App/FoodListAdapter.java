package com.example.Covid19App;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder> {

    Context context;
    ArrayList<FoodClass> food = new ArrayList<>();

    public FoodListAdapter(Context context, ArrayList<FoodClass> food) {
        this.context = context;
        this.food = food;
    }

    @NonNull
    @Override
    public FoodListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.food_lender_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodListAdapter.MyViewHolder holder, final int position) {

        final FoodClass foodClass = food.get(position);

        if(foodClass.getName() != null)
        {
            holder.name.setText(foodClass.getName());
            holder.address.setText(foodClass.getAddress());
            holder.no.setText(foodClass.getNo());
        }

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = foodClass.getUserId();

                Intent intent = new Intent(context, FoodAdminActivity.class);
                intent.putExtra("Id", id);

//                Toast.makeText(context, id, Toast.LENGTH_SHORT).show();

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return food.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView name, address, no;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.foodLayout);
            name = itemView.findViewById(R.id.foodLenderName);
            address = itemView.findViewById(R.id.foodLenderAddress);
            no = itemView.findViewById(R.id.foodLenderNumber);

        }
    }
}
