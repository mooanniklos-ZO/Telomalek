package com.example.telegram

enum class TDLibClientState {
    DISCONNECTED,
    WAIT_API_KEYS,
    WAIT_PHONE,
    WAIT_CODE,
    WAIT_PASSWORD,
    CONNECTED,
    LISTENING,
    ERROR
}

data class TelegramMessageRaw(
    val msgId: String,
    val text: String,
    val senderUsernameOrLink: String,
    val sourceGroupName: String,
    val timestamp: Long = System.currentTimeMillis()
)
