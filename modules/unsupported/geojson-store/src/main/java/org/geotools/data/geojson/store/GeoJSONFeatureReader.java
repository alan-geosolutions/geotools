/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2019, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.geojson.store;

import java.io.IOException;
import java.util.NoSuchElementException;
import org.geotools.api.data.FeatureReader;
import org.geotools.api.data.Query;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.geojson.GeoJSONReader;
import org.geotools.data.store.ContentState;
import org.geotools.feature.FeatureIterator;

public class GeoJSONFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

    private ContentState state;

    protected GeoJSONReader reader;

    private FeatureIterator<SimpleFeature> iterator;

    public GeoJSONFeatureReader(ContentState contentState, Query query) throws IOException {
        this.state = contentState;
        GeoJSONDataStore ds = (GeoJSONDataStore) state.getEntry().getDataStore();
        reader = ds.read();
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        GeoJSONDataStore ds = (GeoJSONDataStore) state.getEntry().getDataStore();
        try {
            SimpleFeatureType schema = ds.getSchema(state.getEntry().getName());
            if (schema == null) {
                schema = ds.getSchema("features");
            }
            return schema;
        } catch (IOException e) {

            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.INFO, "", e);
        }
        return null;
    }

    @Override
    public SimpleFeature next() throws IOException, IllegalArgumentException, NoSuchElementException {
        if (iterator == null) {
            iterator = reader.getIterator();
        }
        return iterator.next();
    }

    @Override
    public boolean hasNext() throws IOException {
        if (iterator == null) {
            iterator = reader.getIterator();
        }
        return iterator.hasNext();
    }

    @Override
    @SuppressWarnings("PMD.UseTryWithResources") // closing field
    public void close() throws IOException {
        try {
            if (iterator != null) {
                iterator.close();
            }
        } finally {
            reader.close();
        }
    }
}
