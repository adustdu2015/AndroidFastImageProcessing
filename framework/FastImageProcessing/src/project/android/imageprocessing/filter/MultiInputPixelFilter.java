package project.android.imageprocessing.filter;

import android.opengl.GLES20;

/**
 * An extension of MultiInputFilter. This class allows for multi-pixel access on multiple inputs.  For 
 * more details on setup of this class and usage see {@link MultiInputFilter} and {@link MultiPixelRenderer}.
 * @author Chris Batt
 */
public class MultiInputPixelFilter extends MultiInputFilter {
	protected static final String UNIFORM_TEXELWIDTH = "u_TexelWidth";
	protected static final String UNIFORM_TEXELHEIGHT = "u_TexelHeight";
	
	protected float texelWidth;
	protected float texelHeight;
	private int texelWidthHandle;
	private int texelHeightHandle;
	
	/**
	 * Creates a MultiInputPixelFilter that passes the texel width and height information to the shaders and
	 * accepts a given textures as input.  
	 * @param numOfInputs
	 * The number of input textures used by the fragment shader.
	 */
	public MultiInputPixelFilter(int numOfInputs) {
		super(numOfInputs);
	}
	
	@Override
	protected void handleSizeChange() {
		super.handleSizeChange();
		texelWidth = 1.0f / (float)getWidth();
		texelHeight = 1.0f / (float)getHeight();
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		texelWidthHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXELWIDTH);
		texelHeightHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXELHEIGHT);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1f(texelWidthHandle, texelWidth);
		GLES20.glUniform1f(texelHeightHandle, texelHeight);
	}

}