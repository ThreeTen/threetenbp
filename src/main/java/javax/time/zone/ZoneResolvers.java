/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDateTime;
import javax.time.OffsetDateTime;
import javax.time.ZoneId;

/**
 * Provides common implementations of {@code ZoneResolver}.
 * <p>
 * A {@link ZoneResolver} provides a strategy for handling the gaps and overlaps
 * on the time-line that occur due to changes in the offset from UTC, usually
 * caused by daylight saving time.
 * 
 * <h4>Implementation notes</h4>
 * This is a thread-safe utility class.
 * All returned resolvers are immutable and thread-safe.
 */
public final class ZoneResolvers {

    /**
     * Private constructor.
     */
    private ZoneResolvers() {
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains the strict zone resolver which rejects all gaps and overlaps
     * as invalid, resulting in an exception.
     * <p>
     * Gap - throws an exception.<br />
     * Overlap - throws an exception.<br />
     * Other - applies the appropriate offset.<br />
     *
     * @return the strict resolver, not null
     */
    public static ZoneResolver strict() {
        return Impl.STRICT;
    }

    /**
     * Obtains the pre-transition zone resolver, which returns the instant one nanosecond
     * before the transition for gaps, and the earlier offset for overlaps.
     * <p>
     * Gap - changes the local date-time to one nanosecond before the transition using the "before" offset.<br />
     * Overlap - chooses the earlier of the two offsets, which is the "before" offset.<br />
     * Other - applies the appropriate offset.<br />
     *
     * @return the pre-transition resolver, not null
     */
    public static ZoneResolver preTransition() {
        return Impl.PRE_TRANSITION;
    }

    /**
     * Obtains the post-transition zone resolver, which returns the instant after the
     * transition for gaps, and the later offset for overlaps.
     * <p>
     * Gap - changes the local date-time to the instant of the transition using the "after" offset.<br />
     * Overlap - chooses the later of the two offsets, which is the "after" offset.<br />
     * Other - applies the appropriate offset.<br />
     *
     * @return the post-transition resolver, not null
     */
    public static ZoneResolver postTransition() {
        return Impl.POST_TRANSITION;
    }

    /**
     * Obtains the post-gap-pre-overlap zone resolver, which returns the instant
     * after the transition for gaps, and the earlier offset for overlaps.
     * <p>
     * Gap - changes the local date-time to the instant of the transition using the "after" offset.<br />
     * Overlap - chooses the earlier of the two offsets, which is the "before" offset.<br />
     * Other - applies the appropriate offset.<br />
     *
     * @return the post-transition resolver, not null
     */
    public static ZoneResolver postGapPreOverlap() {
        return Impl.POST_GAP_PRE_OVERLAP;
    }

    /**
     * Obtains the retain offset resolver, which returns the instant after the
     * transition for gaps, and the same offset for overlaps.
     * <p>
     * This resolver is the same as the {@link #postGapPreOverlap()} resolver with
     * one additional rule. When processing an overlap, this resolver attempts
     * to use the same offset as the offset specified in the old date-time.
     * If that offset is invalid then the later offset is chosen.
     * <p>
     * This resolver is most especially useful when adding or subtracting time
     * from a {@code ZonedDateTime}.
     * <p>
     * Gap - changes the local date-time to the instant of the transition using the "after" offset.<br />
     * Overlap - chooses the same offset as the old date-time specified, unless that is invalid when
     * it chooses the earlier of the two offsets.<br />
     * Other - applies the appropriate offset.<br />
     *
     * @return the retain offset resolver, not null
     */
    public static ZoneResolver retainOffset() {
        return Impl.RETAIN_OFFSET;
    }

    /**
     * Obtains the push forward resolver, which changes the time of the result
     * in a gap by adding the length of the gap.
     * <p>
     * If the transition is a gap, then the resolver will add the length of
     * the gap in seconds to the local time.
     * For example, given a gap from 01:00 to 02:00 and a time of 01:20, this
     * will add one hour to result in 02:20.
     * <p>
     * If the transition is an overlap, then the resolver will choose the
     * later of the two offsets.
     * <p>
     * Gap - changes the local date-time to be later by the length of the gap, using the "after" offset.<br />
     * Overlap - chooses the later of the two offsets, which is the "after" offset.<br />
     * Other - applies the appropriate offset.<br />
     *
     * @return the push forward resolver, not null
     */
    public static ZoneResolver pushForward() {
        return Impl.PUSH_FORWARD;
    }

    /**
     * Creates a combined resolver, using two different strategies for gap and overlap.
     * <p>
     * If the local date-time is neither a gap nor an overlap, the appropriate offset
     * is simply added.
     *
     * @param gapResolver  the resolver to use for a gap, not null
     * @param overlapResolver  the resolver to use for an overlap, not null
     * @return the combination resolver, not null
     */
    public static ZoneResolver combination(ZoneResolver gapResolver, ZoneResolver overlapResolver) {
        DateTimes.checkNotNull(gapResolver, "ZoneResolver must not be null");
        DateTimes.checkNotNull(overlapResolver, "ZoneResolver must not be null");
        if (gapResolver == overlapResolver) {
            return gapResolver;
        }
        return new Combination(gapResolver, overlapResolver);
    }

    //-----------------------------------------------------------------------
    /**
     * Class implementing combination resolver.
     */
    private static final class Combination implements ZoneResolver {
        /** The gap resolver. */
        private final ZoneResolver gapResolver;
        /** The overlap resolver. */
        private final ZoneResolver overlapResolver;

        /**
         * Constructor.
         * @param gapResolver  the resolver to use for a gap, not null
         * @param overlapResolver  the resolver to use for an overlap, not null
         */
        Combination(ZoneResolver gapResolver, ZoneResolver overlapResolver) {
            this.gapResolver = gapResolver;
            this.overlapResolver = overlapResolver;
        }
        @Override
        public OffsetDateTime resolve(LocalDateTime desiredLocalDateTime, ZoneOffsetInfo info, ZoneRules rules, ZoneId zone, OffsetDateTime oldDateTime) {
            if (info.isTransition()) {
                ZoneOffsetTransition transition = info.getTransition();
                if (transition.isGap()) {
                    return gapResolver.resolve(desiredLocalDateTime, info, rules, zone, oldDateTime);
                } else {  // overlap
                    return overlapResolver.resolve(desiredLocalDateTime, info, rules, zone, oldDateTime);
                }
            }
            return OffsetDateTime.of(desiredLocalDateTime, info.getOffset());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the resolvers.
     */
    private static enum Impl implements ZoneResolver {
        /** Strict. */
        STRICT {
            /** {@inheritDoc} */
            @Override
            public OffsetDateTime resolve(LocalDateTime desiredLocalDateTime, ZoneOffsetInfo info, ZoneRules rules, ZoneId zone, OffsetDateTime oldDateTime) {
                if (info.isTransition()) {
                    ZoneOffsetTransition transition = info.getTransition();
                    if (transition.isGap()) {
                        throw new CalendricalException("Local date-time " + desiredLocalDateTime + " does not exist in time-zone " +
                                zone + " due to a gap in the local time-line");
                    } else {  // overlap
                        throw new CalendricalException("Local date-time " + desiredLocalDateTime +
                                " has two matching offsets, " + transition.getOffsetBefore() +
                                " and " + transition.getOffsetAfter() + ", in time-zone " + zone);
                    }
                }
                return OffsetDateTime.of(desiredLocalDateTime, info.getOffset());
            }
        },
        /** Pre-transition. */
        PRE_TRANSITION {
            /** {@inheritDoc} */
            @Override
            public OffsetDateTime resolve(LocalDateTime desiredLocalDateTime, ZoneOffsetInfo info, ZoneRules rules, ZoneId zone, OffsetDateTime oldDateTime) {
                if (info.isTransition()) {
                    ZoneOffsetTransition transition = info.getTransition();
                    if (transition.isGap()) {
                        return transition.getDateTimeBefore().minusNanos(1);
                    } else {  // overlap
                        return OffsetDateTime.of(desiredLocalDateTime, transition.getOffsetBefore());
                    }
                }
                return OffsetDateTime.of(desiredLocalDateTime, info.getOffset());
            }
        },
        /** Post-transition. */
        POST_TRANSITION {
            /** {@inheritDoc} */
            @Override
            public OffsetDateTime resolve(LocalDateTime desiredLocalDateTime, ZoneOffsetInfo info, ZoneRules rules, ZoneId zone, OffsetDateTime oldDateTime) {
                if (info.isTransition()) {
                    ZoneOffsetTransition transition = info.getTransition();
                    if (transition.isGap()) {
                        return transition.getDateTimeAfter();
                    } else {  // overlap
                        return OffsetDateTime.of(desiredLocalDateTime, transition.getOffsetAfter());
                    }
                }
                return OffsetDateTime.of(desiredLocalDateTime, info.getOffset());
            }
        },
        /** Post-gap pre-overlap. */
        POST_GAP_PRE_OVERLAP {
            /** {@inheritDoc} */
            @Override
            public OffsetDateTime resolve(LocalDateTime desiredLocalDateTime, ZoneOffsetInfo info, ZoneRules rules, ZoneId zone, OffsetDateTime oldDateTime) {
                if (info.isTransition()) {
                    ZoneOffsetTransition transition = info.getTransition();
                    if (transition.isGap()) {
                        return transition.getDateTimeAfter();
                    } else {  // overlap
                        return OffsetDateTime.of(desiredLocalDateTime, transition.getOffsetBefore());
                    }
                }
                return OffsetDateTime.of(desiredLocalDateTime, info.getOffset());
            }
        },
        /** Retain offset. */
        RETAIN_OFFSET {
            /** {@inheritDoc} */
            @Override
            public OffsetDateTime resolve(LocalDateTime desiredLocalDateTime, ZoneOffsetInfo info, ZoneRules rules, ZoneId zone, OffsetDateTime oldDateTime) {
                if (info.isTransition()) {
                    ZoneOffsetTransition transition = info.getTransition();
                    if (transition.isGap()) {
                        return transition.getDateTimeAfter();
                    } else {  // overlap
                        if (oldDateTime != null && transition.isValidOffset(oldDateTime.getOffset())) {
                            return OffsetDateTime.of(desiredLocalDateTime, oldDateTime.getOffset());
                        }
                        return OffsetDateTime.of(desiredLocalDateTime, transition.getOffsetBefore());
                    }
                }
                return OffsetDateTime.of(desiredLocalDateTime, info.getOffset());
            }
        },
        /** Push forward. */
        PUSH_FORWARD {
            /** {@inheritDoc} */
            @Override
            public OffsetDateTime resolve(LocalDateTime desiredLocalDateTime, ZoneOffsetInfo info, ZoneRules rules, ZoneId zone, OffsetDateTime oldDateTime) {
                if (info.isTransition()) {
                    ZoneOffsetTransition transition = info.getTransition();
                    if (transition.isGap()) {
                        LocalDateTime result = desiredLocalDateTime.plus(transition.getTransitionSize());
                        return OffsetDateTime.of(result, transition.getOffsetAfter());
                    } else {  // overlap
                        return OffsetDateTime.of(desiredLocalDateTime, transition.getOffsetAfter());
                    }
                }
                return OffsetDateTime.of(desiredLocalDateTime, info.getOffset());
            }
        },
    }

}
