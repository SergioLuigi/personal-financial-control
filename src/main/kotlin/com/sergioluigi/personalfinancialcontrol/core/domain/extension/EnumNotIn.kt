package com.sergioluigi.personalfinancialcontrol.core.domain.extension


/**
 * Tests if an enum instance is not equal to one of those placed as parameter.
 * @param enums - varargs of enum instances
 */
fun <T: Enum<*>> T.notIn(vararg enums: T) = enums.contains(this)
