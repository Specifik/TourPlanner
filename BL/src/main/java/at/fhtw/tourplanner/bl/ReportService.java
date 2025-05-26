package at.fhtw.tourplanner.bl;

import at.fhtw.tourplanner.dal.DAL;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportService {

    private static final Logger logger = LogManager.getLogger(ReportService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void generateTourReport(Tour tour, String outputPath) throws IOException {
        logger.info("Generating tour report for: {}", tour.getName());

        try (PdfWriter writer = new PdfWriter(outputPath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Title
            document.add(new Paragraph("Tour Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold());

            // Tour Details
            addTourDetails(document, tour);

            // Tour Logs
            addTourLogs(document, tour);

            logger.info("Tour report generated successfully: {}", outputPath);
        } catch (Exception e) {
            logger.error("Failed to generate tour report: {}", tour.getName(), e);
            throw new IOException("Failed to generate tour report", e);
        }
    }

    public void generateSummaryReport(String outputPath) throws IOException {
        logger.info("Generating summary report");

        try (PdfWriter writer = new PdfWriter(outputPath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Title
            document.add(new Paragraph("Tour Summary Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold());

            document.add(new Paragraph("\n"));

            // Get all tours
            List<Tour> tours = DAL.getInstance().tourDao().getAll();

            if (tours.isEmpty()) {
                document.add(new Paragraph("No tours available for summary."));
                return;
            }

            // Create summary table
            Table table = new Table(5);
            table.setWidth(UnitValue.createPercentValue(100));

            // Headers
            table.addHeaderCell("Tour Name");
            table.addHeaderCell("Logs Count");
            table.addHeaderCell("Avg Distance (km)");
            table.addHeaderCell("Avg Time (min)");
            table.addHeaderCell("Avg Rating");

            // Add data for each tour
            for (Tour tour : tours) {
                List<TourLog> logs = DAL.getInstance().tourLogDao().getByTourId(tour.getId());

                table.addCell(tour.getName());
                table.addCell(String.valueOf(logs.size()));

                if (logs.isEmpty()) {
                    table.addCell("No data");
                    table.addCell("No data");
                    table.addCell("No data");
                } else {
                    double avgDistance = logs.stream()
                            .mapToDouble(TourLog::getTotalDistance)
                            .average().orElse(0.0);

                    double avgTime = logs.stream()
                            .mapToInt(TourLog::getTotalTime)
                            .average().orElse(0.0);

                    double avgRating = logs.stream()
                            .mapToInt(TourLog::getRating)
                            .average().orElse(0.0);

                    table.addCell(String.format("%.1f", avgDistance));
                    table.addCell(String.format("%.0f", avgTime));
                    table.addCell(String.format("%.1f", avgRating));
                }
            }

            document.add(table);

            logger.info("Summary report generated successfully: {}", outputPath);
        } catch (Exception e) {
            logger.error("Failed to generate summary report", e);
            throw new IOException("Failed to generate summary report", e);
        }
    }

    private void addTourDetails(Document document, Tour tour) {
        document.add(new Paragraph("Tour Details").setFontSize(16).setBold());

        document.add(new Paragraph("Name: " + tour.getName()));
        document.add(new Paragraph("From: " + (tour.getFrom() != null ? tour.getFrom() : "N/A")));
        document.add(new Paragraph("To: " + (tour.getTo() != null ? tour.getTo() : "N/A")));
        document.add(new Paragraph("Transport Type: " + (tour.getTransportType() != null ? tour.getTransportType() : "N/A")));
        document.add(new Paragraph("Description: " + (tour.getDescription() != null ? tour.getDescription() : "N/A")));

        if (tour.getTourDistance() > 0) {
            document.add(new Paragraph("Distance: " + String.format("%.1f km", tour.getTourDistance())));
            document.add(new Paragraph("Estimated Time: " + tour.getEstimatedTime() + " minutes"));
        }

        document.add(new Paragraph("\n"));
    }

    private void addTourLogs(Document document, Tour tour) {
        List<TourLog> logs = DAL.getInstance().tourLogDao().getByTourId(tour.getId());

        document.add(new Paragraph("Tour Logs (" + logs.size() + " entries)")
                .setFontSize(16).setBold());

        if (logs.isEmpty()) {
            document.add(new Paragraph("No tour logs available."));
            return;
        }

        // Create table for logs
        Table table = new Table(6); // 6 columns
        table.setWidth(UnitValue.createPercentValue(100));

        // Headers
        table.addHeaderCell("Date");
        table.addHeaderCell("Difficulty");
        table.addHeaderCell("Distance (km)");
        table.addHeaderCell("Time (min)");
        table.addHeaderCell("Rating");
        table.addHeaderCell("Comment");

        // Add log data
        for (TourLog log : logs) {
            table.addCell(log.getDateTime().toLocalDate().format(DATE_FORMATTER));
            table.addCell(log.getDifficulty() != null ? log.getDifficulty() : "");
            table.addCell(String.format("%.1f", log.getTotalDistance()));
            table.addCell(String.valueOf(log.getTotalTime()));
            table.addCell(String.valueOf(log.getRating()));

            String comment = log.getComment() != null ? log.getComment() : "";
            if (comment.length() > 50) {
                comment = comment.substring(0, 47) + "...";
            }
            table.addCell(comment);
        }

        document.add(table);
    }
}