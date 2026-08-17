package com.fabio.perfumeshop_api.chat.internal.dto;


/**
 * Un mensaje del hilo de conversación.
 * role: "user" o "assistant" (los roles que entiende la API de Claude).
 */
public record ChatMessage(String role, String content) {
}
