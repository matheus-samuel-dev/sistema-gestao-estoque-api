package com.matheus.estoque.product.entity;

public enum InventoryOrigin {
    COMPRA,
    VENDA,
    AJUSTE,
    TRANSFERENCIA,
    DEVOLUCAO,
    OUTRO,

    // Valores legados mantidos para leitura segura de registros antigos.
    PURCHASE,
    DONATION,
    TRANSFER,
    INTERNAL_PRODUCTION,
    STOCK_ADJUSTMENT,
    OTHER
}
