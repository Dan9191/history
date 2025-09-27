package dan.competition.history.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dan.competition.history.entity.MedicalDataBatch;

import java.io.ByteArrayOutputStream;

public class PDFCreator {

    public static byte[] createPDF(MedicalDataBatch batch) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            document.add(new Phrase("Medical Data Batch: " + batch.getName() + "\n\n", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2, 2});
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            PdfPCell cell;

            cell = new PdfPCell(new Phrase("Time (sec)", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Uterus", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("BPM", headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
            for (var data : batch.getMedicalDataList()) {
                table.addCell(new Phrase(String.format("%.6f", data.getTimeSec()), cellFont));
                table.addCell(new Phrase(data.getUterus() != 0.0 ? String.format("%.6f", data.getUterus()) : "N/A", cellFont));
                table.addCell(new Phrase(data.getBpm() != 0.0 ? String.format("%.6f", data.getBpm()) : "N/A", cellFont));
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return baos.toByteArray();
    }
}
