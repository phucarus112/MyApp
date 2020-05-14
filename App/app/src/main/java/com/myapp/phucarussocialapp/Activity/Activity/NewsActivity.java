package com.myapp.phucarussocialapp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.myapp.phucarussocialapp.Activity.Adapter.NewsAdapter;
import com.myapp.phucarussocialapp.Activity.Object.News;
import com.myapp.phucarussocialapp.R;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    ImageView back;
    ViewPager viewPager;
    NewsAdapter adapter;
    ArrayList<News> newsArrayList;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        AnhXa();
        SetUpEvent();
    }

    private void SetUpEvent() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Integer[] temp_colors = {
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4),
                getResources().getColor(R.color.color5),
        };
        colors = temp_colors;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position < (adapter.getCount() - 1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
                } else viewPager.setBackgroundColor(colors[colors.length - 1]);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void AnhXa() {
        back = (ImageView)findViewById(R.id.news_back);
        newsArrayList = new ArrayList<>();
        newsArrayList.add(new News(R.drawable.k14,"Kênh 14"));
        newsArrayList.add(new News(R.drawable.baomoi,"Baomoi"));
        newsArrayList.add(new News(R.drawable.tuoitre,"Tuổi Trẻ"));
        newsArrayList.add(new News(R.drawable.express,"VN Express"));
        newsArrayList.add(new News(R.drawable.zing,"Zing News"));
        adapter = new NewsAdapter(newsArrayList,NewsActivity.this);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130,0,130,0);

    }
}
