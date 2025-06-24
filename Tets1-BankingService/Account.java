import java.util.*;

public class Account {

    private static class Transaction {
        String date;
        int amount;
        int balance;

        Transaction(String date, int amount, int balance) {
            this.date = date;
            this.amount = amount;
            this.balance = balance;
        }
    }

    private final List<Transaction> transactions = new ArrayList<>();
    private int balance = 0;

    public void deposit(int amount, String date) {
        if (amount <= 0 || date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Invalid deposit input");
        }
        balance += amount;
        transactions.add(new Transaction(date, amount, balance));
    }

    public void withdraw(int amount, String date) {
        if (amount <= 0 || date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Invalid withdrawal input");
        }
        if (balance < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        balance -= amount;
        transactions.add(new Transaction(date, -amount, balance));
    }

    public void printStatement() {
        System.out.println("DATE | AMOUNT | BALANCE");

        // Print in reverse chronological order
        ListIterator<Transaction> it = transactions.listIterator(transactions.size());
        while (it.hasPrevious()) {
            Transaction t = it.previous();
            System.out.println(t.date + " | " + t.amount + " | " + t.balance);
        }
    }

    // Main method to test
    public static void main(String[] args) {
        Account account = new Account();
        account.deposit(1000, "10-01-2012");
        account.deposit(2000, "13-01-2012");
        account.withdraw(500, "14-01-2012");

        account.printStatement();
    }
}





