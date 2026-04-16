package com.nic7.bot.manager;

import com.nic7.bot.ID;
import com.nic7.bot.util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandsManager extends ListenerAdapter {
    List<SlashCommandEx> commands = new ArrayList<>();
    StatusManager sm = new StatusManager();

    public static boolean commandsEnabled = true;

    @Override
    public void onReady(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        initSlashCommandsEx(event);
    }

    public static class SlashCommandEx {
        String name, description;
        boolean protection;
        String authorizedID;
        SlashCommandData data;

        public SlashCommandEx(String name, String description, boolean protection, String authorizedID) {
            this.name = name; this.description = description; this.protection = protection; this.authorizedID = authorizedID;
            this.data = Commands.slash(name, description)
                    .setContexts(InteractionContextType.GUILD, InteractionContextType.BOT_DM, InteractionContextType.PRIVATE_CHANNEL)
                    .setIntegrationTypes(IntegrationType.GUILD_INSTALL, IntegrationType.USER_INSTALL);
        }

        public SlashCommandEx(String name, String description) {
            this.name = name; this.description = description; this.protection = false;
            this.data = Commands.slash(name, description)
                    .setContexts(InteractionContextType.GUILD, InteractionContextType.BOT_DM, InteractionContextType.PRIVATE_CHANNEL)
                    .setIntegrationTypes(IntegrationType.GUILD_INSTALL, IntegrationType.USER_INSTALL);
        }

        public SlashCommandEx addOption(OptionType type, String name, String desc, boolean required) {
            this.data.addOption(type, name, desc, required);
            return this;
        }

        public SlashCommandEx addOptions(OptionData... options) {
            this.data.addOptions(options);
            return this;
        }
    }

    private boolean isAuthorized(SlashCommandInteraction event, String authorizedID) {
        if (event.getUser().getId().equals(authorizedID)) return true;

        return event.getMember() != null &&
                event.getMember().getRoles().stream()
                        .anyMatch(role -> role.getId().equals(authorizedID));
    }

    public void initSlashCommandsEx(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        // Clear list to prevent duplicates if onReady fires twice
        commands.clear();

        var guild = event.getJDA().getGuildById(ID.SIG_NATION);

        commands.add(new SlashCommandEx("toggle_commands", ".", true, ID.NICO));
        commands.add(new SlashCommandEx("view_toggles", "View which settings are on or off"));
        commands.add(new SlashCommandEx("stop", "Stops Bot", true, ID.NICO));
        commands.add(new SlashCommandEx("restart", "Restarts and checks for updates", true, ID.NICO));
        commands.add(new SlashCommandEx("version", "Get current bot version", true, ID.NICO));
        commands.add(new SlashCommandEx("toggle_andy_reply", "Toggles andy heartbreak", true, ID.NICO));
        commands.add(new SlashCommandEx("toggle_bruh", "Toggles bruh reactions", true, ID.NICO)
                .addOption(OptionType.USER, "user", "User to toggle", true));
        commands.add(new SlashCommandEx("set_status", "Sets Nic7's status", true, ID.NICO)
                .addOption(OptionType.STRING, "status", "Custom Status", true));
        commands.add(new SlashCommandEx("reddit_post", "Create a reddit post")
                .addOption(OptionType.STRING, "title", "Title of post", true)
                .addOption(OptionType.STRING, "body", "Body of post", true)
                .addOption(OptionType.ATTACHMENT, "attachment", "Optional attachment", false));
        commands.add(new SlashCommandEx("ping", "pong"));
        commands.add(new SlashCommandEx("m", ".", true, ID.NICO)
                .addOption(OptionType.STRING, "body", ".", true)
                .addOption(OptionType.ATTACHMENT, "file", ".", false));
        commands.add(new SlashCommandEx("get_avatar", "gets a user's custom avatar")
                .addOption(OptionType.USER, "user", ".", true));
        commands.add(new SlashCommandEx("randomize_status", ".", true, ID.NICO));

        OptionData pieceOption = new OptionData(OptionType.STRING, "piece", "The type of armor", true)
                .addChoice("Helmet", "helmet")
                .addChoice("Chestplate", "chestplate")
                .addChoice("Leggings", "leggings")
                .addChoice("Boots", "boots");

        OptionData hexOption = new OptionData(OptionType.STRING, "hex", "The Hex color (e.g. FFFFFF)", true);

        commands.add(new SlashCommandEx("generate_armor", "Generates leather armor")
                .addOptions(pieceOption, hexOption));

        commands.add(new SlashCommandEx("random_seymour" ,"gen random piece"));

        List<SlashCommandData> jdaData = new ArrayList<>();
        for (SlashCommandEx ex : commands) { jdaData.add(ex.data); }

        // Sync to the specific guild
        if (guild != null) {
            guild.updateCommands().addCommands(jdaData).queue();
            System.out.println("Custom Commands Synced in Sigma Nation");
        }

        // Also update Global Commands so User-Installable apps work everywhere
        event.getJDA().updateCommands().addCommands(jdaData).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommandEx commandEx = null;
        for (SlashCommandEx cmd : commands) {
            if (cmd.name.equals(event.getName())) {
                commandEx = cmd;
                break;
            }
        }

        if (commandEx != null && commandEx.protection) {
            if (!isAuthorized(event, commandEx.authorizedID)) {
                event.reply("Nice try, " + event.getUser().getEffectiveName() + ". You aren't authorized to use this.")
                        .setEphemeral(true)
                        .queue();
                return;
            }
        }

        if (event.getUser().getId().equals(ID.NICO) || commandsEnabled) {
            switch (event.getName()) {
                case "toggle_commands" -> {
                    commandsEnabled = util.toggleBoolean(commandsEnabled);
                    event.reply("Commands are now " + (commandsEnabled ? "**True**" : "**False**")).setEphemeral(true).queue();
                }
                case "stop" -> {
                    event.reply("Shutting Down nic7").queue();
                    event.getJDA().shutdown();
                    System.exit(0);
                }
                case "restart" -> {
                    event.reply("Restarting and checking for update...").queue(success -> {
                        try {
                            ProcessBuilder pb = new ProcessBuilder("setsid", "sh", "/home/ubuntu/DiscordBot/update_bot.sh");
                            pb.start();
                            Thread.sleep(1000);
                            event.getJDA().shutdown();
                            System.exit(0);
                        } catch (Exception e) {
                            event.getChannel().sendMessage("Critical error: " + e.getMessage()).queue();
                        }
                    });
                }
                case "ping" -> event.reply("Pong").queue();
                case "version" -> event.reply("Version #" + util.VERSION).queue();
                case "set_status" -> {
                    String newActivity = event.getOption("status").getAsString();
                    event.getJDA().getPresence().setActivity(Activity.customStatus(newActivity));
                    event.reply("Status updated to: ** " + newActivity + "**").queue();
                }
                case "reddit_post" -> {
                    String postBody = event.getOption("body").getAsString();
                    String postTitle = event.getOption("title").getAsString();
                    var attachmentOption = event.getOption("attachment");
                    var replyAction = event.reply("# " + postTitle + "\n" + postBody + "\n" + "-# __" + "Post created by " + event.getUser().getName() + "__");
                    if (attachmentOption != null) {
                        Message.Attachment attachment = attachmentOption.getAsAttachment();
                        replyAction.addFiles(FileUpload.fromData(attachment.getProxy().download().join(), attachment.getFileName()));
                    }
                    replyAction.queue(hook -> hook.retrieveOriginal().queue(message -> {
                        message.addReaction(Emoji.fromCustom("updoot", 1474560551773536366L, false)).queue();
                        message.addReaction(Emoji.fromCustom("downdoot", 1474560608346312936L, false)).queue();
                    }));
                }
                case "toggle_andy_reply" -> {
                    util.andyReply = util.toggleBoolean(util.andyReply);
                    event.reply("Andy Reply is now " + util.andyReply).setEphemeral(true).queue();
                }
                case "toggle_bruh" -> {
                    var user = event.getOption("user").getAsUser().getId();
                    if (user.equals(ID.ANDY)) {
                        util.andyBruh = util.toggleBoolean(util.andyBruh);
                        event.reply("andyBruh is now " + util.andyBruh).setEphemeral(true).queue();
                    } else if (user.equals(ID.AIDEN)) {
                        util.aidenBruh = util.toggleBoolean(util.aidenBruh);
                        event.reply("aidenBruh is now " + util.aidenBruh).setEphemeral(true).queue();
                    } else {
                        event.deferReply().queue();
                    }
                }
                case "m" -> {
                    String message = event.getOption("body").getAsString();
                    var fileOption = event.getOption("file");
                    var sendAction = event.getChannel().sendMessage(message);
                    if (fileOption != null) {
                        var attachment = fileOption.getAsAttachment();
                        sendAction.addFiles(FileUpload.fromData(attachment.getProxy().download().join(), attachment.getFileName()));
                    }
                    sendAction.queue();
                    event.reply("Sent: **" + message + "**").setEphemeral(true).queue();
                }
                case "get_avatar" -> {
                    var user = event.getOption("user").getAsUser().getEffectiveAvatarUrl();
                    event.reply(user).queue();
                }
                case "randomize_status" -> event.getJDA().getPresence().setActivity(Activity.customStatus(sm.randomStatus()));
                case "generate_armor" -> {
                    var pieceOpt = event.getOption("piece");
                    var hexOpt = event.getOption("hex");
                    if (pieceOpt == null || hexOpt == null) return;

                    String piece = pieceOpt.getAsString();
                    String hex = hexOpt.getAsString().replace("#", "");
                    event.deferReply().queue();

                    try {
                        String urlString = "https://nico-armor-api.vercel.app/api/" + piece + "/" + hex;
                        URL url = new URI(urlString).toURL();
                        try (InputStream in = url.openStream()) {
                            byte[] imageBytes = in.readAllBytes();
                            FileUpload file = FileUpload.fromData(imageBytes, "armor.png");
                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("Dye Result: " + piece.substring(0, 1).toUpperCase() + piece.substring(1))
                                    .setColor(Color.decode("0x" + hex))
                                    .setImage("attachment://armor.png")
                                    .setFooter("Hex: #" + hex);

                            event.getHook().sendMessageEmbeds(embed.build()).addFiles(file).queue();
                        }
                    } catch (Exception e) {
                        event.getHook().sendMessage("Failed to generate armor: " + e.getMessage()).setEphemeral(true).queue();
                    }
                }

                case "random_seymour" -> {
                    // 1. MUST DEFER FIRST
                    event.deferReply().queue();

                    Random ran = new Random();
                    String[] pieceList = {"helmet", "chestplate", "leggings", "boots"};

                    // Pick the piece ONCE so the Title and URL match
                    String piece = pieceList[ran.nextInt(pieceList.length)];

                    String charList = "0123456789ABCDEF";
                    StringBuilder hexBuilder = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        hexBuilder.append(charList.charAt(ran.nextInt(charList.length())));
                    }
                    String hex = hexBuilder.toString();

                    try {
                        // Use the 'piece' variable we picked above
                        String urlString = "https://nico-armor-api.vercel.app/api/" + piece + "/" + hex;
                        URL url = new URI(urlString).toURL();

                        try (InputStream in = url.openStream()) {
                            byte[] imageBytes = in.readAllBytes();
                            FileUpload file = FileUpload.fromData(imageBytes, "armor.png");
                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("Random Dye: " + piece.substring(0, 1).toUpperCase() + piece.substring(1))
                                    .setColor(Color.decode("0x" + hex))
                                    .setImage("attachment://armor.png")
                                    .setFooter("Hex: #" + hex);

                            event.getHook().sendMessageEmbeds(embed.build()).addFiles(file).queue();
                        }
                    } catch (Exception e) {
                        // Use getHook() since we deferred
                        event.getHook().sendMessage("Failed to generate armor: " + e.getMessage()).setEphemeral(true).queue();
                    }
                }

            }
        } else {
            event.deferReply().queue();
        }
    }
}