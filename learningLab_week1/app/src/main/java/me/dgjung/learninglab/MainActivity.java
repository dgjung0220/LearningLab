package me.dgjung.learninglab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static View.OnClickListener itemsOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsOnClickListener = new ItemsOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /* make test data */
        List<Items> data = initializeData();

        mAdapter = new ItemAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
    }

    private static class ItemsOnClickListener implements View.OnClickListener {

        private final Context context;

        private ItemsOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            // click 시 생성...
            Toast.makeText(context, "TEST", Toast.LENGTH_SHORT).show();
        }
    }

    /* Make test data */
    private List<Items> initializeData(){
        List<Items> items = new ArrayList<>();
        items.add(new Items("1", "Monday", 5000, 620, new Date().getTime()));
        items.add(new Items("2", "Tuesday", 2000, 310, new Date().getTime()));
        items.add(new Items("3", "Wednesday", 12000, 7400, new Date().getTime()));

        return items;
    }

}