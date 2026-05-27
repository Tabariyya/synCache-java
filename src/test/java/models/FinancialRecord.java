package models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FinancialRecord {

    public enum Status { PENDING, SETTLED, FAILED, REFUNDED }

    private Long id;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigInteger transactionRef;
    private Status status;
    private String currency;
    private List<BigDecimal> lineItems;
    private Map<String, BigDecimal> breakdown;

    public FinancialRecord() {}

    public FinancialRecord(Long id, BigDecimal amount, BigDecimal tax,
                           BigInteger transactionRef, Status status, String currency,
                           List<BigDecimal> lineItems, Map<String, BigDecimal> breakdown) {
        this.id = id;
        this.amount = amount;
        this.tax = tax;
        this.transactionRef = transactionRef;
        this.status = status;
        this.currency = currency;
        this.lineItems = lineItems;
        this.breakdown = breakdown;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigInteger getTransactionRef() { return transactionRef; }
    public void setTransactionRef(BigInteger transactionRef) { this.transactionRef = transactionRef; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public List<BigDecimal> getLineItems() { return lineItems; }
    public void setLineItems(List<BigDecimal> lineItems) { this.lineItems = lineItems; }

    public Map<String, BigDecimal> getBreakdown() { return breakdown; }
    public void setBreakdown(Map<String, BigDecimal> breakdown) { this.breakdown = breakdown; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinancialRecord that = (FinancialRecord) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(tax, that.tax) &&
                Objects.equals(transactionRef, that.transactionRef) &&
                status == that.status &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(lineItems, that.lineItems) &&
                Objects.equals(breakdown, that.breakdown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, tax, transactionRef, status, currency, lineItems, breakdown);
    }
}
