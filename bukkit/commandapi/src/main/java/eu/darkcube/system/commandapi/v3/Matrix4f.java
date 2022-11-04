package eu.darkcube.system.commandapi.v3;

import java.nio.FloatBuffer;

public final class Matrix4f {
	protected float m00;
	protected float m01;
	protected float m02;
	protected float m03;
	protected float m10;
	protected float m11;
	protected float m12;
	protected float m13;
	protected float m20;
	protected float m21;
	protected float m22;
	protected float m23;
	protected float m30;
	protected float m31;
	protected float m32;
	protected float m33;

	public Matrix4f() {
	}

	public Matrix4f(Matrix4f matrixIn) {
		this.m00 = matrixIn.m00;
		this.m01 = matrixIn.m01;
		this.m02 = matrixIn.m02;
		this.m03 = matrixIn.m03;
		this.m10 = matrixIn.m10;
		this.m11 = matrixIn.m11;
		this.m12 = matrixIn.m12;
		this.m13 = matrixIn.m13;
		this.m20 = matrixIn.m20;
		this.m21 = matrixIn.m21;
		this.m22 = matrixIn.m22;
		this.m23 = matrixIn.m23;
		this.m30 = matrixIn.m30;
		this.m31 = matrixIn.m31;
		this.m32 = matrixIn.m32;
		this.m33 = matrixIn.m33;
	}

	public Matrix4f(Quaternion quaternionIn) {
		float f = quaternionIn.getX();
		float f1 = quaternionIn.getY();
		float f2 = quaternionIn.getZ();
		float f3 = quaternionIn.getW();
		float f4 = 2.0F * f * f;
		float f5 = 2.0F * f1 * f1;
		float f6 = 2.0F * f2 * f2;
		this.m00 = 1.0F - f5 - f6;
		this.m11 = 1.0F - f6 - f4;
		this.m22 = 1.0F - f4 - f5;
		this.m33 = 1.0F;
		float f7 = f * f1;
		float f8 = f1 * f2;
		float f9 = f2 * f;
		float f10 = f * f3;
		float f11 = f1 * f3;
		float f12 = f2 * f3;
		this.m10 = 2.0F * (f7 + f12);
		this.m01 = 2.0F * (f7 - f12);
		this.m20 = 2.0F * (f9 - f11);
		this.m02 = 2.0F * (f9 + f11);
		this.m21 = 2.0F * (f8 + f10);
		this.m12 = 2.0F * (f8 - f10);
	}

	@Override
	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
			Matrix4f matrix4f = (Matrix4f) p_equals_1_;
			return Float.compare(matrix4f.m00, this.m00) == 0 && Float.compare(matrix4f.m01, this.m01) == 0
					&& Float.compare(matrix4f.m02, this.m02) == 0 && Float.compare(matrix4f.m03, this.m03) == 0
					&& Float.compare(matrix4f.m10, this.m10) == 0 && Float.compare(matrix4f.m11, this.m11) == 0
					&& Float.compare(matrix4f.m12, this.m12) == 0 && Float.compare(matrix4f.m13, this.m13) == 0
					&& Float.compare(matrix4f.m20, this.m20) == 0 && Float.compare(matrix4f.m21, this.m21) == 0
					&& Float.compare(matrix4f.m22, this.m22) == 0 && Float.compare(matrix4f.m23, this.m23) == 0
					&& Float.compare(matrix4f.m30, this.m30) == 0 && Float.compare(matrix4f.m31, this.m31) == 0
					&& Float.compare(matrix4f.m32, this.m32) == 0 && Float.compare(matrix4f.m33, this.m33) == 0;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int i = this.m00 != 0.0F ? Float.floatToIntBits(this.m00) : 0;
		i = 31 * i + (this.m01 != 0.0F ? Float.floatToIntBits(this.m01) : 0);
		i = 31 * i + (this.m02 != 0.0F ? Float.floatToIntBits(this.m02) : 0);
		i = 31 * i + (this.m03 != 0.0F ? Float.floatToIntBits(this.m03) : 0);
		i = 31 * i + (this.m10 != 0.0F ? Float.floatToIntBits(this.m10) : 0);
		i = 31 * i + (this.m11 != 0.0F ? Float.floatToIntBits(this.m11) : 0);
		i = 31 * i + (this.m12 != 0.0F ? Float.floatToIntBits(this.m12) : 0);
		i = 31 * i + (this.m13 != 0.0F ? Float.floatToIntBits(this.m13) : 0);
		i = 31 * i + (this.m20 != 0.0F ? Float.floatToIntBits(this.m20) : 0);
		i = 31 * i + (this.m21 != 0.0F ? Float.floatToIntBits(this.m21) : 0);
		i = 31 * i + (this.m22 != 0.0F ? Float.floatToIntBits(this.m22) : 0);
		i = 31 * i + (this.m23 != 0.0F ? Float.floatToIntBits(this.m23) : 0);
		i = 31 * i + (this.m30 != 0.0F ? Float.floatToIntBits(this.m30) : 0);
		i = 31 * i + (this.m31 != 0.0F ? Float.floatToIntBits(this.m31) : 0);
		i = 31 * i + (this.m32 != 0.0F ? Float.floatToIntBits(this.m32) : 0);
		return 31 * i + (this.m33 != 0.0F ? Float.floatToIntBits(this.m33) : 0);
	}

	private static int bufferIndex(int p_226594_0_, int p_226594_1_) {
		return p_226594_1_ * 4 + p_226594_0_;
	}

	@Override
	public String toString() {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("Matrix4f:\n");
		stringbuilder.append(this.m00);
		stringbuilder.append(" ");
		stringbuilder.append(this.m01);
		stringbuilder.append(" ");
		stringbuilder.append(this.m02);
		stringbuilder.append(" ");
		stringbuilder.append(this.m03);
		stringbuilder.append("\n");
		stringbuilder.append(this.m10);
		stringbuilder.append(" ");
		stringbuilder.append(this.m11);
		stringbuilder.append(" ");
		stringbuilder.append(this.m12);
		stringbuilder.append(" ");
		stringbuilder.append(this.m13);
		stringbuilder.append("\n");
		stringbuilder.append(this.m20);
		stringbuilder.append(" ");
		stringbuilder.append(this.m21);
		stringbuilder.append(" ");
		stringbuilder.append(this.m22);
		stringbuilder.append(" ");
		stringbuilder.append(this.m23);
		stringbuilder.append("\n");
		stringbuilder.append(this.m30);
		stringbuilder.append(" ");
		stringbuilder.append(this.m31);
		stringbuilder.append(" ");
		stringbuilder.append(this.m32);
		stringbuilder.append(" ");
		stringbuilder.append(this.m33);
		stringbuilder.append("\n");
		return stringbuilder.toString();
	}

	public void write(FloatBuffer floatBufferIn) {
		floatBufferIn.put(bufferIndex(0, 0), this.m00);
		floatBufferIn.put(bufferIndex(0, 1), this.m01);
		floatBufferIn.put(bufferIndex(0, 2), this.m02);
		floatBufferIn.put(bufferIndex(0, 3), this.m03);
		floatBufferIn.put(bufferIndex(1, 0), this.m10);
		floatBufferIn.put(bufferIndex(1, 1), this.m11);
		floatBufferIn.put(bufferIndex(1, 2), this.m12);
		floatBufferIn.put(bufferIndex(1, 3), this.m13);
		floatBufferIn.put(bufferIndex(2, 0), this.m20);
		floatBufferIn.put(bufferIndex(2, 1), this.m21);
		floatBufferIn.put(bufferIndex(2, 2), this.m22);
		floatBufferIn.put(bufferIndex(2, 3), this.m23);
		floatBufferIn.put(bufferIndex(3, 0), this.m30);
		floatBufferIn.put(bufferIndex(3, 1), this.m31);
		floatBufferIn.put(bufferIndex(3, 2), this.m32);
		floatBufferIn.put(bufferIndex(3, 3), this.m33);
	}

	public void setIdentity() {
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
	}

	public float adjugateAndDet() {
		float f = this.m00 * this.m11 - this.m01 * this.m10;
		float f1 = this.m00 * this.m12 - this.m02 * this.m10;
		float f2 = this.m00 * this.m13 - this.m03 * this.m10;
		float f3 = this.m01 * this.m12 - this.m02 * this.m11;
		float f4 = this.m01 * this.m13 - this.m03 * this.m11;
		float f5 = this.m02 * this.m13 - this.m03 * this.m12;
		float f6 = this.m20 * this.m31 - this.m21 * this.m30;
		float f7 = this.m20 * this.m32 - this.m22 * this.m30;
		float f8 = this.m20 * this.m33 - this.m23 * this.m30;
		float f9 = this.m21 * this.m32 - this.m22 * this.m31;
		float f10 = this.m21 * this.m33 - this.m23 * this.m31;
		float f11 = this.m22 * this.m33 - this.m23 * this.m32;
		float f12 = this.m11 * f11 - this.m12 * f10 + this.m13 * f9;
		float f13 = -this.m10 * f11 + this.m12 * f8 - this.m13 * f7;
		float f14 = this.m10 * f10 - this.m11 * f8 + this.m13 * f6;
		float f15 = -this.m10 * f9 + this.m11 * f7 - this.m12 * f6;
		float f16 = -this.m01 * f11 + this.m02 * f10 - this.m03 * f9;
		float f17 = this.m00 * f11 - this.m02 * f8 + this.m03 * f7;
		float f18 = -this.m00 * f10 + this.m01 * f8 - this.m03 * f6;
		float f19 = this.m00 * f9 - this.m01 * f7 + this.m02 * f6;
		float f20 = this.m31 * f5 - this.m32 * f4 + this.m33 * f3;
		float f21 = -this.m30 * f5 + this.m32 * f2 - this.m33 * f1;
		float f22 = this.m30 * f4 - this.m31 * f2 + this.m33 * f;
		float f23 = -this.m30 * f3 + this.m31 * f1 - this.m32 * f;
		float f24 = -this.m21 * f5 + this.m22 * f4 - this.m23 * f3;
		float f25 = this.m20 * f5 - this.m22 * f2 + this.m23 * f1;
		float f26 = -this.m20 * f4 + this.m21 * f2 - this.m23 * f;
		float f27 = this.m20 * f3 - this.m21 * f1 + this.m22 * f;
		this.m00 = f12;
		this.m10 = f13;
		this.m20 = f14;
		this.m30 = f15;
		this.m01 = f16;
		this.m11 = f17;
		this.m21 = f18;
		this.m31 = f19;
		this.m02 = f20;
		this.m12 = f21;
		this.m22 = f22;
		this.m32 = f23;
		this.m03 = f24;
		this.m13 = f25;
		this.m23 = f26;
		this.m33 = f27;
		return f * f11 - f1 * f10 + f2 * f9 + f3 * f8 - f4 * f7 + f5 * f6;
	}

	public void transpose() {
		float f = this.m10;
		this.m10 = this.m01;
		this.m01 = f;
		f = this.m20;
		this.m20 = this.m02;
		this.m02 = f;
		f = this.m21;
		this.m21 = this.m12;
		this.m12 = f;
		f = this.m30;
		this.m30 = this.m03;
		this.m03 = f;
		f = this.m31;
		this.m31 = this.m13;
		this.m13 = f;
		f = this.m32;
		this.m32 = this.m23;
		this.m23 = f;
	}

	public boolean invert() {
		float f = this.adjugateAndDet();
		if (Math.abs(f) > 1.0E-6F) {
			this.mul(f);
			return true;
		} else {
			return false;
		}
	}

	public void mul(Matrix4f matrix) {
		float f = this.m00 * matrix.m00 + this.m01 * matrix.m10 + this.m02 * matrix.m20 + this.m03 * matrix.m30;
		float f1 = this.m00 * matrix.m01 + this.m01 * matrix.m11 + this.m02 * matrix.m21 + this.m03 * matrix.m31;
		float f2 = this.m00 * matrix.m02 + this.m01 * matrix.m12 + this.m02 * matrix.m22 + this.m03 * matrix.m32;
		float f3 = this.m00 * matrix.m03 + this.m01 * matrix.m13 + this.m02 * matrix.m23 + this.m03 * matrix.m33;
		float f4 = this.m10 * matrix.m00 + this.m11 * matrix.m10 + this.m12 * matrix.m20 + this.m13 * matrix.m30;
		float f5 = this.m10 * matrix.m01 + this.m11 * matrix.m11 + this.m12 * matrix.m21 + this.m13 * matrix.m31;
		float f6 = this.m10 * matrix.m02 + this.m11 * matrix.m12 + this.m12 * matrix.m22 + this.m13 * matrix.m32;
		float f7 = this.m10 * matrix.m03 + this.m11 * matrix.m13 + this.m12 * matrix.m23 + this.m13 * matrix.m33;
		float f8 = this.m20 * matrix.m00 + this.m21 * matrix.m10 + this.m22 * matrix.m20 + this.m23 * matrix.m30;
		float f9 = this.m20 * matrix.m01 + this.m21 * matrix.m11 + this.m22 * matrix.m21 + this.m23 * matrix.m31;
		float f10 = this.m20 * matrix.m02 + this.m21 * matrix.m12 + this.m22 * matrix.m22 + this.m23 * matrix.m32;
		float f11 = this.m20 * matrix.m03 + this.m21 * matrix.m13 + this.m22 * matrix.m23 + this.m23 * matrix.m33;
		float f12 = this.m30 * matrix.m00 + this.m31 * matrix.m10 + this.m32 * matrix.m20 + this.m33 * matrix.m30;
		float f13 = this.m30 * matrix.m01 + this.m31 * matrix.m11 + this.m32 * matrix.m21 + this.m33 * matrix.m31;
		float f14 = this.m30 * matrix.m02 + this.m31 * matrix.m12 + this.m32 * matrix.m22 + this.m33 * matrix.m32;
		float f15 = this.m30 * matrix.m03 + this.m31 * matrix.m13 + this.m32 * matrix.m23 + this.m33 * matrix.m33;
		this.m00 = f;
		this.m01 = f1;
		this.m02 = f2;
		this.m03 = f3;
		this.m10 = f4;
		this.m11 = f5;
		this.m12 = f6;
		this.m13 = f7;
		this.m20 = f8;
		this.m21 = f9;
		this.m22 = f10;
		this.m23 = f11;
		this.m30 = f12;
		this.m31 = f13;
		this.m32 = f14;
		this.m33 = f15;
	}

	public void mul(Quaternion quaternion) {
		this.mul(new Matrix4f(quaternion));
	}

	public void mul(float scale) {
		this.m00 *= scale;
		this.m01 *= scale;
		this.m02 *= scale;
		this.m03 *= scale;
		this.m10 *= scale;
		this.m11 *= scale;
		this.m12 *= scale;
		this.m13 *= scale;
		this.m20 *= scale;
		this.m21 *= scale;
		this.m22 *= scale;
		this.m23 *= scale;
		this.m30 *= scale;
		this.m31 *= scale;
		this.m32 *= scale;
		this.m33 *= scale;
	}

	public static Matrix4f perspective(double fov, float aspectRatio, float nearPlane, float farPlane) {
		float f = (float) (1.0D / Math.tan(fov * ((float) Math.PI / 180F) / 2.0D));
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.m00 = f / aspectRatio;
		matrix4f.m11 = f;
		matrix4f.m22 = (farPlane + nearPlane) / (nearPlane - farPlane);
		matrix4f.m32 = -1.0F;
		matrix4f.m23 = 2.0F * farPlane * nearPlane / (nearPlane - farPlane);
		return matrix4f;
	}

	public static Matrix4f orthographic(float width, float height, float nearPlane, float farPlane) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.m00 = 2.0F / width;
		matrix4f.m11 = 2.0F / height;
		float f = farPlane - nearPlane;
		matrix4f.m22 = -2.0F / f;
		matrix4f.m33 = 1.0F;
		matrix4f.m03 = -1.0F;
		matrix4f.m13 = -1.0F;
		matrix4f.m23 = -(farPlane + nearPlane) / f;
		return matrix4f;
	}

	public void translate(Vector3f vector) {
		this.m03 += vector.getX();
		this.m13 += vector.getY();
		this.m23 += vector.getZ();
	}

	public Matrix4f copy() {
		return new Matrix4f(this);
	}

	public static Matrix4f makeScale(float p_226593_0_, float p_226593_1_, float p_226593_2_) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.m00 = p_226593_0_;
		matrix4f.m11 = p_226593_1_;
		matrix4f.m22 = p_226593_2_;
		matrix4f.m33 = 1.0F;
		return matrix4f;
	}

	public static Matrix4f makeTranslate(float p_226599_0_, float p_226599_1_, float p_226599_2_) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.m00 = 1.0F;
		matrix4f.m11 = 1.0F;
		matrix4f.m22 = 1.0F;
		matrix4f.m33 = 1.0F;
		matrix4f.m03 = p_226599_0_;
		matrix4f.m13 = p_226599_1_;
		matrix4f.m23 = p_226599_2_;
		return matrix4f;
	}
}
