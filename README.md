# 📅 MisEventos / MyEvents

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![API](https://img.shields.io/badge/API-24%2B-brightgreen?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

**Aplicación Android para la gestión personal de eventos**

[Características](#-características) • [Instalación](#-instalación) • [Uso](#-uso) • [Tecnologías](#-tecnologías) • [Roadmap](#-roadmap)

</div>

---

## 📖 Descripción

**MisEventos** es una aplicación nativa Android que permite a los usuarios gestionar sus eventos personales de manera organizada y segura. Con un sistema completo de autenticación, los usuarios pueden crear, visualizar y organizar eventos con diferentes niveles de importancia.

### 🎯 Objetivo

Proporcionar una herramienta simple pero efectiva para que los usuarios puedan:

- Registrar y recordar eventos importantes
- Organizar eventos por nivel de importancia
- Mantener un historial personal de actividades
- Acceder a sus datos de manera segura y privada

---

## ✨ Características

### Gestión de Usuarios

- ✅ Registro de nuevos usuarios
- ✅ Sistema de login seguro
- ✅ Recuperación de contraseña mediante pista de seguridad
- ✅ Cambio de contraseña
- ✅ Eliminación de cuenta con borrado en cascada

### Gestión de Eventos

- ✅ Creación de eventos con información completa
- ✅ Visualización de eventos personales
- ✅ Clasificación por importancia (Baja, Media, Alta)
- ✅ Campos detallados: título, fecha, lugar, observaciones
- ✅ Aislamiento de datos por usuario

### Características Técnicas

- ✅ Base de datos SQLite local
- ✅ Borrado lógico para mantener integridad histórica
- ✅ Internacionalización (Español e Inglés)
- ✅ Material Design components
- ✅ Soporte para tema claro y oscuro

---

## 📱 Capturas de Pantalla

_Próximamente: Capturas de las pantallas principales_

---

## 🚀 Instalación

### Requisitos Previos

- Android Studio Arctic Fox o superior
- JDK 8 o superior
- Android SDK API 24 (Android 7.0) o superior
- Gradle 8.7

### Pasos de Instalación

1. **Clonar el repositorio**

```bash
git clone https://github.com/tuusuario/MisEventos.git
cd MisEventos
```

2. **Abrir en Android Studio**

```bash
# Opción 1: Desde la terminal
studio .

# Opción 2: Abrir Android Studio y seleccionar "Open an Existing Project"
```

3. **Configurar permisos de Gradle**

```bash
chmod +x gradlew
```

4. **Sincronizar el proyecto**

- Android Studio sincronizará automáticamente las dependencias
- Si no, ve a `File > Sync Project with Gradle Files`

5. **Ejecutar la aplicación**

- Conecta un dispositivo Android o inicia un emulador
- Click en el botón `Run` (▶️) o presiona `Shift + F10`

---

## 🎮 Uso

### Primera vez

1. **Registro**

   - Abre la aplicación
   - Completa el formulario de registro con:
     - Nombre de usuario
     - Contraseña
     - Pista de recuperación
   - Presiona "Registrar"

2. **Iniciar Sesión**
   - Ingresa tu nombre de usuario y contraseña
   - Presiona "Iniciar Sesión"

### Gestionar Eventos

1. **Crear un Evento**

   - Después del login, presiona "Crear Evento"
   - Completa todos los campos:
     - Título del evento
     - Fecha
     - Lugar
     - Observaciones
     - Nivel de importancia
   - Presiona "Crear Evento"

2. **Ver tus Eventos**
   - Los eventos se muestran automáticamente en la lista
   - Cada evento muestra toda su información

### Gestionar Perfil

1. **Cambiar Contraseña**

   - Desde la pantalla principal tras login
   - Ingresa contraseña actual
   - Ingresa nueva contraseña dos veces
   - Presiona "Cambiar Clave"

2. **Recuperar Contraseña**

   - Desde la pantalla de login, presiona "¿Olvidaste tu contraseña?"
   - Ingresa nombre de usuario y pista de recuperación
   - Establece nueva contraseña
   - Presiona "Recuperar Clave"

3. **Eliminar Cuenta**
   - Desde la pantalla de perfil
   - Presiona "Eliminar Cuenta"
   - **Advertencia:** Esto eliminará tu cuenta y todos tus eventos

---

## 🛠️ Tecnologías

### Lenguajes y Frameworks

- **Java 8** - Lenguaje de programación principal
- **Android SDK API 34** - Framework nativo de Android
- **Gradle 8.7** - Sistema de compilación

### Bibliotecas Principales

```gradle
androidx.appcompat:appcompat:1.7.0
com.google.android.material:material:1.12.0
androidx.activity:activity:1.9.3
androidx.constraintlayout:constraintlayout:2.2.0
```

### Base de Datos

- **SQLite** - Base de datos local embebida

### Herramientas de Desarrollo

- Android Studio
- Gradle Kotlin DSL
- Git para control de versiones

---

## 📂 Estructura del Proyecto

```
MisEventos/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/miseventos/
│   │   │   │   ├── MainActivity.java          # Pantalla de login/registro
│   │   │   │   ├── EventosActivity.java       # Gestión de eventos
│   │   │   │   ├── ClaveActivity.java         # Cambio de contraseña
│   │   │   │   ├── RecuperarActivity.java     # Recuperación de contraseña
│   │   │   │   ├── BDActivity.java            # Capa de acceso a datos
│   │   │   │   └── Usuario.java               # Modelo de usuario
│   │   │   ├── res/
│   │   │   │   ├── layout/                    # Archivos XML de interfaz
│   │   │   │   ├── values/                    # Recursos (ES)
│   │   │   │   └── values-en/                 # Recursos (EN)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                              # Tests unitarios
│   │   └── androidTest/                       # Tests de instrumentación
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── REPORTE_DESARROLLO.md                      # Reporte técnico detallado
└── README.md                                  # Este archivo
```

---

## 🗄️ Modelo de Datos

### Tabla: usuarios

| Campo              | Tipo    | Descripción                     |
| ------------------ | ------- | ------------------------------- |
| id                 | INTEGER | Identificador único (PK)        |
| nombre             | TEXT    | Nombre de usuario               |
| contrasena         | TEXT    | Contraseña del usuario          |
| pista_recuperacion | TEXT    | Pista para recuperar contraseña |
| is_deleted         | INTEGER | Flag de borrado lógico (0/1)    |

### Tabla: eventos

| Campo       | Tipo    | Descripción                     |
| ----------- | ------- | ------------------------------- |
| id          | INTEGER | Identificador único (PK)        |
| titulo      | TEXT    | Título del evento               |
| fecha       | TEXT    | Fecha del evento                |
| observacion | TEXT    | Observaciones adicionales       |
| lugar       | TEXT    | Lugar del evento                |
| importancia | TEXT    | Nivel: Baja, Media, Alta        |
| usuario_id  | INTEGER | ID del usuario propietario (FK) |
| is_deleted  | INTEGER | Flag de borrado lógico (0/1)    |

---

## 🧪 Testing

### Ejecutar Tests Unitarios

```bash
./gradlew test
```

### Ejecutar Tests de Instrumentación

```bash
./gradlew connectedAndroidTest
```

**Nota:** Actualmente no hay tests implementados. Ver [Roadmap](#-roadmap).

---

## 🚧 Roadmap

### ✅ Versión 1.0 (Actual)

- [x] Sistema de autenticación básico
- [x] CRUD de eventos (Create, Read)
- [x] Borrado lógico de cuentas
- [x] Soporte multiidioma (ES/EN)
- [x] UI funcional con Material Design

### 🔄 Versión 1.1 (Próxima)

- [ ] **CRÍTICO:** Implementar hashing de contraseñas
- [ ] Edición de eventos existentes
- [ ] Eliminación individual de eventos
- [ ] Validación de fechas con DatePicker
- [ ] Tests unitarios básicos

### 📋 Versión 1.2

- [ ] Rediseño UI con Material Design 3
- [ ] Búsqueda y filtros de eventos
- [ ] Ordenamiento por fecha/importancia
- [ ] Animaciones y transiciones
- [ ] Feedback visual mejorado

### 🎯 Versión 2.0

- [ ] Arquitectura MVVM
- [ ] Room Database
- [ ] Navigation Component
- [ ] Notificaciones de eventos
- [ ] Calendario integrado
- [ ] Exportar/Importar eventos

### 🚀 Versión 3.0

- [ ] Sincronización en la nube
- [ ] Compartir eventos
- [ ] Colaboración entre usuarios
- [ ] Widget para pantalla de inicio
- [ ] Integración con Google Calendar

Ver el [REPORTE_DESARROLLO.md](REPORTE_DESARROLLO.md) para más detalles.

---

## ⚠️ Problemas Conocidos

### Críticos

- 🔴 **Contraseñas sin encriptar:** Las contraseñas se almacenan en texto plano. Se implementará hashing en la próxima versión.

### Importantes

- 🟡 **No hay edición de eventos:** Solo se pueden crear y visualizar eventos.
- 🟡 **Validación de fechas:** El campo fecha acepta cualquier texto.
- 🟡 **Sin tests:** No hay cobertura de testing.

### Mejoras Planificadas

- 🟢 UI básica pero funcional
- 🟢 Sin manejo de rotación de pantalla
- 🟢 Sin accesibilidad optimizada

---

## 🤝 Contribuir

Las contribuciones son bienvenidas! Si quieres contribuir:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Guías de Contribución

- Sigue las convenciones de código Java
- Escribe tests para nuevas funcionalidades
- Actualiza la documentación según corresponda
- Un PR por feature o bug fix

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

## 👥 Autores

- **Desarrollador Principal** - _Trabajo Inicial_ - [Tu Nombre](https://github.com/tuusuario)

---

## 🙏 Agradecimientos

- Material Design por los componentes de UI
- La comunidad de Android Developers
- Todos los contribuidores del proyecto

---

## 📞 Contacto

- 📧 Email: tu.email@ejemplo.com
- 🐱 GitHub: [@tuusuario](https://github.com/tuusuario)
- 💼 LinkedIn: [Tu Nombre](https://linkedin.com/in/tunombre)

---

## 📊 Estadísticas del Proyecto

![GitHub repo size](https://img.shields.io/github/repo-size/tuusuario/MisEventos)
![GitHub contributors](https://img.shields.io/github/contributors/tuusuario/MisEventos)
![GitHub stars](https://img.shields.io/github/stars/tuusuario/MisEventos?style=social)
![GitHub forks](https://img.shields.io/github/forks/tuusuario/MisEventos?style=social)

---

<div align="center">

**⭐ Si este proyecto te fue útil, considera darle una estrella! ⭐**

Hecho con ❤️ en Android

</div>
