/*
 * Copyright (c) 2009-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.zone;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import javax.time.DateTimeException;
import javax.time.ZoneId;

/**
 * Provider of time-zone rules to the system.
 * <p>
 * This class manages time-zone rules.
 * The static methods provide the public API that can be used to manage the providers.
 * The abstract methods provide the SPI that allows rules to be provided.
 * <p>
 * Rules are looked up be group, version and region. The group and region are
 * expressed in the {@link ZoneId}, whereas the version is specific to the providers.
 * <p>
 * Since time-zone rules are political, the data can change at any time.
 * The set of current rules is managed by groups which publish new versions of
 * the data set over time to handle the political changes.
 * The default 'TZDB' group uses version numbering consisting of the year followed
 * by a letter, such as '2009e' or '2012f'.
 * Versions are assumed to be sortable in lexicographical order.
 * Version IDs must match the regex {@code [A-Za-z0-9._-]+}.
 * <p>
 * Caching is the responsibility of the SPI implementation.
 * Any rules that are returned once for a given lookup must continue to be returned
 * in the future.
 * <p>
 * Many systems would like to receive new time-zone rules dynamically.
 * This must be implemented separately from this interface, typically using a listener.
 * Whenever the listener detects new rules it should call
 * {@link #registerProvider(ZoneRulesProvider)} using a standard
 * immutable provider implementation.
 *
 * <h4>Implementation notes</h4>
 * This interface is a service provider that can be called by multiple threads.
 * Implementations must be immutable and thread-safe.
 */
public abstract class ZoneRulesProvider {

    /**
     * Group ID pattern.
     */
    private static final Pattern PATTERN_GROUP = Pattern.compile("[A-Za-z0-9._-]+");
    /**
     * The zone rule groups.
     * Should not be empty.
     */
    private static final ConcurrentMap<String, ZoneRulesProvider> GROUPS = new ConcurrentHashMap<>(16, 0.75f, 2);
    static {
        ServiceLoader<ZoneRulesProvider> sl = ServiceLoader.load(ZoneRulesProvider.class, ClassLoader.getSystemClassLoader());
        Iterator<ZoneRulesProvider> it = sl.iterator();
        while (it.hasNext()) {
            ZoneRulesProvider provider;
            try {
                provider = it.next();
            } catch (ServiceConfigurationError ex) {
                if (ex.getCause() instanceof SecurityException) {
                    continue;  // ignore the security exception, try the next provider
                }
                throw ex;
            }
            registerProvider(provider);
        }
    }

    /**
     * The zone rules group ID, such as 'TZDB'.
     */
    private final String groupId;

    /**
     * Gets the set of available zone rule groups.
     * <p>
     * Which groups are available is dependent on the registered providers.
     * <p>
     * The returned groups will remain available and valid for the lifetime of the application as
     * there is no way to deregister time-zone information. More groups may be added during
     * the lifetime of the application, however the returned set will not be altered.
     * <p>
     * The set should always contain 'TZDB'.
     * The group 'UTC' is never included as it is handled entirely within {@code ZoneId}.
     *
     * @return a modifiable copy of the available group IDs, not null
     */
    public static Set<String> getAvailableGroupIds() {
        return new HashSet<>(GROUPS.keySet());
    }

    /**
     * Gets a group by ID, such as 'TZDB'.
     * <p>
     * This gets the provider of rule data for the specified group.
     * <p>
     * This method relies on time-zone data provider files. These are often loaded as jar files.
     * If no providers have been {@link #registerProvider(ZoneRulesProvider) registered} or no
     * provider has been registered for the requested group then an exception is thrown.
     *
     * @param groupId  the group ID, not null
     * @return the provider for the group, not null
     * @throws DateTimeException if there is no provider for the specified group
     */
    public static ZoneRulesProvider getProvider(String groupId) {
        Objects.requireNonNull(groupId, "groupId");
        ZoneRulesProvider group = GROUPS.get(groupId);
        if (group == null) {
            if (GROUPS.isEmpty()) {
                throw new DateTimeException("Unknown time-zone group '" + groupId
                        + "', no time-zone data files registered");
            }
            throw new DateTimeException("Unknown time-zone group '" + groupId + '\'');
        }
        return group;
    }

    /**
     * Registers a zone rules provider.
     * <p>
     * This adds a new provider to those currently available.
     * Each provider is specific to one group and no two provider may supply
     * information about the same group.
     * <p>
     * To ensure the integrity of time-zones already created, there is no way
     * to deregister providers.
     *
     * @param provider  the provider to register, not null
     * @throws DateTimeException if the provider is already registered
     */
    public static void registerProvider(ZoneRulesProvider provider) {
        ZoneRulesProvider old = GROUPS.putIfAbsent(provider.getGroupId(), provider);
        if (old != null) {
            throw new DateTimeException("Provider already registered for time-zone group: " + provider.getGroupId());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param groupId  the group ID, not null
     * @throws DateTimeException if the group ID is invalid
     */
    protected ZoneRulesProvider(String groupId) {
        Objects.requireNonNull(groupId, "groupId");
        if (PATTERN_GROUP.matcher(groupId).matches() == false) {
            throw new DateTimeException("Invalid group ID '" + groupId + "', must match regex [A-Za-z0-9._-]+");
        }
        this.groupId = groupId;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ID of the group, such as 'TZDB'.
     *
     * @return the ID of the group, not null
     */
    public final String getGroupId() {
        return groupId;
    }

    /**
     * Gets the set of available regions.
     * <p>
     * This obtains the IDs of the available regions of rule data.
     * A provider should provide data for at least one region.
     * <p>
     * The returned regions remain available and valid for the lifetime of the application.
     * A dynamic provider may increase the set of regions as more data becomes available.
     * <p>
     * Not all combinations of region and version will be valid.
     * Use {@link #isValid(String, String)} to determine which combinations are valid.
     *
     * @return a modifiable copy of the available region IDs, not null
     */
    public abstract Set<String> getAvailableRegionIds();

    /**
     * Gets the set of available versions.
     * <p>
     * This obtains the IDs of the available versions of rule data.
     * A provider should provide data for at least one version.
     * <p>
     * The returned versions remain available and valid for the lifetime of the application.
     * A dynamic provider may increase the set of versions as more data becomes available.
     * <p>
     * Not all providers support multiple versions of the data, so this method
     * frequently returns a set of size one representing the "default" version.
     * The null version, representing the "latest" version, will not be included in the set.
     * <p>
     * The returned versions remain available and valid for the lifetime of the application.
     * A dynamic provider may increase the set of versions as more data becomes available.
     * <p>
     * Not all combinations of region and version will be valid.
     * Use {@link #isValid(String, String)} to determine which combinations are valid.
     *
     * @return a modifiable copy of the available version IDs, natural string sort order, not null
     */
    public abstract SortedSet<String> getAvailableVersionIds();

    /**
     * Checks if the combination of region and version is valid.
     * <p>
     * This checks whether rules can be obtained for the region.
     * The version may be null to indicate the "latest" version.
     * If this method returns true, the {@link #getRules} method should
     * return a set of rules for the same parameters.
     *
     * @param regionId  the time-zone region ID, null returns false
     * @param versionId  the time-zone version ID, null means "latest"
     * @return true if rules can be obtained
     */
    public abstract boolean isValid(String regionId, String versionId);

    /**
     * Gets rules for the combination of region and version.
     * <p>
     * This loads the rules for the region and version specified.
     * The version may be null to indicate the "latest" version.
     *
     * @param regionId  the time-zone region ID, not null
     * @param versionId  the time-zone version ID, null means "latest"
     * @return the rules, not null
     * @throws DateTimeException if rules cannot be obtained
     */
    public abstract ZoneRules getRules(String regionId, String versionId);

    /**
     * Refreshes the rules from the underlying data provider.
     * <p>
     * This method provides the opportunity for a provider to dynamically
     * recheck the underlying data provider to find the latest rules.
     * This could be used to load new rules without stopping the JVM.
     * Dynamic behavior is entirely optional and most providers do not support it.
     *
     * @param classLoader  the class loader to use, not null
     * @return true if the rules were updated
     * @throws DateTimeException if an error occurs during the refresh
     */
    public abstract boolean refresh(ClassLoader classLoader);

    //-------------------------------------------------------------------------
    @Override
    public String toString() {
        return groupId;
    }

}
