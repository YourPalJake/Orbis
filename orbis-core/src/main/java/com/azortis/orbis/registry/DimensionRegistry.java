/*
 * MIT License
 *
 * Copyright (c) 2021 Azortis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.azortis.orbis.registry;

import com.azortis.orbis.Orbis;
import com.azortis.orbis.container.Container;
import com.azortis.orbis.generator.Dimension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class DimensionRegistry implements Registry<Dimension> {

    public DimensionRegistry() {
    }

    @Override
    public Dimension loadType(Container container, String name, Object... context) {
        File dimensionFile = new File(container.getSettingsFolder(), name + ".json");
        try {
            return Orbis.getGson().fromJson(new FileReader(dimensionFile), Dimension.class);
        }catch (FileNotFoundException ex){
            Orbis.getLogger().error("Dimension file {} not found!", name);
        }
        return null;
    }

    @Override
    public List<String> getEntries(Container container) {
        return null;
    }

    @Override
    public void createFolders(Container container) {
        //Do nothing, dimensions are root
    }

    @Override
    public File getFolder(Container container) {
        return container.getSettingsFolder();
    }

}
