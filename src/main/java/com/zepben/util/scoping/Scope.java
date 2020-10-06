/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.util.scoping;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class that can run a block of code within a "scope" of a resource.
 * <p>
 * It basically wraps up the following pattern:
 * <blockquote>
 * <pre>{@code
 * resource.acquire();
 * try {
 *     // do some work while the resource is held
 * } finally {
 *     resource.release();
 * }
 * }
 * </pre>
 * </blockquote>
 * You might argue that pattern is fine, however the niceness of this class comes from the
 * {@link Scope#supply(Function)} method. In the above try/finally syntax, it gets messy if you want to calculate a
 * value while the resource is held, then release the resource to use the value. You either need to release the
 * resource in the try block (and either double release due to the finally block or track it has already been released)
 * or declare a variable outside the try block and use it after.
 * <p>
 * Which is nicer?
 * <p>
 * Stopping double release:
 * <blockquote>
 * <pre>{@code
 * bool released = false;
 * resource.acquire();
 * try {
 *     int nextValue = resource.nextValue();
 *     resource.release();
 *     released = true;
 *
 *     // do something with nextValue
 * } finally {
 *     if (!released) {
 *         resource.release();
 *     }
 * }
 * }
 * </pre>
 * </blockquote>
 * Declaring outside try
 * <blockquote>
 * <pre>{@code
 * int nextValue;
 * resource.acquire();
 * try {
 *     nextValue = resource.nextValue();
 * } finally {
 *     resource.release();
 * }
 *
 * // do something with nextValue
 * }
 * </pre>
 * </blockquote>
 * or
 * <blockquote>
 * <pre>{@code
 * // As a member of your class:
 * Scope<Resource> scope = Scope.alwaysAcquires(resource, resource::acquire, resource::release);
 *
 * // In a method acquiring the resource
 * int nextValue = scope.supply(res -> res.nextValue());
 * // do something with nextValue
 * }
 * </pre>
 * </blockquote>
 *
 * @param <T> The type of resource held by the scope.
 */
@SuppressWarnings("WeakerAccess")
public class Scope<T> {
    private final T resource;
    private final Predicate<T> enter;
    private final Consumer<T> exit;

    /**
     * Create a new Scope instance.
     * <p>
     * The {@code enter} is a {@link Predicate} that takes the resources. If the resource can be acquired, this should
     * return true, otherwise false.
     * <p>
     * The {@code exit} consumer is used to release your resource after the scope block has been executed.
     *
     * @param resource The resource to acquire / release.
     * @param enter    callback to acquire the resource.
     * @param exit     callback to release the resource.
     */
    public Scope(T resource, Predicate<T> enter, Consumer<T> exit) {
        this.resource = resource;
        this.enter = enter;
        this.exit = exit;
    }

    /**
     * Creates a new instance that can always acquire the resource.
     * <p>
     * Because it can always acquire, the {@code enter} is a supplier that is then wrapped in a predicate that always
     * returns true.
     * <p>
     * This factory method will allow you to instantiate instances in a concise fashion, e.g.:
     * <blockquote>
     * <pre>{@code
     * Scope<Resource> scope = Scope.alwaysAcquires(resource, resource::acquire, resource::release);
     * }
     * </pre>
     * </blockquote>
     *
     * @param resource The resource to acquire / release.
     * @param enter    callback to acquire the resource.
     * @param exit     callback to release the resource.
     * @param <T>      The type of resource held by the scope.
     * @return A new scope instance where the resource can always be acquired.
     */
    public static <T> Scope<T> alwaysAcquires(T resource, Consumer<T> enter, Consumer<T> exit) {
        return new Scope<>(
            resource,
            r -> {
                enter.accept(r);
                return true;
            },
            exit);
    }

    /**
     * Returns the resource contained by the scope.
     *
     * @return The resource contained by this scope.
     */
    public T resource() {
        return resource;
    }

    /**
     * Acquires the resource, executes the provided code, and releases the resource when finished.
     * <p>
     * If the resource can not be acquired, the provided code block will never be executed.
     *
     * @param block The code to be executed within the scope.
     * @return true if the resource was acquired, otherwise false.
     */
    public boolean run(Consumer<T> block) {
        if (enter.test(resource)) {
            try {
                block.accept(resource);
                return true;
            } finally {
                exit.accept(resource);
            }
        }
        return false;
    }

    /**
     * Behaves the same as {@link Scope#run(Consumer)}, except returns a value returned by the provided
     * code block.
     * <p>
     * If the resource cannot be acquired, the block is never executed and null is returned.
     *
     * @param block The code to be executed within the scope, returning a value to be returned by this method.
     * @param <R>   The return type of the code block.
     * @return The return value of the supplied block if the resource was acquired, otherwise null.
     */
    @Nullable
    public <R> R supply(Function<T, R> block) {
        if (enter.test(resource)) {
            try {
                return block.apply(resource);
            } finally {
                exit.accept(resource);
            }
        }

        return null;
    }

    /**
     * Behaves exactly the same as {@link Scope#supply(Function)} but the result is wrapped in an {@link Optional}.
     *
     * @param block The code to be executed within the scope, returning a value to be returned by this method.
     * @param <R>   The return type of the code block.
     * @return The return value of the supplied block, wrapped in an Optional.
     */
    public <R> Optional<R> supplyOptionally(Function<T, R> block) {
        return Optional.ofNullable(supply(block));
    }

    /**
     * Allows use of this scope in a try-with-resources block. This may be useful when using lambdas or method
     * references is undesirable for some reason.
     * <p>
     * The {@link ScopeTwr} returned is an {@link AutoCloseable} that holds the resource of the scope. You can check if
     * the resource was required by calling {@link ScopeTwr#acquiredResource()} within the TWR block. If the resource
     * was not acquired, it will not attempt to close it at the end of the TWR block.
     * <blockquote><pre>{@code
     * try (ScopeTwr twr = scope.twr()) {
     *     if (twr.acquiredResource()) {
     *         String id = twr.resource().nextId();
     *         ...
     *     }
     * }
     * }</pre></blockquote>
     *
     * @return A {@link ScopeTwr} instance containing the resource of this scope.
     */
    public ScopeTwr<T> twr() {
        return new ScopeTwr<>(resource, enter.test(resource), exit);
    }
}
