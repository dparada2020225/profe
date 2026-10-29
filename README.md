# Profe 🎓

App Android personal para aprender inglés **desde cero hasta C2**: curso con lecciones, examen de ubicación, vocabulario con repetición espaciada (heredado de Repaso), dictados, pronunciación, corrector de escritura y un tutor de conversación con IA. Todo con servicios gratuitos.

## Funciones

- **Examen de ubicación adaptativo (A1 → C2)**: empieza en A2, sube o baja por bloques de 5 preguntas y te coloca en el nivel correcto (marca como sabidas las lecciones de niveles inferiores).
- **Curso de 54 lecciones** (A1·10, A2·10, B1·10, B2·10, C1·8, C2·6): explicación (español → inglés gradual), ejemplos con audio, vocabulario y ~17 ejercicios por lección (opción múltiple, completar, traducir, ordenar, dictado y pronunciación). Se aprueba con 80 %.
- **Vocabulario con SM-2**: al abrir una lección sus palabras entran a tu mazo «Vocabulario A1…C2». Incluye todo lo de Repaso: mazos, tarjetas cloze, CSV, respaldo, estadísticas y el curso *Grammar in Context 2* (se añade desde Ajustes).
- **Dictado** y **pronunciación** con frases de tu nivel (TextToSpeech y reconocimiento de voz de Android).
- **Escritura**: corrección gratuita con LanguageTool y, con la key de Groq, corrección completa explicada en español.
- **Tutor con IA (Groq)**: 13 escenarios (restaurante, aeropuerto, entrevista de trabajo tech, daily standup, debate…), por texto o voz; te corrige con ✏️ y explica con 💡.
- **Diccionario**: definiciones y audio (Free Dictionary API), traducción (MyMemory) y oraciones reales con traducción (Tatoeba). Guarda palabras en «Mis palabras».
- XP, racha diaria, meta diaria y recordatorio.
- **Respaldo** JSON; puede importar el respaldo de Repaso sin borrar nada.

## Servicios gratuitos usados

| Servicio | Para qué | Key |
|---|---|---|
| Groq (llama-3.3-70b-versatile / llama-3.1-8b-instant) | Tutor, corrección y ejercicios extra | Sí, gratis (console.groq.com) |
| LanguageTool | Corrector gramatical | No |
| Free Dictionary API | Definiciones y audio | No |
| Tatoeba | Oraciones con traducción | No |
| MyMemory | Traducción corta | No |
| TextToSpeech / reconocimiento de voz de Android | Audio y pronunciación | No |

La key de Groq se pega en **Ajustes → Tutor con IA** y se guarda solo en el teléfono (no está en el código).

## Stack

Kotlin · Jetpack Compose + Material 3 · MVVM con Hilt · Navigation Compose · Coroutines/Flow · Room · DataStore · KSP. `minSdk 26` · `targetSdk 36`.

## Contenido del curso

Se escribe en `tools/curriculum/*.py` y se genera con:

```bash
python3 tools/curriculum/build.py
```

que produce `app/src/main/assets/curriculum/curriculum.json` y `placement.json`.

## Compilar

```bash
./gradlew testDebugUnitTest assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`. GitHub Actions compila, corre las pruebas y publica el APK como artefacto en cada push.
