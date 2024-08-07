package me.quartz.survivalsmp;

import me.quartz.survivalsmp.commands.SetupLangCommand;
import me.quartz.survivalsmp.listeners.ButtonInteractionListener;
import me.quartz.survivalsmp.listeners.MessageReceivedListener;
import me.quartz.survivalsmp.translate.TranslateManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    private static JDA jda;
    private static TranslateManager translateManager;

    public static void main(String[] args) {
        jda = JDABuilder
                .createDefault("TOKEN")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.watching("The Tickets"))
                .build();

        translateManager = new TranslateManager();

        try {
            jda.awaitReady();

            jda.updateCommands().addCommands(
                    Commands.slash("setuplang", "Setup a new language you want.")
                            .addOption(OptionType.STRING, "language", "The language you want to have added.")
            ).queue();

            jda.addEventListener(new SetupLangCommand());
            jda.addEventListener(new ButtonInteractionListener());
            jda.addEventListener(new MessageReceivedListener());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static JDA getJda() {
        return jda;
    }

    public static TranslateManager getTranslateManager() {
        return translateManager;
    }
}