package com.nic7.bot.manager;

import com.nic7.bot.ID;
import com.nic7.bot.util;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class StatusManager extends ListenerAdapter {

    private String lastStatus = "";

    @Override
    public void onUserUpdateActivities(@NotNull UserUpdateActivitiesEvent event) {
        if (!event.getUser().getId().equals("YOUR_USER_ID")) return;

        TextChannel channel = event.getGuild().getTextChannelById(ID.NIC7_LOGS);
        if (channel == null) return;

        String currentStatus = "";

        for (Activity activity : event.getMember().getActivities()) {
            if (activity.getType() == Activity.ActivityType.CUSTOM_STATUS) {
                currentStatus = activity.getName();
                break;
            }
        }

        if (!currentStatus.equals(lastStatus)) {
            lastStatus = currentStatus;

            String displayStatus = currentStatus.isEmpty() ? "Cleared their status" : "updated their status to: " + currentStatus;
            channel.sendMessage(event.getUser().getEffectiveName() + " " + displayStatus).queue();
        }
    }
}