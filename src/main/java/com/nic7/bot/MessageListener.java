package com.nic7.bot;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {

    public static String command = "<>";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // 1. Ignore messages from other bots (and yourself) to prevent infinite loops
        if (event.getAuthor().isBot()) return;

        // 2. Get the raw text of the message
        String message = event.getMessage().getContentRaw();

        // Simple Command Handling
        if (message.startsWith(command)) {
            event.getChannel().sendMessage("Command Recieved").queue();

            if (message.equalsIgnoreCase(command + " hello droid")) {
                event.getChannel().sendMessage("Hello " + event.getAuthor().getName()).queue();

            }

            if (message.equalsIgnoreCase(command + " bulkScan")) {
                event.getChannel().sendMessage("TODO: Seymour Scanner Here").queue();
            }

        }

        // 4. Reading specific content
        if (message.toLowerCase().contains("andy")) {
            event.getMessage().reply("https://cdn.discordapp.com/attachments/1280707386012860476/1474229053920776252/andy.png?ex=6999164c&is=6997c4cc&hm=1897d39b0607e846e53fbfa96df8e88fd63f14b8555039ef90a8fc5022f14886&").queue();
        }

    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Check which command was used
        if (event.getName().equals("hello")) {
            // Get the option we defined earlier
            String name = event.getOption("name").getAsString();

            // Respond to the user
            // Note: You must acknowledge a slash command within 3 seconds
            event.reply("Hello " + name + "! Command received via slash! 🚀").queue();
        }
    }
}