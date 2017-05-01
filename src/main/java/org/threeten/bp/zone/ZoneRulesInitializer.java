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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Provides instructions on how to initialize {@link ZoneRulesProvider}. If you need to override the default behavior
 * of how to load time zone data, you should set this up before any other usage of the library.
 */
public abstract class ZoneRulesInitializer {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    private static final AtomicReference<ZoneRulesInitializer> initializer = new AtomicReference<ZoneRulesInitializer>();

    public static void setInitializer(final ZoneRulesInitializer newInitializer) {
        if (!initializer.compareAndSet(null, newInitializer)) {
            throw new IllegalStateException("Initializer was already set, possibly with a default during initialization");
        }
    }

    static void initialize() {
        if (INITIALIZED.getAndSet(true)) {
            throw new IllegalStateException("Already initialized");
        }
        // Set the default initializer if none has been provided yet.
        initializer.compareAndSet(null, new ServiceLoaderZoneRulesInitializer());
        initializer.get().initializeZoneProviders();
    }

    protected abstract void initializeZoneProviders();
}
