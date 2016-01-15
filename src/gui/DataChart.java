package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

/*
 * Code Written by Daniel Chandross
 * danchan@umich.edu
 * 11.5.2015
 * M - Fly [SAS]
 */
public class DataChart extends JPanel 
{
	private JFreeChart chart;
	private ChartPanel chartPanel;
	private XYPlot plot;
	private XYLineAndShapeRenderer renderer;
	private XYSeries dataPoints;
	private XYSeries dropPoints;
	private XYSeriesCollection dataSet;
	/**
	 * Constructs a DataChart object, initially containing no points
	 * @param title - the String to be placed above the chart
	 * @param xSize - the x Dimension of the requested size 
	 * @param ySize - the y Dimension of the requested size
	 */
	
   public DataChart(String title, int xSize, int ySize)
   {
      dataPoints = new XYSeries( "Altitude" );   
      dropPoints = new XYSeries( "Dropped" );   
      dataSet = new XYSeriesCollection( );
      dataSet.addSeries(dropPoints);
      dataSet.addSeries(dataPoints);
      chart = ChartFactory.createXYLineChart(
         title, "Time (s)", "Altitude (m)", dataSet,
         PlotOrientation.VERTICAL ,true , true , false);
      chartPanel = new ChartPanel(chart);
 
      this.add(chartPanel);
      chartPanel.setPreferredSize(new Dimension(xSize, ySize));
      plot = chart.getXYPlot();
      renderer = new XYLineAndShapeRenderer( );
      renderer.setSeriesPaint(1, Color.BLUE );
      renderer.setSeriesStroke(0, new BasicStroke(4.0f));
      renderer.setSeriesPaint(0, Color.YELLOW );
      renderer.setSeriesStroke(1, new BasicStroke(4.0f));
      plot.setRenderer( renderer ); 
      renderer.setSeriesLinesVisible(0, false);
      Shape cross = ShapeUtilities.createDiamond(7);
      renderer.setSeriesShape(0, cross);
     ((XYPlot) chart.getPlot()).getDomainAxis().setUpperBound(30);

   }
   /**
    * Updates the chart with the given input
    * @param inputPoint - The Point to be added to the graph (x,y)
    */
   public void update (Point2D.Double inputPoint, boolean justDropped) {
	   if (inputPoint.getX() > 30) {
		   int scaleAmount = (int) (inputPoint.getX() - 30);
		   ((XYPlot) chart.getPlot()).getDomainAxis().setUpperBound(30 + scaleAmount);
		   ((XYPlot) chart.getPlot()).getDomainAxis().setLowerBound(scaleAmount);
	   }
	   
	   dataPoints.add(inputPoint.getX(), inputPoint.getY());
	   if (justDropped) {
		    //  renderer.setSeriesPaint(0, Color.BLUE );
		      dropPoints.add(inputPoint.getX(), inputPoint.getY());
		      //renderer.setSeriesPaint(0, Color.RED );
	   } else {dataPoints.add(inputPoint.getX(), inputPoint.getY());}
   }
   
   public void update (Point2D.Double inputPoint) {
	   update(inputPoint, false);
   }
  /*
   * Example of how to use the object
   */
   public static void main( String[ ] args ) 
   {
	  Random r = new Random();  //rng
      DataChart chart = new DataChart ("Altitude", 3000, 200); //constructor
      JFrame frame = new JFrame(); // frame, you would replace this with the JPanel
      frame.add(chart);   // add the created opject to your Panel/Frame
      frame.setVisible(true); //set the master frame visible
      frame.setSize(3000, 2000); //set your size, should be slightly larger than construcor size
     
      int oldY = 0; //used to make data nicer
      for (int i = 0; i < 60; i++) { //loop through 60 times
    	  int y = 0; 
    	  if (i < 30) y = r.nextInt(4) + oldY - 1; 
    	  //if it's the first half, set the rng to roughly make the points larger on average
    	 
    	  if (i >= 30) y = r.nextInt(4) + oldY - 2;
    	  //if it's the second half, set the rng to roughly make the points smaller on average
    	  if (i % 10 == 0) chart.update(new Point2D.Double(i,y), true);
    	  else chart.update(new Point2D.Double(i,y)); //adds the point to the chart
    	  try {Thread.sleep(100);} catch (InterruptedException e) {} //sleep to simulate time
    	  oldY = y; //prepare for next loop
      }   
   }
}
