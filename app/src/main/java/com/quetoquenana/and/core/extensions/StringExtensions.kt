package com.quetoquenana.and.core.extensions

fun String?.orEmptyIfLiteralNull(): String {
    return this
        ?.takeUnless { it.equals("null", ignoreCase = true) }
        .orEmpty()
}

