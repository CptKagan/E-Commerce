package com.cptkagan.ecommerce.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.springframework.stereotype.Service;

import com.cptkagan.ecommerce.models.Order;
import com.cptkagan.ecommerce.models.OrderItem;

// https://pdfbox.apache.org/docs/2.0.2/javadocs/org/apache/pdfbox/pdmodel/PDDocument.html
// Using PDFBox instead iText, iText has vulnerabilities.


@Service
public class ReceiptService {
    private static final String INVOICE_DIR = "C:\\Users\\mkaga\\Videos\\Invoices"; // Where invoices will be stored

    public String generateInvoice(Order order){
        String filePath = INVOICE_DIR + "\\invoice_" + order.getId() + ".pdf";

        try{
            PDDocument document = new PDDocument(); // Blank PDF
            PDPage page = new PDPage(); // Blank Page
            document.addPage(page); // Add page to document

            PDPageContentStream contentStream = new PDPageContentStream(document, page); // Open the document
            PDFont headerFont = new PDType1Font(FontName.HELVETICA_BOLD); // Header Font Selection
            PDFont normalFont = new PDType1Font(FontName.HELVETICA); // Normal Text Font Selection

            contentStream.setFont(headerFont, 20);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Invoice - Order #" + order.getId()); // Writes Incoive - Order #" + order.getId() at position (100,700)
            contentStream.endText();

            // Display buyer details
            contentStream.setFont(normalFont, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 670);
            contentStream.showText("Buyer: " + order.getBuyer().getFirstName() + " " + order.getBuyer().getLastName());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Email: " + order.getBuyer().getEmail());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Address: " + order.getAddress());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Order Date: " + order.getOrderDate());
            contentStream.endText();

            // Product table header , Not good, just to test
            contentStream.setFont(headerFont, 14);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 600);
            contentStream.showText("Product | Seller | Qty | Unit Price | Total");
            contentStream.endText();

            contentStream.moveTo(100, 595);
            contentStream.lineTo(500, 595);
            contentStream.stroke();

            float y = 575;
            double totalOrderPrice = 0;
            for(OrderItem item : order.getOrderItems()){
                contentStream.beginText();
                contentStream.newLineAtOffset(100, y);
                contentStream.showText(item.getProduct().getName() + " | " + 
                                       item.getProduct().getSeller().getCompanyName() + " | " + 
                                       item.getQuantity() + " | " + 
                                       item.getProduct().getPrice() + "$ | " + 
                                       (item.getQuantity() * item.getProduct().getPrice()) + "$");
                contentStream.endText();
                y -= 20;
                totalOrderPrice += item.getQuantity() * item.getProduct().getPrice();
            }

            contentStream.setFont(headerFont, 14);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, y-20);
            contentStream.showText("Total Order Price: $" + totalOrderPrice);
            contentStream.endText();

            // Footer
            contentStream.beginText();
            contentStream.newLineAtOffset(100, y-60);
            contentStream.showText("Thank you for your purchase!");
            contentStream.endText();

            contentStream.close();
            document.save(filePath);
            document.close();

            return filePath;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
}
