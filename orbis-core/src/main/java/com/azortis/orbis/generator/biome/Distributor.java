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

package com.azortis.orbis.generator.biome;

import com.azortis.orbis.pack.studio.annotations.Directory;
import com.azortis.orbis.pack.studio.annotations.GeneratorType;
import com.azortis.orbis.registry.DistributorRegistry;
import com.azortis.orbis.world.Container;
import net.kyori.adventure.key.Key;

import java.io.File;

@Directory(value = "/generators/distributor/", searchSubFolders = false)
@GeneratorType(DistributorRegistry.class)
public abstract class Distributor {

    private String name;
    private Key providerKey;
    private transient Container container;
    private transient File settingsFolder;

    // Used for GSON deserialization
    protected Distributor() {
    }

    public String getName() {
        return name;
    }

    public Key getProviderKey() {
        return providerKey;
    }

    public Distributor(String name, Key providerKey) {
        this.name = name;
        this.providerKey = providerKey;
    }

    //
    // Container
    //

    public void setContainer(Container container) {
        if (this.container == null) this.container = container;
    }

    public Container getContainer() {
        return container;
    }


    //
    // SettingsFolder
    //

    public void setSettingsFolder(File settingsFolder) {
        if (this.settingsFolder == null) this.settingsFolder = settingsFolder;
    }

    public File getSettingsFolder() {
        return settingsFolder;
    }

    //
    // Abstracted methods
    //

    public abstract void load();

    public abstract Biome getBiomeAt(int x, int z);

    public abstract Biome getBiomeAt(double x, double z);

}
