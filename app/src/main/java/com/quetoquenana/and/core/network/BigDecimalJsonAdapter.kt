package com.quetoquenana.and.core.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.math.BigDecimal

class BigDecimalJsonAdapter {

    @FromJson
    fun fromJson(reader: JsonReader): BigDecimal? {
        return when (reader.peek()) {
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                null
            }

            JsonReader.Token.NUMBER,
            JsonReader.Token.STRING -> {
                val value = reader.nextString()
                value.toBigDecimalOrNull()
                    ?: throw JsonDataException(
                        "Expected BigDecimal-compatible value at ${'$'}{reader.path} but was ${'$'}value"
                    )
            }

            else -> throw JsonDataException(
                "Expected BigDecimal as a number or string at ${'$'}{reader.path} but was ${'$'}{reader.peek()}"
            )
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: BigDecimal?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.value(value)
    }
}


