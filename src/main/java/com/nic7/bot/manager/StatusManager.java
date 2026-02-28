package com.nic7.bot.manager;

import com.nic7.bot.ID;
import com.nic7.bot.util;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class StatusManager extends ListenerAdapter {
    Random r = new Random();

    public static String[] defaultStatusList = {
            "Hello Sigma Nation!",
            "Default Status Here",
            "Hello Steven...",
            "Insert Status Here",
            "|---|---|---|"
    };

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getPresence().setActivity(Activity.customStatus(randomStatus()));
    }

    public String randomStatus() {
        return (defaultStatusList[r.nextInt(0,defaultStatusList.length)]);
    }

}