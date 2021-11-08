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

package com.azortis.orbis.block.data.type;

import com.azortis.orbis.block.data.Directional;
import com.azortis.orbis.block.data.Powerable;
import com.azortis.orbis.block.property.BellAttachment;
import com.azortis.orbis.block.property.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Bell extends Directional, Powerable {

    EnumProperty<BellAttachment> ATTACHMENT = new EnumProperty<>("attachment",
            Set.of(BellAttachment.FLOOR, BellAttachment.CEILING, BellAttachment.SINGLE_WALL, BellAttachment.DOUBLE_WALL));

    @NotNull
    default BellAttachment getAttachment(){
        return getProperty(ATTACHMENT);
    }

    default void setAttachment(@NotNull BellAttachment attachment){
        setProperty(ATTACHMENT, attachment);
    }

}
