/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.ws.adapters;

import org.seedstack.seed.core.api.Configuration;
import org.seedstack.seed.ws.internal.WSPlugin;
import com.sun.xml.wss.RealmAuthenticationAdapter;
import com.sun.xml.wss.XWSSecurityException;

import javax.security.auth.Subject;

/**
 * This {@link com.sun.xml.wss.RealmAuthenticationAdapter} validates authentication against configured username
 * (org.seedstack.seed.ws.wss.username property) and password (org.seedstack.seed.ws.wss.password property).
 *
 * @author adrien.lauer@mpsa.com
 */
public class ConfigurationRealmAuthenticationAdapter extends RealmAuthenticationAdapter {
    @Configuration(WSPlugin.WS_PLUGIN_CONFIGURATION_PREFIX + ".wss.username")
    private String configuredUsername;

    @Configuration(WSPlugin.WS_PLUGIN_CONFIGURATION_PREFIX + ".wss.password")
    private String configuredPassword;

    @Override
    public boolean authenticate(Subject callerSubject, String username, String password) throws XWSSecurityException {
        return configuredUsername.equals(username) && configuredPassword.equals(configuredPassword);
    }
}
