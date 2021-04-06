package com.example.myapplication

import androidx.room.*

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val date: String
)

@Dao
interface GameDao {
    @Query("SELECT * FROM Game")
    fun getAllGames(): List<Game>

    @Query("SELECT * FROM Game WHERE date = :date LIMIT 1")
    fun getGameByDate(date: String): Game?

    @Query("SELECT * FROM Game WHERE id = :id LIMIT 1")
    fun getGame(id: Int): Game?

    @Insert
    fun insertGame(game: Game)

    @Update
    fun updateGame(game: Game)

    @Delete
    fun deleteGame(game: Game)
}