/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.util.scoping;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

public class ScopeTest {

    private Object obj = new Object();
    @SuppressWarnings("unchecked") private Predicate<Object> onEnter = mock(Predicate.class);
    @SuppressWarnings("unchecked") private Consumer<Object> onExit = mock(Consumer.class);
    private Scope<Object> scope = new Scope<>(obj, onEnter, onExit);

    @SuppressWarnings("unchecked")
    @Test
    public void alwaysAcquires() throws Exception {
        Consumer<Object> alwaysEnter = mock(Consumer.class);
        scope = Scope.alwaysAcquires(obj, alwaysEnter, onExit);

        assertThat(scope.run(r -> {}), is(true));
        verify(alwaysEnter).accept(obj);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void run() throws Exception {
        doReturn(true).when(onEnter).test(obj);
        Consumer<Object> runnable = mock(Consumer.class);

        assertThat(scope.run(runnable), is(true));

        InOrder inOrder = inOrder(onEnter, runnable, onExit);
        inOrder.verify(onEnter).test(obj);
        inOrder.verify(runnable).accept(obj);
        inOrder.verify(onExit).accept(obj);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void doesNotRunWhenOnEnterIsFalse() throws Exception {
        doReturn(false).when(onEnter).test(obj);
        Consumer<Object> runnable = mock(Consumer.class);

        assertThat(scope.run(runnable), is(false));

        verify(onEnter).test(obj);
        verify(runnable, never()).accept(obj);
        verify(onExit, never()).accept(obj);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void supply() throws Exception {
        doReturn(true).when(onEnter).test(obj);

        Function<Object, Object> supplier = mock(Function.class);
        Object supplied = new Object();
        doReturn(supplied).when(supplier).apply(obj);

        Object returnValue = scope.supply(supplier);

        assertThat(returnValue, is(supplied));
        InOrder inOrder = inOrder(onEnter, supplier, onExit);
        inOrder.verify(onEnter).test(obj);
        inOrder.verify(supplier).apply(obj);
        inOrder.verify(onExit).accept(obj);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void supplyOptionally() throws Exception {
        doReturn(true).when(onEnter).test(obj);

        Function<Object, Object> supplier = mock(Function.class);
        Object supplied = new Object();
        doReturn(supplied).when(supplier).apply(obj);

        Optional<Object> returnValue = scope.supplyOptionally(supplier);

        assertThat(returnValue, is(Optional.of(supplied)));
        InOrder inOrder = inOrder(onEnter, supplier, onExit);
        inOrder.verify(onEnter).test(obj);
        inOrder.verify(supplier).apply(obj);
        inOrder.verify(onExit).accept(obj);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void supplyReturnsNullWhenOnEnterReturnsFalse() throws Exception {
        doReturn(false).when(onEnter).test(obj);

        Function<Object, Object> supplier = mock(Function.class);
        Object supplied = new Object();
        doReturn(supplied).when(supplier).apply(obj);

        Object returnValue = scope.supply(supplier);

        assertThat(returnValue, is(nullValue()));
        verify(onEnter).test(obj);
        verify(supplier, never()).apply(obj);
        verify(onExit, never()).accept(obj);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void supplyReturnsEmptyOptionalWhenOnEnterReturnsFalse() throws Exception {
        doReturn(false).when(onEnter).test(obj);

        Function<Object, Object> supplier = mock(Function.class);
        Optional<Object> returnValue = scope.supplyOptionally(supplier);

        assertThat(returnValue, is(Optional.empty()));
    }

    @Test
    public void twr() throws Exception {
        doReturn(true).when(onEnter).test(obj);
        try (ScopeTwr twr = scope.twr()) {
            verify(onEnter).test(obj);
            assertThat(twr.acquiredResource(), is(true));
            verify(onExit, never()).accept(obj);
            assertThat(twr.isClosed(), is(false));
        }
        verify(onExit).accept(obj);
    }

    @Test
    public void twrReturnsNullWhenOnEnterReturnsFalse() throws Exception {
        doReturn(false).when(onEnter).test(obj);
        try (ScopeTwr twr = scope.twr()) {
            assertThat(twr.acquiredResource(), is(false));
            verify(onEnter).test(obj);
        }
        verify(onExit, never()).accept(obj);
    }
}