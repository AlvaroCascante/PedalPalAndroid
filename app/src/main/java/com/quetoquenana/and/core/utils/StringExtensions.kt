package com.quetoquenana.and.core.utils

fun String?.orEmptyIfLiteralNull(): String {
    return this
        ?.takeUnless { it.equals(other = NULL, ignoreCase = true) }
        .orEmpty()
}

