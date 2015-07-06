package main.Controller;

/**
 * Created by User on 30.06.2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.main.R;

import java.util.ArrayList;

import main.Model.News;

public class SqlAdapter extends BaseAdapter {
    static final String DATABASE_TABLE = "news";
    static final String DB_NAME = "DB";
    static final int DB_VERSION = 1;
    static final String KEY_ID = "_id";
    static final int ID_COLUMN = 0;
    static final String KEY_TITLE = "title";
    static final int TITLE_COLUMN = 1;
    static final String KEY_IMG_URL = "img_url";
    static final int IMG_URL_COLUMN = 2;
    static final String KEY_CONTENT = "content";
    static final int CONTENT_COLUMN = 3;
    private Cursor cursor;
    private SQLiteDatabase database;
    private DbOpenHelper dbOpenHelper;
    private Context context;
    private News[] news;

    public SqlAdapter(Context context) {
        super();
        this.context = context;
        init();
    }

    @Override
    public long getItemId(int position) {
        News nameOnPosition = getItem(position);
        return nameOnPosition.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (null == convertView) {
            textView = (TextView) View.inflate(context, R.layout.item,
                    null);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(getItem(position).getTitle());
        return textView;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public News getItem(int position) {
        if (cursor.moveToPosition(position)) {
            long id = cursor.getLong(ID_COLUMN);
            String title = cursor.getString(TITLE_COLUMN);
            String img_url = cursor.getString(IMG_URL_COLUMN);
            String content = cursor.getString(CONTENT_COLUMN);

            News nameOnPositon = new News(id, title, img_url, content);
            return nameOnPositon;
        } else {
            throw new CursorIndexOutOfBoundsException(
                    "Cant move cursor to position");
        }
    }

    public Cursor getAllEntries() {
        String[] columnsToTake = {KEY_ID, KEY_TITLE, KEY_IMG_URL, KEY_CONTENT};
        return database.query(DATABASE_TABLE, columnsToTake,
                null, null, null, null, KEY_ID);
    }

    public News[] getAll(){
        int i = 0;
        news = new News[cursor.getCount()];
        while (!cursor.isLast()) {
            news[i] = getItem(i);
            i++;
            cursor.moveToNext();
        }
        return news;
    }

    public long addItem(News news) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, news.getTitle());
        values.put(KEY_IMG_URL, news.getImg_url());
        values.put(KEY_CONTENT, news.getContent());
        long id = database.insert(DATABASE_TABLE, null, values);
        refresh();
        return id;
    }

    public void addNews(ArrayList<News> news) {
        for (News aNew : news) {
            addItem(aNew);
        }
    }

    public boolean removeItem(News nameToRemove) {
        boolean isDeleted = (database.delete(DATABASE_TABLE, KEY_TITLE + "=?",
                new String[] { nameToRemove.getTitle() })) > 0;
        refresh();
        return isDeleted;
    }

    public void removeAllItems() {
        database.delete(DATABASE_TABLE, null, null);
    }

    public boolean updateItem(long id, String newName) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, newName);
        boolean isUpdated = (database.update(DATABASE_TABLE, values, KEY_ID + "=?",
                new String[] {id+""})) > 0;
        return isUpdated;
    }

    public void onDestroy() {
        dbOpenHelper.close();
    }

    private void refresh() {
        cursor = getAllEntries();
        notifyDataSetChanged();
    }

    private void init() {
        dbOpenHelper = new DbOpenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            database = dbOpenHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(this.toString(), "Error while getting database");
            throw new Error("The end");
        }
        cursor = getAllEntries();
    }

    private static class DbOpenHelper extends SQLiteOpenHelper {
        public DbOpenHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String CREATE_DB = "CREATE TABLE " + DATABASE_TABLE + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_IMG_URL + " TEXT NOT NULL, " + KEY_TITLE + " TEXT NOT NULL, " + KEY_CONTENT + " TEXT NOT NULL);";
            db.execSQL(CREATE_DB);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
}
