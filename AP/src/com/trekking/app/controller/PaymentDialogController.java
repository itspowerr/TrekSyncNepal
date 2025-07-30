package com.trekking.app.controller;

import com.trekking.app.model.Booking;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PaymentDialogController {

    @FXML private Label titleLabel;
    @FXML private Label amountLabel;
    @FXML private Button cancelButton;
    @FXML private Button payButton;

    private Stage dialogStage;
    private boolean paymentSuccessful = false;

    @FXML
    private void initialize() {
        payButton.setOnAction(event -> handlePay());
        cancelButton.setOnAction(event -> handleCancel());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setBooking(Booking booking) {
        titleLabel.setText("Payment for Booking: " + booking.getBookingId());
        amountLabel.setText(String.format("$%.2f", booking.getTotalCost()));
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    private void handlePay() {
        paymentSuccessful = true;
        dialogStage.close();
    }

    private void handleCancel() {
        dialogStage.close();
    }
}