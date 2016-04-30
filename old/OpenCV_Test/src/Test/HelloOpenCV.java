package Test;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
//import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoCapture;
//
// Detects faces in an image, draws boxes around them, and writes the results
// to "faceDetection.png".
//
class DetectFaceDemo {
  public void run() {
    System.out.println("\nRunning DetectFaceDemo");
    Videoio I;
    // Create a face detector from the cascade file in the resources
    // directory.
    //String filename0 = "C:\\Users\\Samuel\\Downloads\\opencv\\sources\\modules\\java\\common_test\\res\\raw\\lbpcascade_frontalface.xml";
    //String filename0 = "C:\Users\Samuel\Downloads\opencv\sources\samples\winrt\FaceDetection\FaceDetection\Assets\"
    //String filename0 = "C:\\Users\\Samuel\\Downloads\\opencv\\sources\\samples\\winrt\\Face\\Detection\\FaceDetection\\Assets\\haarcascade_frontalface_alt.xml";
    String filename0 = "C:\\Users\\Samuel\\Downloads\\opencv\\sources\\samples\\winrt\\Face\\Detection\\FaceDetection\\Assets\\haarcascade_frontalface_alt.xml";
    System.out.println("here1:");
    //System.out.println("here:"+gp);
    CascadeClassifier faceDetector = new CascadeClassifier(getClass().getResource(filename0).getPath());
    //CascadeClassifier faceDetector = new CascadeClassifier(FaceDetection.class.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1));
    if ( faceDetector.empty() ) { System.out.println("oww!"); }
    String filename = "C:\\Users\\Samuel\\Downloads\\opencv\\sources\\samples\\wp8\\OpenCVXaml\\OpenCVXaml\\Assets\\lena.png";
    Mat image = Imgcodecs.imread(filename);

    // Detect faces in the image.
    // MatOfRect is a special container class for Rect.
    MatOfRect faceDetections = new MatOfRect();
    faceDetector.detectMultiScale(image, faceDetections);

    System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

    // Draw a bounding box around each face.
    for (Rect rect : faceDetections.toArray()) {
        Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
    }

    // Save the visualized detection.
    String filename2 = "faceDetection.png";
    System.out.println(String.format("Writing %s", filename2));
    Imgcodecs.imwrite(filename2, image);
  }
}

public class HelloOpenCV {
  public static void main(String[] args) {
    System.out.println("Hello, OpenCV");

    // Load the native library.
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    new DetectFaceDemo().run();
  }
}