package org.apache.commons.math3.distribution;
public class DiscreteDistribution<T> {
	protected final org.apache.commons.math3.random.RandomGenerator random;

	private final java.util.List<T> singletons;

	private final double[] probabilities;

	public DiscreteDistribution(final java.util.List<org.apache.commons.math3.util.Pair<T, java.lang.Double>> samples) throws org.apache.commons.math3.exception.NotPositiveException, org.apache.commons.math3.exception.MathArithmeticException, org.apache.commons.math3.exception.MathIllegalArgumentException {
		this(new org.apache.commons.math3.random.Well19937c(), samples);
	}

	public DiscreteDistribution(final org.apache.commons.math3.random.RandomGenerator rng, final java.util.List<org.apache.commons.math3.util.Pair<T, java.lang.Double>> samples) throws org.apache.commons.math3.exception.NotPositiveException, org.apache.commons.math3.exception.MathArithmeticException, org.apache.commons.math3.exception.MathIllegalArgumentException {
		random = rng;
		singletons = new java.util.ArrayList<T>(samples.size());
		final double[] probs = new double[samples.size()];
		for (int i = 0; i < samples.size(); i++) {
			final org.apache.commons.math3.util.Pair<T, java.lang.Double> sample = samples.get(i);
			singletons.add(sample.getKey());
			if (sample.getValue() < 0) {
				throw new org.apache.commons.math3.exception.NotPositiveException(sample.getValue());
			}
			probs[i] = sample.getValue();
		}
		probabilities = org.apache.commons.math3.util.MathArrays.normalizeArray(probs, 1.0);
	}

	public void reseedRandomGenerator(long seed) {
		random.setSeed(seed);
	}

	double probability(final T x) {
		double probability = 0;
		for (int i = 0; i < probabilities.length; i++) {
			if (((x == null) && (singletons.get(i) == null)) || ((x != null) && x.equals(singletons.get(i)))) {
				probability += probabilities[i];
			}
		}
		return probability;
	}

	public java.util.List<org.apache.commons.math3.util.Pair<T, java.lang.Double>> getSamples() {
		final java.util.List<org.apache.commons.math3.util.Pair<T, java.lang.Double>> samples = new java.util.ArrayList<org.apache.commons.math3.util.Pair<T, java.lang.Double>>(probabilities.length);
		for (int i = 0; i < probabilities.length; i++) {
			samples.add(new org.apache.commons.math3.util.Pair<T, java.lang.Double>(singletons.get(i), probabilities[i]));
		}
		return samples;
	}

	public T sample() {
		final double randomValue = random.nextDouble();
		double sum = 0;
		for (int i = 0; i < probabilities.length; i++) {
			sum += probabilities[i];
			if (randomValue < sum) {
				return singletons.get(i);
			}
		}
		return singletons.get(singletons.size() - 1);
	}

	public T[] sample(int sampleSize) throws org.apache.commons.math3.exception.NotStrictlyPositiveException {
		if (sampleSize <= 0) {
			throw new org.apache.commons.math3.exception.NotStrictlyPositiveException(org.apache.commons.math3.exception.util.LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize);
		}
		final T[] out = ((T[]) (java.lang.reflect.Array.newInstance(singletons.get(0).getClass(), sampleSize)));
		return out;
	}
}