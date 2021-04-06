package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MyActivity"

data class Count(var count: Int)

class MainActivity : AppCompatActivity() {
    private var count = Count(0)
    private val mAdapter = CustomAdapter(count)
    private val todaysDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        application.setTheme(R.style.Theme_AppCompat_Light_DarkActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Show the current date
        val date = findViewById<TextView>(R.id.date)
        date.text = todaysDate

        // Set up the recycler
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = mAdapter

        val nlparams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val scoreTable = findViewById<TableLayout>(R.id.scoreTable)
        val newRow = TableRow(applicationContext)
        scoreTable.addView(newRow, 1)
        val dateField = TextView(applicationContext)
        newRow.addView(dateField, nlparams)

        Thread {
            // Set up the database
            val db = AppDatabase.getInstance(applicationContext)

            val players = db.playerDao().getAllPlayers()
            if (players.isEmpty()) {
                Log.i(TAG, "No players in database")
                db.playerDao()
                    .insertPlayers(
                        Player(0, "Emilis"),
                        Player(0, "Rytis"),
                        Player(0, "Lina")
                    )
            } else {
                Log.i(
                    TAG,
                    "${players.size} players in database, player ids: ${players.map { it -> it.id }}"
                )
            }

            val todaysGame = db.gameDao().getAllGames().find { it.date == todaysDate }
            if (todaysGame == null) {
                Log.d(TAG, "No game today")
                db.gameDao().insertGame(Game(0, todaysDate))
            } else {
                Log.d(TAG, "Game exists today, id: ${todaysGame.id}")
                createScore(todaysGame.id, 1, 2, 3)
            }

            onPlayerScoresGet()
        }.start()
    }

    fun onAddButtonClick(view: View) {
        count.count++
        mAdapter.notifyItemInserted(mAdapter.itemCount - 1)

        // Update the database
        Thread {
            val db = AppDatabase.getInstance(applicationContext)

            val gameId = db.gameDao().getGameByDate(todaysDate)?.id
            if (gameId == null) {
                Log.e(TAG, "No game in database with date $todaysDate")
                return@Thread
            }

            val playerId = 1

            val score = db.playerScoreDao().getPlayerScore(gameId, playerId)
            if (score == null) {
                Log.e(TAG, "No score in database with gameId $gameId and playerId $playerId")
                return@Thread
            }

            db.playerScoreDao().updatePlayerScore(PlayerScore(gameId, playerId, score.score + 1))
        }.start()
    }

    private fun onPlayerScoresGet() {
        val db = AppDatabase.getInstance(applicationContext)

        val gameId = db.gameDao().getGameByDate(todaysDate)?.id
        if (gameId == null) {
            Log.e(TAG, "No game in database with date $todaysDate")
            return
        }

        val playerId = 1

        val score = db.playerScoreDao().getPlayerScore(gameId, playerId)
        if (score == null) {
            Log.e(TAG, "No score in database with gameId $gameId and playerId $playerId")
            return
        }

        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val prevCount = count.count
            count.count = score.score
            mAdapter.notifyItemRangeInserted(mAdapter.itemCount - 1, score.score - prevCount)
        }
    }

    private fun createScore(gameId: Int, vararg playerIds: Int) {
        val db = AppDatabase.getInstance(applicationContext)
        for (playerId in playerIds) {
            val score = db.playerScoreDao().getPlayerScore(gameId, playerId)
            if (score == null) {
                db.playerScoreDao().insertPlayerScore(PlayerScore(gameId, playerId, 0))
            }
        }
    }
}