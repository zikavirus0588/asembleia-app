-- adicionar função pra gerar uuid
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS public.pauta (
	id uuid not null default uuid_generate_v4(),
	nome varchar(128) not null,
	CONSTRAINT uq_pauta UNIQUE (nome),
	CONSTRAINT pk_pauta PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS public.voto (
	id uuid not null default uuid_generate_v4(),
	resposta_usuario bpchar(3) not null,
	usuario char(11) not null,
	pauta_id uuid not null,
	CONSTRAINT pk_voto PRIMARY KEY (id),
	CONSTRAINT uq_voto UNIQUE (usuario, pauta_id),
	CONSTRAINT fk_voto_pauta FOREIGN KEY (pauta_id) REFERENCES public.pauta(id)
);

CREATE TABLE IF NOT EXISTS public.sessao (
	pauta_id uuid not null,
	duracao int not null default 1,
	qtd_votos bigint null,
	votos_validos bigint null,
	resultado varchar(24) null,
	finalizada boolean null,
	constraint pk_sessao PRIMARY KEY (pauta_id),
	constraint fk_sessao_pauta foreign key (pauta_id) references public.pauta(id)
);