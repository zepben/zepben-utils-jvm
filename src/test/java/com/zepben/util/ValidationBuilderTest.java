/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.util;

import org.junit.jupiter.api.Test;

import static com.zepben.testutils.exception.ExpectException.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ValidationBuilderTest {

    @Test
    public void validates() {
        TestValidationBuilder testValidationBuilder = new TestValidationBuilder();

        StringBuilder sb = testValidationBuilder.exposeValidBuildState();
        assertThat(sb.toString(), equalTo("Internal Error:"));
        testValidationBuilder.exposeCheckForErrors(sb);

        sb.append(" this is a test");
        expect(() -> testValidationBuilder.exposeCheckForErrors(sb))
            .toThrow(IllegalStateException.class)
            .withMessage("Internal Error: this is a test");
    }

    private class TestValidationBuilder extends ValidationBuilder {

        void exposeCheckForErrors(StringBuilder errors) throws IllegalStateException {
            checkForErrors(errors);
        }

        StringBuilder exposeValidBuildState() {
            return validBuildState();
        }

    }

}
