/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.shell.commands;

import org.seedstack.seed.core.spi.command.CommandDefinition;
import org.seedstack.seed.core.spi.command.Command;
import org.seedstack.seed.core.spi.command.Option;

@CommandDefinition(scope = "test", name = "withoptions", description = "Test command")
public class WithOptionsTestCommand implements Command {
    @Option(name = "a", longName = "with-argument", mandatory = false, description = "", hasArgument = true)
    private String withArgument;

    @Option(name = "n", longName = "no-argument", mandatory = false, description = "", hasArgument = false)
    private boolean withoutArgument;

    @Override
    public Object execute(Object object) throws Exception {
        if (withoutArgument)
            return "without-argument";

        if (withArgument != null)
            return "with-argument: " + withArgument;

        return "no-option";
    }
}
