// Class representing the shared bank account
class BankAccount {
    private long accountNumber;
    private String accountHolderName;
    private double accountBalance;

    // Constructor to initialize account details
    public BankAccount(long accountNumber, String accountHolderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.accountBalance = initialBalance;
    }

    // Synchronized deposit method to prevent race conditions
    public synchronized void deposit(String userName, double amount) {
        if (amount <= 0) {
            System.out.println(userName + " attempted invalid deposit of Rs." + amount + " (Amount must be positive)");
            return;
        }
        accountBalance += amount;
        System.out.println(userName + " deposited: Rs." + amount + " | Updated Balance: Rs." + accountBalance);
    }

    // Synchronized withdraw method ensuring thread safety
    public synchronized void withdraw(String userName, double amount) {
        if (amount <= 0) {
            System.out.println(userName + " attempted invalid withdrawal of Rs." + amount + " (Amount must be positive)");
            return;
        }
        if (amount > accountBalance) {
            System.out.println(userName + " attempted to withdraw: Rs." + amount + " | Insufficient Balance! Current Balance: Rs." + accountBalance);
        } else {
            accountBalance -= amount;
            System.out.println(userName + " withdrew: Rs." + amount + " | Updated Balance: Rs." + accountBalance);
        }
    }

    // Method to display current account balance
    public void displayBalance() {
        System.out.println("----------------------------------------");
        System.out.println("Account Holder : " + accountHolderName);
        System.out.println("Account Number : " + accountNumber);
        System.out.println("Final Balance  : Rs." + accountBalance);
        System.out.println("----------------------------------------");
    }
}

// Thread class to represent a user performing a transaction
class UserTransaction extends Thread {
    private String userName;
    private BankAccount account;
    private String transactionType;
    private double amount;

    // Constructor initializing thread details
    public UserTransaction(String userName, BankAccount account, String transactionType, double amount) {
        this.userName = userName;
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    // Run method executing the corresponding transaction
    @Override
    public void run() {
        if (transactionType.equalsIgnoreCase("Deposit")) {
            account.deposit(userName, amount);
        } else if (transactionType.equalsIgnoreCase("Withdrawal") || transactionType.equalsIgnoreCase("Withdraw")) {
            account.withdraw(userName, amount);
        }
    }
}

// Main class to run concurrent banking transactions
public class OnlineBankingSystem {
    public static void main(String[] args) {
        // Shared bank account initialized with Rs. 10,000
        BankAccount sharedAccount = new BankAccount(10010101, "Savings Account", 10000.0);

        System.out.println("Initial Balance: Rs. 10000.0\n");

        // Concurrent transaction threads covering all manual scenarios:
        // Valid deposits, valid withdrawals, simultaneous transactions, overdrafts, and zero/negative cases
        Thread t1 = new UserTransaction("User1", sharedAccount, "Deposit", 2000.0);
        Thread t2 = new UserTransaction("User2", sharedAccount, "Withdrawal", 3000.0);
        Thread t3 = new UserTransaction("User3", sharedAccount, "Deposit", 5000.0);
        Thread t4 = new UserTransaction("User4", sharedAccount, "Withdrawal", 8000.0);
        Thread t5 = new UserTransaction("User5", sharedAccount, "Withdrawal", 25000.0); // Insufficient balance
        Thread t6 = new UserTransaction("User6", sharedAccount, "Deposit", -2000.0);     // Negative deposit
        Thread t7 = new UserTransaction("User7", sharedAccount, "Withdrawal", 0.0);       // Zero amount

        // Start all threads concurrently
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();

        // Wait for all threads to finish execution
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
            t6.join();
            t7.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted: " + e.getMessage());
        }

        // Display final balance after all synchronized operations complete
        sharedAccount.displayBalance();
    }
}
