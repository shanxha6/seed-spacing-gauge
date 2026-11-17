package com.seedspacing.gauge.data.repository

import com.seedspacing.gauge.core.config.GameConfig
import com.seedspacing.gauge.data.local.SeedPreferences
import com.seedspacing.gauge.data.sample.SampleData
import com.seedspacing.gauge.domain.model.Bed
import com.seedspacing.gauge.domain.model.Note
import com.seedspacing.gauge.domain.repository.BedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class BedRepositoryImpl(private val prefs: SeedPreferences) : BedRepository {

    private val state = MutableStateFlow(bootstrap())

    override val beds: StateFlow<List<Bed>> = state.asStateFlow()

    override fun find(id: String): Bed? = state.value.firstOrNull { it.id == id }

    override fun store(bed: Bed): String {
        val id = bed.id.ifBlank { "bed_" + System.currentTimeMillis().toString() }
        val saved = bed.copy(id = id)
        val current = state.value.toMutableList()
        val at = current.indexOfFirst { it.id == id }
        if (at >= 0) {
            current[at] = saved.copy(notes = current[at].notes)
        } else {
            current.add(0, saved)
            while (current.size > GameConfig.MAX_BEDS) {
                current.removeAt(current.size - 1)
            }
        }
        publish(current)
        return id
    }

    override fun addNote(bedId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val note = Note(
            id = "note_" + System.currentTimeMillis().toString(),
            text = trimmed.take(GameConfig.MAX_NOTE_CHARS),
            createdAt = System.currentTimeMillis()
        )
        publish(state.value.map { if (it.id == bedId) it.copy(notes = listOf(note) + it.notes) else it })
    }

    override fun removeNote(bedId: String, noteId: String) {
        publish(
            state.value.map { bed ->
                if (bed.id == bedId) bed.copy(notes = bed.notes.filterNot { it.id == noteId }) else bed
            }
        )
    }

    override fun setOnHold(bedId: String, onHold: Boolean) {
        publish(state.value.map { if (it.id == bedId) it.copy(onHold = onHold) else it })
    }

    override fun clearAll() {
        publish(emptyList())
    }

    private fun publish(list: List<Bed>) {
        state.value = list
        prefs.writeString(SeedPreferences.KEY_BEDS, encode(list))
    }

    private fun bootstrap(): List<Bed> {
        if (!prefs.readBoolean(SeedPreferences.KEY_SEEDED, false)) {
            val starter = SampleData.starterBeds(prefs, System.currentTimeMillis())
            prefs.writeBoolean(SeedPreferences.KEY_SEEDED, true)
            prefs.writeString(SeedPreferences.KEY_BEDS, encode(starter))
            return starter
        }
        return decode(prefs.readString(SeedPreferences.KEY_BEDS, ""))
    }

    private fun encode(list: List<Bed>): String {
        val array = JSONArray()
        list.forEach { bed ->
            val item = JSONObject()
            item.put(F_ID, bed.id)
            item.put(F_NAME, bed.name)
            item.put(F_LENGTH, bed.lengthCm)
            item.put(F_WIDTH, bed.widthCm)
            item.put(F_CROP_A, bed.primaryCropId)
            item.put(F_CROP_B, bed.secondaryCropId ?: "")
            item.put(F_RATIO_A, bed.ratioA)
            item.put(F_RATIO_B, bed.ratioB)
            item.put(F_OFFSET, bed.offsetRows)
            item.put(F_HOLD, bed.onHold)
            item.put(F_TIME, bed.createdAt)
            val notes = JSONArray()
            bed.notes.forEach { note ->
                val entry = JSONObject()
                entry.put(F_ID, note.id)
                entry.put(F_TEXT, note.text)
                entry.put(F_TIME, note.createdAt)
                notes.put(entry)
            }
            item.put(F_NOTES, notes)
            array.put(item)
        }
        return array.toString()
    }

    private fun decode(raw: String): List<Bed> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            val result = ArrayList<Bed>(array.length())
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val secondary = item.optString(F_CROP_B, "")
                val notesArray = item.optJSONArray(F_NOTES) ?: JSONArray()
                val notes = ArrayList<Note>(notesArray.length())
                for (n in 0 until notesArray.length()) {
                    val entry = notesArray.getJSONObject(n)
                    notes.add(
                        Note(
                            id = entry.optString(F_ID, "note_" + n.toString()),
                            text = entry.optString(F_TEXT, ""),
                            createdAt = entry.optLong(F_TIME, 0L)
                        )
                    )
                }
                result.add(
                    Bed(
                        id = item.optString(F_ID, "bed_" + i.toString()),
                        name = item.optString(F_NAME, ""),
                        lengthCm = item.optInt(F_LENGTH, GameConfig.DEFAULT_LENGTH_CM),
                        widthCm = item.optInt(F_WIDTH, GameConfig.DEFAULT_WIDTH_CM),
                        primaryCropId = item.optString(F_CROP_A, "carrot"),
                        secondaryCropId = secondary.ifBlank { null },
                        ratioA = item.optInt(F_RATIO_A, 1),
                        ratioB = item.optInt(F_RATIO_B, 1),
                        offsetRows = item.optBoolean(F_OFFSET, false),
                        onHold = item.optBoolean(F_HOLD, false),
                        createdAt = item.optLong(F_TIME, 0L),
                        notes = notes
                    )
                )
            }
            result.toList()
        }.getOrDefault(emptyList())
    }

    private companion object {
        const val F_ID = "id"
        const val F_NAME = "name"
        const val F_LENGTH = "l"
        const val F_WIDTH = "w"
        const val F_CROP_A = "a"
        const val F_CROP_B = "b"
        const val F_RATIO_A = "ra"
        const val F_RATIO_B = "rb"
        const val F_OFFSET = "off"
        const val F_HOLD = "hold"
        const val F_TIME = "ts"
        const val F_NOTES = "notes"
        const val F_TEXT = "t"
    }
}
