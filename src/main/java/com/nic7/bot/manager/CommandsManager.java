package com.nic7.bot.manager;

import com.nic7.bot.util;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
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
                event.getChannel().sendMessage("andy " + Emoji.fromUnicode("\uD83D\uDC94").getAsReactionCode()).queue();
            }
            else {
                System.out.println("Couldn't find andy.png");
            }
        }

        if (message.contains("😳")) {
            event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDE33")).queue();
        }
        if (message.contains("🍔")) {
            event.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDF54")).queue();
        }

    }

    private void initSlashCommands(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        List<SlashCommandData> commandData = new ArrayList<>();

        // Add new commands below
        commandData.add(Commands.slash("stop", "Stops Bot"));
        commandData.add(Commands.slash("restart", "Restarts the bot"));
        commandData.add(Commands.slash("testing_new", "Test me"));

        commandData.add(Commands.slash("hello", "Reply Hello"));
        commandData.add(Commands.slash("andyreply", "Toggle Andy heartbreak reply"));

        commandData.add(Commands.slash("redditpost", "Create A Post with Upvotes and Downvotes").
                addOption(OptionType.STRING, "title", "The title of your post", true)
                .addOption(OptionType.STRING, "body", "The Main Content of your post", true)
                .addOption(OptionType.ATTACHMENT, "attachment", "Add an attachment to your post (optional)", false));

        var guild = event.getJDA().getGuildById(util.SIG_NATION);

        if (guild != null) {
            guild.updateCommands().addCommands(commandData).queue();
            System.out.println("Slash Commands Updated for " + guild.getName());
        } else {
            System.err.println("Could not find Guild ID: " + util.SIG_NATION);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName()) {

            case "stop" -> {
                if (!event.getUser().getId().equals(util.MY_ID)) {
                    event.reply("Nice try, " + event.getUser().getEffectiveName()).setEphemeral(true).queue();
                    return;
                }
                else {
                    event.reply("Shutting Down nic7").queue();
                    event.getJDA().shutdown();
                    System.out.println("Bot was shut down by " + event.getUser().getName());
                    System.exit(0);
                }
            }

            case "restart" -> {
                if (!event.getUser().getId().equals(util.MY_ID)) {
                    event.reply("Nice try, " + event.getUser().getEffectiveName()).setEphemeral(true).queue();
                    return;
                }

                event.reply("Launching independent update script... 🚀").queue(success -> {
                    try {
                        // setsid runs the script in its own session, independent of the bot
                        ProcessBuilder pb = new ProcessBuilder("setsid", "sh", "/home/ubuntu/DiscordBot/update_bot.sh");
                        pb.start();

                        // Wait 1 second to ensure the script has actually started
                        Thread.sleep(1000);

                        event.getJDA().shutdown();
                        System.exit(0);
                    } catch (Exception e) {
                        event.getChannel().sendMessage("Critical error: " + e.getMessage()).queue();
                    }
                });
            }

            case "hello" ->
                    event.reply("Hello " + event.getMember().getEffectiveName()).queue();

            case "testing_new" ->
                    event.reply("Test Successful").queue();

            case "test" ->
                    event.reply("Test Command Successful").queue();

            case "andyreply" -> {
                CommandsManager.andyReply = util.toggleBoolean(CommandsManager.andyReply);
                event.reply("Andy Reply is now " + CommandsManager.andyReply).queue(); }

            case "redditpost" -> {
                String postBody = event.getOption("body").getAsString();
                String postTitle = event.getOption("title").getAsString();
                var attachmentOption = event.getOption("attachment");

                var replyAction = event.reply("# " + postTitle + "\n" + postBody);

                if (attachmentOption != null) {
                    Message.Attachment attachment = attachmentOption.getAsAttachment();
                    replyAction.addFiles(FileUpload.fromData(attachment.getProxy().download().join(), attachment.getFileName()));
                }

                replyAction.queue(hook -> {
                    hook.retrieveOriginal().queue(message -> {
                        message.addReaction(Emoji.fromCustom("updoot", 1474560551773536366L, false)).queue();
                        message.addReaction(Emoji.fromCustom("downdoot", 1474560608346312936L, false)).queue();
                    });
                });

            }


        }
    }

}
