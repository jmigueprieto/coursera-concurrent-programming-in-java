package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import java.util.ArrayList;
import java.util.List;

import static edu.rice.pcdp.PCDP.*;


/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 * <p>
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     * <p>
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        final SieveActorActor actor = new SieveActorActor(2);
        finish(() -> {
            for (int i = 3; i <= limit; i += 2) {
                actor.send(i);
            }
        });

        int n = 0;
        SieveActorActor loopActor = actor;
        while (loopActor != null) {
            n += loopActor.localPrimes.size();
            loopActor = loopActor.nextActor;
        }

        return n;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {

        private static final int MAX_LOCAL_PRIMES = 1_000;

        private SieveActorActor nextActor;

        private List<Integer> localPrimes = new ArrayList<>();

        SieveActorActor(final int localPrime) {
            localPrimes.add(localPrime);
        }

        /**
         * Process a single message sent to this actor.
         * <p>
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            final int candidate = (Integer) msg;
            if (isLocallyPrime(candidate)) {
                if (localPrimes.size() < MAX_LOCAL_PRIMES) {
                    localPrimes.add(candidate);
                } else if (nextActor == null) {
                    nextActor = new SieveActorActor(candidate);
                } else {
                    nextActor.send(candidate);
                }
            }
        }

        private boolean isLocallyPrime(final int candidate) {
            boolean isPrime = true;
            final int s = localPrimes.size();
            for (int i = 0; i < s; ++i) {
                final Integer loopPrime = localPrimes.get(i);
                if (candidate % loopPrime.intValue() == 0) {
                    isPrime = false;
                    break;
                }
            }
            return isPrime;
        }
    }
}
