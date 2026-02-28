package com.nic7.bot.manager;

import com.nic7.bot.ID;
import com.nic7.bot.util;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;

public class CommandsManager extends ListenerAdapter {
    List<SlashCommandEx> commands = new ArrayList<>();
    StatusManager sm = new StatusManager();

    public static boolean commandsEnabled = true;

    // Initializes Commands On Bot Ready
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
            this.data = Commands.slash(name, description);
        }

        public SlashCommandEx(String name, String description) {
            this.name = name; this.description = description; this.protection = false;
            this.data = Commands.slash(name, description);
        }

        public SlashCommandEx addOption(OptionType type, String name, String desc, boolean required) {
            this.data.addOption(type, name, desc, required);
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
        var guild = event.getJDA().getGuildById(ID.SIG_NATION);

        commands.add(new SlashCommandEx("toggle_commands", ".", true, ID.NICO));

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

        commands.add(new SlashCommandEx("message", "insert text here", true, ID.NICO)
                .addOption(OptionType.STRING, "body", "insert text here", true));

        commands.add(new SlashCommandEx("get_avatar", "gets a user's custom avatar")
                .addOption(OptionType.USER, "user", ".", true));

        commands.add(new SlashCommandEx("randomize_status", ".", true, ID.NICO));

        List<SlashCommandData> jdaData = new ArrayList<>();

        for (SlashCommandEx ex : commands) {jdaData.add(ex.data);}

        if (guild != null) {
            guild.updateCommands().addCommands(jdaData).queue();
            System.out.println("Custom Commands Synced in Sigma Nation");
        }
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

                case "ping" -> {
                    event.reply("Pong").queue();
                }

                case "version" -> {
                    event.reply("Version #" + util.VERSION).queue();
                }

                case "set_status" -> {
                    String newActivity = event.getOption("status").getAsString();

                    event.getJDA().getPresence().setActivity(Activity.customStatus(newActivity));
                    event.reply("Status updated to: ** " + newActivity + "**").queue();
                }

                case "reddit_post" -> {
                    String postBody = event.getOption("body").getAsString();
                    String postTitle = event.getOption("title").getAsString();
                    var attachmentOption = event.getOption("attachment");

                    var replyAction = event.reply(
                            "# " + postTitle
                                    + "\n"
                                    + postBody
                                    + "\n"
                                    + "-# __" + "Post created by " + event.getUser().getName() + "__");

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
                        return;
                    }

                }

                case "message" -> {
                    var message = event.getOption("body").getAsString();
                    event.getChannel().sendMessage(message).queue();
                    event.deferReply().setEphemeral(true).queue();
                }

                case "get_avatar" -> {
                    var user = event.getOption("user").getAsUser().getEffectiveAvatarUrl();
                    event.reply(user).queue();
                }

                case "randomize_status" -> {
                    event.getJDA().getPresence().setActivity(Activity.customStatus(sm.randomStatus()));
                }

            }
        }
    }

}
