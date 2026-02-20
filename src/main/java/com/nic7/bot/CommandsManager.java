package com.nic7.bot;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandsManager extends ListenerAdapter {

    public static String command = "<>";
    public static boolean andyReply = true;

    // Initializes Commands On Bot Ready
    @Override
    public void onReady(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        initSlashCommands(event);
    }

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String message = event.getMessage().getContentRaw();

        if (message.toLowerCase().startsWith(command)) {


        }

        // Andy :heartbreak:
        if (message.toLowerCase().contains("andy") && andyReply) {
            java.io.InputStream andyImage = getClass().getResourceAsStream("/com/nic7/bot/file/andy.png");
            if (andyImage != null) {
                event.getMessage().replyFiles(FileUpload.fromData(andyImage, "andy.png")).queue();
                event.getChannel().sendMessage(Emoji.fromUnicode("\uD83D\uDC94").getAsReactionCode()).queue();
            }
            else {
                System.out.println("Couldn't find andy.png");
            }
        }

    }

    private void initSlashCommands(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        List<SlashCommandData> commandData = new ArrayList<>();

        // Add new commands below
        commandData.add(Commands.slash("hello", "Reply Hello"));
        commandData.add(Commands.slash("andyreply", "Toggle Andy heartbreak reply"));

        var guild = event.getJDA().getGuildById(Util.SIG_NATION);
        if (guild != null) {
            guild.updateCommands().addCommands(commandData).queue();
            System.out.println("Slash Commands Updated for " + guild.getName());
        } else {
            System.err.println("Could not find Guild ID: " + Util.SIG_NATION);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {

            case "hello" ->
                    event.reply("Hello " + event.getMember().getEffectiveName()).queue();

            case "test" ->
                    event.reply("Test Command Successful").queue();

            case "andyreply" -> {
                CommandsManager.andyReply = Util.toggleBoolean(CommandsManager.andyReply);
                event.reply("Andy Reply is now " + CommandsManager.andyReply).queue(); }
        }
    }

}
