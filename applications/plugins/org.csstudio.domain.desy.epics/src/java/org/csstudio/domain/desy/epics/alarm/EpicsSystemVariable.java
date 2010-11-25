/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.desy.epics.alarm;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.ISystemVariable;
import org.csstudio.domain.desy.SystemVariableId;
import org.csstudio.domain.desy.alarm.IHasAlarm;
import org.csstudio.domain.desy.time.TimeInstant;

/**
 * An EPICS system variable is tightly bound to EpicsAlarms
 *
 * @author bknerr
 * @since 17.11.2010
 */
public class EpicsSystemVariable<T> implements ISystemVariable<T>, IHasAlarm {


    private final SystemVariableId _id;
    private final String _name;
    private final T _value;
    private final TimeInstant _timestamp;
    private final EpicsAlarm _alarm;


    /**
     * Constructor.
     * @param id
     * @param name
     * @param value
     * @param timestamp
     * @param alarm
     */
    public EpicsSystemVariable(@Nonnull final SystemVariableId id,
                               @Nonnull final String name,
                               @Nonnull final T value,
                               @Nullable final TimeInstant timestamp,
                               @Nullable final EpicsAlarm alarm) {
        _id = id;
        _name = name;
        _value = value;
        _timestamp = timestamp;
        // TODO (bknerr) : Check for plausibility whether the contained status makes sense for type T.
        _alarm = alarm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public SystemVariableId getId() {
        return _id;
    }

    /**
     * Gets the name (also called channel identifier).
     * @return the name
     */
    @Nonnull
    public String getName() {
        return _name;
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void setValue(@Nonnull final T value) {
//        _value = value;
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public T getValue() {
        return _value;
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void setTimestamp(@Nullable final TimeInstant timestamp) {
//        _timestamp = timestamp;
//    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public TimeInstant getTimestamp() {
        return _timestamp;
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void setAlarm(@Nullable final IAlarm alarm) {
//        _alarm = (EpicsAlarm) alarm;
//    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public EpicsAlarm getAlarm() {
        return _alarm;
    }
}
