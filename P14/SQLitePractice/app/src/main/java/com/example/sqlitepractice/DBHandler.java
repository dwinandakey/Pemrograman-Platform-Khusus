package com.example.sqlitepractice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "mahasiswadb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME =
            "mahasiswatbl";
    private static final String ID_COL = "id";
    private static final String NIM_COL = "nim";
    private static final String NAMA_COL = "nama";
    private static final String KELAS_COL = "kelas";
    private static final String NOHP_COL = "nohp";

    public DBHandler(@Nullable Context context, @Nullable
    String name, @Nullable SQLiteDatabase.CursorFactory
                             factory, int version) {
        super(context, name, factory, version);
    }

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NIM_COL + " TEXT,"
                + NAMA_COL + " TEXT,"
                + KELAS_COL + " TEXT,"
                + NOHP_COL + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int
            oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addNewMahasiswa(String nim, String nama,
                                String kelas, String nohp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NIM_COL, nim);
        values.put(NAMA_COL, nama);
        values.put(KELAS_COL, kelas);
        values.put(NOHP_COL, nohp);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<MahasiswaModal> readMahasiswa() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +
                TABLE_NAME, null);

        ArrayList<MahasiswaModal> mahasiswaModalArrayList
                = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                mahasiswaModalArrayList.add(new
                        MahasiswaModal(cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mahasiswaModalArrayList;
    }

    public void updateMahasiswa(String originalnim,
                                String nim, String nama,
                                String kelas, String nohp) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NIM_COL, nim);
        values.put(NAMA_COL, nama);
        values.put(KELAS_COL, kelas);
        values.put(NOHP_COL, nohp);

        db.update(TABLE_NAME, values, "nim=?", new
                String[]{originalnim});
        db.close();
    }

    public void deleteMahasiswa(String nim) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "nim=?", new
                String[]{nim});
        db.close();
    }
}