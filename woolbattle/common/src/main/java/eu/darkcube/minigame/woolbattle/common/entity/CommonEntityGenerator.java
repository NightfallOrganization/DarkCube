/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.entity;

import static java.lang.classfile.ClassFile.*;
import static java.lang.constant.ConstantDescs.*;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.TypeKind;
import java.lang.classfile.attribute.SourceFileAttribute;
import java.lang.classfile.constantpool.FieldRefEntry;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class CommonEntityGenerator {
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private final ConcurrentMap<EntityType<?>, WrappedClass<?>> wrappedClasses = new ConcurrentHashMap<>();

    public <T extends Entity, W extends Entity> @NotNull T createWrapped(@NotNull EntityType<T> type, @NotNull W wrapped) {
        var w = wrappedClass(type, (EntityType<W>) wrapped.type());
        try {
            return type.entityTypeClass().cast(w.constructor().invoke(type, wrapped));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends Entity, W extends Entity> WrappedClass<? extends T> wrappedClass(EntityType<T> type, EntityType<W> wrappedType) {
        if (wrappedClasses.containsKey(type)) {
            return (WrappedClass<? extends T>) wrappedClasses.get(type);
        }
        return (WrappedClass<? extends T>) wrappedClasses.computeIfAbsent(type, _ -> {
            var cls = compute(type, wrappedType);
            return new WrappedClass<T>(cls, constructor(wrappedType, cls));
        });
    }

    private MethodHandle constructor(EntityType<?> type, Class<?> cls) {
        try {
            return lookup.findConstructor(cls, MethodType.methodType(void.class, EntityType.class, type.entityTypeClass()));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends Entity, W extends Entity> Class<? extends T> compute(EntityType<T> type, EntityType<W> wrappedType) {
        var CD_this = ClassDesc.of(CommonEntityImplementations.class.getPackageName() + ".Generated_" + type.key().asString().replace(":", "_"));
        var wrappedClass = type.entityTypeClass();
        if (!wrappedClass.isInterface()) {
            throw new IllegalArgumentException("EntityType base class must be an interface for wrapped generation");
        }
        var CD_wrapped = ClassDesc.ofDescriptor(wrappedClass.descriptorString());
        var CD_EntityType = ClassDesc.ofDescriptor(EntityType.class.descriptorString());
        var CD_GeneratedEntity = ClassDesc.ofDescriptor(GeneratedEntity.class.descriptorString());
        var CD_handle = ClassDesc.ofDescriptor(wrappedType.entityTypeClass().descriptorString());
        var CD_Entity = ClassDesc.ofDescriptor(Entity.class.descriptorString());
        var MTD_unwrapGeneratedEntity = MethodTypeDesc.of(CD_Entity);
        var MTD_init = MethodTypeDesc.of(CD_void, CD_EntityType, CD_handle);
        var MTD_type = MethodTypeDesc.of(CD_EntityType);
        var classFileBytes = of().build(CD_this, classBuilder -> {
            var FRE_handle = classBuilder.constantPool().fieldRefEntry(CD_this, "handle", CD_handle);
            var FRE_type = classBuilder.constantPool().fieldRefEntry(CD_this, "type", CD_EntityType);

            classBuilder.withInterfaceSymbols(CD_wrapped, CD_GeneratedEntity);
            classBuilder.with(SourceFileAttribute.of("generated"));
            classBuilder.withField(FRE_type.name(), FRE_type.type(), ACC_PRIVATE | ACC_FINAL);
            classBuilder.withField(FRE_handle.name(), FRE_handle.type(), ACC_PRIVATE | ACC_FINAL);
            classBuilder.withMethodBody("<init>", MTD_init, ACC_PUBLIC, code -> {
                var l_type = code.parameterSlot(0);
                var l_entity = code.parameterSlot(1);
                code.localVariable(l_type, "type", FRE_type.typeSymbol(), code.startLabel(), code.endLabel());
                code.localVariable(l_entity, "entity", FRE_handle.typeSymbol(), code.startLabel(), code.endLabel());
                code.aload(0);
                code.invokespecial(CD_Object, "<init>", MTD_void);

                code.aload(0);
                code.aload(l_type);
                code.putfield(FRE_type);

                code.aload(0);
                code.aload(l_entity);
                code.putfield(FRE_handle);
                code.return_();
            });
            classBuilder.withMethodBody("unwrapGeneratedEntity", MTD_unwrapGeneratedEntity, ACC_PUBLIC | ACC_FINAL, code -> {
                code.aload(0);
                code.getfield(FRE_handle);
                code.areturn();
            });
            Map<MethodType, Map.Entry<Method, MethodType>> methodMap = new HashMap<>();
            for (var method : wrappedClass.getMethods()) {
                var mod = method.getModifiers();
                if (Modifier.isStatic(mod)) continue;
                if (Modifier.isFinal(mod)) continue;
                var methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
                try {
                    var wrappedMethod = wrappedType.entityTypeClass().getMethod(method.getName(), method.getParameterTypes());
                    var wrappedMethodType = MethodType.methodType(wrappedMethod.getReturnType(), wrappedMethod.getParameterTypes());
                    methodMap.put(methodType, Map.entry(wrappedMethod, wrappedMethodType));
                } catch (NoSuchMethodException e) {
                    throw new Error(e);
                }
            }

            for (var entry : methodMap.entrySet()) {
                var key = entry.getKey();
                var value = entry.getValue();
                var MTD_invoke = MethodTypeDesc.ofDescriptor(key.descriptorString());
                var MTD_handle_invoke = MethodTypeDesc.ofDescriptor(value.getValue().descriptorString());
                var CD_target = ClassDesc.ofDescriptor(value.getKey().getDeclaringClass().descriptorString());
                var TK_return = findType(MTD_invoke.returnType());
                var name = value.getKey().getName();
                classBuilder.withMethodBody(name, MTD_invoke, ACC_PUBLIC | ACC_FINAL, code -> {
                    if (name.equals("type") && MTD_invoke.equals(MTD_type)) {
                        code.aload(0).getfield(FRE_type).areturn();
                    } else {
                        redirect(code, name, FRE_handle, MTD_invoke, CD_target, MTD_handle_invoke, TK_return);
                    }
                });
            }
        });
        Class<?> cls;
        try {
            cls = lookup.defineClass(classFileBytes);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return cls.asSubclass(type.entityTypeClass());
    }

    private void redirect(CodeBuilder code, String name, FieldRefEntry FRE_handle, MethodTypeDesc MTD_invoke, ClassDesc CD_target, MethodTypeDesc MTD_handle_invoke, TypeKind TK_return) {
        code.aload(0);
        code.getfield(FRE_handle);
        for (var i = 0; i < MTD_invoke.parameterCount(); i++) {
            var CD_param = MTD_invoke.parameterType(i);
            var TK_param = findType(CD_param);
            var local = code.parameterSlot(i);
            code.loadInstruction(TK_param, local);
        }
        code.invokeinterface(CD_target, name, MTD_handle_invoke);

        code.returnInstruction(TK_return);
    }

    public record WrappedClass<T>(Class<? extends T> cls, MethodHandle constructor) {
    }

    private static TypeKind findType(ClassDesc cls) {
        return TypeKind.fromDescriptor(cls.descriptorString());
    }
}
