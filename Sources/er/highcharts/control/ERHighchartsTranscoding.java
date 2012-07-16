package er.highcharts.control;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.fop.svg.PDFTranscoder;

import com.webobjects.foundation.NSData;

/**
 * SVG Transcoding utility methods using Batik frameworks.
 * 
 * @author matt
 *
 */
public class ERHighchartsTranscoding {


	/**
	 * Transcode the given SVG document string as a PDF object
	 * @param svgString
	 * @param width
	 * @return
	 */
	public static NSData pdfTranscode(String svgString, Float width) {
		PDFTranscoder t = new PDFTranscoder();
		t.addTranscodingHint(PDFTranscoder.KEY_WIDTH, width);
		return transcode(svgString, t);
	}

	/**
	 * Transcode the given SVG document string as a PNG object
	 * @param svgString
	 * @param width
	 * @return
	 */
	public static NSData pngTranscode(String svgString, Float width){
		PNGTranscoder t = new PNGTranscoder();
		t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
		return transcode(svgString, t);
	}
	
	/**
	 * Transcode the given SVG document string as a JPEG object
	 * @param svgString
	 * @param width
	 * @return
	 */
	public static NSData jpegTranscode(String svgString, Float width){
		JPEGTranscoder t = new JPEGTranscoder();
		t.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, width);
		t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(0.8));
		return transcode(svgString, t);
	}
	
	/**
	 * Given an abstract transcoder, build the NSData response object from the
	 * given SVG document string.
	 * @param svgString
	 * @param t the primed transcoder
	 * @return
	 */
	public static NSData transcode(String svgString, SVGAbstractTranscoder t){
		TranscoderInput input = new TranscoderInput(new StringReader(svgString));

		NSData im = null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			TranscoderOutput output = new TranscoderOutput();
			output.setOutputStream(baos);
			
			t.transcode(input, output);
			
			byte[] ba = baos.toByteArray();
			im = new NSData(ba);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return im;
	}
}
