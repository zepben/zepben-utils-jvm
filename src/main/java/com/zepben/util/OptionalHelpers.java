/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.util;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@EverythingIsNonnullByDefault
@SuppressWarnings("WeakerAccess")
public abstract class OptionalHelpers {

    @SafeVarargs
    public static <T> Optional<T> firstOf(Supplier<Optional<T>>... suppliers) {
        return Stream
            .of(suppliers)
            .map(Supplier::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

}
