package br.cesul.catalogomusical.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// Aba "Playlists" — esta aba é o maior pedaço do trabalho.
// O que ela tem que fazer:
//
//   COLUNA 1: lista das playlists existentes
//             + campo de texto pra nova playlist + botão "+ Nova"
//             + botões "Renomear" e "Excluir" pra playlist selecionada
//
//   COLUNA 2: lista das músicas da playlist selecionada
//             + label "N músicas, duração total: mm:ss"
//             + botão "<< Remover da playlist"
//
//   COLUNA 3: lista de TODAS as músicas (biblioteca)
//             + botão "Adicionar à playlist >>"

// Dicas gerais (a gente nunca viu ListView no quiz, então segue
// um mini guia):
//
// 1) ListView<X> é parecido com TableView mas só tem uma "coluna".
//    Em vez de cell value factory, a gente usa setCellFactory pra
//    dizer o texto que cada item vai mostrar:
//
//        listaPlaylists.setCellFactory(lv -> new ListCell<Playlist>(){
//            @Override
//            protected void updateItem(Playlist p, boolean empty){
//                super.updateItem(p, empty);
//                if (empty || p == null) setText(null);
//                else setText(p.nome() + "  (" + p.quantidade() + ")");
//            }
//        });
//
//    Por dentro, isso é a mesma ideia do cellValueFactory: pra cada
//    linha, o JavaFX pergunta "que texto vai aqui?".
//
// 2) Pra detectar quando o usuário muda de playlist selecionada,
//    usa o mesmo selectedItemProperty().addListener() do MainApp:
//
//        listaPlaylists.getSelectionModel().selectedItemProperty()
//            .addListener((obs, anterior, atual) -> recarregarMusicasDaPlaylist());
//
// 3) Pra mostrar as músicas da playlist selecionada, você faz o
//    "join manual": pega os IDs (sel.musicaIds()) e chama
//    musicaDao.findByIds(...). É como se fosse um JOIN do banco
//    relacional só que feito na mão em duas idas ao mongo.
//
// 4) Pra somar a duração total, chama MusicaDao.somaDuracao(...)
//    que já está pronto e te devolve em segundos. Aí divide por 60
//    pra ter os minutos e usa %02d pra formatar os segundos.
//
// 5) Pra organizar 3 colunas lado a lado, use um HBox no centro e
//    cada coluna é um VBox.
//
// 6) Pra renomear playlist, o JavaFX tem um diálogo pronto chamado
//    TextInputDialog:
//
//        TextInputDialog dlg = new TextInputDialog(sel.nome());
//        dlg.setHeaderText("Renomear playlist");
//        dlg.setContentText("Novo nome:");
//        dlg.showAndWait().ifPresent(novo -> {
//            playlistDao.renomear(sel.id(), novo);
//            refresh();
//        });
//
// 7) Lembra de implementar refresh() pra recarregar do banco —
//    é o método da interface Refreshable. O MainApp chama ele
//    automaticamente quando essa aba ganha foco.

public class PlaylistPane extends BorderPane implements Refreshable {

    public PlaylistPane(){
        setPadding(new Insets(10));
        setCenter(new VBox(new Label("TODO: monta a aba aqui (3 colunas)")));
    }

    @Override
    public void refresh(){
        // TODO: depois de montar, recarrega tudo do banco
    }
}
