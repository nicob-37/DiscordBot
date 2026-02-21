package com.nic7.bot;

import com.nic7.bot.manager.CommandsManager;
import com.nic7.bot.manager.MessageScannerManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.EnumSet;

public class Main {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");

        if (token == null || token.isEmpty()) {
            System.err.println("!!! Check .env file Discord Token is missing !!!");
            return;
        }

        JDABuilder builder = JDABuilder.createDefault(token);

        builder.enableIntents(EnumSet.allOf(GatewayIntent.class));
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ACTIVITY);

        builder.addEventListeners(new CommandsManager(), new MessageScannerManager());

        JDA bot = builder.build();

    }
}
