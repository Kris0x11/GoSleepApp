package com.gosleep.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Nota del modulo "Brain Dump" (caso d'uso Roberto): testo o trascrizione vocale
 * salvata localmente per esternalizzare i pensieri prima di dormire.
 */
@Entity(tableName = "brain_dump_notes")
data class BrainDumpEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val isVoiceNote: Boolean,
    val createdAtEpochMillis: Long,
    val resolved: Boolean = false, // marcata true quando l'utente la archivia/elimina il giorno dopo
)
