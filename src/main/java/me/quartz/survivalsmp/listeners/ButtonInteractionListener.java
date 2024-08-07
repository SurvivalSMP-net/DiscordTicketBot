package me.quartz.survivalsmp.listeners;

import me.quartz.survivalsmp.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.Random;

public class ButtonInteractionListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("create-ticket")) {
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);
            String[] splitted = messageEmbed.getFooter().getText().split(" ");
            String language = splitted[splitted.length - 1];

            event.getGuild().createTextChannel("ticket-" + language + "-" + event.getUser().getEffectiveName()).queue(textChannel -> {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setDescription(Main.getTranslateManager().translateText("Please shoot your requirements, support will be with you shortly.\nHappy Gaming", language) + " 🎉");
                embedBuilder.setFooter(Main.getTranslateManager().translateText("SurvivalSMP - Ticket Creator", language));
                embedBuilder.setColor(Color.decode("#008000"));
                textChannel.sendMessageEmbeds(embedBuilder.build()).setActionRow(Button.secondary("close-ticket", "📩 " + Main.getTranslateManager().translateText("Terminate", language))).queue();
                event.getHook().sendMessage("Your language has been setupped.").queue();
            });
        } else if (event.getComponentId().equals("close-ticket")) {
            event.getChannel().delete().queue();
        }
    }
}
