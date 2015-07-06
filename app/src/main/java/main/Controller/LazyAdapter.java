package main.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.R;

import java.util.ArrayList;

import main.MainActivity;
import main.Model.ImageLoader;
import main.Model.News;
import main.NewsPageActivity;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<News> news;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    private Context context;

    public LazyAdapter(Activity activity, ArrayList<News> news, Context context) {
        this.activity = activity;
        this.news = news;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(this.activity.getApplicationContext());
        this.context = context;
    }

    public void addNews(ArrayList<News> news) {
        this.news = news;
    }

    public void removeAll() {
        news.clear();
        news.trimToSize();
    }

    public int getCount() {
        return news.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null)
            view = inflater.inflate(R.layout.item, null);
        final TextView text = (TextView) view.findViewById(R.id.text);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        text.setText(news.get(position).getTitle());
        imageLoader.DisplayImage(news.get(position).getImg_url(), image);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewsPageActivity.class);
                intent.putExtra("title", news.get(position).getTitle());
                intent.putExtra("img", news.get(position).getImg_url());
                intent.putExtra("content", news.get(position).getContent());
                activity.startActivity(intent);
            }
        });
        return view;
    }
}