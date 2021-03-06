/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.cli;

import org.seedstack.seed.cli.api.WithCommandLine;
import org.seedstack.seed.it.AbstractSeedIT;
import org.junit.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class WithWithCommandLineIT extends AbstractSeedIT {
    @Inject
    private Fixture fixture;

    @Test
    @WithCommandLine(value = "--option=value", expectedExitCode = 255)
    public void test_with_annotation() {
        assertThat(fixture).isNotNull();
        assertThat(DummyCommandLineHandler.called).isTrue();
    }
}
