# 🔧 ANÁLISIS TÉCNICO DETALLADO - MISEVENTOS

## Especificaciones Técnicas y Mejoras Recomendadas

---

## 📊 TABLA DE CONTENIDOS

1. [Análisis de la Base de Datos](#-análisis-de-la-base-de-datos)
2. [Análisis de Código](#-análisis-de-código)
3. [Mejoras de Seguridad](#-mejoras-de-seguridad)
4. [Optimizaciones de Rendimiento](#-optimizaciones-de-rendimiento)
5. [Refactoring Sugerido](#-refactoring-sugerido)
6. [Guía de Migración a Arquitectura Moderna](#-guía-de-migración-a-arquitectura-moderna)

---

## 💾 ANÁLISIS DE LA BASE DE DATOS

### Esquema Actual

#### Tabla: usuarios

```sql
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT,
    contrasena TEXT,
    pista_recuperacion TEXT,
    is_deleted INTEGER DEFAULT 0
);
```

**Análisis:**

- ✅ **Bueno:** Uso de AUTOINCREMENT para IDs
- ✅ **Bueno:** Implementación de borrado lógico
- ❌ **Malo:** Sin índices en campos de búsqueda
- ❌ **Malo:** Sin restricción UNIQUE en nombre
- ❌ **Crítico:** Contraseñas sin hash
- ⚠️ **Mejorable:** Sin timestamps (created_at, updated_at)

**Mejoras Recomendadas:**

```sql
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    contrasena_hash TEXT NOT NULL,
    pista_recuperacion TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME,
    is_deleted INTEGER DEFAULT 0,
    deleted_at DATETIME
);

CREATE INDEX idx_usuarios_nombre ON usuarios(nombre) WHERE is_deleted = 0;
CREATE INDEX idx_usuarios_deleted ON usuarios(is_deleted);
```

#### Tabla: eventos

```sql
CREATE TABLE eventos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT,
    fecha TEXT,
    observacion TEXT,
    lugar TEXT,
    importancia TEXT,
    usuario_id INTEGER,
    is_deleted INTEGER DEFAULT 0,
    FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
);
```

**Análisis:**

- ✅ **Bueno:** Foreign Key implementada
- ✅ **Bueno:** Borrado lógico
- ❌ **Malo:** Fecha como TEXT sin validación
- ❌ **Malo:** Sin índices en usuario_id y fecha
- ⚠️ **Mejorable:** Importancia como TEXT libre
- ⚠️ **Mejorable:** Sin timestamps de auditoría

**Mejoras Recomendadas:**

```sql
CREATE TABLE eventos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT NOT NULL,
    fecha DATE NOT NULL,  -- o INTEGER con timestamp UNIX
    hora TIME,
    observacion TEXT,
    lugar TEXT,
    importancia INTEGER NOT NULL CHECK(importancia IN (1, 2, 3)),  -- 1=Baja, 2=Media, 3=Alta
    usuario_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_deleted INTEGER DEFAULT 0,
    deleted_at DATETIME,
    FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE INDEX idx_eventos_usuario ON eventos(usuario_id, is_deleted);
CREATE INDEX idx_eventos_fecha ON eventos(fecha) WHERE is_deleted = 0;
CREATE INDEX idx_eventos_importancia ON eventos(importancia) WHERE is_deleted = 0;
```

### Tablas Adicionales Recomendadas

#### Tabla: categorias

```sql
CREATE TABLE categorias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    color TEXT,  -- Código hexadecimal
    icono TEXT,  -- Nombre del recurso drawable
    usuario_id INTEGER NOT NULL,
    FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
```

#### Tabla: etiquetas (Tags)

```sql
CREATE TABLE etiquetas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    color TEXT,
    usuario_id INTEGER NOT NULL,
    FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE eventos_etiquetas (
    evento_id INTEGER NOT NULL,
    etiqueta_id INTEGER NOT NULL,
    PRIMARY KEY(evento_id, etiqueta_id),
    FOREIGN KEY(evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    FOREIGN KEY(etiqueta_id) REFERENCES etiquetas(id) ON DELETE CASCADE
);
```

#### Tabla: recordatorios

```sql
CREATE TABLE recordatorios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    evento_id INTEGER NOT NULL,
    minutos_antes INTEGER NOT NULL,  -- 30, 60, 1440 (1 día), etc.
    notificacion_enviada INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(evento_id) REFERENCES eventos(id) ON DELETE CASCADE
);
```

### Plan de Migración de BD

#### Versión 2 (Mejoras Críticas)

```java
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion < 2) {
        // Migración de contraseñas a hash (requiere lógica especial)
        db.execSQL("ALTER TABLE usuarios ADD COLUMN contrasena_hash TEXT");
        db.execSQL("ALTER TABLE usuarios ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP");
        db.execSQL("ALTER TABLE usuarios ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP");

        // Agregar índices
        db.execSQL("CREATE INDEX idx_usuarios_nombre ON usuarios(nombre) WHERE is_deleted = 0");

        // Mejorar tabla eventos
        db.execSQL("ALTER TABLE eventos ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP");
        db.execSQL("ALTER TABLE eventos ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP");
        db.execSQL("CREATE INDEX idx_eventos_usuario ON eventos(usuario_id, is_deleted)");
    }
}
```

---

## 🔍 ANÁLISIS DE CÓDIGO

### MainActivity.java

**Problemas Identificados:**

1. **Lógica de negocio en Activity**

   - Violación de Single Responsibility Principle
   - Dificulta testing

2. **Sin manejo de ciclo de vida**

   ```java
   // Actual - SE PIERDE EN ROTACIÓN
   etRegUser.getText().toString()

   // Recomendado - Con ViewModel
   viewModel.getUsernameLiveData().observe(...)
   ```

3. **Toast para todos los mensajes**
   - Usar Snackbar para mensajes con acciones
   - Material Design recomienda Snackbar

**Refactoring Sugerido:**

```java
// MainActivity.java (Vista)
public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setupObservers();
        setupListeners();
    }

    private void setupObservers() {
        viewModel.getLoginResult().observe(this, result -> {
            if (result.isSuccess()) {
                navigateToEvents(result.getUser());
            } else {
                showError(result.getError());
            }
        });
    }
}

// MainViewModel.java (Lógica)
public class MainViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public MainViewModel(UserRepository repository) {
        this.repository = repository;
    }

    public void login(String username, String password) {
        if (validateInput(username, password)) {
            repository.login(username, password, new Callback<User>() {
                @Override
                public void onSuccess(User user) {
                    loginResult.setValue(LoginResult.success(user));
                }

                @Override
                public void onError(String error) {
                    loginResult.setValue(LoginResult.error(error));
                }
            });
        }
    }
}

// UserRepository.java (Datos)
public class UserRepository {
    private final UserDao userDao;

    public void login(String username, String password, Callback<User> callback) {
        // Lógica de acceso a datos
    }
}
```

### BDActivity.java

**Problemas Identificados:**

1. **Acceso directo a SQLite**

   - Room sería más robusto
   - Compilación verificada de queries

2. **Sin manejo de errores robusto**

   ```java
   // Actual
   long result = db.insert(TABLE_USUARIOS, null, values);
   return result != -1;

   // Recomendado
   try {
       long result = db.insert(TABLE_USUARIOS, null, values);
       if (result == -1) {
           Log.e(TAG, "Failed to insert user");
           return false;
       }
       return true;
   } catch (SQLiteException e) {
       Log.e(TAG, "SQLite error: " + e.getMessage(), e);
       return false;
   }
   ```

3. **Queries no preparadas para concurrencia**
   - Sin synchronized
   - Potenciales race conditions

**Migración a Room Recomendada:**

```java
// Usuario.java (Entity)
@Entity(tableName = "usuarios",
        indices = {@Index(value = "nombre", unique = true)})
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "nombre")
    @NonNull
    private String nombre;

    @ColumnInfo(name = "contrasena_hash")
    @NonNull
    private String contrasenaHash;

    @ColumnInfo(name = "pista_recuperacion")
    private String pistaRecuperacion;

    @ColumnInfo(name = "is_deleted")
    private boolean isDeleted = false;

    // Getters y Setters
}

// UsuarioDao.java (DAO)
@Dao
public interface UsuarioDao {
    @Insert
    long insert(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE nombre = :nombre AND contrasena_hash = :passwordHash AND is_deleted = 0")
    Usuario login(String nombre, String passwordHash);

    @Query("UPDATE usuarios SET is_deleted = 1 WHERE id = :usuarioId")
    int deleteLogico(int usuarioId);

    @Query("SELECT * FROM usuarios WHERE nombre = :nombre AND is_deleted = 0")
    LiveData<Usuario> getUsuarioPorNombre(String nombre);
}

// AppDatabase.java
@Database(entities = {Usuario.class, Evento.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract EventoDao eventoDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "eventos_database"
                    )
                    .addMigrations(MIGRATION_1_2)
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Lógica de migración
        }
    };
}
```

### EventosActivity.java

**Problemas Identificados:**

1. **ListView obsoleto**

   - RecyclerView es el estándar
   - Mejor rendimiento con muchos items

2. **ArrayAdapter básico**

   - Sin ViewHolder pattern
   - Sin diseño personalizado

3. **Actualización completa de la lista**
   - Ineficiente, recarga todo
   - DiffUtil sería mejor

**Refactoring con RecyclerView:**

```java
// EventoViewHolder.java
public class EventoViewHolder extends RecyclerView.ViewHolder {
    private final ItemEventoBinding binding;

    public EventoViewHolder(ItemEventoBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Evento evento) {
        binding.tvTitulo.setText(evento.getTitulo());
        binding.tvFecha.setText(evento.getFecha());
        binding.tvLugar.setText(evento.getLugar());
        binding.tvObservacion.setText(evento.getObservacion());

        int importanciaColor = getImportanciaColor(evento.getImportancia());
        binding.ivImportancia.setColorFilter(importanciaColor);
    }
}

// EventoAdapter.java
public class EventoAdapter extends RecyclerView.Adapter<EventoViewHolder> {
    private List<Evento> eventos = new ArrayList<>();
    private final OnEventoClickListener listener;

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventoBinding binding = ItemEventoBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false
        );
        return new EventoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        holder.bind(eventos.get(position));
        holder.itemView.setOnClickListener(v ->
            listener.onEventoClick(eventos.get(position))
        );
    }

    public void submitList(List<Evento> newEventos) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new EventoDiffCallback(this.eventos, newEventos)
        );
        this.eventos = newEventos;
        diffResult.dispatchUpdatesTo(this);
    }
}
```

---

## 🔒 MEJORAS DE SEGURIDAD

### 1. Implementación de Hashing de Contraseñas

#### Opción 1: BCrypt (Recomendado)

```gradle
// build.gradle
dependencies {
    implementation 'at.favre.lib:bcrypt:0.10.2'
}
```

```java
// PasswordHasher.java
public class PasswordHasher {
    private static final int BCRYPT_COST = 12;

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
    }

    public static boolean verifyPassword(String password, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hash);
        return result.verified;
    }
}

// Uso en MainActivity
private void registrarUsuario() {
    String nombre = etRegUser.getText().toString();
    String password = etRegPass.getText().toString();
    String pistaRecuperacion = etPistaRecu1.getText().toString();

    if (validateInputs(nombre, password, pistaRecuperacion)) {
        String passwordHash = PasswordHasher.hashPassword(password);
        if (dbHelper.addUsuario(nombre, passwordHash, pistaRecuperacion)) {
            showSuccess(R.string.usuario_registrado_exitosamente);
        }
    }
}

private void loginUsuario() {
    String nombre = etLogUser.getText().toString();
    String password = etLogPass.getText().toString();

    if (validateInputs(nombre, password)) {
        Usuario usuario = dbHelper.getUsuarioPorNombre(nombre);
        if (usuario != null && PasswordHasher.verifyPassword(password, usuario.getContrasenaHash())) {
            navigateToEvents(usuario);
        } else {
            showError(R.string.usuario_o_contrasena_incorrectos);
        }
    }
}
```

#### Opción 2: Android Keystore + AES

```java
// Para datos muy sensibles
public class SecureStorage {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "MisEventosKey";

    public static String encrypt(String plainText) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);

        SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        if (secretKey == null) {
            secretKey = generateKey();
        }

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] iv = cipher.getIV();
        byte[] encryption = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeToString(iv, Base64.DEFAULT) + ":" +
               Base64.encodeToString(encryption, Base64.DEFAULT);
    }

    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE
        );

        keyGenerator.init(
            new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        );

        return keyGenerator.generateKey();
    }
}
```

### 2. Validaciones de Entrada

```java
// InputValidator.java
public class InputValidator {

    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.error("El nombre de usuario es obligatorio");
        }

        if (username.length() < 3) {
            return ValidationResult.error("El nombre debe tener al menos 3 caracteres");
        }

        if (username.length() > 50) {
            return ValidationResult.error("El nombre no puede exceder 50 caracteres");
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return ValidationResult.error("Solo letras, números y guion bajo permitidos");
        }

        return ValidationResult.success();
    }

    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.error("La contraseña es obligatoria");
        }

        if (password.length() < 8) {
            return ValidationResult.error("La contraseña debe tener al menos 8 caracteres");
        }

        if (!password.matches(".*[A-Z].*")) {
            return ValidationResult.error("Debe contener al menos una mayúscula");
        }

        if (!password.matches(".*[a-z].*")) {
            return ValidationResult.error("Debe contener al menos una minúscula");
        }

        if (!password.matches(".*\\d.*")) {
            return ValidationResult.error("Debe contener al menos un número");
        }

        return ValidationResult.success();
    }

    public static ValidationResult validateDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            Date date = sdf.parse(dateStr);

            if (date == null) {
                return ValidationResult.error("Fecha inválida");
            }

            return ValidationResult.success();
        } catch (ParseException e) {
            return ValidationResult.error("Formato de fecha inválido. Use YYYY-MM-DD");
        }
    }
}
```

### 3. Protección contra Inyección SQL

```java
// Actual - YA ESTÁ BIEN (usa placeholders)
String query = "SELECT * FROM usuarios WHERE nombre = ? AND contrasena = ?";
Cursor cursor = db.rawQuery(query, new String[]{nombre, contrasena});

// NUNCA hacer esto:
// String query = "SELECT * FROM usuarios WHERE nombre = '" + nombre + "'"; // ❌ VULNERABLE
```

---

## ⚡ OPTIMIZACIONES DE RENDIMIENTO

### 1. Operaciones de BD en Background

```java
// Usar AsyncTask (deprecated pero funcional) o Executors
public class DatabaseExecutor {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void execute(Runnable task) {
        executor.execute(task);
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }
}

// Uso
private void loginUsuario() {
    String nombre = etLogUser.getText().toString();
    String password = etLogPass.getText().toString();

    showLoading(true);

    DatabaseExecutor.execute(() -> {
        boolean loginSuccess = dbHelper.checkUsuario(nombre, password);

        runOnUiThread(() -> {
            showLoading(false);
            if (loginSuccess) {
                navigateToEvents();
            } else {
                showError(R.string.usuario_o_contrasena_incorrectos);
            }
        });
    });
}
```

### 2. Caché de Queries Frecuentes

```java
public class EventoCache {
    private static final LruCache<Integer, List<Evento>> cache =
        new LruCache<>(100); // Máximo 100 entradas

    public static List<Evento> getEventos(int usuarioId, BDActivity db) {
        List<Evento> cached = cache.get(usuarioId);
        if (cached != null) {
            return cached;
        }

        List<Evento> eventos = db.getEventosAsList(usuarioId);
        cache.put(usuarioId, eventos);
        return eventos;
    }

    public static void invalidate(int usuarioId) {
        cache.remove(usuarioId);
    }
}
```

### 3. Paginación de Eventos

```java
public Cursor getEventosPaginados(int usuarioId, int limit, int offset) {
    SQLiteDatabase db = this.getReadableDatabase();
    String query = "SELECT * FROM " + TABLE_EVENTOS +
                   " WHERE " + COLUMN_EVENTO_USUARIO_ID + " = ? AND is_deleted = 0" +
                   " ORDER BY fecha DESC" +
                   " LIMIT ? OFFSET ?";
    return db.rawQuery(query, new String[]{
        String.valueOf(usuarioId),
        String.valueOf(limit),
        String.valueOf(offset)
    });
}
```

---

## 🔄 REFACTORING SUGERIDO

### Arquitectura MVVM Completa

```
app/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   ├── UsuarioDao.java
│   │   │   └── EventoDao.java
│   │   ├── database/
│   │   │   └── AppDatabase.java
│   │   └── entities/
│   │       ├── Usuario.java
│   │       └── Evento.java
│   ├── repository/
│   │   ├── UsuarioRepository.java
│   │   └── EventoRepository.java
│   └── models/
│       └── (DTOs si es necesario)
├── ui/
│   ├── main/
│   │   ├── MainActivity.java
│   │   └── MainViewModel.java
│   ├── eventos/
│   │   ├── EventosActivity.java
│   │   ├── EventosViewModel.java
│   │   └── EventoAdapter.java
│   └── common/
│       └── BaseActivity.java
└── utils/
    ├── PasswordHasher.java
    ├── InputValidator.java
    └── Constants.java
```

---

## 📋 CHECKLIST DE MIGRACIÓN

### Fase 1: Seguridad

- [ ] Implementar BCrypt para passwords
- [ ] Migrar contraseñas existentes a hash
- [ ] Agregar validaciones de entrada
- [ ] Implementar sesiones seguras

### Fase 2: Base de Datos

- [ ] Migrar a Room Database
- [ ] Agregar índices en tablas
- [ ] Implementar timestamps
- [ ] Agregar constraints

### Fase 3: Arquitectura

- [ ] Implementar MVVM
- [ ] Crear ViewModels
- [ ] Crear Repositories
- [ ] Agregar LiveData

### Fase 4: UI

- [ ] Migrar a RecyclerView
- [ ] Implementar ViewBinding
- [ ] Agregar Material Design 3
- [ ] Implementar animaciones

### Fase 5: Testing

- [ ] Tests unitarios ViewModels
- [ ] Tests de Repository
- [ ] Tests de DAO
- [ ] Tests de UI con Espresso

---

**Documento elaborado:** 25 de Diciembre de 2025  
**Versión:** 1.0  
**Estado:** Completo
