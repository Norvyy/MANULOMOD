package com.example.examplemod.item;

import com.example.examplemod.ManuloMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class ModItems {
    private static final DeferredRegister<Item> ITEMS= DeferredRegister.create(ITEMS,ManuloMod.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
