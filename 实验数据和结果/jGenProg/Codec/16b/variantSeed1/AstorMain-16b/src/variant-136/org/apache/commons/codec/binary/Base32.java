package org.apache.commons.codec.binary;
public class Base32 extends 









































org.apache.commons.codec.binary.BaseNCodec {






	private static final int BITS_PER_ENCODED_BYTE = 5;
	private static final int BYTES_PER_ENCODED_BLOCK = 8;
	private static final int BYTES_PER_UNENCODED_BLOCK = 5;






	private static final byte[] CHUNK_SEPARATOR = new byte[]{ '\r', '\n' };






	private static final byte[] DECODE_TABLE = new byte[]{ 

	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
	-1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, 
	-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
	15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };






	private static final byte[] ENCODE_TABLE = new byte[]{ 
	'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 
	'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
	'2', '3', '4', '5', '6', '7' };







	private static final byte[] HEX_DECODE_TABLE = new byte[]{ 

	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
	0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, 
	-1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
	25, 26, 27, 28, 29, 30, 31, 32 };






	private static final byte[] HEX_ENCODE_TABLE = new byte[]{ 
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
	'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 
	'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V' };



	private static final int MASK_5BITS = 0x1f;














	private final int decodeSize;




	private final byte[] decodeTable;





	private final int encodeSize;




	private final byte[] encodeTable;




	private final byte[] lineSeparator;








	public Base32() {
		this(false);
	}








	public Base32(final byte pad) {
		this(false, pad);
	}








	public Base32(final boolean useHex) {
		this(0, null, useHex, org.apache.commons.codec.binary.BaseNCodec.PAD_DEFAULT);
	}









	public Base32(final boolean useHex, final byte pad) {
		this(0, null, useHex, pad);
	}












	public Base32(final int lineLength) {
		this(lineLength, org.apache.commons.codec.binary.Base32.CHUNK_SEPARATOR);
	}



















	public Base32(final int lineLength, final byte[] lineSeparator) {
		this(lineLength, lineSeparator, false, org.apache.commons.codec.binary.BaseNCodec.PAD_DEFAULT);
	}






















	public Base32(final int lineLength, final byte[] lineSeparator, final boolean useHex) {
		this(lineLength, lineSeparator, useHex, org.apache.commons.codec.binary.BaseNCodec.PAD_DEFAULT);
	}























	public Base32(final int lineLength, final byte[] lineSeparator, final boolean useHex, final byte pad) {
		super(org.apache.commons.codec.binary.Base32.BYTES_PER_UNENCODED_BLOCK, org.apache.commons.codec.binary.Base32.BYTES_PER_ENCODED_BLOCK, lineLength, 
		lineSeparator == null ? 0 : lineSeparator.length, pad);
		if (useHex) {
			this.encodeTable = org.apache.commons.codec.binary.Base32.HEX_ENCODE_TABLE;
			this.decodeTable = org.apache.commons.codec.binary.Base32.HEX_DECODE_TABLE;
		} else {
			this.encodeTable = org.apache.commons.codec.binary.Base32.ENCODE_TABLE;
			this.decodeTable = org.apache.commons.codec.binary.Base32.DECODE_TABLE;
		}
		if (lineLength > 0) {
			if (lineSeparator == null) {
				throw new java.lang.IllegalArgumentException(("lineLength " + lineLength) + " > 0, but lineSeparator is null");
			}

			if (containsAlphabetOrPad(lineSeparator)) {
				final java.lang.String sep = org.apache.commons.codec.binary.StringUtils.newStringUtf8(lineSeparator);
				throw new java.lang.IllegalArgumentException(("lineSeparator must not contain Base32 characters: [" + sep) + "]");
			}
			this.encodeSize = org.apache.commons.codec.binary.Base32.BYTES_PER_ENCODED_BLOCK + lineSeparator.length;
			this.lineSeparator = new byte[lineSeparator.length];
			java.lang.System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
		} else {
			this.encodeSize = org.apache.commons.codec.binary.Base32.BYTES_PER_ENCODED_BLOCK;
			this.lineSeparator = null;
		}
		this.decodeSize = this.encodeSize - 1;

		if (isInAlphabet(pad) || org.apache.commons.codec.binary.BaseNCodec.isWhiteSpace(pad)) {
			throw new java.lang.IllegalArgumentException("pad must not be in alphabet or whitespace");
		}
	}























	@java.lang.Override
	void decode(final byte[] in, int inPos, final int inAvail, final org.apache.commons.codec.binary.BaseNCodec.Context context) {


		if (context.eof) {
			return;
		}
		if (inAvail < 0) {
			context.eof = true;
		}
		for (int i = 0; i < inAvail; i++) {
			final byte b = in[inPos++];
			if (b == pad) {

				context.eof = true;
				break;
			} else {
				final byte[] buffer = ensureBufferSize(decodeSize, context);
				if ((b >= 0) && (b < this.decodeTable.length)) {
					final int result = this.decodeTable[b];
					if (result >= 0) {
						context.modulus = (context.modulus + 1) % org.apache.commons.codec.binary.Base32.BYTES_PER_ENCODED_BLOCK;

						context.lbitWorkArea = (context.lbitWorkArea << org.apache.commons.codec.binary.Base32.BITS_PER_ENCODED_BYTE) + result;
						if (context.modulus == 0) {
							buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 32) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
							buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 24) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
							buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 16) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
							buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 8) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
							buffer[context.pos++] = ((byte) (context.lbitWorkArea & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
						}
					}
				}
			}
		}




		if (context.eof && (context.modulus >= 2)) {
			final byte[] buffer = ensureBufferSize(decodeSize, context);


			switch (context.modulus) {
				case 2 :
					buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 2) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					break;
				case 3 :
					buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 7) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					break;
				case 4 :
					context.lbitWorkArea = context.lbitWorkArea >> 4;
					buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 8) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					buffer[context.pos++] = ((byte) (context.lbitWorkArea & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					break;
				case 5 :
					context.lbitWorkArea = context.lbitWorkArea >> 1;
					buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 16) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 8) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					buffer[context.pos++] = ((byte) (context.lbitWorkArea & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					break;
				case 6 :
					context.lbitWorkArea = context.lbitWorkArea >> 6;
					buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 16) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 8) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					buffer[context.pos++] = ((byte) (context.lbitWorkArea & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					break;
				case 7 :
					context.lbitWorkArea = context.lbitWorkArea >> 3;
					buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 24) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 16) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					buffer[context.pos++] = ((byte) ((context.lbitWorkArea >> 8) & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					buffer[context.pos++] = ((byte) (context.lbitWorkArea & org.apache.commons.codec.binary.BaseNCodec.MASK_8BITS));
					break;
				default :

					throw new java.lang.IllegalStateException("Impossible modulus " + context.modulus);}

		}
	}
















	@java.lang.Override
	void encode(final byte[] in, int inPos, final int inAvail, final org.apache.commons.codec.binary.BaseNCodec.Context context) {


		if (context.eof) {
			return;
		}


		if (inAvail < 0) {
			context.eof = true;
			if ((0 == context.modulus) && (lineLength == 0)) {
				return;
			}
			final byte[] buffer = ensureBufferSize(encodeSize, context);
			final int savedPos = context.pos;
			switch (context.modulus) {
				case 0 :
					break;
				case 1 :
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 3)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea << 2)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = pad;
					buffer[context.pos++] = pad;
					buffer[context.pos++] = pad;
					buffer[context.pos++] = pad;
					buffer[context.pos++] = pad;
					buffer[context.pos++] = pad;
					break;
				case 2 :
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 11)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 6)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 1)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea << 4)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = pad;
					buffer[context.pos++] = pad;
					buffer[context.pos++] = pad;
					buffer[context.pos++] = pad;
					break;
				case 3 :
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 19)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 14)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 9)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 4)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea << 1)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = pad;
					buffer[context.pos++] = pad;
					buffer[context.pos++] = pad;
					break;
				case 4 :
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 27)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 22)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 17)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 12)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 7)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 2)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea << 3)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = pad;
					break;
				default :
					throw new java.lang.IllegalStateException("Impossible modulus " + context.modulus);}

			context.currentLinePos += context.pos - savedPos;

			if ((lineLength > 0) && (context.currentLinePos > 0)) {
				java.lang.System.arraycopy(lineSeparator, 0, buffer, context.pos, lineSeparator.length);
				context.pos += lineSeparator.length;
			}
		} else {
			for (int i = 0; i < inAvail; i++) {
				final byte[] buffer = ensureBufferSize(encodeSize, context);
				context.modulus = (context.modulus + 1) % org.apache.commons.codec.binary.Base32.BYTES_PER_UNENCODED_BLOCK;
				int b = in[inPos++];
				if (b < 0) {
					b += 256;
				}
				context.lbitWorkArea = (context.lbitWorkArea << 8) + b;
				if (0 == context.modulus) {
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 35)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 30)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 25)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 20)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 15)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 10)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea >> 5)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					buffer[context.pos++] = encodeTable[((int) (context.lbitWorkArea)) & org.apache.commons.codec.binary.Base32.MASK_5BITS];
					context.currentLinePos += org.apache.commons.codec.binary.Base32.BYTES_PER_ENCODED_BLOCK;
					if ((lineLength > 0) && (lineLength <= context.currentLinePos)) {
						java.lang.System.arraycopy(lineSeparator, 0, buffer, context.pos, lineSeparator.length);
						context.pos += lineSeparator.length;
						context.currentLinePos = 0;
					}
				}
			}
		}
	}








	@java.lang.Override
	public boolean isInAlphabet(final byte octet) {



		return false;}}