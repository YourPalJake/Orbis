/*
 * A dynamic data-driven world generator plugin/library for Minecraft servers.
 *     Copyright (C) 2021  Azortis
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.azortis.orbis.paper;

import com.azortis.orbis.Orbis;
import com.azortis.orbis.pack.Pack;
import com.azortis.orbis.paper.generator.PaperChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OrbisPlugin extends JavaPlugin {

    private static final Map<String, PaperWorld> worldMap = new HashMap<>();
    private static PaperPlatform platform;
    private Metrics metrics;

    @Override
    public void onLoad() {
        if (!Bukkit.getMinecraftVersion().replaceAll("\\.", "_").equalsIgnoreCase(Orbis.MC_VERSION)) {
            this.getSLF4JLogger().error("Unsupported minecraft version! Only works on {}", Orbis.MC_VERSION);
            return;
        }
        this.metrics = new Metrics(this, 10874);
        try {
            platform = new PaperPlatform(this);
        } catch (Exception e) {
            this.getSLF4JLogger().error("Failed to create paper platform instance");
            e.printStackTrace();
            return;
        }
        Orbis.initialize(platform);
    }

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String packName) {
        if (packName != null) {
            Pack pack = Orbis.getPackManager().getPack(packName);
            if (pack != null) {
                return new PaperChunkGenerator(worldName, pack);
            } else {
                Orbis.getLogger().error("No pack by the fieldName {} exists!", packName);
                return super.getDefaultWorldGenerator(worldName, packName);
            }
        }
        Orbis.getLogger().error("No pack fieldName specified for world {}", worldName);
        return super.getDefaultWorldGenerator(worldName, null);
    }

    @NotNull
    public static PaperWorld loadWorld(@NotNull WorldInfo worldInfo, @NotNull Pack pack) {
        PaperWorld paperWorld = new PaperWorld(worldInfo);
        paperWorld.installPack(pack, false);
        if (!paperWorld.isLoaded()) paperWorld.load(worldInfo.getSeed());
        worldMap.put(worldInfo.getName(), paperWorld);
        return paperWorld;
    }

    @Nullable
    public static PaperWorld getWorld(WorldInfo worldInfo) {
        return worldMap.get(worldInfo.getName());
    }

    @Nullable
    public static PaperWorld getWorld(String worldName) {
        return worldMap.get(worldName);
    }

    public static Collection<PaperWorld> getWorlds() {
        return worldMap.values();
    }

    public static PaperPlatform getPlatform() {
        return platform;
    }
}
