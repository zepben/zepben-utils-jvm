/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.time;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import java.time.temporal.Temporal;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;

/**
 * A class that represents a range of time, such as 2017-01-20T13:00.000Z to 2017-12-01T09:00.000Z
 * <p>
 * The start and end temporal values are considered inclusive to the range in all comparison operations.
 *
 * @param <T> The temporal type that this range uses.
 */
@SuppressWarnings("WeakerAccess")
@EverythingIsNonnullByDefault
public class TemporalRange<T extends Temporal & Comparable<? super T>> {

    private final T from;
    private final T to;

    /**
     * Constructs a new range with the specified from and to.
     * <p>
     * If the supplied to is greater than the from using {@code compareTo}, they will be switched such that calling
     * {@link TemporalRange#from()} will be the given to and {@link TemporalRange#to()} will be the given from after
     * construction.
     *
     * @param from The start of the time range.
     * @param to   The end of the time range.
     */
    public TemporalRange(T from, T to) {
        if (from.compareTo(to) < 0) {
            this.from = from;
            this.to = to;
        } else {
            this.to = from;
            this.from = to;
        }
    }

    /**
     * Returns the start value of this range.
     *
     * @return The start value.
     */
    public T from() {
        return from;
    }

    /**
     * Returns the end value of this range.
     *
     * @return The end value.
     */
    public T to() {
        return to;
    }

    /**
     * Tests if this range is completely before the supplied temporal.
     *
     * @param temporal the temporal to test.
     * @return true if this range is completely before the supplied temporal.
     */
    public boolean isBefore(T temporal) {
        return to().compareTo(temporal) < 0;
    }

    /**
     * Tests if this range is completely after the supplied temporal.
     *
     * @param temporal the temporal to test.
     * @return true if this range is completely after the supplied temporal.
     */
    public boolean isAfter(T temporal) {
        return from().compareTo(temporal) > 0;
    }

    /**
     * Tests if the supplied temporal falls within the range (inclusive of from and to).
     *
     * @param temporal the temporal to test.
     * @return true if the supplied temporal falls within this range.
     */
    public boolean contains(T temporal) {
        return !isBefore(temporal) && !isAfter(temporal);
    }

    /**
     * Tests if this range is completely before the supplied range. That is the to of this range is before the from
     * of the supplied range.
     *
     * @param range The range to test.
     * @return true if this range is completely before the supplied range.
     */
    public boolean isBefore(TemporalRange<T> range) {
        return to().compareTo(range.from()) < 0;
    }

    /**
     * Tests if this range is completely after the supplied range. That is the from of this range is after the to of
     * the supplied range.
     *
     * @param range The range to test.
     * @return true if this range is completely after the supplied range.
     */
    public boolean isAfter(TemporalRange<T> range) {
        return from().compareTo(range.to()) > 0;
    }

    /**
     * Tests if this range begins before the supplied range. That is the from of this range is before the from of the
     * supplied range.
     *
     * @param range The range to test.
     * @return true if this range is beings before the supplied range.
     */
    public boolean beginsBefore(TemporalRange<T> range) {
        return from().compareTo(range.from()) < 0;
    }

    /**
     * Tests if this range ends after the supplied range. That is the to of this range is after the to of the
     * supplied range.
     *
     * @param range The range to test.
     * @return true if this range is ends after the supplied range.
     */
    public boolean endsAfter(TemporalRange<T> range) {
        return to().compareTo(range.to()) > 0;
    }

    /**
     * Tests if this range overlaps with the supplied range.
     *
     * @param range The range to test.
     * @return true if the ranges overlap.
     */
    public boolean overlaps(TemporalRange<T> range) {
        return contains(range.from()) || contains(range.to());
    }

    /**
     * This method can be used to create an iterator to iterate over time within the range using a supplied adjuster
     * in ascending order. That is starting at the {@code from()} of this range.
     * <p>
     * The adjuster is used to move to the next temporal value on each step until the value returned from the adjuster
     * is greater than the {@code to()} of this range.
     *
     * @param adjuster Takes the current temporal value of the iterator and returns the next temporal to iterate to.
     * @return a new iterator.
     */
    public Iterator<T> iteratorAsc(UnaryOperator<T> adjuster) {
        return iterator(from, to, adjuster, (curr, end) -> curr.compareTo(end) <= 0);
    }

    /**
     * This method can be used to create an iterator to iterate over time within the range using a supplied adjuster
     * in descending order. That is starting at the {@code to()} of this range.
     * <p>
     * The adjuster is used to move to the next temporal value on each step until the value returned from the adjuster
     * is less than the {@code from()} of this range.
     *
     * @param adjuster Takes the current temporal value of the iterator and returns the next temporal to iterate to.
     * @return a new iterator.
     */
    public Iterator<T> iteratorDesc(UnaryOperator<T> adjuster) {
        return iterator(to, from, adjuster, (curr, end) -> curr.compareTo(end) >= 0);
    }

    private Iterator<T> iterator(T start, T end, UnaryOperator<T> adjuster, BiPredicate<T, T> hasNext) {
        return new Iterator<T>() {
            T current = start;

            @Override
            public boolean hasNext() {
                return hasNext.test(current, end);
            }

            @Override
            public T next() {
                if (!hasNext())
                    throw new NoSuchElementException();

                T ret = current;
                current = adjuster.apply(current);
                return ret;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemporalRange<?> that = (TemporalRange<?>) o;
        return Objects.equals(from, that.from) &&
            Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "TemporalRange{" +
            "from=" + from +
            ", to=" + to +
            '}';
    }
}