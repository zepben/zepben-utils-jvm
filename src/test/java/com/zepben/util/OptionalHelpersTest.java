/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.util;

import com.zepben.testutils.junit.SystemLogExtension;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.zepben.util.OptionalHelpers.firstOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class OptionalHelpersTest {

    public SystemLogExtension systemErr = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess();

    @Test
    public void firstOfOnlyCallsWhatIsNeeded() {
        OptionalFunctions functions = mock(OptionalFunctions.class);

        validateOptionals(functions, true, true, true, Optional.of("func1"));
        validateOptionals(functions, true, true, false, Optional.of("func1"));
        validateOptionals(functions, true, false, true, Optional.of("func1"));
        validateOptionals(functions, true, false, false, Optional.of("func1"));
        validateOptionals(functions, false, true, true, Optional.of("func2"));
        validateOptionals(functions, false, true, false, Optional.of("func2"));
        validateOptionals(functions, false, false, true, Optional.of("func3"));
        validateOptionals(functions, false, false, false, Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void validateOptionals(OptionalFunctions functions,
                                   boolean isFunc1Valid,
                                   boolean isFunc2Valid,
                                   boolean isFunc3Valid,
                                   Optional<String> expectedResult) {
        configureOptionals(functions, isFunc1Valid, isFunc2Valid, isFunc3Valid);

        assertThat(firstOf(functions::func1, functions::func2, functions::func3), equalTo(expectedResult));

        verify(functions, times(1)).func1();
        verify(functions, times(!isFunc1Valid ? 1 : 0)).func2();
        verify(functions, times(!isFunc1Valid && !isFunc2Valid ? 1 : 0)).func3();
    }

    private void configureOptionals(OptionalFunctions functions, boolean isFunc1Valid, boolean isFunc2Valid, boolean isFunc3Valid) {
        if (isFunc1Valid)
            doReturn(Optional.of("func1")).when(functions).func1();
        else
            doReturn(Optional.empty()).when(functions).func1();

        if (isFunc2Valid)
            doReturn(Optional.of("func2")).when(functions).func2();
        else
            doReturn(Optional.empty()).when(functions).func2();

        if (isFunc3Valid)
            doReturn(Optional.of("func3")).when(functions).func3();
        else
            doReturn(Optional.empty()).when(functions).func3();

        clearInvocations(functions);
    }

    private interface OptionalFunctions {

        Optional<String> func1();

        Optional<String> func2();

        Optional<String> func3();

    }

}
