package org.fusesource.ide.camel.editor.features.custom;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutBendPoint;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.VerticalLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.BendPoint;
import org.eclipse.zest.layouts.exampleStructures.SimpleNode;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;


/**
 * A class to test various Zest {@link LayoutAlgorithm} in Graphiti
 *
 * @author Patrick Talbot - Initial code
 * @author Karsten Thoms - Adoption for Spray
 * @see http://www.eclipse.org/forums/index.php/m/758573/#msg_758573
 * @see http://code.google.com/a/eclipselabs.org/p/spray/issues/detail?id=91
 */
public class ZestLayoutDiagramFeature extends AbstractCustomFeature {
    static List<String> layouts = Arrays.asList("Spring Layout", 
    		"Tree Layout", 
    		"Grid Layout", 
    		"Horizontal Layout", 
    		"Horizontal Tree Layout", 
    		"Vertical Layout", 
    		"Radial Layout", 
    		"Directed Graph Layout", 
    		"Composite Layout [Directed Graph + Horizontal Shift]", 
    		"Composite Layout [Spring Layout + Horizontal Shift]", 
    		"Composite Layout [Radial Layout + Horizontal Shift]", 
    		"Composite Layout [Tree Layout + Horizontal Shift]");
    

    /**
     * Constructor
     */
    public ZestLayoutDiagramFeature(IFeatureProvider fp) {
        super(fp);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
     */
    @Override
    public String getDescription() {
        return "Layout diagram with Zest Layouter"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
     */
    @Override
    public String getName() {
        return "Layout Diagram (Zest)"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
     */
    @Override
    public boolean canExecute(ICustomContext context) {
        PictogramElement[] pes = context.getPictogramElements();
        if (pes != null && pes.length == 1 && pes[0] instanceof ContainerShape) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
     */
    @Override
    public void execute(ICustomContext context) {
        // ask the user for a LayoutAlgorithmn
        Integer type = askForLayoutType();

        if (type != null) {
            // get a map of the self connection anchor locations
            final Map<Connection, Point> selves = getSelfConnections();

            // get the chosen LayoutAlgorithmn instance
            LayoutAlgorithm layoutAlgorithm = getLayoutAlgorithmn(type.intValue());

            if (layoutAlgorithm != null) {
                try {

                    // Get the map of SimpleNode per Shapes
                    Map<Shape, SimpleNode> map = getLayoutEntities();

                    // Get the array of Connection LayoutRelationships
                    LayoutRelationship[] connections = getConnectionEntities(map);

                    // Setup the array of Shape LayoutEntity
                    LayoutEntity[] entities = map.values().toArray(new LayoutEntity[0]);

                    // Get the diagram GraphicsAlgorithmn (we need the graph dimensions)
                    GraphicsAlgorithm ga = getDiagram().getGraphicsAlgorithm();

                    // Apply the LayoutAlgorithmn
                    layoutAlgorithm.applyLayout(entities, connections, 0, 0, ga.getWidth(), ga.getHeight(), false, false);

                    // Update the Graphiti Shapes and Connections locations
                    updateGraphCoordinates(entities, connections);

                    // Reposition the self connections bendpoints:
                    adaptSelfBendPoints(selves);

                } catch (InvalidLayoutConfiguration e) {
                	CamelEditorUIActivator.pluginLog().logError(e);
                }
            }
        }
    }

    /**
     * Used to keep track of the initial Connection locations for self connections<br/>
     * The self connections cannot be computed by the LayoutAlgorithmn but the Nodes will probably be moved<br/>
     * So we need to recompute the bend points locations based on the offset of the Anchor from the initial location
     *
     * @return a {@link Map} of initial {@link Anchor} location {@link Point} per {@link Connection}s
     */
    private Map<Connection, Point> getSelfConnections() {
        IGaService gaService = Graphiti.getGaService();
        Map<Connection, Point> selves = new HashMap<>();
        EList<Connection> connections = getDiagram().getConnections();
        for (Connection connection : connections) {
            AnchorContainer source = connection.getStart().getParent();
            AnchorContainer target = connection.getEnd().getParent();
            if (source == target) {
                GraphicsAlgorithm p = source.getGraphicsAlgorithm();
                Point start = gaService.createPoint(p.getX(), p.getY());
                selves.put(connection, start);
            }
        }
        return selves;
    }

    /**
     * Reposition the bendpoints based on the offset from the initial {@link Anchor} location to the new location
     *
     * @param selves
     *            The {@link Map} of initial {@link Anchor} location {@link Point} per {@link Connection}s
     */
    private void adaptSelfBendPoints(Map<Connection, Point> selves) {
        for (Map.Entry<Connection, Point> entry : selves.entrySet()) {
            Point p = entry.getValue();
            FreeFormConnection ffcon = (FreeFormConnection) entry.getKey();
            EList<Point> pointList = ffcon.getBendpoints();
            AnchorContainer source = ffcon.getStart().getParent();
            GraphicsAlgorithm start = source.getGraphicsAlgorithm();
            int deltaX = start.getX() - p.getX();
            int deltaY = start.getY() - p.getY();
            for (int i = 0; i < pointList.size(); i++) {
                Point bendPoint = pointList.get(i);
                int x = bendPoint.getX();
                bendPoint.setX(x + deltaX);
                int y = bendPoint.getY();
                bendPoint.setY(y + deltaY);
            }
        }
    }

    /**
     * Reposition the Graphiti {@link PictogramElement}s and {@link Connection}s based on the
     * Zest {@link LayoutAlgorithm} computed locations
     *
     * @param entities
     * @param connections
     */
    private void updateGraphCoordinates(LayoutEntity[] entities, LayoutRelationship[] connections) {
        for (LayoutEntity entity : entities) {
            SimpleNode node = (SimpleNode) entity;
            Shape shape = (Shape) node.getRealObject();
            Double x = node.getX();
            Double y = node.getY();
            shape.getGraphicsAlgorithm().setX(x.intValue());
            shape.getGraphicsAlgorithm().setY(y.intValue());
            Double width = node.getWidth();
            Double height = node.getHeight();
            shape.getGraphicsAlgorithm().setWidth(width.intValue());
            shape.getGraphicsAlgorithm().setHeight(height.intValue());
        }

        IGaService gaService = Graphiti.getGaService();
        for (LayoutRelationship relationship : connections) {
            SimpleRelationship rel = (SimpleRelationship) relationship;
            // Using FreeFormConnections with BendPoints, we reset them to the Zest computed locations
            FreeFormConnection connection = (FreeFormConnection) rel.getGraphData();
            connection.getBendpoints().clear();
            LayoutBendPoint[] bendPoints = rel.getBendPoints();
            for (LayoutBendPoint bendPoint : bendPoints) {
                Double x = bendPoint.getX();
                Double y = bendPoint.getY();
                Point p = gaService.createPoint(x.intValue(), y.intValue());
                connection.getBendpoints().add(p);
            }
        }
    }

    /**
     * @return a {@link Map} of {@link SimpleNode} per {@link Shape}
     */
    private Map<Shape, SimpleNode> getLayoutEntities() {
        Map<Shape, SimpleNode> map = new HashMap<Shape, SimpleNode>();
        EList<Shape> children = getDiagram().getChildren();
        for (Shape shape : children) {
            GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
            SimpleNode entity = new SimpleNode(shape, ga.getX(), ga.getY(), ga.getWidth(), ga.getHeight());
            map.put(shape, entity);
        }
        return map;
    }

    /**
     * @param map
     *            a {@link Map} of {@link SimpleNode} per {@link Shape} - used to link {@link SimpleRelationship} to source and target entities
     * @return the array of {@link LayoutRelationship}s to compute
     */
    private LayoutRelationship[] getConnectionEntities(Map<Shape, SimpleNode> map) {
        List<LayoutRelationship> liste = new ArrayList<LayoutRelationship>();
        EList<Connection> connections = getDiagram().getConnections();
        for (Connection connection : connections) {

            String label = null;
            EList<ConnectionDecorator> decorators = connection.getConnectionDecorators();
            for (ConnectionDecorator decorator : decorators) {
                if (decorator.getGraphicsAlgorithm() instanceof Text) {
                    label = ((Text) decorator.getGraphicsAlgorithm()).getValue();
                }
            }

            // get the SimpleNode already created from the map:
            Shape source = (Shape) connection.getStart().getParent();
            SimpleNode sourceEntity = map.get(source);
            Shape target = (Shape) connection.getEnd().getParent();
            SimpleNode targetEntity = map.get(target);

            if (source != target) { // we don't add self relations to avoid Cycle errors
                SimpleRelationship relationship = new SimpleRelationship(sourceEntity, targetEntity, (source != target));
                relationship.setGraphData(connection);
                relationship.clearBendPoints();
                relationship.setLabel(label);
                FreeFormConnection ffcon = (FreeFormConnection) connection;

                EList<Point> pointList = ffcon.getBendpoints();
                List<LayoutBendPoint> bendPoints = new ArrayList<LayoutBendPoint>();
                for (int i = 0; i < pointList.size(); i++) {
                    Point point = pointList.get(i);
                    boolean isControlPoint = (i != 0) && (i != pointList.size() - 1);
                    LayoutBendPoint bendPoint = new BendPoint(point.getX(), point.getY(), isControlPoint);
                    bendPoints.add(bendPoint);
                }
                relationship.setBendPoints(bendPoints.toArray(new LayoutBendPoint[0]));
                liste.add(relationship);
                if (sourceEntity != null) sourceEntity.addRelationship(relationship);
                if (targetEntity != null) targetEntity.addRelationship(relationship);
            }
        }
        return liste.toArray(new LayoutRelationship[0]);
    }

    /**
     * Simple dialog to ask the user for a {@link LayoutAlgorithm}<br/>
     * Used to test various Zest algorithmns
     *
     * @return the chosen algorithmn
     */
    private Integer askForLayoutType() {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        /** ZestLayoutAlgorithmnChoiceDialog is a simple Dialog asking for an Integer for now */
        ZestLayoutAlgorithmnChoiceDialog dialog = new ZestLayoutAlgorithmnChoiceDialog(shell);
        return dialog.open();
    }

    /**
     * @param current
     * @return
     */
    private LayoutAlgorithm getLayoutAlgorithmn(int current) {
        LayoutAlgorithm layout;
        int style = LayoutStyles.NO_LAYOUT_NODE_RESIZING;
        switch (current) {
        case 1:
            layout = new SpringLayoutAlgorithm(style);
            System.out.println("SpringLayoutAlgorithmn");
            break;
        case 2:
            layout = new TreeLayoutAlgorithm(style);
            System.out.println("TreeLayoutAlgorithm");
            break;
        case 3:
            layout = new GridLayoutAlgorithm(style);
            System.out.println("GridLayoutAlgorithm");
            break;
        case 4:
            layout = new HorizontalLayoutAlgorithm(style);
            System.out.println("HorizontalLayoutAlgorithm");
            break;
        case 5:
            layout = new HorizontalTreeLayoutAlgorithm(style);
            System.out.println("HorizontalTreeLayoutAlgorithm");
            break;
        case 6:
            layout = new VerticalLayoutAlgorithm(style);
            System.out.println("VerticalLayoutAlgorithm");
            break;
        case 7:
            layout = new RadialLayoutAlgorithm(style);
            System.out.println("RadialLayoutAlgorithm");
            break;
        case 8:
            layout = new DirectedGraphLayoutAlgorithm(style);
            System.out.println("DirectedGraphLayoutAlgorithm");
            break;
        case 9:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[]{new DirectedGraphLayoutAlgorithm(style), new HorizontalShift(style)});
            System.out.println("CompositeLayoutAlgorithm [DirectedGraphLayoutAlgorithm+HorizontalShift]");
            break;
        case 10:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[]{new SpringLayoutAlgorithm(style), new HorizontalShift(style)});
            System.out.println("CompositeLayoutAlgorithm [SpringLayoutAlgorithm+HorizontalShift]");
            break;
        case 11:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[]{new RadialLayoutAlgorithm(style), new HorizontalShift(style)});
            System.out.println("CompositeLayoutAlgorithm [RadialLayoutAlgorithm+HorizontalShift]");
            break;
        case 12:
            layout = new HorizontalShift(style);
            System.out.println("HorizontalShift");
            break;
        default:
            layout = new CompositeLayoutAlgorithm(new LayoutAlgorithm[]{new TreeLayoutAlgorithm(style), new HorizontalShift(style)});
            System.out.println("CompositeLayoutAlgorithm [TreeLayoutAlgorithm+HorizontalShift]");
        }
        return layout;
    }

    /**
     * A {@link org.eclipse.zest.layouts.exampleStructures.SimpleRelationship} subclass
     * used to hold the Graphiti connection reference
     */
    private class SimpleRelationship extends org.eclipse.zest.layouts.exampleStructures.SimpleRelationship {

        private Object graphData;

        public SimpleRelationship(LayoutEntity sourceEntity, LayoutEntity destinationEntity, boolean bidirectional) {
            super(sourceEntity, destinationEntity, bidirectional);
        }

        @Override
        public Object getGraphData() {
            return graphData;
        }

        @Override
        public void setGraphData(Object o) {
            this.graphData = o;
        }
    }

}