package me.quartz.survivalsmp.commands;

import me.quartz.survivalsmp.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.EnumSet;

public class SetupLangCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("setuplang")) {
            event.deferReply(true).queue();

            String language = event.getOption("language") != null ? event.getOption("language").getAsString() : "";

            if (language.length() == 2) {
                final Category[] categoryj = {null};
                event.getGuild().getCategories().forEach(c -> {
                    if (c.getName().contains("support")) {
                        categoryj[0] = c;
                    }
                });
                if(categoryj[0] != null) {
                    Category category = categoryj[0];
                    event.getGuild().createRole()
                            .setName(language)
                            .setHoisted(false)
                            .setMentionable(false)
                            .queue(role -> {
                                category.createTextChannel(Main.getTranslateManager().translateText("Support", language)).queue(textChannel -> {
                                    textChannel.getManager().putPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)).queue();
                                    textChannel.getManager().putPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null).queue();

                                    EmbedBuilder embedBuilder = new EmbedBuilder();
                                    embedBuilder.setTitle("Tickets");
                                    embedBuilder.setDescription(Main.getTranslateManager().translateText("To create a ticket react with", language) + " 📩");
                                    embedBuilder.setFooter(Main.getTranslateManager().translateText("SurvivalSMP - Ticket Creator", language) + " " + language);
                                    embedBuilder.setColor(Color.decode("#008000"));
                                    textChannel.sendMessageEmbeds(embedBuilder.build())
                                            .setActionRow(Button.primary("create-ticket", "📩 " + Main.getTranslateManager().translateText("Create Ticket", language)))
                                            .queue();
                                    event.getHook().sendMessage("Your language has been set up.").queue();
                                });
                            });
                } else {
                    event.getHook().sendMessage("There is no category for support.").queue();
                }
            } else {
                event.getHook().sendMessage("The code of your language is invalid.").queue();
            }
        }
    }
}
