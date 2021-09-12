package com.example.w3_d4_databindinglab

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.w3_d4_databindinglab.databinding.CounterFragmentBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CounterFragment : Fragment() {

    companion object {
        fun newInstance() = CounterFragment()
    }

    private lateinit var binding: CounterFragmentBinding
    private lateinit var viewModel: CounterViewModel

    private lateinit var job : Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.counter_fragment, container, false)
        binding.lifecycleOwner = this

        job = GlobalScope.launch {
            val counter = binding.root.findViewById<TextView>(R.id.counterTxt)
            val increment = binding.root.findViewById<EditText>(R.id.editTxt)

            while (true)
            {
                delay(1000)
                if(increment.text.toString().isNotEmpty())
                    viewModel.setIncrement(increment = increment.text.toString().toInt())

                // Blocks
                if((viewModel.getCounter()!!.value!!.toInt() + viewModel.getIncrement()!!.value!!) < Int.MAX_VALUE)
                    viewModel.incCounter()
            }
        }

        return binding.root
    }

    // Save state
    override fun onDestroy() {
        super.onDestroy()
        save()
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
        save()
    }

    private fun save()
    {
        activity?.getSharedPreferences("viewmodel", Context.MODE_PRIVATE)
            ?.edit()
            ?.putString("counter",viewModel.getCounter()?.value)
            ?.commit()
    }

    private fun load()
    {
        val sharedPreferences = activity?.getSharedPreferences( "viewmodel", Context.MODE_PRIVATE)
        val load = sharedPreferences?.getString("counter","0").toString()
        viewModel.setCounter(load)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CounterViewModel::class.java)
        load()
        binding.viewModel = viewModel


    }

}