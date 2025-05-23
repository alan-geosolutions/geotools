/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2004-2007 Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.api.feature.type;

import org.geotools.api.feature.Attribute;

/**
 * The type of an attribute.
 *
 * <p>An attribute is similar to the notion of a UML attribute, or a field of a java object. See the javadoc of
 * {@link Attribute} for more info on the semantics of attributes.
 *
 * <p>
 *
 * <h3>Identifiableness</h3>
 *
 * An attribute may be "identifiable". When this is the case the attribute has a unique identifier associated with it.
 * See {@link Attribute#getIdentifier()}. The type of the attribute specifies whether it is identifiable or not
 * ({@link #isIdentified()}.
 *
 * @author Jody Garnett, Refractions Research
 * @author Justin Deoliveira, The Open Planning Project
 */
public interface AttributeType extends PropertyType {

    /**
     * Indicates if the type is identified or not.
     *
     * <p>If this method returns <code>true</code>, then the corresponding attribute must have a unique identifier, ie,
     * {@link Attribute#getIdentifier()} must return a value, and cannot be <code>
     * null</code>.
     *
     * @return <code>true</code> if the attribute is identified, otherwise <code>false</code>.
     * @see Attribute#getIdentifier()
     */
    boolean isIdentified();

    /**
     * Override of {@link PropertyType#getSuper()} which type narrows to {@link AttributeType}.
     *
     * @see PropertyType#getSuper()
     */
    @Override
    AttributeType getSuper();
}
