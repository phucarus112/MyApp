package com.myapp.phucarussocialapp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.myapp.phucarussocialapp.Activity.Activity.NewsActivity;
import com.myapp.phucarussocialapp.Activity.Activity.ReadNewsActivity;
import com.myapp.phucarussocialapp.Activity.Object.News;
import com.myapp.phucarussocialapp.R;

import java.util.ArrayList;

public class NewsAdapter extends PagerAdapter {

    ArrayList<News> newsArrayList;
    Context context;
    LayoutInflater inflater;

    public NewsAdapter(ArrayList<News> newsArrayList, Context context) {
        this.newsArrayList = newsArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return newsArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_news,container,false);
        ImageView img;
        TextView name;
        img = (ImageView)view.findViewById(R.id.news_img);
        name = (TextView)view.findViewById(R.id.news_name);
        img.setImageResource(newsArrayList.get(position).getImg());
        name.setText(newsArrayList.get(position).getName());
        container.addView(view,position);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadNewsActivity.class);
                switch (position) {
                    case 0:
                        intent.putExtra("type", "k14");
                        break;
                    case 1:
                        intent.putExtra("type", "baomoi");
                        break;
                    case 2:
                        intent.putExtra("type", "tuoitre");
                        break;
                    case 3:
                        intent.putExtra("type", "vnexpress");
                        break;
                    case 4:
                        intent.putExtra("type", "zing");
                        break;
                }
                context.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
