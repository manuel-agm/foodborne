package database;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.foodborne.R;

import java.util.Date;

//GESTOR DE BASE DE DATOS
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

    //ESTA BASE DE DATOS SÓLO CONTIENE UNA TABLA DAY, QUE GUARDA LAS COMIDAS PLANIFICADAS PARA ESE DÍA
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DAY +
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

    //OBTIENE LAS COMIDAS DE UN DÍA DEL AÑO CONCRETO
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

    //INSERTA UNA RECETA EN UNA DE LAS CUATRO COMIDAS DE UN DÍA
    public void insertRecipeIntoDay(int recipeID, int meal, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase dbSel = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT " + COLUMN_DATE + " FROM " + TABLE_NAME_DAY +
                " WHERE " + COLUMN_DATE + " = ?", new String[] {date});

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
        long result = -1 ;
        if(!c.moveToFirst()){
            result = db.insert(TABLE_NAME_DAY, null, cv);
        } else {
            result = db.update(TABLE_NAME_DAY, cv, COLUMN_DATE + "= ?", new String[] {date});
        }
        if (result == -1) {
            Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show();
        }

        c.close();
        db.close();
        dbSel.close();
    }

    //BORRA UNA RECETA ASIGNADA A UN DÍA
    public void deleteRecipeFromDay(int meal, String date) {
        int nulls = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase dbSel = this.getReadableDatabase();

        Cursor c = dbSel.rawQuery("SELECT " +  COLUMN_BREAKFAST + ", " + COLUMN_LAUNCH + ", " +
                COLUMN_SNACK + ", " +  COLUMN_DINNER + " FROM " + TABLE_NAME_DAY + " WHERE " + COLUMN_DATE + " = ?", new String[] {date});

        if(c.moveToFirst()){
            for(int i = 0; i < 4; i++){
                if(c.isNull(i)){
                    if(i == meal){
                        Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    nulls++;
                }
            }
            int result;
            if(nulls == 3){
                result = db.delete(TABLE_NAME_DAY, COLUMN_DATE + "= ?", new String[] {date});
            } else {
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_DATE,Integer.parseInt(date));
                switch(meal) {
                    case 0:
                        cv.put(COLUMN_BREAKFAST, (Integer) null);
                        break;
                    case 1:
                        cv.put(COLUMN_LAUNCH,(Integer) null);
                        break;
                    case 2:
                        cv.put(COLUMN_SNACK,(Integer) null);
                        break;
                    case 3:
                        cv.put(COLUMN_DINNER,(Integer) null);
                        break;
                }
                result = db.update(TABLE_NAME_DAY, cv, COLUMN_DATE + "= ?", new String[] {date});
            }
            if (result == -1) {
                Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.deleted), Toast.LENGTH_SHORT).show();
            }
        } else {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setMessage(context.getString(R.string.nomeal));
            b.setCancelable(true);
            b.setPositiveButton(context.getString(R.string.accept),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }
            );
            AlertDialog a = b.create();
            a.show();
        }
    }
}
