package ru.frolov.TestTaskSpringBoot.dto;

import java.time.LocalDateTime;

public class PaymentDTO {
    private int id;

    private LocalDateTime date;

    private String recipientPhoneNumber;

    private double value;

    public PaymentDTO() {}

    public PaymentDTO(String number, double v, LocalDateTime now) {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getRecipientPhoneNumber() {
        return recipientPhoneNumber;
    }

    public void setRecipientPhoneNumber(String recipientPhoneNumber) {
        this.recipientPhoneNumber = recipientPhoneNumber;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
