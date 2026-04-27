package com.myapplication.features.auth.data.datasource.remote.model

data class SendPushRequest(
    val user_id: Int,
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap()
)

data class SendBroadcastRequest(
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap()
)

data class GenericMessageResponse(
    val message: String
)
