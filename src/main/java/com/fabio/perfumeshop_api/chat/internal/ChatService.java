package com.fabio.perfumeshop_api.chat.internal;


import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.fabio.perfumeshop_api.catalog.api.CatalogApi;
import com.fabio.perfumeshop_api.catalog.internal.CatalogItemRecommendation;
import com.fabio.perfumeshop_api.chat.internal.dto.ChatMessage;
import com.fabio.perfumeshop_api.chat.internal.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final CatalogApi catalogApi;

    //El cliente lee automáticamente la Api key de la variable de entorno.
    private final AnthropicClient client = AnthropicOkHttpClient.fromEnv();

    ChatResponse chat(List<ChatMessage> history){

        //Construimos el prompt con el catálogo como contexto.
        String systemPrompt = buildSystemPrompt();

        //Preparamos los parámetros de la llamada
        MessageCreateParams.Builder builder = MessageCreateParams.builder()
                .model(Model.CLAUDE_HAIKU_4_5)
                .maxTokens(1024L)
                .system(systemPrompt);

        //Añadimos cada mensaje del historial
        for (ChatMessage msg : history){
            if ("assistant".equals(msg.role())){
                builder.addAssistantMessage(msg.content());
            }else {
                builder.addUserMessage(msg.content());
            }
        }

        //Llamamos a Claude
        Message response = client.messages().create(builder.build());

        //Extraemos el texto de la respuesta
        String reply = response.content().stream()
                .filter(contentBlock -> contentBlock.text().isPresent())
                .map(contentBlock -> contentBlock.text().get().text())
                .collect(Collectors.joining());

        return new ChatResponse(reply);

    }


    private String buildSystemPrompt(){

        List<CatalogItemRecommendation> perfumes = catalogApi.findAllForRecommendation();

        String catalogText = perfumes.stream()
                .map(p -> "- " + p.name() + " (" + p.brand() + "), familia " + p.family()
                        + ", notas: " + String.join(", ", p.notes()))
                .collect(Collectors.joining("\n"));

        return """
                Eres el asistente de PerfumeShop, una tienda de perfumería.
                Ayudas a los clientes a encontrar el perfume ideal según lo que buscan.

                Cuando el cliente describa lo que quiere, recomienda perfumes de NUESTRO
                catálogo cuando encajen, explicando por qué. Si preguntan sobre perfumería
                en general, responde con tu conocimiento. Sé cercano y conciso.

                Este es nuestro catálogo actual:
                %s
                """.formatted(catalogText);
    }


}
