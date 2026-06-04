package com.quetoquenana.and.core.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.util.UUID

class UuidJsonAdapter {

    @FromJson
    fun fromJson(reader: JsonReader): UUID? {
        return when (reader.peek()) {
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                null
            }

            JsonReader.Token.STRING -> {
                val value = reader.nextString()
                try {
                    UUID.fromString(value)
                } catch (_: IllegalArgumentException) {
                    throw JsonDataException(
                        "Expected UUID-compatible value at ${reader.path} but was $value"
                    )
                }
            }

            else -> throw JsonDataException(
                "Expected UUID as a string at ${reader.path} but was ${reader.peek()}"
            )
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: UUID?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.value(value.toString())
    }
}

