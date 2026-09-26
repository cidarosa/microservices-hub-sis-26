CREATE TABLE IF NOT EXISTS tb_pagamento(
    id BIGINT NOT NULL AUTO_INCREMENT,
    valor DECIMAL(10, 2) NOT NULL,
    nome VARCHAR(50) NOT NULL,
    numero_cartao VARCHAR(16) NOT NULL,
    validade VARCHAR(5) NOT NULL,
    codigo_seguranca VARCHAR(3) NOT NULL,
    status VARCHAR(35) NOT NULL,
    pedido_id BIGINT NOT NULL,
    CONSTRAINT pk_tb_pabamento PRIMARY KEY(id)
);