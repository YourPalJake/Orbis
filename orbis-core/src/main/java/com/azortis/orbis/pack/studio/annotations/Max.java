/*
 * A dynamic data-driven world generator plugin/library for Minecraft servers.
 *     Copyright (C) 2023 Azortis
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

package com.azortis.orbis.pack.studio.annotations;

import org.apiguardian.api.API;

import java.lang.annotation.*;

/**
 * Sets the maximum value an {@link Number} can be of a {@link java.lang.reflect.Field}
 * or {@link java.util.Collection} entry.
 *
 * @author Jake Nijssen
 * @since 0.3-Alpha
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@API(status = API.Status.STABLE, since = "0.3-Alpha")
public @interface Max {

    /**
     * The maximum value if the annotated type is an integer.
     *
     * @return The minimum integer value for the field/entry.
     */
    long value() default Long.MAX_VALUE;

    /**
     * The maximum value if the annotated type is a floating point number.
     *
     * @return The minimum floating point value for the field/entry.
     */
    double floating() default Double.MAX_VALUE;
}
