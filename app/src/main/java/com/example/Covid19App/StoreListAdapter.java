package com.example.Covid19App;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.MyViewHolder> {

    ArrayList<String> storeAdapterName = new ArrayList<>();
    ArrayList<String> storeAdapterAddress = new ArrayList<>();

    UserStoreClick userStoreClick;

    Context context;

    public interface UserStoreClick{
        void onEvent(int pos);
    }

    public StoreListAdapter(Context context, ArrayList<String> storeName, ArrayList<String> storeAddress, UserStoreClick userStoreClick)
    {
        this.userStoreClick = userStoreClick;
        this.context = context;
        this.storeAdapterName.addAll(storeName);
        this.storeAdapterAddress.addAll(storeAddress);
    }

    @NonNull
    @Override
    public StoreListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.store_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreListAdapter.MyViewHolder holder, final int position) {

        holder.Name.setText(storeAdapterName.get(position));
        holder.Address.setText(storeAdapterAddress.get(position));

        holder.storeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userStoreClick.onEvent( position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeAdapterName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name, Address;
        ConstraintLayout storeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.sellerRecyclerProductName);
            Address = itemView.findViewById(R.id.sellerRecyclerProductPrice);

            storeLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
