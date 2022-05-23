package com.gamla.mymovies.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gamla.mymovies.databinding.ActivityMainBinding
import com.gamla.mymovies.model.Movie
import com.gamla.mymovies.model.MoviesRepository
import com.gamla.mymovies.ui.common.visible
import com.gamla.mymovies.ui.detail.DetailActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(MoviesRepository(this)) }

    private val moviesAdapter = MoviesAdapter { viewModel.onMovieClicked(it) }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recycler.adapter = moviesAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::updateUi)
            }
        }
    }

    private fun updateUi(state: MainViewModel.UiState) {
        binding.progressBar.visible = state.loading
        state.movies?.let(moviesAdapter::submitList)
        state.navigateTo?.let(::navigateTo)
    }

    private fun navigateTo(movie: Movie) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.MOVIE, movie)
        startActivity(intent)
    }
}