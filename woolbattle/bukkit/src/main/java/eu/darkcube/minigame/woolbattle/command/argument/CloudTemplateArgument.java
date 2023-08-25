/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.argument;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.driver.template.TemplateStorage;
import eu.cloudnetservice.driver.template.TemplateStorageProvider;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class CloudTemplateArgument implements ArgumentType<ServiceTemplate> {

    private static final DynamicCommandExceptionType NO_TEMPLATE = Messages.INVALID_ENUM.newDynamicCommandExceptionType();

    private CloudTemplateArgument() {
    }

    public static CloudTemplateArgument template() {
        return new CloudTemplateArgument();
    }

    public static ServiceTemplate template(CommandContext<?> ctx, String name) {
        return ctx.getArgument(name, ServiceTemplate.class);
    }

    @Override public ServiceTemplate parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String text = read(reader);
        ServiceTemplate template = ServiceTemplate.parse(text);
        if (template == null) {
            reader.setCursor(cursor);
            throw NO_TEMPLATE.createWithContext(reader, text);
        }
        return template;
    }

    private String read(StringReader reader) {
        StringBuilder b = new StringBuilder();
        while (reader.canRead()) {
            char c = reader.peek();
            if (c == ' ') break;
            b.append(c);
            reader.skip();
        }
        return b.toString();
    }

    @Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        TemplateStorageProvider provider = InjectionLayer.boot().instance(TemplateStorageProvider.class);
        return provider.availableTemplateStoragesAsync().thenCompose(s -> {
            Collection<CompletableFuture<Collection<ServiceTemplate>>> templatesFutures = new ArrayList<>();
            for (String storageName : s) {
                TemplateStorage storage = provider.templateStorage(storageName);
                if (storage != null) templatesFutures.add(storage.templatesAsync());
            }
            return CompletableFuture.allOf(templatesFutures.toArray(new CompletableFuture[0])).thenApply(ignored -> {
                Collection<ServiceTemplate> built = new ArrayList<>();
                for (CompletableFuture<Collection<ServiceTemplate>> fut : templatesFutures) {
                    built.addAll(fut.getNow(null));
                }
                ISuggestionProvider.suggest(built.stream().map(ServiceTemplate::toString), builder);
                return builder.build();
            });
        });
    }
}
