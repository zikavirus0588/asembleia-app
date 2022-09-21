# <span style="color: orange">Assembleia-app</span>
Conjunto de API's para simular uma sessão de votos numa assembléia. 

## <span style="color: orange">Pré-Requisitos</span>

- Docker & Docker-compose instalados
- Postman Version 9.30.4

## <span style="color: orange">Subindo a aplicação no container</span>

Na raiz do projeto, executar o seguinte comando:

**docker-compose up -d**

Em seguida, verificar se os containers estão de pé:

**docker ps**

Verificar os logs da aplicação:

**docker logs assembleia-app**

Parar a execução do container:

**docker-compose down**

A documentação das API's poderá ser vista localmente através do link [assembleia-app](http://localhost:8080/assembleia/api/swagger-ui/)

## <span style="color: orange">Testando a aplicação com o Postman</span>

Através da collection [assembleia-app-collection](/collections/Aseembleia-app.postman_collection.json) é possível simular
uma sessão de votação, seguindo as seguintes etapas:

#### <span style="color: red">Importando a collection para o postman</span>

**File --> import --> upload file --> path/to/assembleia-app-collection.json**

![importando_collection](collections/images/import_collection.png)

#### <span style="color: red">Editando as collections variables</span>

**Collections --> Assembleia-app --> botão direito em cima da collection --> Edit**

| VARIABLE       | INITIAL VALUE |          CURRENT VALUE |
|----------------|:-------------:|-----------------------:|
| nome-pauta     |               |     ex: **minhaPauta** |
| duracao-sessao |               | ex: **1** (em minutos) |


## <span style="color: orange">Executando a collection</span>

Definidos os valores das variáveis **nome-pauta** e **duracao-sessao**, executar os seguintes passos:

#### <span style="color: red">Importar a collection para o runner</span>

**File --> New Runner Tab**

![runner_collection_1](collections/images/runner_collection_1.png)

Após a nova aba do runner ser aberta, arrastar a collection **Assembleia-app** para dentro do runner e selecionar
as ***três primeiras requisições*** para serem executadas e executar a collection clicando em **Run Assembleia-app**

#### <span style="color: red">Importar os votos dos usuários</span>

Fazer o download do arquivo [votos-teste](collections/arquivos/votos.json) e em seguida importar para a collection:

**Run Settings --> Data --> Select File --> path/to/votos.json**

![runner_collection_2](collections/images/runner_collection_2.png)

Selecionar a requisição **votar**, setar o número de iterações para **50**, o delay para **200ms** e em seguida
executar novamente a collection clicando em **Run Assembleia-app**

#### <span style="color: red">Visualizando o resultado da sessão</span>

O resultado da sessão pode ser visto através da requisição **Busca detalhes da sessão**

![runner_collection_3](collections/images/runner_collection_3.png)