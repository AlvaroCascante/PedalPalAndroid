package com.quetoquenana.and.core.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.time.Instant

class InstantJsonAdapter {

    @RequiresApi(Build.VERSION_CODES.O)
    @FromJson
    fun fromJson(reader: JsonReader): Instant? {
        return when (reader.peek()) {
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                null
            }

            JsonReader.Token.STRING -> {
                val value = reader.nextString()
                try {
                    Instant.parse(value)
                } catch (_: Exception) {
                    throw JsonDataException(
                        "Expected ISO-8601 instant at ${reader.path} but was $value"
                    )
                }
            }

            else -> throw JsonDataException(
                "Expected Instant as a string at ${reader.path} but was ${reader.peek()}"
            )
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: Instant?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.value(value.toString())
    }
}

