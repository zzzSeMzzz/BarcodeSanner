package sem.ru.barscaner.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sem.ru.barscaner.mvp.model.LocalPhoto;


public class SQLiteDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "main.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "SQLiteDB";
    private static final String sqlPhotosDb =
            "CREATE TABLE \"photos\" (\n" +
                    "\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    "\"name\"  TEXT,\n" +
                    "\"photo\"  TEXT,\n" +
                    "\"scaleW\"  INTEGER,\n" +
                    "\"scaleH\"  INTEGER,\n" +
                    "\"barcode\"  TEXT\n" +
                    ");";


    /*public boolean isFieldExist(SQLiteDatabase db, String tableName, String fieldName) {
        boolean isExist = false;
        //SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("PRAGMA table_info("+tableName+")", null);
        //logCursor(res);//Испортит курсор!!!
        while (res.moveToNext()){
            Log.d(TAG, res.getString(1));
            if(res.getString(res.getColumnIndex("name")).equals(fieldName)){
                isExist=true;
            }
        }
        return isExist;
    }*/

    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlPhotosDb);
        Log.d(TAG, "onCreate: Tables Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //this.onCreate(db);
        Log.d(TAG, "onUpgrade: new version="+newVersion);
        if(oldVersion==1 && newVersion==2){
            db.execSQL("ALTER TABLE photos ADD COLUMN barcode TEXT DEFAULT ''");
        }
    }



    public long addPhoto(LocalPhoto item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getFileName());
        values.put("photo", item.getPhoto().getAbsolutePath());
        values.put("scaleW", item.getScaleWidth());
        values.put("scaleH", item.getSclaeHeight());
        values.put("barcode", item.getBarcode());
        long id = db.insert("photos", null, values);
        item.setId(id);
        //db.close();
        Log.d(TAG, "Added animal success");
        return id;
    }

    public List<LocalPhoto> getAllPhotos() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LocalPhoto> animals = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("select * from photos", null)) {
            while (cursor.moveToNext()) {
                LocalPhoto localPhoto = new LocalPhoto();
                localPhoto.setFileName(cursor.getString(1));
                localPhoto.setPhoto(new File(cursor.getString(2)));
                localPhoto.setSclaeHeight(cursor.getInt(4));
                localPhoto.setScaleWidth(cursor.getInt(3));
                localPhoto.setId(cursor.getLong(0));
                localPhoto.setBarcode(cursor.getString(5));
                animals.add(localPhoto);
            }
        }
        //db.close();
        return animals;
    }


   /* public Animal getAnimal(long id) {
        Animal animal = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("select * from animals where id=?", new String[]{String.valueOf(id)})) {
            if (cursor.moveToNext()) {
                Date dtL = new Date();
                Date dtN = new Date();
                try {
                    dtL = format.parse(cursor.getString(4));
                    dtN = format.parse(cursor.getString(5));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "getAnimal: dtL="+dtL);
                Log.d(TAG, "getAnimal: dtN="+dtN);
                animal = new Animal(
                        cursor.getString(6),
                        cursor.getString(1),
                        Gender.values()[cursor.getInt(3)],
                        cursor.getString(2),
                        dtL,
                        dtN,
                        cursor.getInt(7)
                );
                animal.setId(cursor.getLong(0));
            }
        }
        //db.close();
        return animal;
    }

    public Animal updateAnimal(Animal animal){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", animal.getName());
        values.put("photo", animal.getPhoto());
        values.put("kind", animal.getKind());
        values.put("gender", animal.getGender().ordinal());
        values.put("interval", animal.getInterval());
        values.put("lastFeed", format.format(animal.getLastFeed()));
        values.put("nextFeed", format.format(animal.getNextFeed()));
        db.update("animals",values, "id=?",
                new String[]{String.valueOf(animal.getId())});
        return animal;
    }*/

    public boolean delPhoto(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.d(TAG, "delPhoto: "+db.isOpen());
        int cnt = db.delete("photos", "id=" + String.valueOf(id), null);
        //db.close();
        return cnt!=0;
    }

    public int clearPhotos(){
        SQLiteDatabase db = this.getWritableDatabase();
        //Log.d(TAG, "delPhoto: "+db.isOpen());
        return db.delete("photos", null, null);
    }

}
