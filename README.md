# Desafio programação - para vaga desenvolvedor Mobile
***Realizei o teste, achei bem simples, porém o uso das ferramentas do Firebase e o uso da Api de mapas trazem um esforço extra para o desafio que não acho necessário, o uso destas ferramentas é bem simples, porém o setup inicial é chato e fica aqui o feedback pois o tempo gasto com essas ferramentas poderia ser aplicado em pontos mais importantes de serem avaliados, como conceitos de programação orientada a objetos, programação funcional, boas práticas e arquitetura de software mobile.
O teste ser genérico (me parece pela descrição que foi pensado para React Native) não me agradou também, poderia ser mais focado, no caso aqui, em Android nativo.
Abaixo vou comentar o que foi pedido no desafio:***

# Descrição do projeto

1. Tela de login usando (email e senha);

    ***Implementado com sucesso***

2. Tela home com mapa renderizando um ponto na localização atual do device;

    ***Implementado com sucesso. Criei uma activity que exibe uma view do Google Maps, para testar minha implementação vocês vão precisar de uma API KEY***

3. Realizar o login utilizando Firebase Auth;

    ***Implementado com sucesso***

4. Armazenar os dados do usuário na store global;

    ***Não sei do que se trata, imagino ser algo de React. Para não deixar em branco, salvei os dados usando SharedPreferences***

5. Rastrear login com sucesso e renderização com sucesso com Analytics (enviar um evento com dados considerados primordiais nesses dois casos);

    ***Logs do Analytics que implementei: login, cadastro do usuário e renderização do mapa***

6. Rastrear os erros e envia-los ao Crashlytics;

    ***Crashlytics está funcionando ao enviar as falhas do app, além disso implementei outros logs: erro de login, erro de cadastro do usuário e erro ao buscar/atualizar localização***

7. Armazenar na base de dados local (preferência por WatermelonDB, mas pode usar outro banco de dados) o usuário logado e sua última posição no mapa;

    ***Salvei o usuário logado e a última posição do mapa no banco de dados local com Room***

8. Testar fluxo de login (unit e e2e);

    ***Criei testes para o fluxo de login, um teste para login com sucesso e outro para login com erro***

9. Testar fluxo da home (unit e e2e).

    ***Não criei testes para isso, pois nao me parece fazer sentido, ao invés, criei testes para o cadastro de usuário, um teste para cadastro com sucesso e outro para cadastro com erro***

# Avaliação

Seu projeto será avaliado de acordo com os seguintes critérios.

1. Sua aplicação preenche os requerimentos básicos?

    ***Sim***

2. Você documentou a maneira de configurar o ambiente e rodar sua aplicação?

    ***O App está pronto para ser executado com Android Studio, acredito que a única coisa extra necessária para rodar a aplicação será adicionar a API KEY do google maps no seu arquivo "local.properties"***

3. Você seguiu as instruções de envio do desafio?

    ***Sim***

4. Qualidade e cobertura dos testes unitários.

    ***Como eu disse anteriormente, criei testes para o fluxo de login e de cadastro, os testes estão excelentes, porém não são testes de unidade, mas sim de interface. Testes unitários não são necessários na minha implementação***
