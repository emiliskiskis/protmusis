package com.example.myapplication

import androidx.room.*

@Entity(primaryKeys = ["gameId", "playerId"])
data class PlayerScore (
    val gameId: Int,
    val playerId: Int,
    val score: Int
)

@Dao
interface PlayerScoreDao {
    @Query("SELECT * FROM PlayerScore WHERE gameId = :gameId")
    fun getPlayerScoresByGame(gameId: Int): List<PlayerScore>

    @Query("SELECT * FROM PlayerScore WHERE gameId = :gameId AND playerId = :playerId LIMIT 1")
    fun getPlayerScore(gameId: Int, playerId: Int): PlayerScore?

    @Insert
    fun insertPlayerScore(playerScore: PlayerScore)

    @Update
    fun updatePlayerScore(playerScore: PlayerScore)

    @Delete
    fun deletePlayerScore(playerScore: PlayerScore)
}