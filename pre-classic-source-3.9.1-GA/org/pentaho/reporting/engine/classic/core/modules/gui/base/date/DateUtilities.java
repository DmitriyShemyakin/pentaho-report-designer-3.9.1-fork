/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2000 - 2009 Pentaho Corporation, Object Refinery Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.date;

import java.util.Calendar;
import java.util.Date;

/**
 * Some useful date methods.
 *
 * @author David Gilbert.
 * @deprecated
 */
public class DateUtilities {

    /**
     * Private constructor to prevent object creation.
     */
    private DateUtilities() {
    }

    /** A working calendar. */
    private static final Calendar CALENDAR = Calendar.getInstance();

    /**
     * Creates a date.
     *
     * @param yyyy  the year.
     * @param month  the month (1 - 12).
     * @param day  the day.
     *
     * @return a date.
     */
    public static synchronized Date createDate(final int yyyy, final int month, final int day) {
        CALENDAR.clear();
        CALENDAR.set(yyyy, month - 1, day);
        return CALENDAR.getTime();
    }

    /**
     * Creates a date.
     *
     * @param yyyy  the year.
     * @param month  the month (1 - 12).
     * @param day  the day.
     * @param hour  the hour.
     * @param min  the minute.
     *
     * @return a date.
     */
    public static synchronized Date createDate(final int yyyy, final int month, final int day, final int hour, final int min) {

        CALENDAR.clear();
        CALENDAR.set(yyyy, month - 1, day, hour, min);
        return CALENDAR.getTime();

    }


}
