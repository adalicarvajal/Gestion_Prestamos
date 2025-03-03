package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LoanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Loan getLoanSample1() {
        return new Loan().id(1L).paymentTermMonths(1).status(1);
    }

    public static Loan getLoanSample2() {
        return new Loan().id(2L).paymentTermMonths(2).status(2);
    }

    public static Loan getLoanRandomSampleGenerator() {
        return new Loan().id(longCount.incrementAndGet()).paymentTermMonths(intCount.incrementAndGet()).status(intCount.incrementAndGet());
    }
}
