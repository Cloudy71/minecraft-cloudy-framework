/*
  User: Cloudy
  Date: 17/01/2022
  Time: 22:44
*/

package cz.cloudy.minecraft.core.hashing;

/**
 * @author Cloudy
 */
public record HashingAlgorithm(String name) {
    /**
     * SHA1 or SHA160 Hashing algorithm
     */
    public static final HashingAlgorithm SHA1   = new HashingAlgorithm("SHA-1");
    /**
     * SHA160 or SHA1 Hashing algorithm
     */
    public static final HashingAlgorithm SHA160 = SHA1;
    /**
     * SHA256 Hashing algorithm
     */
    public static final HashingAlgorithm SHA256 = new HashingAlgorithm("SHA-256");
    /**
     * SHA384 Hashing algorithm
     */
    public static final HashingAlgorithm SHA384 = new HashingAlgorithm("SHA-384");
    /**
     * SHA512 Hashing algorithm
     */
    public static final HashingAlgorithm SHA512 = new HashingAlgorithm("SHA-512");
}
