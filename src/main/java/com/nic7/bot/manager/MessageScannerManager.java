package com.nic7.bot.manager;

import com.nic7.bot.ID;
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
            try (java.io.InputStream andyImage = getClass().getResourceAsStream("/com/nic7/bot/file/andy.png")) {
                if (andyImage != null) {
                    event.getMessage().replyFiles(FileUpload.fromData(andyImage, "andy.png")).queue();
                    event.getChannel().sendMessage("andy " + Emoji.fromUnicode("\uD83D\uDC94").getAsReactionCode()).queue();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        if (message.contains("😳")) {event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDE33")).queue();}

        if (message.toLowerCase().contains("burger")) {event.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDF54")).queue();}

        // Andy or Aiden
        if ((event.getAuthor().getId().equals(ID.ANDY) && util.andyBruh) || (event.getAuthor().getId().equals(ID.AIDEN) && util.aidenBruh)) {

            event.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDDE7")).queue(v -> {
                event.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDDF7")).queue(v2 -> {
                    event.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDDFA")).queue(v3 -> {
                        event.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDDED")).queue(v4 -> {

                            String fileName = event.getAuthor().getId().equals(ID.ANDY) ? "andy.png" : "professor.gif";
                            String path = "/com/nic7/bot/file/" + fileName;

                            try (java.io.InputStream stream = getClass().getResourceAsStream(path)) {
                                if (stream != null) {
                                    event.getMessage().replyFiles(FileUpload.fromData(stream, fileName)).queue();
                                }
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                });
            });
        }

        if (message.equals("<@1474096115589714071> vc?") && event.getAuthor().getId().equals(ID.NICO)) {
            var member = event.getMember();
            var audioManager = event.getGuild().getAudioManager();

            // Get the bot's own voice state in this server
            var selfVoiceState = event.getGuild().getSelfMember().getVoiceState();

            if (member != null && member.getVoiceState() != null && member.getVoiceState().inAudioChannel()) {
                var targetChannel = member.getVoiceState().getChannel();

                // More robust check: Is the bot ALREADY in a channel?
                if (selfVoiceState != null && selfVoiceState.inAudioChannel()) {
                    // If we are already in the right channel, do nothing
                    if (selfVoiceState.getChannel().equals(targetChannel)) {
                        return;
                    }
                }

                audioManager.openAudioConnection(targetChannel);
            }
        }

        if (message.toLowerCase().contains("jarvis clip that")) {
            net.dv8tion.jda.api.entities.Message repliedMessage = event.getMessage().getReferencedMessage();

            if (repliedMessage != null) {
                String originalText = repliedMessage.getContentRaw();
                String originalAuthor;

                switch (repliedMessage.getAuthor().getId()) {
                    case ID.AIDEN -> originalAuthor = "Aiden";
                    case ID.WYATT -> originalAuthor = "Wyatt";
                    case ID.ANDY -> originalAuthor = "Andy";
                    case ID.ASPEN -> originalAuthor = "Aspen";
                    case ID.CARSON -> originalAuthor = "Carson";
                    case ID.MARI -> originalAuthor = "Mari";
                    case ID.MACEY -> originalAuthor = "Macey";
                    case ID.NICO -> originalAuthor = "Nico";
                    default -> originalAuthor = repliedMessage.getAuthor().getEffectiveName();
                }

                event.getChannel().sendMessage("nahhh " + Emoji.fromUnicode("\uD83D\uDC80") + " " + originalAuthor + " really said \"" + originalText + "\"");
            }
        }
    }

}
