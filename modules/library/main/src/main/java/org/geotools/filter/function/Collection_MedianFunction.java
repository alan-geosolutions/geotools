/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
 *
 * Created on May 11, 2005, 6:21 PM
 */
package org.geotools.filter.function;

import static org.geotools.filter.capability.FunctionNameImpl.parameter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.api.feature.Feature;
import org.geotools.api.feature.type.FeatureType;
import org.geotools.api.filter.capability.FunctionName;
import org.geotools.api.filter.expression.Expression;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.visitor.CalcResult;
import org.geotools.feature.visitor.MedianVisitor;
import org.geotools.filter.FunctionExpressionImpl;
import org.geotools.filter.IllegalFilterException;
import org.geotools.filter.capability.FunctionNameImpl;

/**
 * Calculates the median value of an attribute for a given FeatureCollection and Expression.
 *
 * @author Cory Horner
 * @since 2.2M2
 */
public class Collection_MedianFunction extends FunctionExpressionImpl {
    /** The logger for the filter module. */
    private static final Logger LOGGER = org.geotools.util.logging.Logging.getLogger(Collection_MedianFunction.class);

    FeatureCollection<? extends FeatureType, ? extends Feature> previousFeatureCollection = null;
    Object median = null;

    // public static FunctionName NAME = new FunctionNameImpl("Collection_Median","value");
    public static FunctionName NAME = new FunctionNameImpl(
            "Collection_Median", parameter("median", Comparable.class), parameter("expression", Comparable.class));
    /** Creates a new instance of Collection_MedianFunction */
    public Collection_MedianFunction() {
        super(NAME);
    }

    /**
     * Calculate median (using FeatureCalc) - only one parameter is used.
     *
     * @param collection collection to calculate the median
     * @param expression Single Expression argument
     * @return An object containing the median value of the attributes
     */
    static CalcResult calculateMedian(
            FeatureCollection<? extends FeatureType, ? extends Feature> collection, Expression expression)
            throws IllegalFilterException, IOException {
        MedianVisitor medianVisitor = new MedianVisitor(expression);
        collection.accepts(medianVisitor, null);

        return medianVisitor.getResult();
    }

    /**
     * The provided arguments are evaulated with respect to the FeatureCollection.
     *
     * <p>For an aggregate function (like median) please use the WFS mandated XPath syntax to refer to featureMember
     * content.
     *
     * <p>To refer to all 'X': <code>featureMember/asterisk/X</code>
     */
    @Override
    public void setParameters(List<Expression> args) {
        // if we see "featureMembers/*/ATTRIBUTE" change to "ATTRIBUTE"
        org.geotools.api.filter.expression.Expression expr = args.get(0);
        expr = (org.geotools.api.filter.expression.Expression)
                expr.accept(new CollectionFeatureMemberFilterVisitor(), null);
        args.set(0, expr);
        super.setParameters(args);
    }

    @Override
    public Object evaluate(Object feature) {
        if (feature == null) {
            return Integer.valueOf(0); // no features were visited in the making of this answer
        }
        Expression expr = getExpression(0);
        FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection =
                (SimpleFeatureCollection) feature;
        synchronized (featureCollection) {
            if (featureCollection != previousFeatureCollection) {
                previousFeatureCollection = featureCollection;
                median = null;
                try {
                    CalcResult result = calculateMedian(featureCollection, expr);
                    if (result != null) {
                        median = result.getValue();
                    }
                } catch (IllegalFilterException | IOException e) {
                    LOGGER.log(Level.FINER, e.getLocalizedMessage(), e);
                }
            }
        }
        return median;
    }

    public void setExpression(Expression e) {
        setParameters(Collections.singletonList(e));
    }
}
