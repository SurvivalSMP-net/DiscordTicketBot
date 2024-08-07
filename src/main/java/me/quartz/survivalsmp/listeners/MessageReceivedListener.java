package me.quartz.survivalsmp.listeners;

import me.quartz.survivalsmp.Main;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageReceivedListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT) && event.getChannel().getName().startsWith("ticket-") && !event.getAuthor().isBot()) {
            TextChannel textChannel = (TextChannel) event.getGuild().getChannels().stream().filter(guildChannel -> guildChannel.getName().contains("staff-support")).findAny().orElse(null);
            if(textChannel != null) {
                String translation = Main.getTranslateManager().translateText(event.getMessage().getContentRaw(), "en");
                ThreadChannel thread = textChannel.getThreadChannels().stream().filter(threadChannel -> threadChannel.getName().equals(event.getChannel().getName())).findAny().orElse(null);
                if(thread != null) {
                    thread.sendMessage("**Message in " + event.getChannel().getAsMention() + " from " + event.getAuthor().getAsMention() + "!**\n\n" + translation).queue();
                } else {
                    textChannel.createThreadChannel(event.getChannel().getName()).queue(threadChannel -> {
                        threadChannel.sendMessage("**Message in " + event.getChannel().getAsMention() + " from " + event.getAuthor().getAsMention() + "!**\n\n" + translation).queue();
                    });
                }
            }
        } else if (event.isFromType(ChannelType.TEXT) && event.getChannel().getName().contains("staff-support") && !event.getAuthor().isBot() && event.getMessage().getReferencedMessage() != null) {
            if(!event.getMessage().getReferencedMessage().getMentions().getChannels().isEmpty()) {
                TextChannel textChannel = (TextChannel) event.getMessage().getReferencedMessage().getMentions().getChannels().get(0);
                if(textChannel != null) {
                    String language = textChannel.getName().split("-")[1];
                    String translation = Main.getTranslateManager().translateText(event.getMessage().getContentRaw(), language);
                    textChannel.sendMessage(
                            translation
                    ).queue();
                }
            }
        }
    }
}
