CREATE TABLE IF NOT EXISTS tb_item_do_pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quantidade INT NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    pedido_id BIGINT NOT NULL,
    CONSTRAINT pk_tb_item_do_pedido
        PRIMARY KEY (id),
    CONSTRAINT fk_tb_item_do_pedido_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES tb_pedido(id)
    );