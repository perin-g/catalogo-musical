package br.cesul.catalogomusical.ui;

// Ponto de entrada do app. Mesma ideia do MainApp do quiz:
// monta a janela, monta as abas, e dá show.

// O que muda em relação ao quiz é que aqui a gente quebrou cada
// aba em um arquivo próprio (MusicaPane, PlaylistPane, StatsPane).
// Isso ajuda a não ter um MainApp gigante.

// Cada aba implementa a interface Refreshable. Quando o usuário troca
// de aba, o listener aqui debaixo chama refresh() na aba que ganhou
// foco — assim os dados nunca ficam velhos.

import br.cesul.catalogomusical.util.MongoConfig;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        // Popula o banco com as músicas iniciais (so na primeira execução).
        MongoConfig.seedMusicasIfEmpty();

        // Cria as 3 abas com cada tela.
        // Cada *Pane é uma classe própria que herda de BorderPane (JavaFX).
        TabPane tabs = new TabPane(
                abaNaoFechavel("Músicas",      new MusicaPane()),
                abaNaoFechavel("Playlists",    new PlaylistPane()),
                abaNaoFechavel("Estatísticas", new StatsPane())
        );

        // Refresh automático ao trocar de aba.
        // selectedItemProperty() é o "estado da seleção" do TabPane.
        // Toda vez que esse estado muda (clique em outra aba), o
        // listener é chamado com (observable, abaAntiga, abaNova).
        //
        // Aí a gente pega o conteudo da aba nova e, se ele for um
        // Refreshable, manda recarregar. O instanceof aqui é a
        // forma classica que ja vimos no PetShop com Urgente.
        tabs.getSelectionModel().selectedItemProperty()
            .addListener((obs, antiga, nova) -> {
                if (nova == null) return;
                Node conteudo = nova.getContent();
                if (conteudo instanceof Refreshable) {
                    ((Refreshable) conteudo).refresh();
                }
            });

        Scene cena = new Scene(tabs, 820, 520);
        stage.setScene(cena);
        stage.setTitle("Catálogo Musical");
        stage.show();
    }

    // Cria uma Tab que o usuário não consegue fechar com X.
    private Tab abaNaoFechavel(String titulo, Node conteudo){
        Tab t = new Tab(titulo, conteudo);
        t.setClosable(false);
        return t;
    }

    public static void main(String[] args){
        launch(args);
    }
}
