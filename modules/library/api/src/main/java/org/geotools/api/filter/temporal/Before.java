/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.api.filter.temporal;

/**
 * Filter operator that determines if a temporal object is before another temporal object as defined by the Filter
 * Encoding Specification.
 *
 * <p>The Before operator is defined by ISO 19108 and has the following semantics:
 *
 * <table border='1'>
 *   <tr align='center'>
 *     <td>t1,t2</td><td>t1[],t2</td><td>t1,t2[]</td><td>t1[],t2[]</td>
 *   </tr>
 *   <tr>
 *     <td>t1 &lt; t2</td><td>t1.end &lt; t2</td><td>t1 &lt; t2.start</td><td>t1.end &lt; t2.start</td>
 *   </tr>
 * </table>
 *
 * @author Justin Deoliveira, OpenGeo
 * @see http://portal.opengeospatial.org/files/?artifact_id=39968
 * @since 8.0
 */
public interface Before extends BinaryTemporalOperator {
    /** Operator name */
    public static final String NAME = "Before";
}
