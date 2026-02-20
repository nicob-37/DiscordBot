package com.nic7.bot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");

        CommandsManager commandsManager = new CommandsManager();

        if (token == null || token.isEmpty()) {
            System.err.println("!!! Check .env file Discord Token is missing !!!");
            return;
        }

        JDABuilder builder = JDABuilder.createDefault(token);

        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES);

        builder.addEventListeners(commandsManager);

        JDA bot = builder.build();

    }
}
