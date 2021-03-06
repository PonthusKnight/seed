/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Creation : 12 mars 2015
 */
package org.seedstack.seed.persistence.jpa.internal;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

class InternalPersistenceUnitInfo implements PersistenceUnitInfo {

    String persistenceUnitName;

    String persistenceProviderClassName;

    PersistenceUnitTransactionType persistenceUnitTransactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;

    DataSource jtaDataSource;

    DataSource nonJtaDataSource;

    List<String> mappingFileNames;

    List<String> managedClassNames;

    SharedCacheMode sharedCacheMode = SharedCacheMode.UNSPECIFIED;

    ValidationMode validationMode = ValidationMode.AUTO;

    Properties properties;

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return persistenceProviderClassName;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return persistenceUnitTransactionType;
    }

    @Override
    public DataSource getJtaDataSource() {
        return jtaDataSource;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return nonJtaDataSource;
    }

    @Override
    public List<String> getMappingFileNames() {
        return mappingFileNames;
    }

    @Override
    public List<URL> getJarFileUrls() {
        // Not used as Seed will scan the classes
        return Collections.emptyList();
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        // Not used as Seed will scan the classes
        return null;
    }

    @Override
    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    @Override
    public boolean excludeUnlistedClasses() {
        // Not used as Seed will scan the classes
        return false;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return sharedCacheMode;
    }

    @Override
    public ValidationMode getValidationMode() {
        return validationMode;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        // not supported
        return null;
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
        // does nothing
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        // not supported
        return null;
    }

}
