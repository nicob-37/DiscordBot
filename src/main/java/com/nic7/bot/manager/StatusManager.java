package com.nic7.bot.manager;

import com.nic7.bot.ID;
import com.nic7.bot.util;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class StatusManager extends ListenerAdapter {

    public static String[] defaultStatusList = {
            "Hello Sigma Nation!",
            "Default Status Here",
            "Hello Steven...",
            "Hello Aiden..."};

    @Override
    public void onReady(ReadyEvent event) {
        Random r = new Random();
        event.getJDA().getPresence().setActivity(Activity.customStatus(defaultStatusList[r.nextInt(0,defaultStatusList.length)]));
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        String message = event.getRawData().toString();

        var NIC7_LOGS = event.getGuild().getTextChannelById(ID.NIC7_LOGS);

        NIC7_LOGS.sendMessage(message).queue();
    }
}