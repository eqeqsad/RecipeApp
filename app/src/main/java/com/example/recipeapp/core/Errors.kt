package com.example.recipeapp.core

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserMessage(): String {
    val networkCause = generateSequence(this) { it.cause }
        .firstOrNull {
            it is UnknownHostException || it is SocketTimeoutException || it is ConnectException
        }
    return if (networkCause != null) {
        "No internet connection. Please check your network and try again."
    } else {
        message?.takeIf { it.isNotBlank() } ?: "Something went wrong. Please try again."
    }
}
