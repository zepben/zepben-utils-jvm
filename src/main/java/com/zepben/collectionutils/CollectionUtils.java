/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.collectionutils;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import java.util.*;

@EverythingIsNonnullByDefault
@SuppressWarnings("WeakerAccess")
public class CollectionUtils {

    @SafeVarargs
    public static <T> T[] arrayOf(T... args) {
        return args;
    }

    @SafeVarargs
    public static <T> Set<T> setOf(T... args) {
        return new HashSet<>(Arrays.asList(args));
    }

    @SafeVarargs
    public static <T> Map<T, T> mapOf(T... args) {
        Map<T, T> map = new HashMap<>();
        for (int i = 0; i < args.length; i += 2)
            map.put(args[i], args[i + 1]);
        return map;
    }

}
