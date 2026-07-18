package com.example.websocketserversideeventapp.data.remote

import okhttp3.Request

interface WikimediaApi {
    fun getRecentChangesRequest(): Request
}

class WikimediaApiImpl : WikimediaApi {
    override fun getRecentChangesRequest(): Request {
        return Request.Builder()
            .url("https://stream.wikimedia.org/v2/stream/recentchange")
            .header("Accept", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .header("User-Agent", "Android-SSE-App/1.0 (https://github.com/example/repo; contact@example.com)")
            .build()
    }
}
