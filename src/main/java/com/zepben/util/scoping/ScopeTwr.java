/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.util.scoping;

import java.util.function.Consumer;

/**
 * Class returned by {@link Scope#twr()} that contains the resource to be closed at the end of a try-with-resources block.
 * <p>
 * You can check if the resource was successfully acquired before doing any processing in your TWR block.
 *
 * @param <T> The type of resource.
 */
@SuppressWarnings("WeakerAccess")
public class ScopeTwr<T> implements AutoCloseable {

    private final T resource;
    private final boolean acquired;
    private final Consumer<T> onClose;
    private boolean closed = false;

    ScopeTwr(T resource, boolean acquired, Consumer<T> onClose) {
        this.resource = resource;
        this.acquired = acquired;
        this.onClose = onClose;
    }

    /**
     * The resource held by this instance.
     *
     * @return The resource held by this instance.
     */
    public T resource() {
        return resource;
    }

    /**
     * Returns true if the resource was acquired. This should be checked within the try-with-resources block before
     * continuing to use things that require the resource.
     *
     * @return true if the resource is acquired.
     */
    public boolean acquiredResource() {
        return acquired;
    }

    /**
     * Check to see if this {@code Autoclosable} has been closed.
     *
     * @return true if {@link ScopeTwr#close()} has been called.
     */
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        if (acquired && !closed) {
            onClose.accept(resource);
            closed = true;
        }
    }
}
