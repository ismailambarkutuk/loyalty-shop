package edu.tedu.ismail;

/**
 * Purchase işleminin çıktısını temsil eden basit bir DTO sınıfı.
 */
public class PurchaseResult {

    private final boolean success;
    private final String message;
    private final int earnedPoints;

    public PurchaseResult(boolean success, String message, int earnedPoints) {
        this.success = success;
        this.message = message;
        this.earnedPoints = earnedPoints;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getEarnedPoints() {
        return earnedPoints;
    }

    @Override
    public String toString() {
        return "PurchaseResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", earnedPoints=" + earnedPoints +
                '}';
    }
}
