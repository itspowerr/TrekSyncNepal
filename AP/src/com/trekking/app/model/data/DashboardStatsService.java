package com.trekking.app.model.data;

import com.trekking.app.model.Booking;
import com.trekking.app.model.Tourist;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardStatsService {

    private final TouristManager touristManager = new TouristManager();
    private final GuideManager guideManager = new GuideManager();
    private final AttractionManager attractionManager = new AttractionManager();
    private final AdminManager adminManager = new AdminManager();
    private final BookingManager bookingManager = new BookingManager();

    public int getTouristCount() { return touristManager.loadTourists().size(); }
    public int getGuideCount() { return guideManager.loadGuides().size(); }
    public int getAttractionCount() { return attractionManager.loadAttractions().size(); }
    public int getAdminCount() { return adminManager.loadAdmins().size(); }

    public long getDailyRegistrations() {
        List<Tourist> tourists = touristManager.loadTourists();
        LocalDate today = LocalDate.now();
        return tourists.stream()
                .filter(tourist -> tourist.getDateOfBirth() != null)
                .filter(tourist -> tourist.getDateOfBirth().isEqual(today))
                .count();
    }

    public ObservableList<PieChart.Data> getBookingStatusData() {
        List<Booking> bookings = bookingManager.loadBookings();
        Map<String, Long> statusCounts = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getStatus, Collectors.counting()));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // --- UPDATED to add data in a fixed order to match CSS ---
        long pending = statusCounts.getOrDefault("Pending", 0L);
        long confirmed = statusCounts.getOrDefault("Confirmed", 0L);
        long cancelled = statusCounts.getOrDefault("Cancelled", 0L);

        // Only add slices if they have a value greater than 0
        if (pending > 0) {
            pieChartData.add(new PieChart.Data("Pending (" + pending + ")", pending));
        }
        if (confirmed > 0) {
            pieChartData.add(new PieChart.Data("Confirmed (" + confirmed + ")", confirmed));
        }
        if (cancelled > 0) {
            pieChartData.add(new PieChart.Data("Cancelled (" + cancelled + ")", cancelled));
        }
        // -----------------------------------------------------------

        return pieChartData;
    }
}