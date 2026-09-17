INSERT INTO tb_pedido(nome, cpf, data, status, valor_total) VALUES('Jon Snow', '12345678963', '2025-11-25', 'CRIADO', 790.0); -- 1
INSERT INTO tb_pedido(nome, cpf, data, status, valor_total) VALUES('JAyra Stark', '86245712365', '2026-01-25', 'CRIADO', 3599.0); -- 2

INSERT INTO tb_item_do_pedido(quantidade, descricao, preco_unitario, pedido_id) VALUES(2, 'Mouse sem fio Microsoft', 250.0, 1 );
INSERT INTO tb_item_do_pedido(quantidade, descricao, preco_unitario, pedido_id) VALUES(1, 'Teclado sem fio Microsoft', 290.0, 1 );
INSERT INTO tb_item_do_pedido(quantidade, descricao, preco_unitario, pedido_id) VALUES(1, 'Smart TV LG LED', 3599.0, 2 );