package main;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.main.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import main.Controller.SqlAdapter;
import main.Controller.LazyAdapter;
import main.Model.News;

public class MainActivity extends Activity {

    private ListView list;
    private LazyAdapter lazyAdapter;
    private SqlAdapter sqlAdapter;
    final public static String LOG_TAG = "my_log";
    private ArrayList<News> news;
    private int maxNews = 50;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        list = (ListView) findViewById(R.id.list);
        news = new ArrayList<News>();
        sqlAdapter = new SqlAdapter(this);
        lazyAdapter = new LazyAdapter(this, news, this);
        list.setAdapter(lazyAdapter);
        if (!downloadFromBDTask()) {
            new ParseTask().execute();
        }
    }

    private boolean downloadFromBDTask() {
        if (sqlAdapter.getCount() > 0) {
            for (int i = 0; i < sqlAdapter.getCount() && i < maxNews; i++) {
                news.add(sqlAdapter.getItem(i));
            }
            lazyAdapter.addNews(news);
            return true;
        }
        ;
        return false;
    }

    @Override
    public void onDestroy() {
        list.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            sqlAdapter.removeAllItems();
            lazyAdapter.imageLoader.clearCache();
            lazyAdapter.notifyDataSetChanged();

            new ParseTask().execute();
            return true;
        }

//        if (id == R.id.favourites) {
//            lazyAdapter.removeAll();
//            lazyAdapter.imageLoader.clearCache();
//            lazyAdapter.notifyDataSetChanged();
//            downloadFromFavourites();
//        }

        return super.onOptionsItemSelected(item);
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://api.innogest.ru/api/v3/amobile/news");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            Log.d(LOG_TAG, strJson);
            JSONArray dataJsonObj = null;
            news.clear();
            try {
                dataJsonObj = new JSONArray(strJson);
                String title;
                String img_url;
                String content;
                long id;

                int i;
                for (i = 0; i < dataJsonObj.length() && i < maxNews; i++) {
                    JSONObject post = dataJsonObj.getJSONObject(i);
                    id = post.getInt("nid");
                    img_url = post.getString("img_url");
                    title = post.getString("title");
                    content = post.getString("body");
                    Log.d(LOG_TAG, "img: " + img_url);
                    Log.d(LOG_TAG, "title: " + title);
                    Log.d(LOG_TAG, "body: " + content);
                    news.add(new News(id, title, img_url, content));
                    lazyAdapter.imageLoader.clearCache();
                    lazyAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sqlAdapter.addNews(news);
            lazyAdapter.addNews(news);
            Log.d(LOG_TAG, "count: " + sqlAdapter.getAllEntries().getCount());
        }
    }
}