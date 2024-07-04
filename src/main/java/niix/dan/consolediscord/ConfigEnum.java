package niix.dan.consolediscord;

import java.util.List;

public enum ConfigEnum {
    DISCORD_BOT_TOKEN("BotToken", "Discord-Bot.Token", "Token para a aplicação Discord", "string"),
    DISCORD_BOT_CHANNEL("BotChannel", "Discord-Bot.Channel", "Canal Discord associado ao bot", "string"),

    BLOCKED_CMDS("BlockedCmds", "Discord-Bot.BlockedCmds", "Comandos bloqueados que não podem ser usados no console", "list"),
    WHITELIST_ENABLED("Whitelist_Enabled", "Discord-Bot.Whitelist.Enabled", "Indica se a lista de pessoas permitidas está ativada", "boolean"),
    WHITELIST_LIST("Whitelist_List", "Discord-Bot.Whitelist.List", "Lista de IDs Discord permitidos na lista de permissões", "list"),
    WHITELIST_MESSAGE("Whitelist_Message", "Discord-Bot.Whitelist.Message", "Mensagem exibida para usuários não autorizados", "string"),

    DISCORD_MESSAGE_LIMIT("Msg_MessageLimit", "Messages.Discord.MessageLimit", "Mensagem exibida quando uma mensagem excede o limite do Discord", "string"),
    IN_GAME_NO_PERMISSION("Msg_NoPerm", "Messages.InGame.NoPerm", "Mensagem exibida quando o jogador não tem permissão", "string"),

    IGNORE_WORDS("IgnoreWords", "Config.IgnoreWords", "Palavras que, se encontradas nos logs, serão ignoradas", "list"),

    LOG_DELAY("UpdateDelay", "Config.LogDelay", "Atraso entre verificações de logs do console para enviar para o Discord (em ticks)", "long"),

    FILTER_ENABLED("EnableFilter", "Config.Filter.Enabled", "Indica se o filtro de comandos do Discord está habilitado", "boolean"),
    FILTER_REGEX("FilterRegex", "Config.Filter.RegEx", "Expressão regular usada para filtrar comandos do Discord", "string"),
    FILTER_REPLACE("FilterReplace", "Config.Filter.Replace", "Texto de substituição para comandos filtrados do Discord", "string");

    private final String key;
    private final String description;
    private final String type;
    private final String name;

    ConfigEnum(String name, String key, String description, String type) {
        this.name = name;
        this.key = key;
        this.type = type;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Método de parse para encontrar um ConfigKey com base no caminho da configuração.
     *
     * @param path O caminho da configuração a ser procurado
     * @return O ConfigKey correspondente ao caminho, ou null se não encontrado
     */
    public static ConfigEnum parse(String path) {
        for (ConfigEnum configKey : values()) {
            if (configKey.getKey().equals(path)) {
                return configKey;
            }
        }
        return null;
    }
}
