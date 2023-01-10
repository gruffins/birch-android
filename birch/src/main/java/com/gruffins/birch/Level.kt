package com.gruffins.birch

enum class Level(val level: Int) {
    TRACE(0),
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4),
    NONE(5);

    companion object {
        fun fromInt(level: Int) = values().first { it.level == level }
    }
}