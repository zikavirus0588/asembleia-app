# Assembleia-app
Conjunto de API's para simular uma sessão de votos numa assembléia. 

## Pré-Requisitos

- Docker & Docker-compose instalados
- Java 11
- Redis 6.2
- Postman Version 9.30.4
- definir uma variável de ambiente para armazenar o password do redis: ${ASSEMBLEIA_REDIS_PASSWORD}

## Preparando container do Redis

Caso sua máquina local não possua o Redis 6.2 instalado, é possível criar um container
utilizando o [docker-compose](./docker-compose.yaml). Será necessário executar os passos a seguir:

- Criar um diretório para armazenar localmente os dados. **(~/redis/data)**
- Criar um diretório para armazenar localmente o arquivo de configuração do redis. **(~/redis/config)**
- Criar um arquivo de configuração do redis (redis.conf) no diretório **~/redis/config**
- Atribuir o valor da variável ${ASSEMBLEIA_REDIS_PASSWORD} à propriedade de configuraão **requirepass**

## Subindo a aplicação localmente

Na raiz do projeto, executar os seguintes comandos:

#### subir o container do redis:
docker-compose up -d

#### executar a aplicação com o profile local:
./mvnw clean install spring-boot:run -Dspring-boot.run.profiles=local

Se tudo correr bem, será possivel ver a mensagem "aplicação está de pé" nos logs de inicialização.

A documentação das API's poderá ser vista localmente através do link [assembleia-app](http://localhost:8080/assembleia/api/swagger-ui/)

## Testando a aplicação com o Postman

Através da collection [assembleia-app-collection](/collections/Aseembleia-app.postman_collection.json) é possível simular
uma sessão de votação, seguindo as seguintes etapas:

#### Importando a collection para o postman

**File --> import --> upload file --> path/to/assembleia-app-collection.json**

![importando_collection](collections/images/import_collection.png)

### Editando as collections variables

Collections --> Assembleia-app --> botão direito em cima da collection --> Edit

| VARIABLE       | INITIAL VALUE |          CURRENT VALUE |
|----------------|:-------------:|-----------------------:|
| nome-pauta     |               |     ex: **minhaPauta** |
| duracao-sessao |               | ex: **1** (em minutos) |


### Executando a collection

Definidos os valores das variáveis **nome-pauta** e **duracao-sessao**, executar os seguintes passos:

File --> New Runner Tab

Após a nova aba do runner ser aberta, arrastar a collection **Assembleia-app** para dentro do runner e selecionar
as 3 primeiras requisições para serem executadas: 

![runner_collection_1](collections/images/runner_collection_1.png)

Executar a collection clicando em **Run Assembleia-app**

Em seguida, importar o arquivo [votos-teste](collections/arquivos/votos.json) para ser executado:

Run Settings --> Data --> Select File --> path/to/votos.json

Selecionar a requisição **votar** e detar o número de iterações para **50** e o delay para **200ms**:

![runner_collection_2](collections/images/runner_collection_2.png)

Executar novamente a collection clicando em **Run Assembleia-app**

O resultado da sessão pode ser visto através da requisição **Busca detalhes da sessão**

![runner_collection_3](collections/images/runner_collection_3.png)