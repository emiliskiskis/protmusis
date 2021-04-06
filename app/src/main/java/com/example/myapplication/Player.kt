package com.example.myapplication

import androidx.room.*

@Entity
data class Player (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String
)

@Dao
interface PlayerDao {
    @Query("SELECT * FROM Player")
    fun getAllPlayers(): List<Player>

    @Query("SELECT * FROM Player WHERE id = :id")
    fun getPlayer(id: Int): Player

    @Insert
    fun insertPlayers(vararg players: Player)

    @Update
    fun updatePlayer(player: Player)

    @Delete
    fun deletePlayer(player: Player)
}