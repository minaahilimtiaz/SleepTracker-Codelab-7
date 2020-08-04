/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sleep_detail.*

class SleepTrackerFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sleep_tracker, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        val sleepTrackerViewModel = ViewModelProviders.of(this, viewModelFactory).get(SleepTrackerViewModel::class.java)
        binding.sleepTrackerViewModel = sleepTrackerViewModel
        val adapter = setAdapter(sleepTrackerViewModel) //my addition
        setObservationforDetailNavigation(sleepTrackerViewModel, this) //my addition
        setObservationforSleepQualityNavigation(sleepTrackerViewModel, this) //my addition
        showSnackBar(sleepTrackerViewModel) // my addition
        updateList(sleepTrackerViewModel, adapter) //my addition
        binding.sleepList.adapter = adapter
        binding.setLifecycleOwner(this)
        val manager = GridLayoutManager(activity, 3)
        binding.sleepList.layoutManager = manager
        return binding.root
    }

    //my addition
    private fun setAdapter(sleepTrackerViewModelObj: SleepTrackerViewModel): SleepNightAdapter {
        return SleepNightAdapter(SleepNightListener { nightId ->
            Toast.makeText(context, "${nightId}", Toast.LENGTH_LONG).show()
            sleepTrackerViewModelObj.onSleepNightClicked(nightId)
        })
    }

    //my addition
    private fun setObservationforDetailNavigation(viewModel: SleepTrackerViewModel, fragmentObj: SleepTrackerFragment) {
        viewModel.navigateToSleepDetail.observe(this, Observer { night ->
            night?.let {
                fragmentObj.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetailFragment(night))
                viewModel.onSleepDetailNavigated()
            }
        })
    }

    //my addition
    private fun setObservationforSleepQualityNavigation(viewModel: SleepTrackerViewModel, fragmentObj: SleepTrackerFragment) {
        viewModel.navigateToSleepQuality.observe(this, Observer { night ->
            night?.let {
                fragmentObj.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                viewModel.doneNavigating()
            }
        })
    }

    //my addition
    private fun showSnackBar(viewModel: SleepTrackerViewModel){
       viewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.cleared_message), Snackbar.LENGTH_SHORT).show()
                viewModel.doneShowingSnackbar()
            }
        })
    }

    //my addition
    private fun updateList(sleepTrackerViewModelObj: SleepTrackerViewModel, adapter:SleepNightAdapter){
        sleepTrackerViewModelObj.nights.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })


    }
}
