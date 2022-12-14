package com.example.expense_track.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.expense_track.R

class TimerFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        val viewModel = ViewModelProvider(this)[TimeFragmentViewModel::class.java]
        val stopBtn = view.findViewById<Button>(R.id.timerstopbtn)
        val startBtn = view.findViewById<Button>(R.id.timerstartbtn)

        viewModel.startTimer()
        viewModel.seconds().observe(viewLifecycleOwner) {
            val timerText = view.findViewById<TextView>(R.id.timer_text)
            timerText.text = it.toString()
        }
        viewModel.finished.observe(viewLifecycleOwner) {
            if (it) {
                stopBtn.visibility = View.GONE
                startBtn.visibility = View.VISIBLE
            }
        }

        stopBtn.setOnClickListener {
            viewModel.stopTimer()
            stopBtn.visibility = View.GONE
            startBtn.visibility = View.VISIBLE
        }

        startBtn.setOnClickListener {
            viewModel.resumeTimer()
            stopBtn.visibility = View.VISIBLE
            startBtn.visibility = View.GONE
        }

        return view
    }
}