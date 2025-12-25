# 🚀 GUÍA DE COMPILACIÓN Y TESTING - MISEVENTOS

## 📋 Requisitos Previos

- Android Studio Arctic Fox o superior
- JDK 8 o superior
- Android SDK API 24+ (Android 7.0+)
- Gradle 8.7 (incluido)

---

## 🔧 Compilar el Proyecto

### Opción 1: Desde Android Studio (Recomendado)

1. **Abrir el proyecto:**

   ```bash
   cd /home/medalcode/Antigravity/MisEventos
   studio .
   ```

2. **Sincronizar Gradle:**

   - Android Studio lo hará automáticamente
   - O manualmente: `File > Sync Project with Gradle Files`

3. **Compilar:**
   - Click en `Build > Make Project`
   - O presiona `Ctrl + F9`

### Opción 2: Desde Terminal

```bash
# Navegar al proyecto
cd /home/medalcode/Antigravity/MisEventos

# Dar permisos a Gradle (solo la primera vez)
chmod +x gradlew

# Limpiar y compilar
./gradlew clean build

# Solo compilar sin tests
./gradlew assembleDebug
```

---

## 🧪 Ejecutar Tests

### Tests Unitarios

```bash
# Ejecutar todos los tests unitarios
./gradlew test

# Ejecutar tests y generar reporte
./gradlew test --info

# Ver reporte HTML
xdg-open app/build/reports/tests/testDebugUnitTest/index.html
```

### Tests Específicos

```bash
# Solo tests de PasswordHasher
./gradlew test --tests "com.example.miseventos.utils.PasswordHasherTest"

# Solo tests de InputValidator
./gradlew test --tests "com.example.miseventos.utils.InputValidatorTest"

# Solo tests de Evento
./gradlew test --tests "com.example.miseventos.models.EventoTest"
```

### Tests de Instrumentación (en Emulador/Dispositivo)

```bash
# Iniciar emulador o conectar dispositivo físico

# Ejecutar tests de instrumentación
./gradlew connectedAndroidTest

# Ver reporte
xdg-open app/build/reports/androidTests/connected/index.html
```

---

## 📱 Ejecutar la Aplicación

### Desde Android Studio

1. Click en el botón `Run` (▶️) o presiona `Shift + F10`
2. Selecciona un dispositivo/emulador
3. La app se instalará y ejecutará automáticamente

### Desde Terminal

```bash
# Instalar en dispositivo conectado
./gradlew installDebug

# Ejecutar después de instalar
adb shell am start -n com.example.miseventos/.MainActivity
```

---

## 📊 Verificar Cobertura de Tests

```bash
# Ejecutar tests con cobertura
./gradlew jacocoTestReport

# Ver reporte de cobertura
xdg-open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

---

## 🔍 Análisis Estático de Código

### Lint

```bash
# Ejecutar Lint
./gradlew lint

# Ver reporte
xdg-open app/build/reports/lint-results-debug.html
```

### Verificar Dependencias

```bash
# Listar dependencias
./gradlew dependencies

# Verificar actualizaciones
./gradlew dependencyUpdates
```

---

## 📦 Generar APK

### Debug APK

```bash
# Generar APK de debug
./gradlew assembleDebug

# Ubicación:
# app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (Firmado)

```bash
# Generar APK de release
./gradlew assembleRelease

# Ubicación:
# app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## 🧹 Limpiar Proyecto

```bash
# Limpiar build
./gradlew clean

# Limpiar completamente (incluye cache)
./gradlew clean --refresh-dependencies
```

---

## 🐛 Solución de Problemas

### Problema: "Permission denied" al ejecutar gradlew

**Solución:**

```bash
chmod +x gradlew
```

### Problema: Tests fallan con error de BCrypt

**Solución:**
Asegúrate de que la dependencia de BCrypt esté en `build.gradle.kts`:

```kotlin
implementation("at.favre.lib:bcrypt:0.10.2")
```

### Problema: No se encuentra el DatePicker

**Solución:**
Sincroniza el proyecto en Android Studio: `File > Sync Project with Gradle Files`

### Problema: RecyclerView no se muestra

**Solución:**
Verifica que la dependencia esté agregada:

```kotlin
implementation("androidx.recyclerview:recyclerview:1.3.2")
```

---

## ✅ Checklist de Verificación

Antes de considerar la app lista para producción:

- [ ] Todos los tests pasan (`./gradlew test`)
- [ ] Lint no muestra errores críticos (`./gradlew lint`)
- [ ] La app compila sin warnings (`./gradlew build`)
- [ ] La app se ejecuta en dispositivo/emulador
- [ ] Todas las funcionalidades probadas manualmente:
  - [ ] Registro de usuario
  - [ ] Login
  - [ ] Crear evento
  - [ ] Editar evento
  - [ ] Eliminar evento
  - [ ] Búsqueda de eventos
  - [ ] Filtros por importancia
  - [ ] Recuperación de contraseña
  - [ ] Cambio de contraseña
  - [ ] Eliminación de cuenta

---

## 📈 Resultados Esperados

### Tests Unitarios

```
✅ PasswordHasherTest:      9/9 tests passing
✅ InputValidatorTest:     27/27 tests passing
✅ EventoTest:              7/7 tests passing
───────────────────────────────────────────
✅ TOTAL:                  43/43 tests passing (100%)
```

### Compilación

```
BUILD SUCCESSFUL in 30s
```

### Lint

```
No issues found (or only minor warnings)
```

---

## 🎯 Comandos Rápidos

```bash
# Full check (compilar + tests + lint)
./gradlew clean build test lint

# Solo verificar que compile
./gradlew assembleDebug

# Solo tests
./gradlew test

# Instalar y ejecutar
./gradlew installDebug && adb shell am start -n com.example.miseventos/.MainActivity
```

---

## 📚 Recursos Adicionales

- [Documentación de Gradle](https://docs.gradle.org/)
- [Android Testing Guide](https://developer.android.com/training/testing)
- [JUnit 4 Documentation](https://junit.org/junit4/)
- [Mockito Guide](https://site.mockito.org/)

---

**Última actualización:** 25/12/2025  
**Versión:** 1.0
