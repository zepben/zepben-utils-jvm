/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.time;

import org.junit.jupiter.api.Test;

import java.time.MonthDay;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MonthDayRangeTest {

    private MonthDay from = MonthDay.of(1, 31);
    private MonthDay to = MonthDay.of(12, 1);
    private MonthDayRange range = new MonthDayRange(from, to);
    private MonthDayRange rolloverRange = new MonthDayRange(to, from);

    @Test
    public void fromTo() {
        assertThat(range.from(), is(from));
        assertThat(range.to(), is(to));
    }

    @Test
    public void rolloverFromTo() {
        assertThat(rolloverRange.from(), is(to));
        assertThat(rolloverRange.to(), is(from));
    }

    @Test
    public void contains() {
        assertThat(range.contains(from), is(true));
        assertThat(range.contains(to), is(true));
        assertThat(range.contains(MonthDay.of(3, 23)), is(true));
        assertThat(range.contains(MonthDay.of(1, 1)), is(false));
        assertThat(range.contains(MonthDay.of(12, 25)), is(false));
    }

    @Test
    public void rolloverContains() {
        assertThat(rolloverRange.contains(from), is(true));
        assertThat(rolloverRange.contains(to), is(true));
        assertThat(rolloverRange.contains(MonthDay.of(3, 23)), is(false));
        assertThat(rolloverRange.contains(MonthDay.of(1, 1)), is(true));
        assertThat(rolloverRange.contains(MonthDay.of(12, 25)), is(true));
    }

    @Test
    public void overlaps() {
        assertThat(range.overlaps(range), is(true));

        MonthDayRange other = new MonthDayRange(MonthDay.of(1, 1), MonthDay.of(1, 30));
        assertThat(range.overlaps(other), is(false));

        other = new MonthDayRange(MonthDay.of(1, 1), from);
        assertThat(range.overlaps(other), is(true));

        other = new MonthDayRange(MonthDay.of(2, 1), MonthDay.of(6, 30));
        assertThat(range.overlaps(other), is(true));

        other = new MonthDayRange(to, MonthDay.of(12, 30));
        assertThat(range.overlaps(other), is(true));

        other = new MonthDayRange(MonthDay.of(12, 30), MonthDay.of(1, 1));
        assertThat(range.overlaps(other), is(false));
    }

    @Test
    public void rollOverOverlaps() {
        assertThat(rolloverRange.overlaps(rolloverRange), is(true));

        MonthDayRange other = new MonthDayRange(MonthDay.of(11, 1), MonthDay.of(11, 30));
        assertThat(rolloverRange.overlaps(other), is(false));

        other = new MonthDayRange(MonthDay.of(11, 1), to);
        assertThat(rolloverRange.overlaps(other), is(true));

        other = new MonthDayRange(MonthDay.of(12, 25), MonthDay.of(1, 26));
        assertThat(rolloverRange.overlaps(other), is(true));

        other = new MonthDayRange(from, MonthDay.of(2, 1));
        assertThat(rolloverRange.overlaps(other), is(true));

        other = new MonthDayRange(MonthDay.of(2, 1), MonthDay.of(3, 23));
        assertThat(rolloverRange.overlaps(other), is(false));
    }

    @Test
    public void equalsEqual() {
        assertThat(range, equalTo(range));
        MonthDayRange other = new MonthDayRange(from, to);
        assertThat(range, equalTo(other));
    }

    @Test
    public void rolloverEqualsEqual() {
        assertThat(rolloverRange, equalTo(rolloverRange));
        MonthDayRange other = new MonthDayRange(to, from);
        assertThat(rolloverRange, equalTo(other));
    }

    @Test
    public void hashCodeEqual() {
        assertThat(range.hashCode(), equalTo(range.hashCode()));
        MonthDayRange other = new MonthDayRange(from, to);
        assertThat(range.hashCode(), equalTo(other.hashCode()));
    }

    @Test
    public void rolloverHashCodeEqual() {
        assertThat(rolloverRange.hashCode(), equalTo(rolloverRange.hashCode()));
        MonthDayRange other = new MonthDayRange(to, from);
        assertThat(rolloverRange.hashCode(), equalTo(other.hashCode()));
    }

    @Test
    public void toStringEqual() {
        String expected = "MonthDayRange{from=" + from + ", to=" + to + "}";
        assertThat(range.toString(), equalTo(expected));
    }

}