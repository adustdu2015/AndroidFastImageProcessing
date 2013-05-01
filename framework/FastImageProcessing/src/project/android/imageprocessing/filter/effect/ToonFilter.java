package project.android.imageprocessing.filter.effect;

import project.android.imageprocessing.filter.MultiPixelRenderer;
import android.opengl.GLES20;

/**
 * This uses Sobel edge detection to place a black border around objects, and then it quantizes the colors present in the image to give a cartoon-like quality to the image.
 * threshold: The sensitivity of the edge detection, with lower values being more sensitive. Ranges from 0.0 to 1.0
 * quantizationLevels: The number of color levels to represent in the final image.
 * @author Chris Batt
 */
public class ToonFilter extends MultiPixelRenderer {
	private static final String UNIFORM_THRESHOLD = "u_Threshold";
	private static final String UNIFORM_QUANTIZATION = "u_Quantization";
	
	private int thresholdHandle;
	private int quantizationLevelsHandle;
	private float threshold;
	private float quantizationLevels;
	
	public ToonFilter(float threshold, float quantizationLevels) {
		this.threshold = threshold;
		this.quantizationLevels = quantizationLevels;
	}
		
	@Override
	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"  
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				+"uniform float "+UNIFORM_THRESHOLD+";\n"
				+"uniform float "+UNIFORM_QUANTIZATION+";\n"
				+"uniform float "+UNIFORM_TEXELWIDTH+";\n"
				+"uniform float "+UNIFORM_TEXELHEIGHT+";\n"
						
				
		  		+"void main(){\n"
		  		+"   vec4 color = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+");\n"
		  		+"   vec2 up = vec2(0.0, "+UNIFORM_TEXELHEIGHT+");\n"
		  		+"   vec2 right = vec2("+UNIFORM_TEXELWIDTH+", 0.0);\n"
		  		+"   float bottomLeftIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - up - right).r;\n"
		  		+"   float topRightIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + up + right).r;\n"
			    +"   float topLeftIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + up - right).r;\n"
			    +"   float bottomRightIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - up + right).r;\n"
			    +"   float leftIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - right).r;\n"
			    +"   float rightIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + right).r;\n"
			    +"   float bottomIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" - up).r;\n"
			    +"   float topIntensity = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+" + up).r;\n"
			    +"   float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n"
			    +"   float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n"
			    +"   float mag = length(vec2(h, v));\n"
			    +"   vec3 posterizedImageColor = floor((color.rgb * "+UNIFORM_QUANTIZATION+") + 0.5) / "+UNIFORM_QUANTIZATION+";\n"
			    +"   float thresholdTest = 1.0 - step("+UNIFORM_THRESHOLD+", mag);\n"
			    +"   gl_FragColor = vec4(posterizedImageColor * thresholdTest, color.a);\n"
		  		+"}\n";
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		thresholdHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_THRESHOLD);
		quantizationLevelsHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_QUANTIZATION);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1f(thresholdHandle, threshold);
		GLES20.glUniform1f(quantizationLevelsHandle, quantizationLevels);
	}
}