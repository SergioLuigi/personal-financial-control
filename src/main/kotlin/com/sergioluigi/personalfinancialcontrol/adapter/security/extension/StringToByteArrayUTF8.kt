package com.sergioluigi.personalfinancialcontrol.adapter.security.extension

import java.nio.charset.StandardCharsets

fun String.toByteArrayUTF8() = this.toByteArray(StandardCharsets.UTF_8)