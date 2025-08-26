package com.example.roommateorganizer.util

fun randomTag(n: Int): String {
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    return (1..n).map { chars.random() }.joinToString("")
}
