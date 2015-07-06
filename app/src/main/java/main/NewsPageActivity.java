package main;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.R;

import java.util.ArrayList;

import main.Model.ImageLoader;
import main.Model.News;

public class NewsPageActivity extends Activity{
    private ImageLoader imageLoader;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        String title = getIntent().getExtras().getString("title");
        String img = getIntent().getExtras().getString("img");
        String content = getIntent().getExtras().getString("content");
        TextView titleView = (TextView)findViewById(R.id.title);
        WebView contentView = (WebView)findViewById(R.id.content);

        titleView.setText(title);
//        contentView.setText(content);
        contentView.loadDataWithBaseURL(null, content, "text/html", "en_US", null);
    }
}
