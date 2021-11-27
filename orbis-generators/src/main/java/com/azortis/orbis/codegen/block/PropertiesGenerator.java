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

package com.azortis.orbis.codegen.block;

import com.azortis.orbis.block.property.*;
import com.azortis.orbis.codegen.OrbisCodeGenerator;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.squareup.javapoet.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class PropertiesGenerator extends OrbisCodeGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesGenerator.class);
    private static final ImmutableMap<String, Class<? extends Enum<?>>> enumClasses;

    static {
        ImmutableMap.Builder<String, Class<? extends Enum<?>>> builder = ImmutableMap.builder();
        enumClasses = builder
                .put("AttachFace", AttachFace.class)
                .put("Axis", Axis.class)
                .put("BambooLeaves", BambooLeaves.class)
                .put("BedPart", BedPart.class)
                .put("BellAttachType", BellAttachType.class)
                .put("ChestType", ChestType.class)
                .put("ComparatorMode", ComparatorMode.class)
                .put("Direction", Direction.class)
                .put("DoorHingeSide", DoorHingeSide.class)
                .put("DoubleBlockHalf", DoubleBlockHalf.class)
                .put("DripstoneThickness", DripstoneThickness.class)
                .put("Half", Half.class)
                .put("NoteBlockInstrument", NoteBlockInstrument.class)
                .put("FrontAndTop", Orientation.class)
                .put("PistonType", PistonType.class)
                .put("RailShape", RailShape.class)
                .put("RedstoneSide", RedstoneSide.class)
                .put("SculkSensorPhase", SculkSensorPhase.class)
                .put("SlabType", SlabType.class)
                .put("StairsShape", StairsShape.class)
                .put("StructureMode", StructureMode.class)
                .put("Tilt", Tilt.class)
                .put("WallSide", WallSide.class).build();
    }

    public PropertiesGenerator(InputStream inputStream, File outputFolder) {
        super(inputStream, outputFolder);
    }

    @Override
    public void generate() {
        if(this.inputStream == null){
            LOGGER.error("Failed to find properties data file");
            return;
        }
        if(!outputFolder.exists() && !outputFolder.mkdirs()){
            LOGGER.error("Output folder doesn't exist and failed to create it!");
            return;
        }

        JsonObject properties = GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);

        // Property types
        ClassName booleanProperty = ClassName.get(BooleanProperty.class);
        ClassName integerProperty = ClassName.get(IntegerProperty.class);
        ClassName enumProperty = ClassName.get(EnumProperty.class);

        // Class to write to
        ClassName propertiesClassName = ClassName.get("com.azortis.orbis.block.property", "Properties");
        AnnotationSpec suppressUnused = AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unused").build();
        TypeSpec.Builder propertiesClass = TypeSpec.classBuilder(propertiesClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(suppressUnused)
                .addJavadoc("This class has been autogenerated.");

        for (Map.Entry<String, JsonElement> entry : properties.entrySet()){
            final String propertyName = getPropertyName(entry.getKey());
            final JsonObject propertyObject = entry.getValue().getAsJsonObject();

            String key = propertyObject.get("key").getAsString();
            JsonArray values = propertyObject.getAsJsonArray("values");

            if(!propertyObject.has("enumMojangName")){
                JsonPrimitive primitive = values.get(0).getAsJsonPrimitive();

                if(primitive.isBoolean()){
                    propertiesClass.addField(
                            FieldSpec.builder(booleanProperty, propertyName)
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                    .addAnnotation(NotNull.class)
                                    .initializer("$T.create($S)", booleanProperty, key).build());
                } else {
                    int min = Integer.MAX_VALUE;
                    int max = Integer.MIN_VALUE;
                    for (JsonElement element : values){
                        int intValue = element.getAsInt();
                        if(intValue < min)min = intValue;
                        if(intValue > max)max = intValue;
                    }
                    propertiesClass.addField(
                            FieldSpec.builder(integerProperty, propertyName)
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                    .addAnnotation(NotNull.class)
                                    .initializer("$T.create($S, $L, $L)", integerProperty, key, min, max)
                                    .build());
                }
            } else {
                String mojangEnumName = propertyObject.get("enumMojangName").getAsString();
                Class<? extends Enum<?>> enumClass = enumClasses.get(mojangEnumName);
                ClassName enumClassName = getEnumClass(mojangEnumName);
                ParameterizedTypeName enumType = ParameterizedTypeName.get(enumProperty, enumClassName);

                // If the values contain all the enum constants we can do it very simple.
                if(enumClass == null){
                    LOGGER.error("Mojang enum class: " + mojangEnumName + " is missing!");
                    break;
                }

                if(values.size() == enumClass.getEnumConstants().length){
                    propertiesClass.addField(
                            FieldSpec.builder(enumType, propertyName)
                                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                    .addAnnotation(NotNull.class)
                                    .initializer("$T.create($S, $T.class)", enumProperty, key, enumClassName)
                                    .build());
                    continue;
                }

                List<String> enumValues = new ArrayList<>();
                for (JsonElement element : values){
                    enumValues.add(element.getAsString());
                }
                CodeBlock.Builder builder = CodeBlock.builder();
                builder.add("$T.create($S, $T.class", enumProperty, key, enumClassName);
                // If size of values > 60% of the possible enum constants then we use a predicate
                if(values.size() >= Math.round(0.60D * enumClass.getEnumConstants().length)){
                    builder.add(", (" + key + ") -> ");
                    List<String> enumsToFilter = getEnumValues(enumClass).stream().filter(s -> !enumValues.contains(s))
                            .collect(Collectors.toList());
                    boolean isFirst = true;
                    for (String enumName : enumsToFilter){
                        if(isFirst){
                            isFirst = false;
                            builder.add(key + " != $T." + enumName, enumClassName);
                            continue;
                        }
                        builder.add(" && " + key + " != $T." + enumName, enumClassName);
                    }
                } else {
                    for (String enumName : enumValues){
                        builder.add(", $T." + enumName, enumClassName);
                    }
                }
                builder.add(")");
                propertiesClass.addField(
                        FieldSpec.builder(enumType, propertyName)
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                .addAnnotation(NotNull.class)
                                .initializer(builder.build())
                                .build());
            }
        }
        JavaFile propertiesFile = JavaFile.builder("com.azortis.orbis.block.property", propertiesClass.build()).build();
        writeFiles(List.of(propertiesFile));
    }

    private List<String> getEnumValues(Class<? extends Enum<?>> enumClass){
        return Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.toList());
    }

    private ClassName getEnumClass(String mojangEnum){
        return ClassName.get(Objects.requireNonNull(enumClasses.get(mojangEnum)));
    }

    private String getPropertyName(String mojangPropertyName){
        if(PropertyRegistry.NAME_REWRITES.containsKey(mojangPropertyName))return PropertyRegistry.NAME_REWRITES.get(mojangPropertyName);
        return mojangPropertyName;
    }

}
