package com.trekking.app.model.data;

import com.trekking.app.model.Booking;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportService {

    /**
     * Your original generic method for exporting any TableView.
     * This will continue to be used for non-financial reports.
     */
    public void exportTableViewToCsv(TableView<?> tableView, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // Write Header Row
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                writer.append(escapeCsvText(tableView.getColumns().get(i).getText()));
                if (i < tableView.getColumns().size() - 1) {
                    writer.append(',');
                }
            }
            writer.append('\n');

            // Write Data Rows
            for (int i = 0; i < tableView.getItems().size(); i++) {
                for (int j = 0; j < tableView.getColumns().size(); j++) {
                    TableColumn<?, ?> col = tableView.getColumns().get(j);
                    String data = col.getCellData(i) != null ? col.getCellData(i).toString() : "";
                    writer.append(escapeCsvText(data));
                    if (j < tableView.getColumns().size() - 1) {
                        writer.append(',');
                    }
                }
                writer.append('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * NEW: A specific method for the financial report that calculates and appends a total revenue row.
     */
    public void exportFinancialReportToCsv(TableView<Booking> tableView, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // 1. Calculate the total revenue from the table's items
            double totalRevenue = tableView.getItems().stream()
                    .mapToDouble(Booking::getTotalCost)
                    .sum();

            // 2. Write the header row
            String header = "Booking ID,Tourist Name,Attraction,Start Date,Revenue ($)";
            writer.append(header).append("\n");

            // 3. Write the data rows
            for (Booking booking : tableView.getItems()) {
                writer.append(escapeCsvText(booking.getBookingId())).append(",");
                writer.append(escapeCsvText(booking.getTouristName())).append(",");
                writer.append(escapeCsvText(booking.getAttractionName())).append(",");
                writer.append(escapeCsvText(booking.getStartDate().toString())).append(",");
                writer.append(String.format("%.2f", booking.getTotalCost())).append("\n");
            }

            // 4. Write the total row at the end
            writer.append("\n"); // Add a blank line for separation
            writer.append(",,,Total Revenue:,").append(String.format("%.2f", totalRevenue)).append("\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to handle commas and quotes in CSV data
    private String escapeCsvText(String text) {
        String escapedText = text.replaceAll("\"", "\"\"");
        if (escapedText.contains(",") || escapedText.contains("\"") || escapedText.contains("\n")) {
            escapedText = "\"" + escapedText + "\"";
        }
        return escapedText;
    }
}