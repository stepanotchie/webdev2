package com.clinic.dto.response;

public class VaccineDTO {
    private Long id;
    private String name;
    private int stockDoses;

    public VaccineDTO() {
    }

    public VaccineDTO(Long id, String name, int stockDoses) {
        this.id = id;
        this.name = name;
        this.stockDoses = stockDoses;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getStockDoses() { return stockDoses; }
}
