package com.automata.calculatorkenya;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;


class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<Model> mList;
    private Context mContext;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.model, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Model model = mList.get(position);
        holder.itemName.setText(model.getName());

        Glide.with(mContext).load(model.getImage()).into(holder.itemImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (holder.itemName.getText().toString()) {

                    case "Income tax-PAYE":
                        intent = new Intent(mContext, IncomeTax.class);
                        mContext.startActivity(intent);
                        break;
                    case "M-PESA Cost":
                        intent = new Intent(mContext, MPesa.class);
                        mContext.startActivity(intent);
                        break;
                    case "Airtel Money Cost":
                        intent = new Intent(mContext, Airtel.class);
                        mContext.startActivity(intent);
                        break;
                    case "M-Shwari Loan":
                        intent = new Intent(mContext, Mshwari.class);
                        mContext.startActivity(intent);
                        break;
                    case "HELB Loan":
                        intent = new Intent(mContext, HELB.class);
                        mContext.startActivity(intent);
                        break;
                    case "KPLC Postpaid":
                        intent = new Intent(mContext, KPLCPost.class);
                        mContext.startActivity(intent);
                        break;
                    case "KPLC Prepaid Tokens":
                        intent = new Intent(mContext, KPLCPre.class);
                        mContext.startActivity(intent);
                        break;
                    case "Nairobi Water Bill":
                        intent = new Intent(mContext, Water.class);
                        mContext.startActivity(intent);
                        break;
                    case "TSC Salary":
                        intent = new Intent(mContext, TSC.class);
                        mContext.startActivity(intent);
                        break;
                    case "Civil Servants Salary":
                        intent = new Intent(mContext, Civil.class);
                        mContext.startActivity(intent);
                        break;
                    case "Motor Insurance":
                        intent = new Intent(mContext, Motor.class);
                        mContext.startActivity(intent);
                        break;
                    case "Currency Converter":
                        intent = new Intent(mContext, Converter.class);
                        mContext.startActivity(intent);
                        break;
                    default:
                        Toast.makeText(mContext, "coming sooon...", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView itemImage;

        MyViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.name);
            itemImage = view.findViewById(R.id.item);


        }
    }

    public RecyclerAdapter(Context mContext, List<Model> shopList) {
        this.mList = shopList;
        this.mContext = mContext;
    }
}
