package Test;

import org.opencv.core.*;
/*import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;*/
//import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoCapture;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.image.DataBufferByte;

import java.io.File;
import javax.imageio.ImageIO;
import java.lang.Thread;

import java.io.*;

class Test2 {
	public static void main(String[] args) {	
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); //IMPORTANT! GOES FIRST
		
		
		VideoCapture camera = new VideoCapture();
		try {
		    Thread.sleep(1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    camera.open(0); //Useless
	    if(!camera.isOpened()){
	        System.out.println("Camera Error");
	    }
	    else{
	        System.out.println("Camera OK?");
	    }
		
		//String fn_lena = "C:\\Users\\Samuel\\Downloads\\opencv\\sources\\samples\\wp8\\OpenCVXaml\\OpenCVXaml\\Assets\\Lena.png";
		//Mat image = Imgcodecs.imread(fn_lena);
	    Mat image = new Mat();
		camera.read(image);
		camera.release();
		
		//String filename0 = "C:\\Users\\Samuel\\Downloads\\opencv\\sources\\samples\\winrt\\Face\\Detection\\FaceDetection\\Assets\\lbpcascade_frontalface.xml";
		//String filename0 = "lbpcascade_frontalface.xml";
		String filename0 = "haarcascade_frontalface_alt.xml";
	    //CascadeClassifier faceDetector = new CascadeClassifier(filename0.getClass().getResource(filename0).getPath()); //getClass().getResource(filename0).getPath());
		CascadeClassifier faceDetector = new CascadeClassifier(filename0); //getClass().getResource(filename0).getPath());
	    if ( faceDetector.empty() ) { System.out.println("oww!"); }

	    // Detect faces in the image.
	    // MatOfRect is a special container class for Rect.
	    MatOfRect faceDetections = new MatOfRect();
	    faceDetector.detectMultiScale(image, faceDetections);

	    System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

	    // Draw a bounding box around each face.
	    for (Rect rect : faceDetections.toArray()) {
	        Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	    }
	    
	    showResult(image);
	}
	public static void showResult(Mat img) {
	    //Imgproc.resize(img, img, new Size(256, 256));
	    MatOfByte matOfByte = new MatOfByte();
	    Imgcodecs.imencode(".jpg", img, matOfByte);
	    byte[] byteArray = matOfByte.toArray();
	    BufferedImage bufImage = null;
	    try {
	        InputStream in = new ByteArrayInputStream(byteArray);
	        bufImage = ImageIO.read(in);
	        JFrame frame = new JFrame();
	        frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
	        frame.pack();
	        frame.setVisible(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
