package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UserDataTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static UserData getUserDataSample1() {
        return new UserData()
            .id(1L)
            .familyLoad(1)
            .workplace("workplace1")
            .housingType("housingType1")
            .yearsOfEmployment(1)
            .employmentStatus(1);
    }

    public static UserData getUserDataSample2() {
        return new UserData()
            .id(2L)
            .familyLoad(2)
            .workplace("workplace2")
            .housingType("housingType2")
            .yearsOfEmployment(2)
            .employmentStatus(2);
    }

    public static UserData getUserDataRandomSampleGenerator() {
        return new UserData()
            .id(longCount.incrementAndGet())
            .familyLoad(intCount.incrementAndGet())
            .workplace(UUID.randomUUID().toString())
            .housingType(UUID.randomUUID().toString())
            .yearsOfEmployment(intCount.incrementAndGet())
            .employmentStatus(intCount.incrementAndGet());
    }
}
