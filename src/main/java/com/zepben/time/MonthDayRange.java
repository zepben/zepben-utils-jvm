/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.time;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import java.time.Month;
import java.time.MonthDay;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
@EverythingIsNonnullByDefault
public class MonthDayRange {

    private static final MonthDay MIN = MonthDay.of(Month.JANUARY, 1);
    private static final MonthDay MAX = MonthDay.of(Month.DECEMBER, Month.DECEMBER.maxLength());

    private final MonthDay from;
    private final MonthDay to;

    public MonthDayRange(MonthDay from, MonthDay to) {
        this.from = from;
        this.to = to;
    }

    public MonthDay from() {
        return from;
    }

    public MonthDay to() {
        return to;
    }

    public boolean contains(MonthDay monthDay) {
        if (from.isBefore(to)) {
            return monthDay.compareTo(from) >= 0 && monthDay.compareTo(to) <= 0;
        } else {
            return (monthDay.compareTo(from) >= 0 && monthDay.compareTo(MAX) <= 0) ||
                (monthDay.compareTo(MIN) >= 0 && monthDay.compareTo(to) <= 0);
        }
    }

    public boolean overlaps(MonthDayRange range) {
        return contains(range.from) || contains(range.to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthDayRange that = (MonthDayRange) o;
        return Objects.equals(from, that.from) &&
            Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "MonthDayRange{" +
            "from=" + from +
            ", to=" + to +
            '}';
    }
}
