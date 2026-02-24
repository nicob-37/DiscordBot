package com.nic7.bot.manager;

import com.nic7.bot.ID;
import com.nic7.bot.util;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class StatusManager extends ListenerAdapter {

    @Override
    public void onUserUpdateActivities(@NotNull UserUpdateActivitiesEvent event) {

    }

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getPresence().setActivity(Activity.customStatus("Hello Sigma Nation!"));
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        String message = event.getRawData().toString();

        var NIC7_LOGS = event.getGuild().getTextChannelById(ID.NIC7_LOGS);

        NIC7_LOGS.sendMessage(message).queue();
    }
}