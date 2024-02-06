package com.example.newsapiclient.domain.usecase

import com.example.newsapiclient.data.model.APIResponse
import com.example.newsapiclient.domain.repository.NewsRepository
import com.example.newsapiclient.data.util.Resource

class GetNewsHeadlinesUseCase(private val newsRepository: NewsRepository) {

    suspend fun execute() : Resource<APIResponse>{
        return newsRepository.getNewsHeadlines()
    }

}