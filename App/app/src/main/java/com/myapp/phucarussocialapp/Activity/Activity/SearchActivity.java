package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.myapp.phucarussocialapp.Activity.Adapter.UserListAdapter;
import com.myapp.phucarussocialapp.Activity.Object.Auth;
import com.myapp.phucarussocialapp.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ListView lv_userlist;
    SearchView searchView;
    UserListAdapter userListAdapter;
    ArrayList<Auth> tempArrayList;
    TextView count;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        AnhXa();
        SetUpValue();
        lv_userlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(SearchActivity.this, UserActivity.class);
                intent.putExtra("user_uid", tempArrayList.get(position).getUid());
                intent.putExtra("user_name", tempArrayList.get(position).getName());
                intent.putExtra("user_avt", tempArrayList.get(position).getAvatar());
                startActivity(intent);
            }
        });
    }

    private void SetUpValue() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tempArrayList = new ArrayList<>();
                filter(newText);
                count.setText("Có " + tempArrayList.size() + " kết quả");
                return false;
            }
        });
    }


    private void filter(String query) {
        if (query.equals("")) {
            tempArrayList = HomeActivity.authArrayList;
        } else {
            for (int i = 0; i < HomeActivity.authArrayList.size(); i++) {
                int index = HomeActivity.authArrayList.get(i).getName().toLowerCase().indexOf(query.toLowerCase());
                if (index != -1) {
                    tempArrayList.add(HomeActivity.authArrayList.get(i));
                }
            }
        }

        userListAdapter = new UserListAdapter(SearchActivity.this, tempArrayList, R.layout.row_userlist);
        lv_userlist.setAdapter(userListAdapter);
    }

    private void AnhXa() {
        lv_userlist = (ListView) findViewById(R.id.rv);
        searchView = (SearchView) findViewById(R.id.sv);
        count = (TextView) findViewById(R.id.count_result_search);
        tempArrayList = new ArrayList<>();
        filter("");
        count.setText("Có " + tempArrayList.size() + " kết quả");
        back = (ImageView)findViewById(R.id.search_back);
    }

}
