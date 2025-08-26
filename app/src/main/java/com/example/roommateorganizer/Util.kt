package com.example.roommateorganizer.core

fun randomTag(n: Int): String {
    // Removed ambiguous characters for readability (no 0/O/I/1)
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    return (1..n).map { chars.random() }.joinToString("")
}
