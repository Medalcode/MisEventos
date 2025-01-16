package com.example.miseventos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDActivity extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eventos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_USUARIO_ID = "id";
    public static final String COLUMN_USUARIO_NOMBRE = "nombre";
    public static final String COLUMN_USUARIO_CONTRASENA = "contrasena";
    public static final String COLUMN_USUARIO_PISTA_RECUPERACION = "pista_recuperacion";

    public static final String TABLE_EVENTOS = "eventos";
    public static final String COLUMN_EVENTO_ID = "id";
    public static final String COLUMN_EVENTO_TITULO = "titulo";
    public static final String COLUMN_EVENTO_FECHA = "fecha";
    public static final String COLUMN_EVENTO_OBSERVACION = "observacion";
    public static final String COLUMN_EVENTO_LUGAR = "lugar";
    public static final String COLUMN_EVENTO_IMPORTANCIA = "importancia";
    public static final String COLUMN_EVENTO_USUARIO_ID = "usuario_id";

    public BDActivity(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsuariosTable = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USUARIO_NOMBRE + " TEXT, " +
                COLUMN_USUARIO_CONTRASENA + " TEXT, " +
                COLUMN_USUARIO_PISTA_RECUPERACION + " TEXT, " +
                "is_deleted INTEGER DEFAULT 0)";
        db.execSQL(createUsuariosTable);

        String createEventosTable = "CREATE TABLE " + TABLE_EVENTOS + " (" +
                COLUMN_EVENTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EVENTO_TITULO + " TEXT, " +
                COLUMN_EVENTO_FECHA + " TEXT, " +
                COLUMN_EVENTO_OBSERVACION + " TEXT, " +
                COLUMN_EVENTO_LUGAR + " TEXT, " +
                COLUMN_EVENTO_IMPORTANCIA + " TEXT, " +
                COLUMN_EVENTO_USUARIO_ID + " INTEGER, " +
                "is_deleted INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_EVENTO_USUARIO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_USUARIO_ID + "))";
        db.execSQL(createEventosTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTOS);
        onCreate(db);
    }

    public boolean addUsuario(String nombre, String contrasena, String pistaRecuperacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USUARIO_NOMBRE, nombre);
        values.put(COLUMN_USUARIO_CONTRASENA, contrasena);
        values.put(COLUMN_USUARIO_PISTA_RECUPERACION, pistaRecuperacion);
        long result = db.insert(TABLE_USUARIOS, null, values);
        return result != -1;
    }

    public boolean checkUsuario(String nombre, String contrasena) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_USUARIO_NOMBRE + " = ? AND " + COLUMN_USUARIO_CONTRASENA + " = ? AND is_deleted = 0";
        Cursor cursor = db.rawQuery(query, new String[]{nombre, contrasena});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public int getUsuarioId(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USUARIO_ID + " FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_USUARIO_NOMBRE + " = ? AND is_deleted = 0";
        Cursor cursor = db.rawQuery(query, new String[]{nombre});
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_USUARIO_ID);
            if (columnIndex != -1) {
                int usuarioId = cursor.getInt(columnIndex);
                cursor.close();
                return usuarioId;
            }
        }
        cursor.close();
        return -1;
    }

    public boolean addEvento(String titulo, String fecha, String observacion, String lugar, String importancia, int usuarioId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENTO_TITULO, titulo);
        values.put(COLUMN_EVENTO_FECHA, fecha);
        values.put(COLUMN_EVENTO_OBSERVACION, observacion);
        values.put(COLUMN_EVENTO_LUGAR, lugar);
        values.put(COLUMN_EVENTO_IMPORTANCIA, importancia);
        values.put(COLUMN_EVENTO_USUARIO_ID, usuarioId);
        long result = db.insert(TABLE_EVENTOS, null, values);
        return result != -1;
    }

    public Cursor getEventos(int usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTOS + " WHERE " + COLUMN_EVENTO_USUARIO_ID + " = ? AND is_deleted = 0";
        return db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
    }

    public boolean deleteUsuarioLogico(int usuarioId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_deleted", 1);
        int result = db.update(TABLE_USUARIOS, values, COLUMN_USUARIO_ID + " = ?", new String[]{String.valueOf(usuarioId)});
        return result > 0;
    }

    public boolean deleteEventoLogico(int eventoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_deleted", 1);
        int result = db.update(TABLE_EVENTOS, values, COLUMN_EVENTO_ID + " = ?", new String[]{String.valueOf(eventoId)});
        return result > 0;
    }

    public boolean deleteEventosLogicoPorUsuario(int usuarioId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_deleted", 1);
        int result = db.update(TABLE_EVENTOS, values, COLUMN_EVENTO_USUARIO_ID + " = ?", new String[]{String.valueOf(usuarioId)});
        return result > 0;
    }

    public Cursor getUsuariosActivos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIOS + " WHERE is_deleted = 0";
        return db.rawQuery(query, null);
    }

    public Cursor getEventosActivos(int usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTOS + " WHERE " + COLUMN_EVENTO_USUARIO_ID + " = ? AND is_deleted = 0";
        return db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
    }

    public boolean checkPistaRecuperacion(String nombre, String pistaRecuperacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_USUARIO_NOMBRE + " = ? AND " + COLUMN_USUARIO_PISTA_RECUPERACION + " = ? AND is_deleted = 0";
        Cursor cursor = db.rawQuery(query, new String[]{nombre, pistaRecuperacion});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean updateContrasena(String nombre, String nuevaContrasena) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USUARIO_CONTRASENA, nuevaContrasena);
        int result = db.update(TABLE_USUARIOS, values, COLUMN_USUARIO_NOMBRE + " = ?", new String[]{nombre});
        return result > 0;
    }
}