package com.mirahome.tetrisgame.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.mirahome.tetrisgame.R
import com.mirahome.tetrisgame.constant.Constants
import com.mirahome.tetrisgame.constant.Constants.HANDLER_CODE_WHAT
import com.mirahome.tetrisgame.view.GameStatusListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GameStatusListener {

    // todo - Stateful apps are SHIT. VIEW MUST BE STUPID. NOW IT IS NOT. IT HAS THIS
    // todo - nasty MUTABLE "mRunFlag var" AND THE VIEW can change STATE,
    // todo - SO we have a fucked-up APP !!!!!!!!!!!!!!!!!!!1


    private var mRunFlag = true // todo breaking SINGLE-RESPONSIBILITY-PRINCIPLE (class have many reasons to change).
    //todo  Now anyone can change this var and break the app


    // todo - Stateful apps are SHIT
    // todo - easy to brake this var
    // todo - we need an enum Delay with 2 types FAST / SLOW
    private var mSleepTime = Constants.DELAY_LONG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        game_view.setGameStatusListener(this)
        onBtnClicks()
    }

    private fun onBtnClicks() {
        main_btn.setOnClickListener{
            executeDrawing()
            // Todo how do we know if the game is ON or PAUSED
            // 1) if we check button String ("play") - we can break app later by changing the text !!!
            // todo SOLUTION ? (I know :L)
        }

        left_btn.setOnClickListener{
            game_view.moveLeft()
        }
        right_btn.setOnClickListener{
            game_view.moveRight()
        }
        top_btn.setOnClickListener{
            game_view.turnBlock()
        }
        bottom_btn.setOnClickListener{
            resetDrawing()
        }
    }


    private fun executeDrawing(){
        handler.sendEmptyMessageDelayed(HANDLER_CODE_WHAT, mSleepTime)
        main_btn.text = getString(R.string.pause)
        mRunFlag = true //todo shit !!!
    }

    // todo when should we pause ?
    private fun pauseDrawing(){
        handler.removeMessages(HANDLER_CODE_WHAT)
        main_btn.text = getString(R.string.play)
        mRunFlag = false //todo shit !!!
    }

    private fun resetDrawing(){
        handler.removeMessages(HANDLER_CODE_WHAT)
        handler.sendEmptyMessage(HANDLER_CODE_WHAT)
        mSleepTime = Constants.DELAY_SHORT
    }

    override fun onBlockToBottom() {
        mSleepTime = Constants.DELAY_LONG
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            game_view.downBlock()
            sendEmptyMessageDelayed(HANDLER_CODE_WHAT, mSleepTime)
        }
    }

}
