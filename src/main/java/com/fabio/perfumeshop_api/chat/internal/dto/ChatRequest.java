package com.fabio.perfumeshop_api.chat.internal.dto;

import java.util.List;

/**
 * Petición de chat: el historial completo de la conversación.
 * El front envía todos los mensajes cada vez, porque Claude no guarda estado
 * entre llamadas; la "memoria" la construimos reenviando el hilo.
 */
public record ChatRequest(List<ChatMessage> messages) {
}
