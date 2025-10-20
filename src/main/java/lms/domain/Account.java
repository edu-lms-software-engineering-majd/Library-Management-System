package lms.domain;

import java.time.LocalDate;
import java.util.UUID;

 
public class Account {
    private final UUID accountId;
    private final UUID userId;
    private double balance;
    private AccountStatus status;
    private final LocalDate createdAt;
    private LocalDate updatedAt;
    private int maxBorrowLimit;
    private int currentBorrowedCount;
    
    public enum AccountStatus {
        ACTIVE, SUSPENDED, INACTIVE, BLACKLISTED
    }
    
    public Account(UUID userId) {
        this.accountId = UUID.randomUUID();
        this.userId = userId;
        this.balance = 0.0;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.maxBorrowLimit = 5;
        this.currentBorrowedCount = 0;
    }
    
    // Getters
    public UUID getAccountId() { return accountId; }
    public UUID getUserId() { return userId; }
    public double getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public LocalDate getCreatedAt() { return createdAt; }
    public LocalDate getUpdatedAt() { return updatedAt; }
    public int getMaxBorrowLimit() { return maxBorrowLimit; }
    public int getCurrentBorrowedCount() { return currentBorrowedCount; }
    
    public boolean canBorrow() {
        return status == AccountStatus.ACTIVE && 
               currentBorrowedCount < maxBorrowLimit && 
               balance <= 0;
    }
    
    public void addFine(double amount, String reason) {
        this.balance += amount;
        this.updatedAt = LocalDate.now();
        
        if (this.balance >= 25.0) {
            suspendAccount();
        }
    }
    
    public void payFine(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance -= amount;
        this.updatedAt = LocalDate.now();
        
        if (this.balance <= 0 && this.status == AccountStatus.SUSPENDED) {
            activateAccount();
        }
    }
    
    public void incrementBorrowedCount() {
        if (currentBorrowedCount >= maxBorrowLimit) {
            throw new IllegalStateException("Cannot borrow more books - limit reached");
        }
        this.currentBorrowedCount++;
        this.updatedAt = LocalDate.now();
    }
    
    public void decrementBorrowedCount() {
        if (currentBorrowedCount <= 0) {
            throw new IllegalStateException("No books to return");
        }
        this.currentBorrowedCount--;
        this.updatedAt = LocalDate.now();
    }
    
    public void setMaxBorrowLimit(int maxBorrowLimit) {
        if (maxBorrowLimit <= 0) {
            throw new IllegalArgumentException("Borrow limit must be positive");
        }
        this.maxBorrowLimit = maxBorrowLimit;
        this.updatedAt = LocalDate.now();
    }
    
    public void setStatus(AccountStatus status) {
        this.status = status;
        this.updatedAt = LocalDate.now();
    }
    
    private void suspendAccount() {
        this.status = AccountStatus.SUSPENDED;
        this.updatedAt = LocalDate.now();
    }
    
    private void activateAccount() {
        this.status = AccountStatus.ACTIVE;
        this.updatedAt = LocalDate.now();
    }
    
    @Override
    public String toString() {
        return String.format("Account[ID: %s, User: %s, Balance: %.2f, Status: %s, Borrowed: %d/%d]", 
            accountId.toString().substring(0, 8), 
            userId.toString().substring(0, 8),
            balance, status, currentBorrowedCount, maxBorrowLimit);
    }
}