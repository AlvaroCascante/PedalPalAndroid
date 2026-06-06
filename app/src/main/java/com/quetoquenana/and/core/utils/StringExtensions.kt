package com.quetoquenana.and.core.utils

fun String?.orEmptyIfLiteralNull(): String {
    return this
        ?.takeUnless { it.equals("null", ignoreCase = true) }
        .orEmpty()
}

