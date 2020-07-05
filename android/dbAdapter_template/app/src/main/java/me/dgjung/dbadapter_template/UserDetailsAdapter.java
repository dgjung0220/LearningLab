package me.dgjung.dbadapter_template;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserDetailsAdapter extends RecyclerView.Adapter<UserDetailsAdapter.UserViewHolder> {

    List<UserDetails> userDetailsList;
    Context context;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    public UserDetailsAdapter(List<UserDetails> userDetailsList) {
        this.userDetailsList = userDetailsList;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View iteView = inflater.inflate(R.layout.list_item, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(iteView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, final int position) {
        UserDetails userDetails = userDetailsList.get(position);

        holder.tvHeader.setText(userDetails.getName().substring(0,1).toUpperCase());
        holder.tvName.setText(userDetails.getName());
        holder.tvAddress.setText(userDetails.getAddress());
        holder.tvPhone.setText(userDetails.getMobileNo());
        holder.tvProfession.setText(userDetails.getProfessiion());

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final UserDetails userDetails = userDetailsList.get(position);
                final int userId = userDetails.getUserId();
                dbHelper = new DatabaseHelper(context);
                db = dbHelper.getWritableDatabase();
                PopupMenu menu = new PopupMenu(context, holder.itemView);

                menu.inflate(R.menu.menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                db.delete(DatabaseContract.UserDatabase.TABLE_NAME, DatabaseContract.UserDatabase._ID + " = " + userId,null);
                                notifyItemRangeChanged(position,userDetailsList.size());
                                userDetailsList.remove(position);
                                notifyItemRemoved(position);
                                db.close();
                                break;
                            case R.id.update:
                                Intent intent = new Intent(context, UpdateActivity.class);
                                intent.putExtra("USERID", userId);
                                context.startActivity(intent);
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
        return userDetailsList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader, tvName, tvAddress, tvPhone, tvProfession;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_header);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvProfession = itemView.findViewById(R.id.tv_profession);
        }
    }
}
