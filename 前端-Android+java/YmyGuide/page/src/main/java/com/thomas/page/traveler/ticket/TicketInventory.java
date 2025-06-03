package com.thomas.page.traveler.ticket;

public class TicketInventory {
    private String ticketName;
    private String ticketType;
    private int currentStock;

    // Getter & Setter
    public String getTicketName() { return ticketName; }
    public void setTicketName(String ticketName) { this.ticketName = ticketName; }

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }

    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
}

