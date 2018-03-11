# tracetool
Tracetool and simulator for Segment Buffers

<h1>Para instalar o Valgrind:</h1>

<p>1) Baixe a versão 3.13 do site do Valgrind (tar.gz) ou use o link direto: ftp://sourceware.org/pub/valgrind/valgrind-3.13.0.tar.bz</p>

<p>2) Descompacte o arquivo em uma pasta no seu usuário. Na pasta do código fonte execute:</p>

<ol>
    <li>mkdir /opt/valgrind/</li>
    <li>./configure --prefix=/opt/valgrind</li>
    <li>make</li>
    <li>sudo make install</li>
</ol>

<p>Se, por algum motivo, você precisar compilar o Valgrind novamente (uma nova versão?) execute os comandos abaixo antes de compilar novamente:</p>

<ol>
    <li>sudo rm -rf /opt/valgrind/</li>
    <li>make distclean</li>
</ol>
  
<h1>Clone o projeto do github:</h1>

<p>Em uma pasta no seu usuário execute:</p>

<ol>
    <li>git clone https://github.com/lauripaulo/tracetool</li>
</ol>

<h1>Instale o Java (da distro, openJDK), Python 3 e o Eclipse (do site oficial):</h1>

<p>Para instalar o java (openJDK):</p>

<ol>
    <li>sudo apt-get update</li>
    <li>sudo apt-get install openjdk-8-jdk python3</li>
</ol>

<p>
Para instalar o Eclipse:
</p>

<p>A maneira mais simples é utilizar o Eclipse instaler em: http://www.eclipse.org/downloads/eclipse-packages/</p>

<p>Outra maneira é puxar o pacote Eclipse IDE for Java Developers na mesma página. Nesse caso você precisa descompactar o arquivo em uma pasta do seu usuário (~/apps/eclipse) e criar um link no desktop para o arquivo (~/apps/eclipse/eclipse)</p>

<h1>Compilar o projeto Java/Maven e configurar o ambiente:</h1>

<p>No eclipse vá em "File", "Import", "Existing Maven projects...", vá onde está o projeto do github e encontre a pasta "java". Nela você irá encontrar o "pom.xml" do Maven. Crie o projeto.</p>

<p>Com o projeto criado clique com o botão direito no arquivo "pom.xml", escolha "Run as...", "Maven build..." (a opção com os três pontinhos...)</p>

<p>Na janela que abrir coloque em "name" o texto "Tracetool build" e em "goal" cloque "compile test install". Clique em "apply" e depois em "ok".</p>

<p>O Maven vai fazer as coisas do Maven. Quando terminar você deve ver uma mensagem assim no console do Eclipse: "BUILD SUCCESS"</p>

<p>Nesse ponto você tem o jar com o necessário para coletar os logs e executar a simulação no diretório "java/target". O nome do jar é "tracetool-all-0.9.2-jar-with-dependencies.jar". Gaurde esse caminho absoluto, ele será utilizado para alterar alguns shell scripts (TODO: definir a pasta do jar como variável de ambiente).<p>

<p>Vá na pasta "shell" e alterer os arquivos "tracetoolsimulator.sh", "tracetoolskip.sh" e "tracetool.sh" para que a variável JAR_FOLDER aponte para o seu "tracetool-all-0.9.2-jar-with-dependencies.jar".</p>

<p>Ainda em "shell" e altere o arquivo "pmap-observer.sh" para que a linha de comando aponte para o caminho absoluto do arquivo "python/pmap-observer.py".</p>

<p>E, finalmente, execute um "chmod +x install.sh" e execute. Ele irá apenas copiar os .sh para sua pasta de usuário e torna-los executáveis.</p>

<p>Agora o ambiente está configurado e pronto.</p>

<h1>Teste de coleta de logs:</h1>

<p>Abra um terminal, crie uma pasta de trabalho e execute o tracetool</p>

<ol>
    <li>cd ~</li>
    <li>mkdir test</li>
    <li>cd test</li>
    <li>~/tracetool.sh</li>
</ol>

<p>Abra outro terminal e execute o script python para monitorar o mapa de memória do Valgrind Lackey</p>

<ol>
    <li>cd ~</li>
    <li>cd test</li>
    <li>~/pmap-observer.sh lackey pmap.txt</li>
</ol>

<p>Abra outro terminal (sim você vai ter três terminais abertos) e execute o script com o aplicativo que você deseja coletar os traços utilizando o Valgrind. No exemplo vamos executar o "lsblk".</p>

<ol>
    <li>cd ~</li>
    <li>cd test</li>
    <li>~/valgrindexec.sh lsblk</li>
</ol>

<p>Se o arquivo "pmap-final.txt" estiver vazio encontre o arquivo "pmap-<numero>.txt" com o maior número que não esteja vazio e renomeie para "pmap-final.txt"</p>

<p>A pasta "test" deve ter pelo menos um arquivo de traços. No exemplo deve ser apenas o "tracefile-trace-00000.txt.zip"</p>

<h1>Teste de simulação:</h1>

<p>Em um dos terminais abertos (você pode fechar os outros) execute o simulador na pasta de coleta de traços:</p>

<ol>
    <li>cd ~</li>
    <li>cd test</li>
    <li>~/tracetoolsimulator.sh</li>
</ol>

<p>Quando a simulação terminar você pode ver o resumo dos resultado no arquivo "stats-simulation-results.txt.csv"</p>

<p>Na pasta "doc" tem duas imagens e um video da simulação.</p>

<p>Enjoy!</p>


