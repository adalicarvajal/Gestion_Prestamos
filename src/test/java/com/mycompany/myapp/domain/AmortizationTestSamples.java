package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AmortizationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Amortization getAmortizationSample1() {
        return new Amortization().id(1L).installmentNumber(1);
    }

    public static Amortization getAmortizationSample2() {
        return new Amortization().id(2L).installmentNumber(2);
    }

    public static Amortization getAmortizationRandomSampleGenerator() {
        return new Amortization().id(longCount.incrementAndGet()).installmentNumber(intCount.incrementAndGet());
    }
}
