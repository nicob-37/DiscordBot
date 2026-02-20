package com.nic7.bot;

import com.nic7.bot.hypixel.GetInfo;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) {
        GetInfo getInfo = new GetInfo();

        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");
        String hypixelAPI = dotenv.get("HYPIXEL_TOKEN");

        if (token == null || token.isEmpty()) {
            System.err.println("Check your .env file! Token is missing.");
            return;
        }

        getInfo.initAPIKey(hypixelAPI);

        JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(new CommandsClass())
                .build();
    }
}
