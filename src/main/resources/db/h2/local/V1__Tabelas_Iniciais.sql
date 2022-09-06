CREATE TABLE pauta(
    id UUID NOT NULL DEFAULT RANDOM_UUID() PRIMARY KEY,
    nome VARCHAR(64) NOT NULL,
    CONSTRAINT uq_pauta UNIQUE(nome)
);

CREATE TABLE voto(
    id UUID NOT NULL DEFAULT RANDOM_UUID() PRIMARY KEY,
    resposta_usuario CHAR(3) NOT NULL,
    usuario CHAR(11) NOT NULL,
    pauta_id UUID NOT NULL,
    CONSTRAINT fk_voto_pauta FOREIGN KEY(pauta_id) REFERENCES pauta(id),
    CONSTRAINT uq_voto UNIQUE(usuario, pauta_id)
);

CREATE TABLE sessao(
    pauta_id UUID NOT NULL PRIMARY KEY,
    duracao BIGINT NOT NULL DEFAULT 1,
    qtd_votos BIGINT NULL,
    votos_validos BIGINT NULL,
    resultado VARCHAR(24) NULL,
    finalizada TINYINT NULL,
    CONSTRAINT fk_sessao_pauta FOREIGN KEY(pauta_id) REFERENCES pauta(id)
);