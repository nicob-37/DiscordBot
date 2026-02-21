package com.nic7.bot.manager;

import com.nic7.bot.util;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

public class MessageScannerManager extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String message = event.getMessage().getContentRaw();

        // Andy :heartbreak:
        if (message.toLowerCase().contains("andy") && util.andyReply) {
            java.io.InputStream andyImage = getClass().getResourceAsStream("/com/nic7/bot/file/andy.png");
            if (andyImage != null) {
                event.getMessage().replyFiles(FileUpload.fromData(andyImage, "andy.png")).queue();
                event.getChannel().sendMessage("andy " + Emoji.fromUnicode("\uD83D\uDC94").getAsReactionCode()).queue();
            }
            else {
                System.out.println("Couldn't find andy.png");
            }
        }

        if (message.contains("😳")) {
            event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDE33")).queue();
        }

        if (message.toLowerCase().contains("burger")) {
            event.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDF54")).queue();
        }

    }
}
