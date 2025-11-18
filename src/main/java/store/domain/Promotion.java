package store.domain;

import java.time.LocalDate;

public class Promotion {
    private final String name;
    private final int buy;
    private final int get;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(String name, int buy, int get,
                     LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isAvailable(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public int calculateFreeCount(int purchaseQuantity) {
        int setSize = buy + get;
        int setCount = purchaseQuantity / setSize;
        return setCount * get;
    }

    public int getApplicableQuantity(int availableStock) {
        int setSize = buy + get;
        int possibleSets = availableStock / setSize;
        return possibleSets * setSize;
    }

    public int getBuy() {
        return buy;
    }

    public int getGet() {
        return get;
    }

    public String getName() {
        return name;
    }
}
