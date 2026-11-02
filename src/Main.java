public class Main {

    public static void main(String[] args) {

        // Создание счетов
        BankAccount alice = new BankAccount("Alice",
                new Balance(100_00, Currency.RUB));
        BankAccount bob = new BankAccount("Bob",
                new Balance(50_00, Currency.USD));

        printAccounts("Начальное состояние", alice, bob);

        alice.deposit(25_50); // +25.50 ₽
        System.out.println("После deposit(25.50 ₽): " + alice);

        //Лимит на вывод
        alice.setWithdrawalLimitMinorUnits(30_00); // 30.00 ₽
        System.out.println("Установлен лимит на вывод: 30.00 ₽");

        try {
            alice.withdraw(40_00); // 40.00 ₽ > лимит
        } catch (RuntimeException e) {
            System.out.println("Ожидаемое исключение (лимит): " + e.getMessage());
        }

        // Перевод между счетами в разных валютах
        System.out.println("\nПеревод 20.00 ₽ с Alice на Bob (USD)...");
        alice.transferTo(bob, 20_00);
        printAccounts("После перевода", alice, bob);

        System.out.println("Ожидалось: +0.20 $ у Bob");

        // Конвертация
        Balance rub = new Balance(100_00, Currency.RUB); // 100.00 ₽
        System.out.println("\nКонвертация 100.00 ₽:");
        System.out.println("  → USD: " + rub.convertTo(Currency.USD));
        System.out.println("  → EUR: " + rub.convertTo(Currency.EUR));
        System.out.println("  → CNY: " + rub.convertTo(Currency.CNY));


        try {
            alice.deposit(-5);
        } catch (RuntimeException e) {
            System.out.println("deposit(-5): " + e.getMessage());
        }

        try {
            alice.transferTo(alice, 1);
        } catch (RuntimeException e) {
            System.out.println("transferTo(self): " + e.getMessage());
        }

        try {
            alice.transferTo(null, 1);
        } catch (RuntimeException e) {
            System.out.println("transferTo(null): " + e.getMessage());
        }

        alice.freeze();
        try {
            alice.deposit(1);
        } catch (RuntimeException e) {
            System.out.println("deposit на замороженном: " + e.getMessage());
        }

        try {
            bob.withdraw(1_000_000_00L);
        } catch (RuntimeException e) {
            System.out.println("withdraw слишком много: " + e.getMessage());
        }

        try {
            new Balance(-1, Currency.RUB);
        } catch (RuntimeException e) {
            System.out.println("Balance(-1): " + e.getMessage());
        }

        // 7. Неизменяемость Balance
        System.out.println("\n--- Неизменяемость Balance ---");
        Balance b1 = new Balance(10_00, Currency.RUB);
        Balance b2 = b1.add(5_00);
        System.out.println("b1 = " + b1 + " (не изменился)");
        System.out.println("b2 = " + b2 + " (новый объект)");
        System.out.println("b1 == b2? " + (b1 == b2));
    }

    private static void printAccounts(String title, BankAccount... accounts) {
        System.out.println("\n[" + title + "]");
        for (BankAccount a : accounts) {
            System.out.println("  " + a);
        }
    }
}