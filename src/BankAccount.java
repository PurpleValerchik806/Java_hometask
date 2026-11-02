import java.util.Objects;

public class BankAccount {

    private final String owner;
    private Balance balance;
    private long withdrawalLimitMinorUnits;
    private boolean frozen;

    public BankAccount(String owner, Balance initialBalance) {
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Владелец не может быть пустым");
        }
        if (initialBalance == null) {
            throw new IllegalArgumentException("Начальный баланс не может быть null");
        }
        this.owner = owner;
        this.balance = initialBalance;
        this.withdrawalLimitMinorUnits = Long.MAX_VALUE; // по умолчанию без лимита
        this.frozen = false;
    }

    public String getOwner() {
        return owner;
    }

    public Balance getBalance() {
        return balance;
    }

    public long getWithdrawalLimitMinorUnits() {
        return withdrawalLimitMinorUnits;
    }

    public boolean isFrozen() {
        return frozen;
    }


    public void deposit(long amountMinorUnits) {
        ensureNotFrozen();
        balance = balance.add(amountMinorUnits);
    }

    public void withdraw(long amountMinorUnits) {
        ensureNotFrozen();
        if (amountMinorUnits < 0) {
            throw new IllegalArgumentException("Сумма снятия не может быть отрицательной");
        }
        if (amountMinorUnits > withdrawalLimitMinorUnits) {
            throw new IllegalStateException(
                    "Сумма " + amountMinorUnits + " превышает лимит на операцию "
                            + withdrawalLimitMinorUnits);
        }
        balance = balance.subtract(amountMinorUnits);
    }
    public void transferTo(BankAccount target, long amountMinorUnits) {
        if (target == null) {
            throw new IllegalArgumentException("Целевой счёт не может быть null");
        }
        if (target == this) {
            throw new IllegalArgumentException("Нельзя переводить на тот же счёт");
        }
        ensureNotFrozen();
        target.ensureNotFrozen();

        if (amountMinorUnits < 0) {
            throw new IllegalArgumentException("Сумма перевода не может быть отрицательной");
        }
        if (amountMinorUnits > withdrawalLimitMinorUnits) {
            throw new IllegalStateException(
                    "Сумма перевода превышает лимит: " + withdrawalLimitMinorUnits);
        }

        // списываем в валюте отправителя
        Balance afterWithdraw = balance.subtract(amountMinorUnits);

        // зачисляем в валюте получателя
        Balance credit = new Balance(amountMinorUnits, balance.getCurrency())
                .convertTo(target.balance.getCurrency());

        // применяем изменения атомарно (сначала списание, потом зачисление)
        this.balance = afterWithdraw;
        target.balance = target.balance.add(credit.getAmountMinorUnits());
    }

    public void setWithdrawalLimitMinorUnits(long limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Лимит не может быть отрицательным");
        }
        this.withdrawalLimitMinorUnits = limit;
    }

    public void freeze() {
        this.frozen = true;
    }

    public void unfreeze() {
        this.frozen = false;
    }

    private void ensureNotFrozen() {
        if (frozen) {
            throw new IllegalStateException("Счёт заморожен: " + owner);
        }
    }

    @Override
    public String toString() {
        return "BankAccount{owner='" + owner + "', balance=" + balance
                + ", limit=" + withdrawalLimitMinorUnits
                + ", frozen=" + frozen + "}";
    }
}