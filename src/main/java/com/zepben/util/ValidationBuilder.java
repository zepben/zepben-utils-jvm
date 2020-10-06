/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.util;

import com.zepben.annotations.EverythingIsNonnullByDefault;

@EverythingIsNonnullByDefault
@SuppressWarnings("WeakerAccess")
public class ValidationBuilder {

    protected void checkForErrors(StringBuilder errors) throws IllegalStateException {
        String errorMessage = errors.toString();
        if (!errorMessage.equals("Internal Error:"))
            throw new IllegalStateException(errorMessage);
    }

    protected StringBuilder validBuildState() {
        return new StringBuilder("Internal Error:");
    }

}
