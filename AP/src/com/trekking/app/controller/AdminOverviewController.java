package com.trekking.app.controller;

import com.trekking.app.model.Booking;
import com.trekking.app.model.data.BookingManager;
import com.trekking.app.model.data.DashboardStatsService;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminOverviewController {

    @FXML private Label touristCountLabel;
    @FXML private Label guideCountLabel;
    @FXML private Label attractionCountLabel;
    @FXML private Label adminCountLabel;
    @FXML private ToggleButton last7DaysButton;
    @FXML private ToggleButton last30DaysButton;
    @FXML private ToggleGroup periodToggleGroup;
    @FXML private Label totalRevenueLabel;
    @FXML private Label paidBookingsLabel;
    @FXML private Label avgBookingValueLabel;
    @FXML private AreaChart<String, Number> revenueChart;

    private DashboardStatsService statsService;
    private BookingManager bookingManager;
    private List<Booking> allPaidBookings;

    @FXML
    public void initialize() {
        statsService = new DashboardStatsService();
        bookingManager = new BookingManager();

        periodToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                updateFinancialStats();
            } else {
                if (oldToggle != null) {
                    oldToggle.setSelected(true);
                }
            }
        });

        refreshData();
    }

    public void refreshData() {
        touristCountLabel.setText(String.valueOf(statsService.getTouristCount()));
        guideCountLabel.setText(String.valueOf(statsService.getGuideCount()));
        attractionCountLabel.setText(String.valueOf(statsService.getAttractionCount()));
        adminCountLabel.setText(String.valueOf(statsService.getAdminCount()));

        loadFinancialData();
        updateFinancialStats();
    }

    private void loadFinancialData() {
        allPaidBookings = bookingManager.loadBookings().stream()
                .filter(b -> "Paid".equalsIgnoreCase(b.getPaymentStatus()))
                .collect(Collectors.toList());
    }

    private void updateFinancialStats() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = (last30DaysButton != null && last30DaysButton.isSelected()) ? today.minusDays(30) : today.minusDays(7);

        List<Booking> periodBookings = allPaidBookings.stream()
                .filter(b -> !b.getStartDate().isBefore(startDate) && !b.getStartDate().isAfter(today))
                .collect(Collectors.toList());

        double totalRevenueAllTime = allPaidBookings.stream().mapToDouble(Booking::getTotalCost).sum();
        double periodRevenue = periodBookings.stream().mapToDouble(Booking::getTotalCost).sum();
        int paidBookingsInPeriod = periodBookings.size();
        double avgBookingValue = (paidBookingsInPeriod == 0) ? 0 : periodRevenue / paidBookingsInPeriod;

        totalRevenueLabel.setText(String.format("$%,.2f", totalRevenueAllTime));
        paidBookingsLabel.setText(String.valueOf(paidBookingsInPeriod));
        avgBookingValueLabel.setText(String.format("$%,.2f", avgBookingValue));

        populateRevenueChart(startDate, today);
    }

    private void populateRevenueChart(LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> revenueByDay = allPaidBookings.stream()
                .filter(b -> !b.getStartDate().isBefore(startDate) && !b.getStartDate().isAfter(endDate))
                .collect(Collectors.groupingBy(
                        Booking::getStartDate,
                        Collectors.summingDouble(Booking::getTotalCost)
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Revenue");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            double revenue = revenueByDay.getOrDefault(date, 0.0);
            series.getData().add(new XYChart.Data<>(date.format(formatter), revenue));
        }

        revenueChart.getData().clear();
        revenueChart.getData().add(series);
    }
}