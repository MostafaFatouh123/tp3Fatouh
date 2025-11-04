package ma.emsi.fatouh.tp3fatouh.llm;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.data.message.SystemMessage;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Gère l'interface avec l'API de Gemini.
 * Son rôle est essentiellement de lancer une requête à chaque nouvelle
 * question qu'on veut envoyer à l'API.
 *
 * De portée dependent pour réinitialiser la conversation à chaque fois que
 * l'instance qui l'utilise est renouvelée.
 * Par exemple, si l'instance qui l'utilise est de portée View, la conversation est
 * réunitialisée à chaque fois que l'utilisateur quitte la page en cours.
 */
@ApplicationScoped
public class LlmClientPourGemini {

    private final String key;
    private ChatMemory chatMemory;
    private Assistant assistant;

    interface Assistant {
        String chat(String prompt);
    }

    public LlmClientPourGemini() {
        this.key = System.getenv("GEMINI_KEY");
        if (this.key == null || this.key.isBlank()) {
            throw new RuntimeException("GEMINI_KEY est null");
        }

        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(key)
                .modelName("gemini-2.5-flash")
                .logRequestsAndResponses(true)
                .temperature(0.2)
                .build();

        this.chatMemory = MessageWindowChatMemory.withMaxMessages(5);

        this.assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    public String envoyerRequete(String lieu) {
        chatMemory.clear();

        String systemRole = """
                Tu es un guide touristique
                Donne exactement ce JSON :
                {
                   "ville_ou_pays": "nom de la ville ou du pays",
                   "endroits_a_visiter": ["endroit 1", "endroit 2"],
                   "prix_moyen_repas": "<prix> <devise du pays>"
                }
                N'utilise pas Markdown.
                """;

        chatMemory.add(SystemMessage.from(systemRole));

        return assistant.chat("Donne les informations pour : " + lieu);
    }
}