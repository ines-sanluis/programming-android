package com.example.rula;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<Trip> myTrips = new ArrayList<>();
    //private ArrayList<String> myKeys = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDatabase = FirebaseDatabase.getInstance().getReference();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView = (ListView) findViewById(R.id.trips_list_view);

        TextView textView = new TextView(getApplication().getApplicationContext());
        textView.setText("Upcoming trips");

        listView.addHeaderView(textView);

        listView.setAdapter(adapter);
        listView.setClickable(true);

        myDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                //Trip myTrip = dataSnapshot.getValue(Trip.class);

                //HashMap<String, String> location;
                String name = null;
                String difficulty = null;
                String date = null;
                String maxPeople = null;
                String latitude = null;
                String longitude = null;

                for(DataSnapshot attribute : dataSnapshot.getChildren()) { //trips
                    for (DataSnapshot nested : attribute.getChildren()) {
                        switch(nested.getKey()){
                            case "lat":
                                latitude = (String) nested.getValue();
                                break;
                            case"lon":
                                longitude = (String) nested.getValue();
                                break;
                        }
                    }
                    switch (attribute.getKey()) {
                        case "date":
                            date = (String) attribute.getValue();
                            break;
                        case "difficulty":
                            difficulty = (String) attribute.getValue();
                            break;
                        case "maxPeople":
                            maxPeople = (String) attribute.getValue();
                            break;
                        case "name":
                            name = (String) attribute.getValue();
                            break;
                    }
                }


                Trip myTrip = new Trip(name, latitude, longitude, difficulty, date, maxPeople);
                myTrips.add(myTrip);
                arrayList.add("\n"+myTrip.getName()+"\n");
                //myKeys.add(key);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), TripDetailActivity.class);
                //String key = myKeys.get(position - 1);
                String name = (String) listView.getItemAtPosition(position);
                Trip selected = myTrips.get(position-1);

                Bundle extras = new Bundle();
                //extras.putString("tripKey", key);
                extras.putString("name", selected.getName());
                extras.putString("latitude", selected.getLatitude());
                extras.putString("longitude", selected.getLongitude());
                extras.putString("date", selected.getDate());
                extras.putString("difficulty", selected.getDifficulty());
                extras.putString("maxPeople", selected.getMaxPeople());
                intent.putExtras(extras);

                startActivity(intent);
            }
        });
    }

}
