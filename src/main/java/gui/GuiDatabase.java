package gui;

import database.*;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.picking.PickedState;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GuiDatabase {

	private JFrame frame;
	private JPanel graphPanel;
	private JPanel tablePanel;
	private JPanel preprocButtonsPanel;
	private JPanel controlButtonsPanel;
	private Collection<GraphVertex> graphCenters = new ArrayList<>();
    private AttRelationshipGraph graph;

	private VisualizationViewer<GraphVertex, GraphEdge> vv;

	/**
	 * Create the application.
	 */
	public GuiDatabase() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(10, 10, 1000, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();
		frame.setLayout(layout);

		preprocButtonsPanel = new JPanel();
		JScrollPane scroller = new JScrollPane(preprocButtonsPanel);
		frame.getContentPane().add(scroller, BorderLayout.EAST);
		preprocButtonsPanel.setLayout(new GridLayout(2, 1, 0, 0));
		scroller.setPreferredSize(new Dimension(400, 600));

		graphPanel = new JPanel();
		frame.getContentPane().add(graphPanel, BorderLayout.CENTER);
		graphPanel.setPreferredSize(new Dimension(800, 300));

		/*
		 * tablePanel = new JPanel(); frame.getContentPane().add(tablePanel,
		 * BorderLayout.SOUTH); tablePanel.setPreferredSize( new Dimension( 800,
		 * 300 ) );
		 * 
		 * 
		 * TableModel dataModel = new DefaultTableModel() { public int
		 * getColumnCount() { return 10; } public int getRowCount() { return
		 * 10;} public Object getValueAt(int row, int col) { return new
		 * Integer(row*col); } }; JTable table = new JTable(dataModel);
		 * JScrollPane scrollpane = new JScrollPane(table);
		 * tablePanel.add(scrollpane);
		 */

		controlButtonsPanel = new JPanel();
		frame.getContentPane().add(controlButtonsPanel, BorderLayout.SOUTH);
		controlButtonsPanel.setLayout(new GridLayout(0, 3, 0, 0));

		displayButtons();

		frame.setVisible(true);

        graph = new AttRelationshipGraph();
	}

	private final class VertexShaper implements Transformer<GraphVertex, Shape> {

		public Shape transform(GraphVertex vertex) {
			if (vertex.getClass().equals(Processor.class)) {
				return new Ellipse2D.Float(-7, -7, 15, 15);
			} else {
				return new Rectangle(-7, -7, 15, 15);
			}
		}

	}

	private final class VertexFillColor implements
			Transformer<GraphVertex, Paint> {
		protected PickedState<GraphVertex> pick;

		public VertexFillColor(PickedState<GraphVertex> pick) {
			this.pick = pick;
		}

		public Paint transform(GraphVertex v) {
			if (pick.isPicked(v)) {
				return new Color(1f, 1f, 0, 1);
			} else {

				if (v.getClass().equals(Att.class)) {
					Att vAtt = (Att) v;

					// Colour the root node white.
					if (vAtt.isRootAtt()) {
						return new Color(1f, 1f, 1f, 1f);
					} else if (vAtt.getBestPredictor() != null
							&& vAtt.getBestPredictor().getAccuracy() > 0.9) {
						return new Color(0, 1f, 0, 1f);
					}

				} else if (v.getClass().equals(Processor.class)) {
					Processor vProc = (Processor) v;

					// Color processors that haven't yet been run white.
					if (!vProc.finished()) {
						return new Color(1f, 1f, 1f, 1f);
					}
					if (vProc.getSuccessfulTransformers() == 0) {
						return new Color(1f, 0, 0, 1f);
					}

				}

				/*
				 * if( relationshipFinder != null) { if(
				 * relationshipFinder.canBePredicted( v.getAtt() ) ) return
				 * Color.green; else { if(
				 * relationshipFinder.canBePredictedOrGeneratedFromParents
				 * (v.getAtt())) return new Color(0, 0.5f, 0, 1); }
				 * 
				 * 
				 * HashMap<String, NumericalRelationshipNoPreprocessing>
				 * relationships = relationshipFinder.getRelationshipsHashMap();
				 * if( relationships != null) {
				 * NumericalRelationshipNoPreprocessing rel = relationships.get(
				 * v.toString() ); if( rel != null ) { if( rel.avgErr < 0.005 )
				 * { return Color.CYAN; //Should never hit this. Should be
				 * coloured green } else { return Color.RED; } } } }
				 */
				return Color.BLACK;
			}
		}
	}

	private final class VertexOutlineDrawer implements
			Transformer<GraphVertex, Paint> {
		public VertexOutlineDrawer() {
		}

		public Paint transform(GraphVertex v) {
			if (graphCenters.contains(v))
				return Color.RED;
			else
				return Color.BLACK;
		}
	}

	private final class EdgeColorer implements Transformer<GraphEdge, Paint> {
		protected PickedInfo<GraphEdge> pick;

		public EdgeColorer(PickedInfo<GraphEdge> pick) {
			this.pick = pick;
		}

		public Paint transform(GraphEdge e) {
			if (pick.isPicked(e)) {
				return new Color(1f, 1f, 0, 1);
			} else {
				// if( e instanceof AttributeRelationshipEdge )
				// return Color.GREEN;
				// else
				return Color.DARK_GRAY;
			}
		}
	}

	public void displayGraph() {
		graphPanel.removeAll();

		AttRelationshipGraph dependencyGraph = graph
				.getSubGraph(graphCenters);

		Layout<GraphVertex, GraphEdge> layout = new ISOMLayout<>(
				dependencyGraph);
		// Layout<GraphVertex, GraphEdge> layout = new
		// CircleLayout<>(dependencyGraph);
		// Layout<GraphVertex, GraphEdge> layout = new
		// TreeLayout<>(dependencyGraph);
		// Layout<GraphVertex, GraphEdge> layout = new
		// KKLayout<>(dependencyGraph);
		// Layout<GraphVertex, GraphEdge> layout = new
		// FRLayout<>(dependencyGraph);
		// Layout<GraphVertex, GraphEdge> layout = new
		// DAGLayout<>(dependencyGraph);
		layout.setSize(new Dimension(800, 600)); // sets the initial size of the
													// space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		vv = new VisualizationViewer<GraphVertex, GraphEdge>(layout);
		vv.setPreferredSize(new Dimension(800, 600)); // Sets the viewing area
														// size
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(DefaultModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(gm);

		// Set color and shape of vertices.
		PickedState<GraphVertex> picked_state = vv.getPickedVertexState();
		VertexFillColor vertexColor = new VertexFillColor(picked_state);
		VertexShaper vertexShaper = new VertexShaper();
		VertexOutlineDrawer vertexOutlineDrawer = new VertexOutlineDrawer();
		vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
		vv.getRenderContext().setVertexShapeTransformer(vertexShaper);
		vv.getRenderContext()
				.setVertexDrawPaintTransformer(vertexOutlineDrawer);

		// Set color of edges.
		PickedState<GraphEdge> picked_edge_state = vv.getPickedEdgeState();
		EdgeColorer edgeColor = new EdgeColorer(picked_edge_state);
		vv.getRenderContext().setEdgeDrawPaintTransformer(edgeColor);

		graphPanel.add(vv);
		frame.pack();
	}

	public void addCenter(GraphVertex subgraphForVertex) {

		graphCenters.add(subgraphForVertex);
	}

	public void removeCenter(GraphVertex subgraphForVertex) {

		graphCenters.remove(subgraphForVertex);
	}

	public void displayButtons() {
		preprocButtonsPanel.removeAll();
		controlButtonsPanel.removeAll();

		final GuiDatabase gui = this;

		JButton centerGraphButton = new JButton("Add to graph Centers");
		centerGraphButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				PickedState<GraphVertex> picked_state = vv
						.getPickedVertexState();
				Set<GraphVertex> picked = picked_state.getPicked();

				for (GraphVertex pick : picked) {
					addCenter(pick);
				}

				displayGraph();
			}
		});
		controlButtonsPanel.add(centerGraphButton);

		JButton removeFromCentersButton = new JButton(
				"Remove from graph Centers");
		removeFromCentersButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				PickedState<GraphVertex> picked_state = vv
						.getPickedVertexState();
				Set<GraphVertex> picked = picked_state.getPicked();

				for (GraphVertex pick : picked) {
					removeCenter(pick);
				}

				displayGraph();
			}
		});
		controlButtonsPanel.add(removeFromCentersButton);

		JButton showPreprocessorsButton = new JButton(
				"Show possible Processors based on displayed graph");
		showPreprocessorsButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ProcessorSelector processorSelector = new BruteForceProcessorSelector();
				// ProcessorSelector processorSelector = new
				// SingleColumnInProcessorSelector();
				Collection<Processor> preprocessors = processorSelector
						.getBestProcessors(graph
								.getSubGraph(graphCenters));

				System.out.println(preprocessors);

				displayGraph();
			}
		});
		controlButtonsPanel.add(showPreprocessorsButton);

		JButton applyProcessorsButton = new JButton("Apply Selected Processors");
		applyProcessorsButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				PickedState<GraphVertex> picked_state = vv
						.getPickedVertexState();
				Set<GraphVertex> picked = picked_state.getPicked();

				for (GraphVertex pick : picked) {
					if (pick.getClass().equals(Processor.class)) {
						Processor pickProc = (Processor) pick;

						if (!pickProc.finished()) {
							pickProc.doWork();
						}
					}
				}

				displayGraph();
			}
		});
		controlButtonsPanel.add(applyProcessorsButton);

		JButton findRelationshipsButton = new JButton("Find All Relationships");
		findRelationshipsButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				List<Relationship> possibleRelationships = RelationshipSelector
						.getBestRelationships(graph
								.getSubGraph(graphCenters));

				for (Relationship rel : possibleRelationships) {
					String debugMsg = "Estimating performance for "
							+ rel.getName() + ", predicting "
							+ rel.getLabel().getName() + " from ";
					for (Att inputAtt : rel.getInputAtts()) {
						debugMsg += inputAtt.getName() + ", ";
					}
					System.out.println(debugMsg);

					try {
						rel.estimatePerformanceUsingXValidation();
						if (rel.getAccuracy() > 0.9) {
							rel.learn();
							System.out.println(rel);
							System.out.println();
						}
					} catch (SQLException | InsufficientRowsException | IncorrectInputRowsException E) {
                        E.printStackTrace();
                    }

                }

				gui.redisplay();
			}
		});
		controlButtonsPanel.add(findRelationshipsButton);

	}

	public void redisplay() {
		vv.repaint();
		frame.pack();
	}

}
