package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.foodborne.R;

public class SQLiteHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "Recipes.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME_DAY = "day";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_BREAKFAST = "breakfast";
    public static final String COLUMN_LAUNCH = "launch";
    public static final String COLUMN_SNACK = "snack";
    public static final String COLUMN_DINNER = "dinner";

    public static final String TABLE_NAME_RECIPE = "day";
    public static final String COLUMN_ID_RECIPE = "_id";
    public static final String COLUMN_NAME = "name";

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTable = "CREATE TABLE " + TABLE_NAME_DAY +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " INTEGER, " +
                COLUMN_BREAKFAST + " INTEGER, " +
                COLUMN_LAUNCH + " INTEGER, " +
                COLUMN_SNACK + " INTEGER, " +
                COLUMN_DINNER + " INTEGER);";
        sqLiteDatabase.execSQL(createTable);

        String createTableRecipe = "CREATE TABLE " + TABLE_NAME_RECIPE +
                " (" + COLUMN_ID_RECIPE + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT);";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DAY);
        onCreate(sqLiteDatabase);
    }

    public String[] getMeals(String date) {
        String[] res = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " +  COLUMN_BREAKFAST + ", " + COLUMN_LAUNCH + ", " +
                COLUMN_SNACK + ", " +  COLUMN_DINNER + " FROM " + TABLE_NAME_DAY + " WHERE " + COLUMN_DATE + " = ?", new String[] {date});
        if (c.moveToFirst()) {
            res = new String[] {c.getString(0), c.getString(1), c.getString(2), c.getString(3)};
        }
        c.close();
        db.close();
        return res;
    }

    public void insertRecipeIntoDay(int recipeID, int meal, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATE,Integer.parseInt(date));
        switch(meal) {
            case 0:
                cv.put(COLUMN_BREAKFAST,recipeID);
                break;
            case 1:
                cv.put(COLUMN_LAUNCH,recipeID);
                break;
            case 2:
                cv.put(COLUMN_SNACK,recipeID);
                break;
            case 3:
                cv.put(COLUMN_DINNER,recipeID);
                break;
        }
        long result = db.insert(TABLE_NAME_DAY, null, cv);
        if (result == -1) {
            Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show();
        }
    }
}
