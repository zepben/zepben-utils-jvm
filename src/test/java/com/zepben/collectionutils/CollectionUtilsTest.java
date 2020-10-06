/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.collectionutils;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.zepben.collectionutils.CollectionUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CollectionUtilsTest {

    @Test
    public void coverage() {
        new CollectionUtils();
    }


    @Test
    public void arrayOfWorks() {
        assertThat(arrayOf("a", "b", "c"), arrayContaining("a", "b", "c"));
        assertThat(arrayOf(1, 2, 3), arrayContaining(1, 2, 3));
    }

    @Test
    public void setOfWorks() {
        assertThat(setOf("a", "b", "c"), contains("a", "b", "c"));
        assertThat(setOf(1, 2, 3), contains(1, 2, 3));
    }

    @Test
    public void mapOfWorks() {
        Map<String, String> stringMap = mapOf("k1", "v1", "k2", "v2");
        assertThat(stringMap, hasEntry("k1", "v1"));
        assertThat(stringMap, hasEntry("k2", "v2"));

        Map<Integer, Integer> intMap = mapOf(11, 12, 21, 22);
        assertThat(intMap, hasEntry(11, 12));
        assertThat(intMap, hasEntry(21, 22));
    }

}
