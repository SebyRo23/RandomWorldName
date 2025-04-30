package com.mcupdater.randomworldname;

import com.mcupdater.randomworldname.setup.Config;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@Mod(RandomWorldName.MODID)
public class RandomWorldName {
    public static final String MODID = "randomworldname";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RandomWorldName(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        NeoForge.EVENT_BUS.addListener(this::injectButton);
    }

    private void injectButton(ScreenEvent.Init.Post evt) {
        if (evt.getScreen() instanceof CreateWorldScreen cws) {
            Button generateName = Button.builder(Component.translatable("button.randomworldname.generate"), button -> {
                cws.tabNavigationBar.tabs.stream().forEach(internalTab -> {
                    if (internalTab instanceof CreateWorldScreen.GameTab gTab) {
                        List<Supplier<String>> patterns = List.of(
                                () -> "The " + capitalizeFirst(capitalizeFirst(getRandomEntry(Config.ADJECTIVES.get()))) + " " + capitalizeFirst(getRandomEntry(Config.PLACES.get())),
                                () -> "The " + capitalizeFirst(getRandomEntry(Config.PLACES.get())) + " of " + capitalizeFirst(getRandomEntry(Config.ADJECTIVES.get())) + " " + capitalizeFirst(getRandomEntry(Config.NOUNS.get())),
                                () -> "The " + capitalizeFirst(getRandomEntry(Config.PLACES.get())) + " of " + capitalizeFirst(getRandomEntry(Config.NOUNS.get())),
                                () -> "The " + capitalizeFirst(getRandomEntry(Config.PLACES.get())) + " of the " + capitalizeFirst(getRandomEntry(Config.NOUNS.get())),
                                () -> capitalizeFirst(getRandomEntry(Config.ADJECTIVES.get())) + " " + capitalizeFirst(getRandomEntry(Config.PLACES.get())),
                                () -> capitalizeFirst(getRandomEntry(Config.PLACES.get())) + " of " + capitalizeFirst(capitalizeFirst(getRandomEntry(Config.ADJECTIVES.get()))) + " " + capitalizeFirst(getRandomEntry(Config.NOUNS.get())),
                                () -> capitalizeFirst(getRandomEntry(Config.PLACES.get())) + " of " + capitalizeFirst(getRandomEntry(Config.NOUNS.get())),
                                () -> capitalizeFirst(getRandomEntry(Config.PLACES.get())) + " of the " + capitalizeFirst(getRandomEntry(Config.NOUNS.get()))
                        );
                        String name = getRandomEntry(patterns.stream().map(Supplier::get).toList());
                        gTab.nameEdit.setValue(name);
                    }
                });
            }).size(210, 20).build();
            cws.tabNavigationBar.tabs.stream().forEach(tab -> {
                if (tab instanceof CreateWorldScreen.GameTab gameTab) {
                    gameTab.layout.visitChildren(child -> {
                        if (child instanceof LinearLayout worldNameLayout) {
                            worldNameLayout.addChild(generateName);
                        }
                    });
                    gameTab.layout.arrangeElements();
                }
            });
            cws.tabNavigationBar.selectTab(1,false);
            cws.tabNavigationBar.selectTab(0,false);
            generateName.onPress();
        }
    }

    private String getRandomEntry(List<? extends String> sourceSet) {
        int size = sourceSet.size();
        int entry = new Random().nextInt(size);
        int i = 0;
        for (String name : sourceSet) {
            if (i == entry) {
                return name;
            }
            i++;
        }
        return "";
    }
    private String capitalizeFirst(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
