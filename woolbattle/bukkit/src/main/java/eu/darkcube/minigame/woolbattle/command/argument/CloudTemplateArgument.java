/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.argument;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.driver.template.TemplateStorage;
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
        CompletableFuture<Suggestions> f = new CompletableFuture<>();
        CloudNetDriver.getInstance().getAvailableTemplateStoragesAsync().onComplete(s -> {
            Collection<CompletableFuture<Collection<ServiceTemplate>>> templatesFutures = new ArrayList<>();
            for (TemplateStorage storage : s) {
                CompletableFuture<Collection<ServiceTemplate>> future = new CompletableFuture<>();
                storage
                        .getTemplatesAsync()
                        .onComplete(future::complete)
                        .onFailure(future::completeExceptionally)
                        .onCancelled(collectionITask -> future.cancel(false));
                templatesFutures.add(future);
            }
            CompletableFuture.allOf(templatesFutures.toArray(new CompletableFuture[0])).thenRun(() -> {
                Collection<ServiceTemplate> built = new ArrayList<>();
                for (CompletableFuture<Collection<ServiceTemplate>> fut : templatesFutures) {
                    built.addAll(fut.getNow(null));
                }
                ISuggestionProvider.suggest(built.stream().map(ServiceTemplate::toString), builder);
                f.complete(builder.build());
            });
        }).onFailure(f::completeExceptionally).onCancelled(collectionITask -> f.cancel(true));
        return f;
    }
}
