package com.example.miseventos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.miseventos.models.Evento;
import com.example.miseventos.utils.PasswordHasher;

import java.util.ArrayList;
import java.util.List;

public class BDActivity extends SQLiteOpenHelper {

    private static final String TAG = "BDActivity";
    private static final String DATABASE_NAME = "eventos.db";
    private static final int DATABASE_VERSION = 2;  // Incrementado para migración

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
                COLUMN_USUARIO_NOMBRE + " TEXT UNIQUE NOT NULL, " +
                COLUMN_USUARIO_CONTRASENA + " TEXT NOT NULL, " +
                COLUMN_USUARIO_PISTA_RECUPERACION + " TEXT, " +
                "is_deleted INTEGER DEFAULT 0)";
        db.execSQL(createUsuariosTable);

        String createEventosTable = "CREATE TABLE " + TABLE_EVENTOS + " (" +
                COLUMN_EVENTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EVENTO_TITULO + " TEXT NOT NULL, " +
                COLUMN_EVENTO_FECHA + " TEXT NOT NULL, " +
                COLUMN_EVENTO_OBSERVACION + " TEXT, " +
                COLUMN_EVENTO_LUGAR + " TEXT, " +
                COLUMN_EVENTO_IMPORTANCIA + " TEXT, " +
                COLUMN_EVENTO_USUARIO_ID + " INTEGER NOT NULL, " +
                "is_deleted INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_EVENTO_USUARIO_ID + ") REFERENCES " + 
                TABLE_USUARIOS + "(" + COLUMN_USUARIO_ID + "))";
        db.execSQL(createEventosTable);
        
        // Crear índices para mejorar rendimiento
        db.execSQL("CREATE INDEX idx_usuarios_nombre ON " + TABLE_USUARIOS + "(" + COLUMN_USUARIO_NOMBRE + ")");
        db.execSQL("CREATE INDEX idx_eventos_usuario ON " + TABLE_EVENTOS + "(" + COLUMN_EVENTO_USUARIO_ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Migración de versión 1 a 2
            // Nota: En producción, deberías migrar los datos existentes
            Log.w(TAG, "Actualizando base de datos de versión " + oldVersion + " a " + newVersion);
            
            // Por ahora, recreamos las tablas (perderemos datos)
            // TODO: Implementar migración real si hay datos importantes
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTOS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
            onCreate(db);
        }
    }

    // ==================== MÉTODOS DE USUARIOS ====================
    
    /**
     * Agrega un nuevo usuario con contraseña hasheada
     */
    public boolean addUsuario(String nombre, String contrasena, String pistaRecuperacion) {
        try {
            // Verificar si el usuario ya existe
            if (existeUsuario(nombre)) {
                Log.w(TAG, "Usuario ya existe: " + nombre);
                return false;
            }
            
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USUARIO_NOMBRE, nombre);
            values.put(COLUMN_USUARIO_CONTRASENA, PasswordHasher.hashPassword(contrasena));
            values.put(COLUMN_USUARIO_PISTA_RECUPERACION, pistaRecuperacion);
            
            long result = db.insert(TABLE_USUARIOS, null, values);
            
            if (result != -1) {
                Log.i(TAG, "Usuario registrado exitosamente: " + nombre);
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error al agregar usuario", e);
            return false;
        }
    }

    /**
     * Verifica si existe un usuario con el nombre dado
     */
    private boolean existeUsuario(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIOS + 
                       " WHERE " + COLUMN_USUARIO_NOMBRE + " = ? AND is_deleted = 0";
        Cursor cursor = db.rawQuery(query, new String[]{nombre});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    /**
     * Verifica credenciales de usuario usando hash
     */
    public boolean checkUsuario(String nombre, String contrasena) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT " + COLUMN_USUARIO_CONTRASENA + " FROM " + TABLE_USUARIOS + 
                           " WHERE " + COLUMN_USUARIO_NOMBRE + " = ? AND is_deleted = 0";
            Cursor cursor = db.rawQuery(query, new String[]{nombre});
            
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_USUARIO_CONTRASENA);
                if (columnIndex != -1) {
                    String storedHash = cursor.getString(columnIndex);
                    cursor.close();
                    return PasswordHasher.verifyPassword(contrasena, storedHash);
                }
            }
            cursor.close();
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar usuario", e);
            return false;
        }
    }

    public int getUsuarioId(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USUARIO_ID + " FROM " + TABLE_USUARIOS + 
                       " WHERE " + COLUMN_USUARIO_NOMBRE + " = ? AND is_deleted = 0";
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

    public boolean checkPistaRecuperacion(String nombre, String pistaRecuperacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIOS + 
                       " WHERE " + COLUMN_USUARIO_NOMBRE + " = ? AND " + 
                       COLUMN_USUARIO_PISTA_RECUPERACION + " = ? AND is_deleted = 0";
        Cursor cursor = db.rawQuery(query, new String[]{nombre, pistaRecuperacion});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean updateContrasena(String nombre, String nuevaContrasena) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USUARIO_CONTRASENA, PasswordHasher.hashPassword(nuevaContrasena));
            
            int result = db.update(TABLE_USUARIOS, values, 
                                  COLUMN_USUARIO_NOMBRE + " = ?", 
                                  new String[]{nombre});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar contraseña", e);
            return false;
        }
    }

    public boolean deleteUsuarioLogico(int usuarioId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_deleted", 1);
        int result = db.update(TABLE_USUARIOS, values, 
                              COLUMN_USUARIO_ID + " = ?", 
                              new String[]{String.valueOf(usuarioId)});
        return result > 0;
    }

    // ==================== MÉTODOS DE EVENTOS ====================
    
    public boolean addEvento(String titulo, String fecha, String observacion, 
                            String lugar, String importancia, int usuarioId) {
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Error al agregar evento", e);
            return false;
        }
    }

    /**
     * NUEVO: Actualiza un evento existente
     */
    public boolean updateEvento(int eventoId, String titulo, String fecha, 
                               String observacion, String lugar, String importancia) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_EVENTO_TITULO, titulo);
            values.put(COLUMN_EVENTO_FECHA, fecha);
            values.put(COLUMN_EVENTO_OBSERVACION, observacion);
            values.put(COLUMN_EVENTO_LUGAR, lugar);
            values.put(COLUMN_EVENTO_IMPORTANCIA, importancia);
            
            int result = db.update(TABLE_EVENTOS, values, 
                                  COLUMN_EVENTO_ID + " = ? AND is_deleted = 0", 
                                  new String[]{String.valueOf(eventoId)});
            
            if (result > 0) {
                Log.i(TAG, "Evento actualizado: ID " + eventoId);
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar evento", e);
            return false;
        }
    }

    /**
     * NUEVO: Obtiene un evento por ID
     */
    public Evento getEventoById(int eventoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTOS + 
                       " WHERE " + COLUMN_EVENTO_ID + " = ? AND is_deleted = 0";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(eventoId)});
        
        Evento evento = null;
        if (cursor.moveToFirst()) {
            evento = cursorToEvento(cursor);
        }
        cursor.close();
        return evento;
    }

    public Cursor getEventos(int usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTOS + 
                       " WHERE " + COLUMN_EVENTO_USUARIO_ID + " = ? AND is_deleted = 0 " +
                       "ORDER BY fecha DESC";
        return db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
    }

    /**
     * NUEVO: Obtiene eventos como lista de objetos
     */
    public List<Evento> getEventosAsList(int usuarioId) {
        List<Evento> eventos = new ArrayList<>();
        Cursor cursor = getEventos(usuarioId);
        
        if (cursor.moveToFirst()) {
            do {
                Evento evento = cursorToEvento(cursor);
                if (evento != null) {
                    eventos.add(evento);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventos;
    }

    /**
     * NUEVO: Convierte un cursor a objeto Evento
     */
    private Evento cursorToEvento(Cursor cursor) {
        try {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_EVENTO_ID));
            String titulo = cursor.getString(cursor.getColumnIndex(COLUMN_EVENTO_TITULO));
            String fecha = cursor.getString(cursor.getColumnIndex(COLUMN_EVENTO_FECHA));
            String observacion = cursor.getString(cursor.getColumnIndex(COLUMN_EVENTO_OBSERVACION));
            String lugar = cursor.getString(cursor.getColumnIndex(COLUMN_EVENTO_LUGAR));
            String importancia = cursor.getString(cursor.getColumnIndex(COLUMN_EVENTO_IMPORTANCIA));
            int usuarioId = cursor.getInt(cursor.getColumnIndex(COLUMN_EVENTO_USUARIO_ID));
            
            return new Evento(id, titulo, fecha, observacion, lugar, importancia, usuarioId);
        } catch (Exception e) {
            Log.e(TAG, "Error al convertir cursor a evento", e);
            return null;
        }
    }

    public boolean deleteEventoLogico(int eventoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_deleted", 1);
        int result = db.update(TABLE_EVENTOS, values, 
                              COLUMN_EVENTO_ID + " = ?", 
                              new String[]{String.valueOf(eventoId)});
        
        if (result > 0) {
            Log.i(TAG, "Evento eliminado lógicamente: ID " + eventoId);
        }
        return result > 0;
    }

    public boolean deleteEventosLogicoPorUsuario(int usuarioId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_deleted", 1);
        int result = db.update(TABLE_EVENTOS, values, 
                              COLUMN_EVENTO_USUARIO_ID + " = ?", 
                              new String[]{String.valueOf(usuarioId)});
        return result > 0;
    }

    /**
     * NUEVO: Busca eventos por título o observación
     */
    public List<Evento> searchEventos(int usuarioId, String searchQuery) {
        List<Evento> eventos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_EVENTOS + 
                       " WHERE " + COLUMN_EVENTO_USUARIO_ID + " = ? " +
                       "AND is_deleted = 0 " +
                       "AND (" + COLUMN_EVENTO_TITULO + " LIKE ? OR " + 
                       COLUMN_EVENTO_OBSERVACION + " LIKE ?) " +
                       "ORDER BY fecha DESC";
        
        String likePattern = "%" + searchQuery + "%";
        Cursor cursor = db.rawQuery(query, 
            new String[]{String.valueOf(usuarioId), likePattern, likePattern});
        
        if (cursor.moveToFirst()) {
            do {
                Evento evento = cursorToEvento(cursor);
                if (evento != null) {
                    eventos.add(evento);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventos;
    }

    /**
     * NUEVO: Filtra eventos por importancia
     */
    public List<Evento> getEventosByImportancia(int usuarioId, String importancia) {
        List<Evento> eventos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM " + TABLE_EVENTOS + 
                       " WHERE " + COLUMN_EVENTO_USUARIO_ID + " = ? " +
                       "AND " + COLUMN_EVENTO_IMPORTANCIA + " = ? " +
                       "AND is_deleted = 0 " +
                       "ORDER BY fecha DESC";
        
        Cursor cursor = db.rawQuery(query, 
            new String[]{String.valueOf(usuarioId), importancia});
        
        if (cursor.moveToFirst()) {
            do {
                Evento evento = cursorToEvento(cursor);
                if (evento != null) {
                    eventos.add(evento);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventos;
    }
}