package com.example.expense_track.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expense_track.R
import com.example.expense_track.retrofit.RetrofitInstance
import com.example.expense_track.retrofit.TodoAdapter
import retrofit2.HttpException
import java.io.IOException

class TodoFragment : Fragment() {

    private lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)

        fun setUp() = view.findViewById<RecyclerView>(R.id.rvTodos).apply {
            todoAdapter = TodoAdapter()
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(container?.context)
        }

        setUp()

        lifecycleScope.launchWhenCreated {
            view.findViewById<ProgressBar>(R.id.progress_bar).isVisible = true
            val response = try {
                RetrofitInstance.api.getTodos()
            } catch (e: IOException) {
                val toast = Toast.makeText(container?.context,
                "No internet connection", Toast.LENGTH_SHORT)
                toast.show()
                view.findViewById<ProgressBar>(R.id.progress_bar).isVisible = false
                return@launchWhenCreated
            } catch (e: HttpException) {
                val toast = Toast.makeText(container?.context,
                    "Unexpected response", Toast.LENGTH_SHORT)
                toast.show()
                view.findViewById<ProgressBar>(R.id.progress_bar).isVisible = false
                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {
                todoAdapter.todos = response.body()!!
            } else {
                val toast = Toast.makeText(container?.context,
                    "Response was not successful", Toast.LENGTH_SHORT)
                toast.show()
            }
            view.findViewById<ProgressBar>(R.id.progress_bar).isVisible = false
        }

        return view
    }
}