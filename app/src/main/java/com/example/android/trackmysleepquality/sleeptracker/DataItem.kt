package com.example.android.trackmysleepquality.sleeptracker

import com.example.android.trackmysleepquality.database.SleepNight

sealed class DataItem {
    abstract val id: Long

    data class SleepNightItem (val nightObj : SleepNight): DataItem(){
        override val id: Long
            get() = nightObj.nightId
    }

    object  Header : DataItem() {
        override val id: Long
            get() = Long.MIN_VALUE
    }
}