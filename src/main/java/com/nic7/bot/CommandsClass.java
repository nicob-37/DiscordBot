package com.nic7.bot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

public class CommandsClass extends ListenerAdapter {

    public static String command = "<>";

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();

        if (message.toLowerCase().startsWith(command)) {



        }

        // Andy :heartbreak:
        if (message.toLowerCase().contains("andy")) {

            java.io.InputStream andyImage = getClass().getResourceAsStream("/com/nic7/bot/file/andy.png");

            if (andyImage != null) {
                event.getMessage().replyFiles(FileUpload.fromData(andyImage, "andy.png")).queue();
            }
            else {
                System.out.println("Couldn't find andy.png");
            }
        }

    }

}
