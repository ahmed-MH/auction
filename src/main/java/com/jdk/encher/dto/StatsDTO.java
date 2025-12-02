package com.jdk.encher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {
    private long totalUsers;
    private long totalProducts;
    private double totalRevenue;
    private int creditsPurchased;
}