/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.gml2.bindings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.geotools.gml2.GML;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

public class GMLLineStringTypeBindingTest extends AbstractGMLBindingTest {
    ElementInstance line;
    ElementInstance coord1;
    ElementInstance coord2;
    ElementInstance coord3;
    ElementInstance coords;
    MutablePicoContainer container;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        line = createElement(GML.NAMESPACE, "myLineString", GML.LineStringType, null);
        coord1 = createElement(GML.NAMESPACE, "coord", GML.CoordType, null);
        coord2 = createElement(GML.NAMESPACE, "coord", GML.CoordType, null);
        coord3 = createElement(GML.NAMESPACE, "coord", GML.CoordType, null);

        coords = createElement(GML.NAMESPACE, "coordinates", GML.CoordinatesType, null);

        container = new DefaultPicoContainer();
        container.registerComponentInstance(CoordinateArraySequenceFactory.instance());
        container.registerComponentImplementation(GeometryFactory.class);
        container.registerComponentImplementation(GMLLineStringTypeBinding.class);
    }

    @Test
    public void testCoordTwo() throws Exception {
        Node node = createNode(
                line,
                new ElementInstance[] {coord1, coord2},
                new Object[] {
                    createCoordinateSequence(new Coordinate(1, 2)), createCoordinateSequence(new Coordinate(3, 4))
                },
                null,
                null);

        GMLLineStringTypeBinding s =
                (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);
        LineString lineString = (LineString) s.parse(line, node, null);

        assertNotNull(lineString);
        assertEquals(lineString.getNumPoints(), 2);
        assertEquals(lineString.getPointN(0).getX(), 1d, 0);
        assertEquals(lineString.getPointN(0).getY(), 2d, 0);
        assertEquals(lineString.getPointN(1).getX(), 3d, 0);
        assertEquals(lineString.getPointN(1).getY(), 4d, 0);
    }

    @Test
    public void testCoordSingle() throws Exception {
        Node node = createNode(
                line,
                new ElementInstance[] {coord1},
                new Object[] {
                    createCoordinateSequence(new Coordinate(1, 2)),
                },
                null,
                null);

        GMLLineStringTypeBinding s =
                (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);

        try {
            s.parse(line, node, null);
            fail("Should have died with just one coordinate");
        } catch (RuntimeException e) {
            // ok
        }
    }

    @Test
    public void testCoordMulti() throws Exception {
        Node node = createNode(
                line,
                new ElementInstance[] {coord1, coord2, coord3},
                new Object[] {
                    createCoordinateSequence(new Coordinate(1, 2)),
                    createCoordinateSequence(new Coordinate(3, 4)),
                    createCoordinateSequence(new Coordinate(5, 6))
                },
                null,
                null);

        GMLLineStringTypeBinding s =
                (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);
        LineString lineString = (LineString) s.parse(line, node, null);

        assertNotNull(lineString);
        assertEquals(lineString.getNumPoints(), 3);
        assertEquals(lineString.getPointN(0).getX(), 1d, 0);
        assertEquals(lineString.getPointN(0).getY(), 2d, 0);
        assertEquals(lineString.getPointN(1).getX(), 3d, 0);
        assertEquals(lineString.getPointN(1).getY(), 4d, 0);
        assertEquals(lineString.getPointN(2).getX(), 5d, 0);
        assertEquals(lineString.getPointN(2).getY(), 6d, 0);
    }

    @Test
    public void testCoordinatesTwo() throws Exception {
        Node node = createNode(
                line,
                new ElementInstance[] {coords},
                new Object[] {
                    createCoordinateSequence(new Coordinate[] {new Coordinate(1, 2), new Coordinate(3, 4)}),
                },
                null,
                null);

        GMLLineStringTypeBinding s =
                (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);

        LineString lineString = (LineString) s.parse(line, node, null);
        assertNotNull(lineString);
        assertEquals(lineString.getNumPoints(), 2);
        assertEquals(lineString.getPointN(0).getX(), 1d, 0);
        assertEquals(lineString.getPointN(0).getY(), 2d, 0);
        assertEquals(lineString.getPointN(1).getX(), 3d, 0);
        assertEquals(lineString.getPointN(1).getY(), 4d, 0);
    }

    @Test
    public void testCoordinatesSingle() throws Exception {
        Node node = createNode(
                line,
                new ElementInstance[] {coords},
                new Object[] {
                    createCoordinateSequence(new Coordinate[] {new Coordinate(1, 2)}),
                },
                null,
                null);

        GMLLineStringTypeBinding s =
                (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);

        try {
            s.parse(line, node, null);
            fail("Should have died with just one coordinate");
        } catch (RuntimeException e) {
            // ok
        }
    }

    @Test
    public void testCoordinatesMulti() throws Exception {
        Node node = createNode(
                line,
                new ElementInstance[] {coords},
                new Object[] {
                    createCoordinateSequence(
                            new Coordinate[] {new Coordinate(1, 2), new Coordinate(3, 4), new Coordinate(5, 6)}),
                },
                null,
                null);

        GMLLineStringTypeBinding s =
                (GMLLineStringTypeBinding) container.getComponentInstanceOfType(GMLLineStringTypeBinding.class);

        LineString lineString = (LineString) s.parse(line, node, null);
        assertNotNull(lineString);
        assertEquals(lineString.getNumPoints(), 3);
        assertEquals(lineString.getPointN(0).getX(), 1d, 0);
        assertEquals(lineString.getPointN(0).getY(), 2d, 0);
        assertEquals(lineString.getPointN(1).getX(), 3d, 0);
        assertEquals(lineString.getPointN(1).getY(), 4d, 0);
        assertEquals(lineString.getPointN(2).getX(), 5d, 0);
        assertEquals(lineString.getPointN(2).getY(), 6d, 0);
    }
}
