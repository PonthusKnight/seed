/**
 * Copyright (c) 2013-2015 by The SeedStack authors. All rights reserved.
 *
 * This file is part of SeedStack, An enterprise-oriented full development stack.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.metrics.internal;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.seedstack.seed.it.AbstractSeedIT;
import org.seedstack.seed.it.api.KernelMode;
import org.seedstack.seed.it.spi.ITKernelMode;
import org.junit.Test;

import javax.inject.Inject;

import static com.codahale.metrics.MetricRegistry.name;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@KernelMode(ITKernelMode.PER_TEST)
public class CachedGaugeIT extends AbstractSeedIT {
    @Inject
    private InstrumentedWithCachedGauge instance;

    @Inject
    private MetricRegistry registry;

    @Test
    @SuppressWarnings("unchecked")
    public void aGaugeAnnotatedMethod() throws Exception {
        instance.doAThing();

        final Gauge metric = registry.getGauges().get(name(InstrumentedWithCachedGauge.class, "cached_gauge_things"));

        assertThat("Guice creates a metric",
                metric,
                is(notNullValue()));

        assertThat("Guice creates a gauge with the given value",
                ((Gauge<String>) metric).getValue(),
                is("poop"));
    }


    @Test
    @SuppressWarnings("unchecked")
    public void aGaugeAnnotatedMethodWithDefaultName() throws Exception {
        instance.doAnotherThing();

        final Gauge metric = registry.getGauges().get(name(InstrumentedWithCachedGauge.class,
                "doAnotherThing", Gauge.class.getSimpleName().toLowerCase()));

        assertThat("Guice creates a metric",
                metric,
                is(notNullValue()));

        assertThat("Guice creates a gauge with the given value",
                ((Gauge<String>) metric).getValue(),
                is("anotherThing"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void aGaugeAnnotatedMethodWithAbsoluteName() throws Exception {
        instance.doAThingWithAbsoluteName();

        final Gauge metric = registry.getGauges().get(name("cached_gauge_absoluteName"));

        assertThat("Guice creates a metric",
                metric,
                is(notNullValue()));

        assertThat("Guice creates a gauge with the given value",
                ((Gauge<String>) metric).getValue(),
                is("anotherThingWithAbsoluteName"));
    }

}
