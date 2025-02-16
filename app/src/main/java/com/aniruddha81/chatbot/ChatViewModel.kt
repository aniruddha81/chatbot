package com.aniruddha81.chatbot

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import java.lang.Exception

class ChatViewModel : ViewModel() {
    val messageList = mutableStateListOf<MessageModel>()

    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = Constants.API_KEY
    )

    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.message) }
                    }
                )
                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Typing...", "model"))

                val response = chat.sendMessage(question)
                val responseText = response.text ?: "No response"

                if (messageList.isNotEmpty()) {
                    messageList.removeAt(messageList.lastIndex) // Remove "Typing..."
                }
                messageList.add(MessageModel(responseText, "model"))

            } catch (e: Exception) {
                if (messageList.isNotEmpty()) {
                    messageList.removeAt(messageList.lastIndex)
                }
                messageList.add(MessageModel("Error: ${e.message}", "model"))
            }
        }
    }
}
