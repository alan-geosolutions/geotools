package org.geotools.brewer.styling.builder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.List;
import org.geotools.api.filter.expression.Function;
import org.geotools.api.style.FeatureTypeStyle;
import org.geotools.api.style.Graphic;
import org.geotools.api.style.GraphicalSymbol;
import org.geotools.api.style.LinePlacement;
import org.geotools.api.style.LineSymbolizer;
import org.geotools.api.style.Mark;
import org.geotools.api.style.PointPlacement;
import org.geotools.api.style.Rule;
import org.geotools.api.style.Style;
import org.geotools.api.style.TextSymbolizer;
import org.geotools.feature.NameImpl;
import org.geotools.filter.function.RecodeFunction;
import org.junit.Test;
import si.uom.SI;

public class CookbookLineTest extends AbstractStyleTest {

    @Test
    public void testSimple() {
        Style style = new StrokeBuilder().color(Color.BLACK).width(3).buildStyle();
        // print(style);

        // round up the basic elements and check its simple
        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertSimpleStyle(collector);

        // check the size
        LineSymbolizer ls = (LineSymbolizer) collector.symbolizers.get(0);
        assertEquals(3, (int) ls.getStroke().getWidth().evaluate(null, Integer.class));
        assertEquals(Color.BLACK, ls.getStroke().getColor().evaluate(null, Color.class));
    }

    @Test
    public void testLineWithBorder() {
        StyleBuilder sb = new StyleBuilder();
        sb.defaultStyle();
        sb.featureTypeStyle()
                .rule()
                .line()
                .stroke()
                .colorHex("#333333")
                .width(5)
                .lineCapName("round");
        sb.featureTypeStyle()
                .rule()
                .line()
                .stroke()
                .colorHex("#6699FF")
                .width(3)
                .lineCapName("round");
        Style style = sb.buildStyle();
        // print(style);

        // round up the basic elements and check its simple
        assertTrue(style.isDefault());
        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertEquals(2, collector.featureTypeStyles.size());
        assertEquals(2, collector.rules.size());
        assertEquals(2, collector.symbolizers.size());

        // check the first line
        LineSymbolizer ls = (LineSymbolizer) collector.symbolizers.get(0);
        assertEquals(5, (int) ls.getStroke().getWidth().evaluate(null, Integer.class));
        assertEquals("#333333", ls.getStroke().getColor().evaluate(null, String.class));

        // check the second line
        ls = (LineSymbolizer) collector.symbolizers.get(1);
        assertEquals(3, (int) ls.getStroke().getWidth().evaluate(null, Integer.class));
        assertEquals("#6699FF", ls.getStroke().getColor().evaluate(null, String.class));
    }

    @Test
    public void testDashed() {
        Style style =
                new StrokeBuilder().color(Color.BLUE).width(3).dashArray(5, 2).buildStyle();
        // print(style);

        // round up the basic elements and check its simple
        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertSimpleStyle(collector);

        // check the size
        LineSymbolizer ls = (LineSymbolizer) collector.symbolizers.get(0);
        assertEquals(3, (int) ls.getStroke().getWidth().evaluate(null, Integer.class));
        assertEquals(Color.BLUE, ls.getStroke().getColor().evaluate(null, Color.class));
        assertArrayEquals(new float[] {5, 2}, ls.getStroke().getDashArray(), 0f);
    }

    @Test
    public void testOffset() {
        FeatureTypeStyleBuilder fts = new FeatureTypeStyleBuilder();
        RuleBuilder rule = fts.rule();
        rule.line().stroke().colorHex("#000000");
        LineSymbolizerBuilder line = rule.line();
        line.stroke().colorHex("#FF0000").dashArray(5, 2);
        line.perpendicularOffset(5);
        FeatureTypeStyle style = fts.build();

        // round up the basic elements and check its simple
        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertEquals(1, collector.rules.size());
        assertEquals(2, collector.symbolizers.size());

        // check the perpendicular offset
        LineSymbolizer ls = (LineSymbolizer) collector.symbolizers.get(1);
        assertEquals(5, ls.getPerpendicularOffset().evaluate(null, Double.class), 0d);
    }

    @Test
    public void testRailroad() {
        FeatureTypeStyleBuilder ftsb = new FeatureTypeStyleBuilder();
        ftsb.setFeatureTypeNames(List.of(new NameImpl("railways")));
        ftsb.rule().line().stroke().colorHex("#333333").width(3);
        ftsb.rule()
                .line()
                .stroke()
                .graphicStroke()
                .size(12)
                .mark()
                .name("shape://vertline")
                .stroke()
                .colorHex("#333333")
                .width(1);
        Style style = ftsb.buildStyle();
        // print(style);

        // round up the elements and check the basics
        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertEquals(1, collector.featureTypeStyles.size());
        assertEquals(2, collector.rules.size());
        assertEquals(2, collector.symbolizers.size());

        // check type name
        FeatureTypeStyle fts = collector.featureTypeStyles.get(0);
        fts.featureTypeNames().forEach(n -> assertEquals("railways", n.getLocalPart()));

        // check the simple line
        LineSymbolizer ls = (LineSymbolizer) collector.symbolizers.get(0);
        assertEquals(3, (int) ls.getStroke().getWidth().evaluate(null, Integer.class));
        assertEquals("#333333", ls.getStroke().getColor().evaluate(null, String.class));

        // check the rail
        ls = (LineSymbolizer) collector.symbolizers.get(1);
        Graphic graphic = ls.getStroke().getGraphicStroke();
        List<GraphicalSymbol> symbols = graphic.graphicalSymbols();
        assertEquals(1, symbols.size());
        Mark mark = (Mark) symbols.get(0);
        assertEquals("shape://vertline", mark.getWellKnownName().evaluate(null));
        assertEquals("#333333", mark.getStroke().getColor().evaluate(null, String.class));
        assertEquals(1, (int) mark.getStroke().getWidth().evaluate(null, Integer.class));
    }

    @Test
    public void testSpacedGraphics() {
        MarkBuilder mb = new StrokeBuilder()
                .dashArray(4, 6)
                .graphicStroke()
                .size(4)
                .mark()
                .name("circle");
        mb.stroke().colorHex("#333333").width(1);
        mb.fill().colorHex("#666666");
        Style style = mb.buildStyle();
        // print(style);

        // round up the basic elements and check its simple
        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertSimpleStyle(collector);

        // check the dots
        LineSymbolizer ls = (LineSymbolizer) collector.symbolizers.get(0);
        assertArrayEquals(new float[] {4, 6}, ls.getStroke().getDashArray(), 0f);
        Graphic graphic = ls.getStroke().getGraphicStroke();
        List<GraphicalSymbol> symbols = graphic.graphicalSymbols();
        assertEquals(1, symbols.size());
        Mark mark = (Mark) symbols.get(0);
        assertEquals("circle", mark.getWellKnownName().evaluate(null));
        assertEquals("#333333", mark.getStroke().getColor().evaluate(null, String.class));
        assertEquals(1, (int) mark.getStroke().getWidth().evaluate(null, Integer.class));
        assertEquals("#666666", mark.getFill().getColor().evaluate(null, String.class));
    }

    @Test
    public void testAlternatingSymbols() {
        RuleBuilder rb = new RuleBuilder();
        rb.line().stroke().color(Color.BLUE).dashArray(10, 10);
        rb.line()
                .stroke()
                .dashArray(5, 15)
                .dashOffset(7.5)
                .graphicStroke()
                .mark()
                .name("circle")
                .stroke()
                .colorHex("#000033");
        Style style = rb.buildStyle();
        // print(style);

        // round up the elements and check the basics
        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertEquals(1, collector.featureTypeStyles.size());
        assertEquals(1, collector.rules.size());
        assertEquals(2, collector.symbolizers.size());

        // check the line
        LineSymbolizer ls = (LineSymbolizer) collector.symbolizers.get(0);
        assertEquals(1, (int) ls.getStroke().getWidth().evaluate(null, Integer.class));
        assertEquals(Color.BLUE, ls.getStroke().getColor().evaluate(null, Color.class));
        assertArrayEquals(new float[] {10, 10}, ls.getStroke().getDashArray(), 0f);

        // check the dots
        ls = (LineSymbolizer) collector.symbolizers.get(1);
        assertArrayEquals(new float[] {5, 15}, ls.getStroke().getDashArray(), 0f);
        assertEquals(7.5, ls.getStroke().getDashOffset().evaluate(null, Double.class), 0.0);
        Graphic graphic = ls.getStroke().getGraphicStroke();
        List<GraphicalSymbol> symbols = graphic.graphicalSymbols();
        assertEquals(1, symbols.size());
        Mark mark = (Mark) symbols.get(0);
        assertEquals("circle", mark.getWellKnownName().evaluate(null));
        assertEquals("#000033", mark.getStroke().getColor().evaluate(null, String.class));
        assertEquals(1, (int) mark.getStroke().getWidth().evaluate(null, Integer.class));
        assertNull(mark.getFill());
    }

    @Test
    public void testLineDefaultLabels() {
        RuleBuilder rb = new RuleBuilder();
        rb.line().stroke().color(Color.RED);
        rb.text().label("name").fill().color(Color.BLACK);
        Style style = rb.buildStyle();
        // print(style);

        // round up the elements and check the basics
        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertEquals(1, collector.featureTypeStyles.size());
        assertEquals(1, collector.rules.size());
        assertEquals(2, collector.symbolizers.size());

        TextSymbolizer ps = (TextSymbolizer) collector.symbolizers.get(1);
        assertEquals(ff.property("name"), ps.getLabel());
        assertEquals(Color.BLACK, ps.getFill().getColor().evaluate(null, Color.class));

        // placement
        PointPlacement pp = (PointPlacement) ps.getLabelPlacement();
        assertNull(pp);
    }

    @Test
    public void testLineOptimizedLabels() {
        RuleBuilder rb = new RuleBuilder();
        rb.line().stroke().color(Color.RED);
        TextSymbolizerBuilder tsb = rb.text()
                .label("name")
                .option("followLine", true)
                .option("maxAngleDelta", 90)
                .option("maxDisplacement", 400)
                .option("repeat", 150);
        tsb.linePlacement();
        tsb.fill().color(Color.BLACK);
        Style style = rb.buildStyle();
        // print(style);

        // round up the elements and check the basics
        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertEquals(1, collector.featureTypeStyles.size());
        assertEquals(1, collector.rules.size());
        assertEquals(2, collector.symbolizers.size());

        TextSymbolizer ts = (TextSymbolizer) collector.symbolizers.get(1);
        assertEquals(ff.property("name"), ts.getLabel());
        assertEquals(Color.BLACK, ts.getFill().getColor().evaluate(null, Color.class));
        assertEquals(4, ts.getOptions().size());

        // placement
        assertTrue(ts.getLabelPlacement() instanceof LinePlacement);
    }

    @Test
    public void testAttributeBased() {
        FeatureTypeStyleBuilder fts = new FeatureTypeStyleBuilder();
        fts.rule()
                .filter("type = 'local-road'")
                .line()
                .stroke()
                .colorHex("#009933")
                .width(2);
        fts.rule()
                .filter("type = 'secondary'")
                .line()
                .stroke()
                .colorHex("#0055CC")
                .width(3);
        fts.rule()
                .filter("type = 'highway'")
                .line()
                .stroke()
                .colorHex("#FF0000")
                .width(6);
        Style style = fts.buildStyle();
        // print(style);

        // round up the elements and check the basics
        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertEquals(1, collector.featureTypeStyles.size());
        assertEquals(3, collector.rules.size());
        assertEquals(3, collector.symbolizers.size());

        // check rules
        assertEquals(
                ff.equals(ff.property("type"), ff.literal("local-road")),
                collector.rules.get(0).getFilter());
        assertEquals(
                ff.equals(ff.property("type"), ff.literal("secondary")),
                collector.rules.get(1).getFilter());
        assertEquals(
                ff.equals(ff.property("type"), ff.literal("highway")),
                collector.rules.get(2).getFilter());

        // check symbolizers
        LineSymbolizer ls = (LineSymbolizer) collector.symbolizers.get(0);
        assertEquals(2, (int) ls.getStroke().getWidth().evaluate(null, Integer.class));
        assertEquals("#009933", ls.getStroke().getColor().evaluate(null, String.class));
        ls = (LineSymbolizer) collector.symbolizers.get(1);
        assertEquals(3, (int) ls.getStroke().getWidth().evaluate(null, Integer.class));
        assertEquals("#0055CC", ls.getStroke().getColor().evaluate(null, String.class));
        ls = (LineSymbolizer) collector.symbolizers.get(2);
        assertEquals(6, (int) ls.getStroke().getWidth().evaluate(null, Integer.class));
        assertEquals("#FF0000", ls.getStroke().getColor().evaluate(null, String.class));
    }

    @Test
    public void testAttributeBasedRecode() {
        // this is a case showing that recode/categorize is not always the most compact solution...
        Function width = ff.function(
                "recode",
                ff.property("type"),
                ff.literal("local-road"),
                ff.literal(2),
                ff.literal("secondary"),
                ff.literal(3),
                ff.literal("highway"),
                ff.literal(6));
        Function color = ff.function(
                "recode",
                ff.property("type"),
                ff.literal("local-road"),
                ff.literal("#009933"),
                ff.literal("secondary"),
                ff.literal("#0055CC2"),
                ff.literal("highway"),
                ff.literal("#FF0000"));
        Style style =
                new LineSymbolizerBuilder().stroke().color(color).width(width).buildStyle();
        // print(style);

        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertSimpleStyle(collector);

        // check the function is there were we expect it
        LineSymbolizer ls = (LineSymbolizer) collector.symbolizers.get(0);
        assertTrue(ls.getStroke().getColor() instanceof RecodeFunction);
        assertTrue(ls.getStroke().getWidth() instanceof RecodeFunction);
    }

    @Test
    public void testZoomBasedLine() {
        FeatureTypeStyleBuilder fts = new FeatureTypeStyleBuilder();
        fts.rule()
                .name("Large")
                .max(180000000)
                .line()
                .stroke()
                .colorHex("#009933")
                .width(6);
        fts.rule()
                .name("Medium")
                .min(180000000)
                .max(360000000)
                .line()
                .stroke()
                .colorHex("#009933")
                .width(4);
        fts.rule()
                .name("Small")
                .min(360000000)
                .line()
                .stroke()
                .colorHex("#009933")
                .width(2);
        Style style = fts.buildStyle();
        // print(style);

        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertEquals(1, collector.featureTypeStyles.size());
        assertEquals(3, collector.rules.size());
        assertEquals(3, collector.symbolizers.size());

        // check rules and styles
        checkScaleBasedRule(collector.rules.get(0), "Large", 0, 180000000, 6);
        checkScaleBasedRule(collector.rules.get(1), "Medium", 180000000, 360000000, 4);
        checkScaleBasedRule(collector.rules.get(2), "Small", 360000000, Double.POSITIVE_INFINITY, 2);
    }

    private void checkScaleBasedRule(Rule rule, String name, double minDenominator, double maxDenominator, int size) {
        assertEquals(name, rule.getName());
        assertEquals(minDenominator, rule.getMinScaleDenominator(), 0.0);
        assertEquals(maxDenominator, rule.getMaxScaleDenominator(), 0.0);
        assertEquals(1, rule.symbolizers().size());
        LineSymbolizer ls = (LineSymbolizer) rule.symbolizers().get(0);
        assertEquals(size, (int) ls.getStroke().getWidth().evaluate(null, Integer.class));
    }

    @Test
    public void testUomLine() {
        Style style = new LineSymbolizerBuilder()
                .uom(SI.METRE)
                .stroke()
                .width(50)
                .colorHex("#009933")
                .buildStyle();
        // print(style);

        StyleCollector collector = new StyleCollector();
        style.accept(collector);
        assertSimpleStyle(collector);

        LineSymbolizer ls = (LineSymbolizer) collector.symbolizers.get(0);
        assertEquals(SI.METRE, ls.getUnitOfMeasure());
    }
}
