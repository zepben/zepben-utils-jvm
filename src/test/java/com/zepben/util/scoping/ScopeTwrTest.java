/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.util.scoping;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class ScopeTwrTest {

    @SuppressWarnings({"unchecked"})
    @Test
    public void closes() throws Exception {
        Object object = new Object();
        Consumer<Object> onExit = mock(Consumer.class);
        try (ScopeTwr<Object> scope = new ScopeTwr<>(object, true, onExit)) {
            verify(onExit, never()).accept(object);
            scope.close();
        }

        verify(onExit, times(1)).accept(object);
    }

    @SuppressWarnings({"EmptyTryBlock", "unchecked"})
    @Test
    public void doesNotCloseWhenResourceNotAcquired() throws Exception {
        Object object = new Object();
        Consumer<Object> onExit = mock(Consumer.class);
        try (ScopeTwr<Object> scope = new ScopeTwr<>(object, false, onExit)) {
            verify(onExit, never()).accept(object);
            scope.close();
        }

        verify(onExit, never()).accept(object);
    }
}