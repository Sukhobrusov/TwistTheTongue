package ru.alexander.twistthetongue.listeners

import ru.alexander.twistthetongue.model.Patter

interface OnPatterClickListener {
    fun onClick(patter: Patter)
    fun onFavorite(patter: Patter)
}