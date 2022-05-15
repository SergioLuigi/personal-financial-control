package com.sergioluigi.personalfinancialcontrol.core.domain.extension

/**
 * Run the block if source value is equal to true
 */
fun <R: Any> Boolean.runWhenTrue(function:() -> R): Unit =
    if(this){
        function()
        Unit
    }else Unit