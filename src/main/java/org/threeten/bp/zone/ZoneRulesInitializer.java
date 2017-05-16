/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.threeten.bp.zone;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controls how the time-zone rules are initialized.
 * <p>
 * The default behavior is to use {@code ServiceLoader} to find instances of {@code ZoneRulesProvider}.
 * If the {@link #blockServiceLoaderProviderSearch()} method is called then the service loader search
 * will not occur and no providers will be registered. As such, code that block the service loader approach
 * should be followed by code to create a {@link ZoneRulesProvider} and
 * {@linkplain ZoneRulesProvider#registerProvider(ZoneRulesProvider) register it}.
 * <p>
 * <b>The {@code blockServiceLoaderProviderSearch()} method must be called <u>before</u> class loading any other
 * ThreeTen-Backport class, including {@code ZoneRulesProvider}, to have any effect!</b>
 * <p>
 * This class has been added primarily for the benefit of Android.
 */
public final class ZoneRulesInitializer {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    private static final AtomicBoolean BLOCK_SERVICE_LOADER = new AtomicBoolean(false);

    /**
     * Blocks the standard service loader initialization.
     * <p>
     * This can only be invoked before the {@link ZoneRulesProvider} class is loaded.
     * Invoking this method at a later point will throw an exception.
     * 
     * @param initializer  the initializer to use
     * @throws IllegalStateException if initialization has already occurred
     */
    public static void blockServiceLoaderProviderSearch() {
        if (INITIALIZED.get()) {
            throw new IllegalStateException("Already initialized");
        }
        BLOCK_SERVICE_LOADER.set(true);
    }

    //-----------------------------------------------------------------------
    // initialize the providers
    static void initialize() {
        if (INITIALIZED.getAndSet(true)) {
            throw new IllegalStateException("Already initialized");
        }
        if (!BLOCK_SERVICE_LOADER.get()) {
            initializeByServiceLoader();
        }
    }

    // implementation that uses the service loader
    private static void initializeByServiceLoader() {
        ServiceLoader<ZoneRulesProvider> loader = ServiceLoader.load(ZoneRulesProvider.class, ZoneRulesProvider.class.getClassLoader());
        for (ZoneRulesProvider provider : loader) {
            try {
                ZoneRulesProvider.registerProvider(provider);
            } catch (ServiceConfigurationError ex) {
                if (!(ex.getCause() instanceof SecurityException)) {
                    throw ex;
                }
            }
        }
    }

    private ZoneRulesInitializer() {
    }

}
