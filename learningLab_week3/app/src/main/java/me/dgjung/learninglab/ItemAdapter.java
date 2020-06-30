package me.dgjung.learninglab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private List<Items> itemsSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView header;
        public TextView title;
        public TextView measureDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.title = (TextView) itemView.findViewById(R.id.item_title);
            this.measureDate = (TextView) itemView.findViewById(R.id.item_date);
        }
    }

    public ItemAdapter(List<Items> items) {
        this.itemsSet = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module, parent, false);
        view.setOnClickListener(MainActivity.itemsOnClickListener);
        MyViewHolder mViewHolder = new MyViewHolder(view);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TextView title = holder.title;
        TextView measureDate = holder.measureDate;

        title.setText(itemsSet.get(position).getStepCount() + " / " + itemsSet.get(position).getSpendTime());
        measureDate.setText(itemsSet.get(position).getMeasureDate().toString());
    }

    @Override
    public int getItemCount() {
        return itemsSet.size();
    }


}
