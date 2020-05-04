package ru.alexander.twistthetongue.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "patters")
data class Patter(
    @PrimaryKey
    @ColumnInfo(name = "patter_id")
    val id: Int,
    @ColumnInfo(name = "patter_text")
    val text: String,
    val title: String,
    var mark: Int,
    var visits: Int,
    var favorite: Boolean
) : Serializable