package com.cptkagan.ecommerce.services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.cptkagan.ecommerce.models.Order;
import com.cptkagan.ecommerce.models.OrderItem;

import org.springframework.stereotype.Service;

@Service
public class ReceiptService {
    private static final String INVOICE_DIR = "C:\\Users\\mkaga\\Videos\\Invoices\\";
    private static final String NORMAL_FONT_PATH = "C:\\Users\\mkaga\\Videos\\Invoices\\DejaVuSans.ttf"; // Unicode Font
    private static final String BOLD_FONT_PATH = "C:\\Users\\mkaga\\Videos\\Invoices\\DejaVuSans-Bold.ttf";
    private static final String ITALIC_FONT_PATH = "C:\\Users\\mkaga\\Videos\\Invoices\\DejaVuSans-Oblique.ttf";

    public String generateInvoice(Order order) {
        String filePath = INVOICE_DIR + "invoice_" + order.getId() + ".pdf";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDate = order.getOrderDate().format(formatter);

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Font
            PdfFont normalFont = PdfFontFactory.createFont(NORMAL_FONT_PATH, "Identity-H");
            PdfFont boldFont = PdfFontFactory.createFont(BOLD_FONT_PATH, "Identity-H");
            PdfFont italicFont = PdfFontFactory.createFont(ITALIC_FONT_PATH, "Identity-H");

            // Logo
            String logoPath = "C:\\Users\\mkaga\\Videos\\Invoices\\logo.png";
            ImageData imageData = ImageDataFactory.create(logoPath);
            Image logo = new Image(imageData);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(logo);

            // Header
            Paragraph header = new Paragraph("Invoice - Order: #" + order.getId())
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(header);

            // Buyer Details
            float fontSize = 7;
            Paragraph buyerInfo = new Paragraph()
                    .add(new Text("Sipariş Veren Kişi    : ")).add(order.getBuyer().getFirstName() + " " + order.getBuyer().getLastName() + "\n")
                    .add(new Text("Adres                       : ")).add(order.getAddress() + "\n")
                    .add(new Text("Sipariş Tarihi           : ")).add(formattedDate + "\n")
                    .add(new Text("E-postası                 : ")).add(order.getBuyer().getEmail())
                    .setFont(normalFont)
                    .setFontSize(fontSize)
                    .setMarginBottom(20);
                    
            document.add(buyerInfo);

            // Product Table
            float[] columnWidths = {2,2,1,1,1};
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
            table.setMarginLeft(10);
            table.setMarginRight(10);
            table.setMarginBottom(20);

            table.addHeaderCell(new Cell().add(new Paragraph("Product Name").setFont(boldFont).setFontSize(8).setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Company Name").setFont(boldFont).setFontSize(8).setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setFont(boldFont).setFontSize(8).setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Unit Price").setFont(boldFont).setFontSize(8).setTextAlignment(TextAlignment.CENTER)));
            table.addHeaderCell(new Cell().add(new Paragraph("Total").setFont(boldFont).setFontSize(8).setTextAlignment(TextAlignment.CENTER)));

            for(OrderItem item : order.getOrderItems()){
                table.addCell(new Cell().add(new Paragraph(item.getProduct().getName()).setFont(normalFont).setFontSize(7).setTextAlignment(TextAlignment.CENTER)));
                table.addCell(new Cell().add(new Paragraph(item.getProduct().getSeller().getCompanyName()).setFont(normalFont).setFontSize(7).setTextAlignment(TextAlignment.CENTER)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFont(normalFont).setFontSize(7).setTextAlignment(TextAlignment.CENTER)));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", item.getProduct().getPrice()) + "$").setFont(normalFont).setFontSize(7).setTextAlignment(TextAlignment.CENTER)));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", item.getQuantity() * item.getProduct().getPrice()) + "$").setFont(normalFont).setFontSize(7).setTextAlignment(TextAlignment.CENTER)));
            }

            document.add(table);

            // Total Price
            double totalPrice = 0;
            for(OrderItem item : order.getOrderItems()){
                totalPrice += item.getQuantity() * item.getProduct().getPrice();
            }

            Paragraph totalPriceInfo = new Paragraph()
                .add(new Text("Total Price: ").setFont(boldFont).setFontSize(10))
                .add(new Text(String.format("%.2f", totalPrice) + "$").setFont(italicFont).setFontSize(10))
                .setMarginTop(15)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginRight(10);

            document.add(totalPriceInfo);

            // Footer
            Integer lastPageNumber = pdfDoc.getNumberOfPages();
            PdfPage lastPage = pdfDoc.getPage(lastPageNumber);
            PdfCanvas canvas = new PdfCanvas(lastPage);

            float footerY = 50;
            float pageWidth = lastPage.getPageSize().getWidth();
            float marginX = 40;

            canvas.setStrokeColor(ColorConstants.BLACK)
                  .setLineWidth(0.5f)
                  .moveTo(marginX, footerY+25)
                  .lineTo(pageWidth - marginX, footerY + 25)
                  .stroke();

            Paragraph footer = new Paragraph()
                .add("This project was developed only for educational purposes by Murat Kağan Kayabaşı.\n")
                .add("All data, content, and design are strictly for non-commercial use.")
                .setFont(normalFont)
                .setFontSize(7)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(marginX, footerY, pageWidth - (marginX * 2));

            document.add(footer);

            document.close();
            return filePath;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
