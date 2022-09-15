/*
 * A dynamic data-driven world generator plugin/library for Minecraft servers.
 *     Copyright (C) 2022 Azortis
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

package com.azortis.orbis;

import com.azortis.orbis.item.ItemFactory;
import com.azortis.orbis.pack.studio.Project;
import com.azortis.orbis.pack.studio.StudioWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

public interface Platform {

    @NotNull String adaptation();

    @NotNull Logger logger();

    @NotNull File directory();

    @NotNull ItemFactory itemFactory();

    @Nullable World getWorld(@NotNull String name);

    @NotNull Collection<World> worlds();

    @Nullable WorldAccess getWorldAccess(@NotNull String name);

    @NotNull Collection<WorldAccess> worldAccesses();

    @NotNull StudioWorld createStudioWorld(@NotNull Project project);

    @Nullable Player getPlayer(@NotNull UUID uuid);

    @NotNull Collection<Player> getPlayers();

    @Nullable Class<?> mainClass();

    @NotNull Class<? extends Settings> settingsClass();

    @NotNull Settings defaultSettings();

}
