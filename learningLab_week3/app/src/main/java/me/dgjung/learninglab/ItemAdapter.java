package me.dgjung.learninglab;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private List<Items> itemsSet;
    private Context context;


    public static class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {

        public TextView title;
        public TextView measureDate;

        public TextView locationFilePath;
        public TextView measurementFilePath;
        public TextView navigationFilePath;
        public TextView gpsStatusFilePath;
        public TextView nmeaFilePath;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.title = itemView.findViewById(R.id.item_title);
            this.measureDate = itemView.findViewById(R.id.item_date);
            this.locationFilePath = itemView.findViewById(R.id.location_file_path);
            this.measurementFilePath = itemView.findViewById(R.id.measurement_file_path);
            this.navigationFilePath = itemView.findViewById(R.id.navi_file_path);
            this.gpsStatusFilePath = itemView.findViewById(R.id.gpsStatus_file_path);
            this.nmeaFilePath = itemView.findViewById(R.id.nmea_file_path);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem desc = menu.add(Menu.NONE, 1001, 1, "자세히");
            MenuItem delete = menu.add(Menu.NONE, 1002, 2, "삭제");

            //desc.setOnMenuItemClickListener(onEditMenu);
            delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case 1001:
                        // 자세히는 나중에
                        break;
                    case 1002:
                        
                        break;
                }

                return true;
            }
        };
    }

    public ItemAdapter(Context context, List<Items> items) {
        this.context = context;
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
        TextView locationFilePath = holder.locationFilePath;
        TextView measurementFilePath = holder.measurementFilePath;
        TextView navigationFilePath = holder.navigationFilePath;
        TextView gpsStatusFilePath = holder.gpsStatusFilePath;
        TextView nmeaFilePath = holder.nmeaFilePath;

        title.setText(itemsSet.get(position).getStepCount() + " / " + itemsSet.get(position).getSpendTime());
        measureDate.setText(itemsSet.get(position).getMeasureDate().toString());
        locationFilePath.setText(itemsSet.get(position).getLocationFilePath());
        measurementFilePath.setText(itemsSet.get(position).getMeasurementFilePath());
        navigationFilePath.setText(itemsSet.get(position).getNavigationFilePath());
        gpsStatusFilePath.setText(itemsSet.get(position).getGpsStatusFilePath());
        nmeaFilePath.setText(itemsSet.get(position).getNmeaFilePath());
    }

    @Override
    public int getItemCount() {
        return itemsSet.size();
    }


}
