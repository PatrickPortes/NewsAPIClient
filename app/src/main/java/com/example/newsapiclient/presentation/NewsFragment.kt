package com.example.newsapiclient.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapiclient.R
import com.example.newsapiclient.data.util.Resource.Error
import com.example.newsapiclient.data.util.Resource.Loading
import com.example.newsapiclient.data.util.Resource.Success
import com.example.newsapiclient.databinding.FragmentNewsBinding
import com.example.newsapiclient.presentation.adapter.NewsAdapter
import com.example.newsapiclient.presentation.viewmodel.NewsViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private var country = "us"
    private var page = 1
    private var isScrolling = false
    private var isLoading = false
    private var isLastPage = false
    private var pages = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        val rootView: View = binding.root

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewsBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        newsAdapter = (activity as MainActivity).newsAdapter

        initRecyclerView()
        viewNewsList()
        setSearchView()

        newsAdapter.setOnItemClickListener {

            Toast.makeText(context, "Item Clicked Successful!!!", Toast.LENGTH_SHORT).show()

            val bundle = Bundle().apply {
                putSerializable("selected_article", it)
            }

            Log.i("MyTag", "LINK: ${it.url}")

            findNavController().navigate(
                R.id.action_newsFragment_to_infoFragment,
                bundle
            )

        }
    }

    private fun viewNewsList() {

        viewModel.getNewsHeadLines(country, page)
        viewModel.newsHeadLines.observe(viewLifecycleOwner) { response ->
            when (response) {

                is Success -> {
                    hideProgressBar()
                    response.data?.let {
                        Log.i("MyTag", "came here ${it.articles.toList().size}")
                        newsAdapter.differ.submitList(it.articles.toList())
                        if (it.totalResults % 20 == 0) {
                            pages = it.totalResults / 20
                        } else {
                            pages = it.totalResults / 20 + 1
                        }
                        isLastPage = page == pages
                    }
                }

                is Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred : $it", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                is Loading -> {
                    showProgressBar()
                }

            }
        }
    }

    private fun initRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@NewsFragment.onScrollListener)
        }
    }

    private fun showProgressBar() {
        isLoading = true
        binding.progressBarNews.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        isLoading = false
        binding.progressBarNews.visibility = View.INVISIBLE
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = binding.rvNews.layoutManager as LinearLayoutManager
            val sizeOfTheCurrentList = layoutManager.itemCount
            val visibleItems = layoutManager.childCount
            val topPosition = layoutManager.findFirstVisibleItemPosition()

            val hasReachedToEnd = topPosition + visibleItems >= sizeOfTheCurrentList
            val shouldPaginate = !isLoading && !isLastPage && hasReachedToEnd && isScrolling
            if (shouldPaginate) {
                page++
                viewModel.getNewsHeadLines(country, page)
                isScrolling = false
            }
        }

    }

    // Search

    private fun setSearchView(){
        binding.svNews.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.searchNews("us", query.toString(), page)
                    viewSearchedNews()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    MainScope().launch {
                        delay(2000)
                        viewModel.searchNews("us", newText.toString(), page)
                        viewSearchedNews()
                    }
                    return false
                }

            }
        )

        binding.svNews.setOnCloseListener(
            object : SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    initRecyclerView()
                    viewNewsList()
                    return false
                }

            })
    }

    private fun viewSearchedNews(){

        viewModel.searchedNews.observe(viewLifecycleOwner) { response ->
            when (response) {

                is Success -> {
                    hideProgressBar()
                    response.data?.let {
                        Log.i("MyTag", "came here ${it.articles.toList().size}")
                        newsAdapter.differ.submitList(it.articles.toList())
                        if (it.totalResults % 20 == 0) {
                            pages = it.totalResults / 20
                        } else {
                            pages = it.totalResults / 20 + 1
                        }
                        isLastPage = page == pages
                    }
                }

                is Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred : $it", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                is Loading -> {
                    showProgressBar()
                }

            }
        }

    }

}