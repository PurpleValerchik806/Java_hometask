import java.util.Objects;
public final class Balance {

    private final long amountMinorUnits; // копейки/центы/фэни
    private final Currency currency;

    public Balance(long amountMinorUnits, Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency не может быть null");
        }
        if (amountMinorUnits < 0) {
            throw new IllegalArgumentException(
                    "Баланс не может быть отрицательным: " + amountMinorUnits);
        }
        this.amountMinorUnits = amountMinorUnits;
        this.currency = currency;
    }

    public static Balance zero(Currency currency) {
        return new Balance(0L, currency);
    }

    public long getAmountMinorUnits() {
        return amountMinorUnits;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Balance add(long deltaMinorUnits) {
        if (deltaMinorUnits < 0) {
            throw new IllegalArgumentException(
                    "Нельзя прибавлять отрицательную сумму: " + deltaMinorUnits);
        }
        long result = Math.addExact(amountMinorUnits, deltaMinorUnits);
        return new Balance(result, currency);
    }

    public Balance subtract(long deltaMinorUnits) {
        if (deltaMinorUnits < 0) {
            throw new IllegalArgumentException(
                    "Нельзя вычитать отрицательную сумму: " + deltaMinorUnits);
        }
        if (deltaMinorUnits > amountMinorUnits) {
            throw new IllegalStateException(
                    "Недостаточно средств: баланс " + amountMinorUnits
                            + ", списание " + deltaMinorUnits);
        }
        return new Balance(amountMinorUnits - deltaMinorUnits, currency);
    }

    /*Конвертация*/
    public Balance convertTo(Currency target) {
        if (target == null) {
            throw new IllegalArgumentException("Целевая валюта не может быть null");
        }
        if (target == currency) {
            return this;
        }
        long inRubKopecks = Math.multiplyExact(
                amountMinorUnits, currency.getPriceInRublesKopecks());
        long result = inRubKopecks * 100L / target.getPriceInRublesKopecks();
        return new Balance(result, target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Balance other)) return false;
        return amountMinorUnits == other.amountMinorUnits && currency == other.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amountMinorUnits, currency);
    }

    /** Красивый вывод */
    public String format() {
        long major = amountMinorUnits / 100;
        long minor = amountMinorUnits % 100;
        return String.format("%d.%02d %s", major, minor, currency.getSymbol());
    }

    @Override
    public String toString() {
        return format() + " (" + currency.getCode() + ")";
    }
}