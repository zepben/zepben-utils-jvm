/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class TemporalRangeTest {

    private LocalDate from = LocalDate.now().minusDays(2);
    private LocalDate to = from.plusDays(2);
    private TemporalRange<LocalDate> range = new TemporalRange<>(from, to);

    @Test
    public void fromTo() throws Exception {
        assertThat(range.from(), is(from));
        assertThat(range.to(), is(to));
    }

    @Test
    public void swapsFromToSoLowerTemporalIsFrom() throws Exception {
        range = new TemporalRange<>(to, from);
        assertThat(range.from(), is(from));
        assertThat(range.to(), is(to));
    }

    @Test
    public void isBefore() throws Exception {
        assertThat(range.isBefore(to.plusDays(1)), is(true));
        assertThat(range.isBefore(to), is(false));
        assertThat(range.isBefore(from), is(false));
        assertThat(range.isBefore(from.minusDays(1)), is(false));
    }

    @Test
    public void isAfter() throws Exception {
        assertThat(range.isAfter(from.minusDays(1)), is(true));
        assertThat(range.isAfter(from), is(false));
        assertThat(range.isAfter(to), is(false));
        assertThat(range.isAfter(to.plusDays(1)), is(false));
    }

    @Test
    public void isBeforeRange() throws Exception {
        TemporalRange<LocalDate> other = new TemporalRange<>(from.minusDays(1), from);
        assertThat(range.isBefore(other), is(false));

        other = new TemporalRange<>(from, to);
        assertThat(range.isBefore(other), is(false));

        other = new TemporalRange<>(from.plusDays(1), to);
        assertThat(range.isBefore(other), is(false));

        other = new TemporalRange<>(to, to.plusDays(1));
        assertThat(range.isBefore(other), is(false));

        other = new TemporalRange<>(to.plusDays(1), to.plusDays(1));
        assertThat(range.isBefore(other), is(true));
    }

    @Test
    public void isAfterRange() throws Exception {
        TemporalRange<LocalDate> other = new TemporalRange<>(to, to.plusDays(1));
        assertThat(range.isAfter(other), is(false));

        other = new TemporalRange<>(from, to);
        assertThat(range.isAfter(other), is(false));

        other = new TemporalRange<>(from, to.minusDays(1));
        assertThat(range.isAfter(other), is(false));

        other = new TemporalRange<>(from.minusDays(1), from);
        assertThat(range.isAfter(other), is(false));

        other = new TemporalRange<>(from.minusDays(1), from.minusDays(1));
        assertThat(range.isAfter(other), is(true));
    }

    @Test
    public void beginsBefore() throws Exception {
        TemporalRange<LocalDate> other = new TemporalRange<>(from.minusDays(1), from);
        assertThat(range.beginsBefore(other), is(false));

        other = new TemporalRange<>(from, to);
        assertThat(range.beginsBefore(other), is(false));

        other = new TemporalRange<>(from.plusDays(1), to);
        assertThat(range.beginsBefore(other), is(true));

        other = new TemporalRange<>(to, to.plusDays(1));
        assertThat(range.beginsBefore(other), is(true));

        other = new TemporalRange<>(to.plusDays(1), to.plusDays(1));
        assertThat(range.beginsBefore(other), is(true));
    }

    @Test
    public void endsAfter() throws Exception {
        TemporalRange<LocalDate> other = new TemporalRange<>(to, to.plusDays(1));
        assertThat(range.endsAfter(other), is(false));

        other = new TemporalRange<>(from, to);
        assertThat(range.endsAfter(other), is(false));

        other = new TemporalRange<>(from, to.minusDays(1));
        assertThat(range.endsAfter(other), is(true));

        other = new TemporalRange<>(from.minusDays(1), from);
        assertThat(range.endsAfter(other), is(true));

        other = new TemporalRange<>(from.minusDays(1), from.minusDays(1));
        assertThat(range.endsAfter(other), is(true));
    }

    @Test
    public void containsRange() throws Exception {
        assertThat(range.contains(from), is(true));
        assertThat(range.contains(from.plusDays(1)), is(true));
        assertThat(range.contains(to), is(true));
        assertThat(range.contains(from.minusDays(1)), is(false));
        assertThat(range.contains(to.plusDays(1)), is(false));
    }

    @Test
    public void overlaps() throws Exception {
        assertThat(range.overlaps(range), is(true));

        TemporalRange<LocalDate> other = new TemporalRange<>(from.minusDays(1), from);
        assertThat(range.overlaps(other), is(true));

        other = new TemporalRange<>(from.minusDays(1), from.minusDays(2));
        assertThat(range.overlaps(other), is(false));

        other = new TemporalRange<>(to.minusDays(1), to);
        assertThat(range.overlaps(other), is(true));

        other = new TemporalRange<>(to, to.plusDays(1));
        assertThat(range.overlaps(other), is(true));

        other = new TemporalRange<>(to.plusDays(1), to.plusDays(2));
        assertThat(range.overlaps(other), is(false));
    }

    @Test
    public void iteratorAsc() throws Exception {
        Iterator<LocalDate> iter = range.iteratorAsc(d -> d.plusDays(1));
        List<LocalDate> dates = new ArrayList<>();
        iter.forEachRemaining(dates::add);
        assertThat(dates, contains(from, from.plusDays(1), to));
    }

    @Test
    public void iteratorDesc() throws Exception {
        Iterator<LocalDate> iter = range.iteratorDesc(d -> d.minusDays(1));
        List<LocalDate> dates = new ArrayList<>();
        iter.forEachRemaining(dates::add);
        assertThat(dates, contains(to, to.minusDays(1), from));
    }

    @Test
    public void equalsEqual() throws Exception {
        assertThat(range, equalTo(range));

        TemporalRange<LocalDate> other = new TemporalRange<>(from, to);
        assertThat(range, equalTo(other));

        other = new TemporalRange<>(from.minusDays(1), to);
        assertThat(range, not(equalTo(other)));

        other = new TemporalRange<>(from, to.minusDays(1));
        assertThat(range, not(equalTo(other)));

        other = new TemporalRange<>(from.minusDays(1), to.minusDays(1));
        assertThat(range, not(equalTo(other)));

        assertThat(range, not(equalTo(null)));
        assertThat(range, not(equalTo(new Object())));
    }

    @Test
    public void hashCodeEqual() throws Exception {
        assertThat(range.hashCode(), equalTo(range.hashCode()));

        TemporalRange<LocalDate> other = new TemporalRange<>(from, to);
        assertThat(range.hashCode(), equalTo(other.hashCode()));

        other = new TemporalRange<>(from.minusDays(1), to);
        assertThat(range.hashCode(), not(equalTo(other.hashCode())));

        other = new TemporalRange<>(from, to.minusDays(1));
        assertThat(range.hashCode(), not(equalTo(other.hashCode())));
    }

    @Test
    public void toStringEqual() throws Exception {
        String expected = "TemporalRange{from=" + from.toString() + ", to=" + to.toString() + "}";
        assertThat(range.toString(), is(expected));
    }

}