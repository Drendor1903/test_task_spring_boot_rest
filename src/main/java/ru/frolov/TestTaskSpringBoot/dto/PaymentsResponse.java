package ru.frolov.TestTaskSpringBoot.dto;


import java.util.List;

public class PaymentsResponse {
    private List<PaymentDTO> paymentDTO;

    public PaymentsResponse(List<PaymentDTO> paymentDTO){
        this.paymentDTO = paymentDTO;
    }

    public List<PaymentDTO> getPaymentDTO() {
        return paymentDTO;
    }

    public void setPaymentDTO(List<PaymentDTO> paymentDTO) {
        this.paymentDTO = paymentDTO;
    }
}
