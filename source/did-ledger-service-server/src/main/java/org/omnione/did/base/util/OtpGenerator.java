/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.base.util;

import java.security.SecureRandom;

/**
 * Utility class for generating OTP (One-Time Password) codes.
 */
public class OtpGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int DEFAULT_OTP_LENGTH = 6;
    private static final int MIN_OTP_VALUE = 100000; // 6-digit minimum (100000)
    private static final int MAX_OTP_VALUE = 999999; // 6-digit maximum (999999)

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private OtpGenerator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a 6-digit OTP code using SecureRandom.
     *
     * @return a 6-digit OTP code as a string
     */
    public static String generateOtp() {
        return generateOtp(DEFAULT_OTP_LENGTH);
    }

    /**
     * Generates an OTP code with the specified length using SecureRandom.
     *
     * @param length the length of the OTP code (minimum 4, maximum 8)
     * @return an OTP code as a string with the specified length
     * @throws IllegalArgumentException if length is not between 4 and 8
     */
    public static String generateOtp(int length) {
        if (length < 4 || length > 8) {
            throw new IllegalArgumentException("OTP length must be between 4 and 8 digits");
        }

        if (length == DEFAULT_OTP_LENGTH) {
            // Optimized path for 6-digit OTP
            int otpValue = SECURE_RANDOM.nextInt(MAX_OTP_VALUE - MIN_OTP_VALUE + 1) + MIN_OTP_VALUE;
            return String.valueOf(otpValue);
        }

        // General path for other lengths
        int minValue = (int) Math.pow(10, length - 1);
        int maxValue = (int) Math.pow(10, length) - 1;
        int otpValue = SECURE_RANDOM.nextInt(maxValue - minValue + 1) + minValue;
        
        return String.format("%0" + length + "d", otpValue);
    }

    /**
     * Validates if the given string is a valid OTP format.
     *
     * @param otp the OTP string to validate
     * @return true if the OTP is valid (6 digits), false otherwise
     */
    public static boolean isValidOtpFormat(String otp) {
        return isValidOtpFormat(otp, DEFAULT_OTP_LENGTH);
    }

    /**
     * Validates if the given string is a valid OTP format with specified length.
     *
     * @param otp    the OTP string to validate
     * @param length the expected length of the OTP
     * @return true if the OTP is valid format, false otherwise
     */
    public static boolean isValidOtpFormat(String otp, int length) {
        if (otp == null || otp.isEmpty()) {
            return false;
        }

        // Check if OTP contains only digits and has the correct length
        return otp.matches("\\d{" + length + "}");
    }
}
