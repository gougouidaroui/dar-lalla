package com.darlalla.service;

import com.darlalla.entity.Order;
import com.darlalla.entity.OrderItem;
import com.darlalla.entity.Product;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PdfService {

    private static final Color BURGUNDY = new Color(90, 0, 21);
    private static final Color GOLD = new Color(212, 164, 77);
    private static final Color CREAM = new Color(255, 248, 241);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);

    public byte[] generateReceipt(Order order) {
        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 28, Font.BOLD, BURGUNDY);
            Font subtitleFont = new Font(Font.HELVETICA, 10, Font.ITALIC, GOLD);
            Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD, BURGUNDY);
            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.BLACK);
            Font smallFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY);

            Paragraph logo = new Paragraph("D", new Font(Font.HELVETICA, 36, Font.BOLD, GOLD));
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            Paragraph brand = new Paragraph("DAR LALLA", new Font(Font.HELVETICA, 24, Font.BOLD, GOLD));
            brand.setAlignment(Element.ALIGN_CENTER);
            document.add(brand);

            Paragraph tagline = new Paragraph("ÉLÉGANCE MAROCAINE", new Font(Font.HELVETICA, 8, Font.NORMAL, GOLD));
            tagline.setAlignment(Element.ALIGN_CENTER);
            document.add(tagline);

            String dateStr = order.getDateCommande() != null ? order.getDateCommande().toString().replace("T", " ").substring(0, 16) : "-";
            Paragraph orderDate = new Paragraph("Commande #" + order.getId() + " | " + dateStr, new Font(Font.HELVETICA, 11, Font.BOLD, BURGUNDY));
            orderDate.setAlignment(Element.ALIGN_CENTER);
            orderDate.setSpacingAfter(5);
            document.add(orderDate);

            Paragraph status = new Paragraph("Statut: " + order.getStatus().toString(), new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE));
            status.setAlignment(Element.ALIGN_CENTER);
            PdfPCell statusCell = new PdfPCell(status);
            statusCell.setBackgroundColor(getStatusColor(order.getStatus().toString()));
            statusCell.setBorder(Rectangle.NO_BORDER);
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusCell.setPadding(8);

            PdfPTable statusTable = new PdfPTable(1);
            statusTable.setWidthPercentage(30);
            statusTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusTable.addCell(statusCell);
            document.add(statusTable);

            PdfPTable clientTable = new PdfPTable(2);
            clientTable.setWidthPercentage(100);
            clientTable.setWidths(new float[]{1, 1});
            clientTable.setSpacingAfter(15);

            PdfPCell clientInfo = new PdfPCell();
            clientInfo.setBorder(Rectangle.NO_BORDER);
            clientInfo.setBackgroundColor(CREAM);
            clientInfo.setPadding(15);
            clientInfo.setBorderWidth(0);

            Paragraph clientTitle = new Paragraph("INFORMATIONS CLIENT", sectionFont);
            clientTitle.setSpacingAfter(10);
            clientInfo.addElement(clientTitle);

            addClientLine(clientInfo, "Nom:", order.getUser().getNom(), boldFont, normalFont);
            addClientLine(clientInfo, "Email:", order.getUser().getEmail(), boldFont, normalFont);
            addClientLine(clientInfo, "Téléphone:", order.getTelephone(), boldFont, normalFont);

            PdfPCell deliveryInfo = new PdfPCell();
            deliveryInfo.setBorder(Rectangle.NO_BORDER);
            deliveryInfo.setBackgroundColor(CREAM);
            deliveryInfo.setPadding(15);

            Paragraph deliveryTitle = new Paragraph("ADRESSE DE LIVRAISON", sectionFont);
            deliveryTitle.setSpacingAfter(10);
            deliveryInfo.addElement(deliveryTitle);

            addClientLine(deliveryInfo, "Adresse:", order.getAdresseLivraison(), boldFont, normalFont);

            clientTable.addCell(clientInfo);
            clientTable.addCell(deliveryInfo);
            document.add(clientTable);

            if (order.getPaiement() != null && !order.getPaiement().isEmpty()) {
                PdfPTable paymentTable = new PdfPTable(2);
                paymentTable.setWidthPercentage(50);
                paymentTable.setSpacingAfter(15);

                addTableRow(paymentTable, "Mode de paiement:", order.getPaiement(), boldFont, normalFont);
                document.add(paymentTable);
            }

            PdfPTable productsTable = new PdfPTable(5);
            productsTable.setWidthPercentage(100);
            productsTable.setSpacingAfter(15);
            productsTable.setWidths(new float[]{1.5f, 2.5f, 1f, 1f, 1f});

            String[] headers = {"Image", "Produit", "Taille/Couleur", "Qté", "Prix"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
                headerCell.setBackgroundColor(BURGUNDY);
                headerCell.setPadding(12);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                productsTable.addCell(headerCell);
            }

            boolean alternate = false;
            for (OrderItem item : order.getItems()) {
                Color bgColor = alternate ? Color.WHITE : LIGHT_GRAY;
                alternate = !alternate;

                PdfPCell imageCell = new PdfPCell();
                imageCell.setBackgroundColor(bgColor);
                imageCell.setPadding(8);
                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);

try {
                        if (item.getProduct() != null && item.getProduct().getImage() != null) {
                            String imagePath = item.getProduct().getImage();
                            if (!imagePath.startsWith("http")) {
                                imagePath = "./uploads/" + imagePath;
                            }
                            java.io.File imgFile = new java.io.File(imagePath);
                            if (imgFile.exists()) {
                                Image img = Image.getInstance(imagePath);
                                img.scaleAbsolute(40f, 40f);
                                imageCell.addElement(img);
                            } else {
                                imageCell.addElement(new Paragraph("-", smallFont));
                            }
                        } else {
                            imageCell.addElement(new Paragraph("-", smallFont));
                        }
                    } catch (Exception e) {
                        imageCell.addElement(new Paragraph("-", smallFont));
                    }
                productsTable.addCell(imageCell);

                PdfPCell nameCell = new PdfPCell(new Phrase(item.getProduct().getNom(), normalFont));
                nameCell.setBackgroundColor(bgColor);
                nameCell.setPadding(10);
                productsTable.addCell(nameCell);

                String variant = "";
                if (item.getProduct().getTaille() != null && !item.getProduct().getTaille().isEmpty()) {
                    variant = item.getProduct().getTaille();
                }
                if (item.getProduct().getCouleur() != null && !item.getProduct().getCouleur().isEmpty()) {
                    variant += (variant.isEmpty() ? "" : " / ") + item.getProduct().getCouleur();
                }
                PdfPCell variantCell = new PdfPCell(new Phrase(variant.isEmpty() ? "-" : variant, smallFont));
                variantCell.setBackgroundColor(bgColor);
                variantCell.setPadding(10);
                productsTable.addCell(variantCell);

                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantite()), normalFont));
                qtyCell.setBackgroundColor(bgColor);
                qtyCell.setPadding(10);
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                productsTable.addCell(qtyCell);

                PdfPCell priceCell = new PdfPCell(new Phrase(String.format("%.2f MAD", item.getQuantite() * item.getPrixUnitaire()), boldFont));
                priceCell.setBackgroundColor(bgColor);
                priceCell.setPadding(10);
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                productsTable.addCell(priceCell);
            }

            document.add(productsTable);

            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(40);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setSpacingBefore(10);

            addTableRow(totalTable, "Sous-total:", String.format("%.2f MAD", order.getTotal()), boldFont, normalFont);
            addTableRow(totalTable, "Livraison:", "Offerte (≥100 MAD)", boldFont, new Font(Font.HELVETICA, 10, Font.BOLD, new Color(40, 167, 69)));

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL", new Font(Font.HELVETICA, 14, Font.BOLD, BURGUNDY)));
            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalLabelCell.setPadding(12);
            totalTable.addCell(totalLabelCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("%.2f MAD", order.getTotal()), new Font(Font.HELVETICA, 14, Font.BOLD, GOLD)));
            totalValueCell.setBorder(Rectangle.NO_BORDER);
            totalValueCell.setPadding(12);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(totalValueCell);

            document.add(totalTable);

            PdfPTable footerBar = new PdfPTable(1);
            footerBar.setWidthPercentage(100);
            footerBar.setSpacingBefore(40);

            PdfPCell barCell = new PdfPCell();
            barCell.setBackgroundColor(BURGUNDY);
            barCell.setFixedHeight(8);
            barCell.setBorder(Rectangle.NO_BORDER);
            footerBar.addCell(barCell);

            document.add(footerBar);

            Paragraph footer = new Paragraph("Merci pour votre confiance !\n\nDar Lalla - Élégance Marocaine\nwww.darlalla.com | contact@darlalla.com", new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(15);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }

        return out.toByteArray();
    }

    private void addClientLine(PdfPCell cell, String label, String value, Font labelFont, Font valueFont) {
        Paragraph p = new Paragraph();
        p.add(new Phrase(label + " ", labelFont));
        p.add(new Phrase(value != null ? value : "-", valueFont));
        p.setSpacingAfter(5);
        cell.addElement(p);
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "-", valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "CONFIRMEE": return new Color(23, 162, 184);
            case "EN_PREPARATION": return new Color(108, 117, 125);
            case "EXPEDIEE": return new Color(0, 123, 255);
            case "LIVREE": return new Color(40, 167, 69);
            case "ANNULEE": return new Color(220, 53, 69);
            default: return new Color(255, 193, 7);
        }
    }
}