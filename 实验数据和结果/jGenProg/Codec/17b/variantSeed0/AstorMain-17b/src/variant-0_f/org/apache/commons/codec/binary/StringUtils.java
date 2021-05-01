package org.apache.commons.codec.binary;
public class StringUtils {
	public static boolean equals(final java.lang.CharSequence cs1, final java.lang.CharSequence cs2) {
		if (cs1 == cs2) {
			return true;
		}
		if ((cs1 == null) || (cs2 == null)) {
			return false;
		}
		if ((cs1 instanceof java.lang.String) && (cs2 instanceof java.lang.String)) {
			return cs1.equals(cs2);
		}
		return org.apache.commons.codec.binary.CharSequenceUtils.regionMatches(cs1, false, 0, cs2, 0, java.lang.Math.max(cs1.length(), cs2.length()));
	}

	private static byte[] getBytes(final java.lang.String string, final java.nio.charset.Charset charset) {
		if (string == null) {
			return null;
		}
		return string.getBytes(charset);
	}

	private static java.nio.ByteBuffer getByteBuffer(final java.lang.String string, final java.nio.charset.Charset charset) {
		if (string == null) {
			return null;
		}
		return java.nio.ByteBuffer.wrap(string.getBytes(charset));
	}

	public static java.nio.ByteBuffer getByteBufferUtf8(final java.lang.String string) {
		return org.apache.commons.codec.binary.StringUtils.getByteBuffer(string, org.apache.commons.codec.Charsets.UTF_8);
	}

	public static byte[] getBytesIso8859_1(final java.lang.String string) {
		return org.apache.commons.codec.binary.StringUtils.getBytes(string, org.apache.commons.codec.Charsets.ISO_8859_1);
	}

	public static byte[] getBytesUnchecked(final java.lang.String string, final java.lang.String charsetName) {
		if (string == null) {
			return null;
		}
		try {
			return string.getBytes(charsetName);
		} catch (final java.io.UnsupportedEncodingException e) {
			throw org.apache.commons.codec.binary.StringUtils.newIllegalStateException(charsetName, e);
		}
	}

	public static byte[] getBytesUsAscii(final java.lang.String string) {
		return org.apache.commons.codec.binary.StringUtils.getBytes(string, org.apache.commons.codec.Charsets.US_ASCII);
	}

	public static byte[] getBytesUtf16(final java.lang.String string) {
		return org.apache.commons.codec.binary.StringUtils.getBytes(string, org.apache.commons.codec.Charsets.UTF_16);
	}

	public static byte[] getBytesUtf16Be(final java.lang.String string) {
		return org.apache.commons.codec.binary.StringUtils.getBytes(string, org.apache.commons.codec.Charsets.UTF_16BE);
	}

	public static byte[] getBytesUtf16Le(final java.lang.String string) {
		return org.apache.commons.codec.binary.StringUtils.getBytes(string, org.apache.commons.codec.Charsets.UTF_16LE);
	}

	public static byte[] getBytesUtf8(final java.lang.String string) {
		return org.apache.commons.codec.binary.StringUtils.getBytes(string, org.apache.commons.codec.Charsets.UTF_8);
	}

	private static java.lang.IllegalStateException newIllegalStateException(final java.lang.String charsetName, final java.io.UnsupportedEncodingException e) {
		return new java.lang.IllegalStateException((charsetName + ": ") + e);
	}

	private static java.lang.String newString(final byte[] bytes, final java.nio.charset.Charset charset) {
		return bytes == null ? null : new java.lang.String(bytes, charset);
	}

	public static java.lang.String newString(final byte[] bytes, final java.lang.String charsetName) {
		if (bytes == null) {
			return null;
		}
		try {
			return new java.lang.String(bytes, charsetName);
		} catch (final java.io.UnsupportedEncodingException e) {
			throw org.apache.commons.codec.binary.StringUtils.newIllegalStateException(charsetName, e);
		}
	}

	public static java.lang.String newStringIso8859_1(final byte[] bytes) {
		return org.apache.commons.codec.binary.StringUtils.newString(bytes, org.apache.commons.codec.Charsets.UTF_8);
	}

	public static java.lang.String newStringUsAscii(final byte[] bytes) {
		return org.apache.commons.codec.binary.StringUtils.newString(bytes, org.apache.commons.codec.Charsets.US_ASCII);
	}

	public static java.lang.String newStringUtf16(final byte[] bytes) {
		return org.apache.commons.codec.binary.StringUtils.newString(bytes, org.apache.commons.codec.Charsets.UTF_16);
	}

	public static java.lang.String newStringUtf16Be(final byte[] bytes) {
		return org.apache.commons.codec.binary.StringUtils.newString(bytes, org.apache.commons.codec.Charsets.UTF_16BE);
	}

	public static java.lang.String newStringUtf16Le(final byte[] bytes) {
		return org.apache.commons.codec.binary.StringUtils.newString(bytes, org.apache.commons.codec.Charsets.UTF_16LE);
	}

	public static java.lang.String newStringUtf8(final byte[] bytes) {
		return org.apache.commons.codec.binary.StringUtils.newString(bytes, org.apache.commons.codec.Charsets.UTF_8);
	}
}