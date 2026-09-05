# GoSleep — Android (Kotlin)

Implementazione Android nativa del progetto **GoSleep** (Interazione Uomo-Macchina,
Università degli Studi di Salerno), fedele alla documentazione: onboarding, dashboard,
Distraction Block / Reverse Alarm (Marco), Routine Flow a 6 step (Elena), Brain Dump
(Roberto), Relax Mode, Morning Feedback.

## Stack

- **Kotlin** + **Jetpack Compose** (Material 3, dark mode)
- **MVVM**: ViewModel + StateFlow per ogni schermata, nessuna libreria di DI esterna
  (container manuale in `GoSleepApplication` + `AppViewModelFactory`, per restare
  leggibile a scopo didattico)
- **Room** per la persistenza locale (sessioni di sonno, note del Brain Dump)
- **DataStore Preferences** per bedtime, streak, crescita della Sleep Plant
- **AlarmManager + full-screen notification** per il Reverse Alarm (`notification/`)
- **Navigation Compose** per il flusso schermate (`navigation/GoSleepNavGraph.kt`)

## Come aprire il progetto

1. Apri la cartella `GoSleep/` con **Android Studio** (Koala o successivo).
2. Lascia che Android Studio sincronizzi Gradle (scarica automaticamente il wrapper
   e le dipendenze da Google/Maven Central — serve una connessione internet).
3. Esegui su un emulatore/dispositivo con **minSdk 26** (Android 8.0+).

> Il wrapper Gradle (`gradlew`/`gradlew.bat`) non è incluso nello zip: Android Studio
> lo rigenera automaticamente al primo sync. In alternativa esegui `gradle wrapper`
> una volta con Gradle 8.7 installato localmente.

## Struttura

```
app/src/main/java/com/gosleep/app/
├── data/
│   ├── local/          Room: entità + DAO + database
│   ├── datastore/       Preferences: bedtime, streak, plant growth
│   └── repository/      SleepRepository, BrainDumpRepository
├── domain/               Logica pura testabile: SleepScoreCalculator,
│                         StreakCalculator, PlantGrowthCalculator
├── notification/         ReverseAlarmScheduler/Receiver, DistractionBlockActivity
├── navigation/           Routes, NavGraph, ViewModel factory
└── ui/
    ├── onboarding/       Benvenuto -> questionario -> setup Reverse Alarm
    ├── dashboard/        Hub principale
    ├── distraction/       Caso d'uso Marco: forced friction 5s (img .1A)
    ├── routine/            Caso d'uso Elena: 6 micro-step (img .1B)
    ├── braindump/          Caso d'uso Roberto: note testo/voce (img .1C)
    ├── relax/               Respirazione guidata
    └── morning/            Sleep Score, streak, crescita della pianta
```

## Test inclusi

- **Unit test** (`app/src/test`): logica di dominio pura (Sleep Score, streak,
  crescita pianta, calcolo del prossimo Reverse Alarm), timer del Distraction Block
  con `kotlinx-coroutines-test`, sequenza della Routine Flow, repository del Brain
  Dumpy. Eseguibili con:
  ```
  ./gradlew testDebugUnitTest
  ```


## Note implementative

- `DistractionBlockActivity` attiva "Non disturbare" solo se il permesso
  `ACCESS_NOTIFICATION_POLICY` è già stato concesso dall'utente nelle impostazioni
  di sistema; il blocco fisico dello schermo richiederebbe permessi Device Admin
  aggiuntivi, volutamente lasciati fuori dal perimetro di questo prototipo.
- L'input vocale del Brain Dump espone un hook (`onVoiceTranscriptReady`) pensato
  per essere collegato a `SpeechRecognizer` di Android; la UI mostra già lo stato
  di registrazione.
- Tutti i dati restano sul dispositivo (Room + DataStore), coerente con quanto
  dichiarato nella sezione "Gestione dei dati" della documentazione.
