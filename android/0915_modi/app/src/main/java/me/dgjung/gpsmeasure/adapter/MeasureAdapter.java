package me.dgjung.gpsmeasure.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.dgjung.gpsmeasure.R;
import me.dgjung.gpsmeasure.db.DatabaseContract;
import me.dgjung.gpsmeasure.db.DatabaseHelper;
import me.dgjung.gpsmeasure.vo.Measurements;

public class MeasureAdapter extends RecyclerView.Adapter<MeasureAdapter.MeasureViewHolder> {

    List<Measurements> measurementsList;
    Context context;
    DatabaseHelper dbhelper;
    SQLiteDatabase db;

    public MeasureAdapter(List<Measurements> measurementsList) {
        this.measurementsList = measurementsList;
    }

    @Override
    public MeasureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View iteView = inflater.inflate(R.layout.item_module, parent, false);
        MeasureViewHolder viewHolder = new MeasureViewHolder(iteView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MeasureViewHolder holder, final int position) {
        Measurements measurements = measurementsList.get(position);

        // holder.tvTitle.setText(measurements.getStepCount() + " 걸음 / " + measurements.getSpendTime() + " 초");
        holder.tvTitle.setText(measurements.getSpendTime() + " 초");
        holder.tvDate.setText(measurements.getMeasureDate());

        holder.locationFilePath.setText(measurements.getLocationFilePath());
        //holder.measurementFilePath.setText(measurements.getMeasurementFilePath());
        //holder.naviFilePath.setText(measurements.getNavigationFilePath());
        //holder.gpsStatusFilePath.setText(measurements.getGpsStatusFilePath());
        //holder.nmeaFilePath.setText(measurements.getNmeaFilePath());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(
                        "JDG", "click"
                );
                final Measurements measurements = measurementsList.get(position);
                final int primaryID = measurements.get_id();

                dbhelper = new DatabaseHelper(context);
                db = dbhelper.getWritableDatabase();
                PopupMenu menu = new PopupMenu(context, holder.itemView);

                menu.inflate(R.menu.menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                db.delete(DatabaseContract.MeasurementsDatabase.TABLE_NAME, DatabaseContract.MeasurementsDatabase._ID + " = " + primaryID,null);
                                notifyItemRangeChanged(position,measurementsList.size());
                                measurementsList.remove(position);
                                notifyItemRemoved(position);
                                db.close();
                                break;
                            case R.id.update:
                                /*Intent intent = new Intent(context, UpdateActivity.class);
                                intent.putExtra("USERID", userId);
                                context.startActivity(intent);*/

                                // 나중에 구현.
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return measurementsList.size();
    }

    public class MeasureViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, locationFilePath, measurementFilePath, naviFilePath, gpsStatusFilePath, nmeaFilePath;

        public MeasureViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);

            locationFilePath = itemView.findViewById(R.id.location_file_path);
//            measurementFilePath = itemView.findViewById(R.id.measurement_file_path);
//            naviFilePath = itemView.findViewById(R.id.navi_file_path);
//            gpsStatusFilePath = itemView.findViewById(R.id.gpsStatus_file_path);
//            nmeaFilePath = itemView.findViewById(R.id.nmea_file_path);
        }
    }
}
