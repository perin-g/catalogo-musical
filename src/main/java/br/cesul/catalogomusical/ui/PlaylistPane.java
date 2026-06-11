package br.cesul.catalogomusical.ui;

import br.cesul.catalogomusical.dao.MusicaDao;
import br.cesul.catalogomusical.dao.PlaylistDao;
import br.cesul.catalogomusical.model.Musica;
import br.cesul.catalogomusical.model.Playlist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

// Aba "Playlists" — versão piloto (tudo implementado).

// Layout: 3 colunas lado a lado.
//
//   COLUNA 1: lista de playlists
//             + nova / renomear / excluir
//   COLUNA 2: lista de músicas DA playlist selecionada
//             + label "N músicas, total mm:ss"
//             + remover da playlist
//   COLUNA 3: biblioteca (todas as músicas)
//             + adicionar à playlist

// O ponto novo deste arquivo:
//   1) ListView com setCellFactory pra exibir os itens de um jeito
//      que faça sentido pro humano (nome + qtd, titulo + artista...).
//   2) "Join manual": a Playlist guarda só os IDs, então pra mostrar
//      as músicas dela a gente chama musicaDao.findByIds(...).

public class PlaylistPane extends BorderPane implements Refreshable {

    private final PlaylistDao playlistDao = new PlaylistDao();
    private final MusicaDao   musicaDao   = new MusicaDao();

    // ObservableList: muda aqui, a UI atualiza sozinha.
    private final ObservableList<Playlist> dadosPlaylists =
            FXCollections.observableArrayList();

    private final ObservableList<Musica> dadosDaPlaylist =
            FXCollections.observableArrayList();

    private final ObservableList<Musica> dadosBiblioteca =
            FXCollections.observableArrayList();

    // Três ListView, uma pra cada coluna.
    private final ListView<Playlist> listaPlaylists  = new ListView<>(dadosPlaylists);
    private final ListView<Musica>   listaDaPlaylist = new ListView<>(dadosDaPlaylist);
    private final ListView<Musica>   listaBiblioteca = new ListView<>(dadosBiblioteca);

    // Label de info da playlist selecionada.
    private final Label lblInfo = new Label();

    public PlaylistPane(){
        setPadding(new Insets(10));

        // ========== COLUNA 1: PLAYLISTS ==========

        // A ListView precisa saber como mostrar cada item. Por padrão
        // ela usa o toString() do objeto, mas o record Playlist tem um
        // toString feio ("Playlist[id=..., nome=..., musicaIds=...]").
        // Por isso a gente define uma celula custom: pra cada item,
        // o JavaFX chama updateItem e a gente diz que texto mostrar.
        listaPlaylists.setCellFactory(lv -> new ListCell<Playlist>(){
            @Override
            protected void updateItem(Playlist p, boolean empty){
                super.updateItem(p, empty);
                if (empty || p == null) setText(null);
                else setText(p.nome() + "  (" + p.quantidade() + ")");
            }
        });

        // Quando a playlist selecionada muda, recarrega o painel do meio.
        // selectedItemProperty + addListener: mesmo padrão do MainApp.
        listaPlaylists.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, atual) -> recarregarMusicasDaPlaylist());

        TextField tNomeNova = new TextField();
        tNomeNova.setPromptText("Nova playlist");

        Button btnNovaPL = new Button("+ Nova");
        btnNovaPL.setOnAction(e -> {
            playlistDao.insert(tNomeNova.getText().trim());
            tNomeNova.clear();
            refresh();
        });

        Button btnRenomear = new Button("Renomear");
        btnRenomear.setOnAction(e -> renomearSelecionada());

        Button btnExcluir = new Button("Excluir");
        btnExcluir.setOnAction(e -> excluirSelecionada());

        VBox colPlaylists = new VBox(6,
                new Label("Playlists:"),
                listaPlaylists,
                new HBox(4, tNomeNova, btnNovaPL),
                new HBox(4, btnRenomear, btnExcluir));
        colPlaylists.setPrefWidth(230);

        // ========== COLUNA 2: MÚSICAS DA PLAYLIST ==========
        listaDaPlaylist.setCellFactory(lv -> celulaDeMusica());

        Button btnRemover = new Button("<< Remover da playlist");
        btnRemover.setOnAction(e -> removerDaPlaylist());

        lblInfo.setStyle("-fx-text-fill:#444; -fx-font-style:italic;");

        VBox colDaPlaylist = new VBox(6,
                new Label("Músicas da playlist:"),
                listaDaPlaylist,
                lblInfo,
                btnRemover);
        colDaPlaylist.setPrefWidth(260);

        // ========== COLUNA 3: BIBLIOTECA ==========
        listaBiblioteca.setCellFactory(lv -> celulaDeMusica());

        Button btnAdicionar = new Button("Adicionar à playlist >>");
        btnAdicionar.setOnAction(e -> adicionarNaPlaylist());

        VBox colBiblioteca = new VBox(6,
                new Label("Biblioteca (todas):"),
                listaBiblioteca,
                btnAdicionar);
        colBiblioteca.setPrefWidth(260);

        // ========== JUNTA AS 3 COLUNAS ==========
        HBox geral = new HBox(10, colPlaylists, colDaPlaylist, colBiblioteca);
        geral.setAlignment(Pos.TOP_LEFT);
        setCenter(geral);

        // Primeira carga de dados
        refresh();
    }

    // Cria uma celula que mostra "Titulo — Artista (m:ss)".
    // Como as duas ListViews de música precisam da mesma celula,
    // a gente extraiu em método pra não repetir código.
    private ListCell<Musica> celulaDeMusica(){
        return new ListCell<Musica>(){
            @Override
            protected void updateItem(Musica m, boolean empty){
                super.updateItem(m, empty);
                if (empty || m == null) setText(null);
                else setText(m.titulo() + " — " + m.artista()
                        + "  (" + m.duracaoFormatada() + ")");
            }
        };
    }

    // Usa o TextInputDialog (caixinha de input pronta do JavaFX).
    // showAndWait().ifPresent(...) só roda o callback se o usuário
    // confirmou (não fechou no X).
    private void renomearSelecionada(){
        Playlist sel = listaPlaylists.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        TextInputDialog dlg = new TextInputDialog(sel.nome());
        dlg.setHeaderText("Renomear playlist");
        dlg.setContentText("Novo nome:");
        dlg.showAndWait().ifPresent(novo -> {
            playlistDao.renomear(sel.id(), novo);
            refresh();
        });
    }

    private void excluirSelecionada(){
        Playlist sel = listaPlaylists.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        playlistDao.delete(sel.id());
        refresh();
    }

    // Pega a playlist selecionada (col 1) e a música selecionada
    // da biblioteca (col 3) e manda adicionar.
    private void adicionarNaPlaylist(){
        Playlist pl = listaPlaylists.getSelectionModel().getSelectedItem();
        Musica   m  = listaBiblioteca.getSelectionModel().getSelectedItem();
        if (pl == null || m == null) return;

        playlistDao.adicionarMusica(pl.id(), m.id());
        refresh();
        // depois do refresh a seleção da playlist some — a gente reseleciona
        selecionarPlaylistPorId(pl.id());
    }

    // Pega a playlist (col 1) e a música DA playlist (col 2) e tira.
    private void removerDaPlaylist(){
        Playlist pl = listaPlaylists.getSelectionModel().getSelectedItem();
        Musica   m  = listaDaPlaylist.getSelectionModel().getSelectedItem();
        if (pl == null || m == null) return;

        playlistDao.removerMusica(pl.id(), m.id());
        refresh();
        selecionarPlaylistPorId(pl.id());
    }

    // Vem da Refreshable. Recarrega TUDO do banco.
    @Override
    public void refresh(){
        dadosPlaylists.setAll(playlistDao.findAll());
        dadosBiblioteca.setAll(musicaDao.findAll());
        recarregarMusicasDaPlaylist();
    }

    // Aqui está o tal "join manual":
    // a Playlist guarda só os IDs das músicas. Pra mostrar as Musica
    // de verdade, a gente pega esses IDs e busca no MusicaDao.
    private void recarregarMusicasDaPlaylist(){
        Playlist sel = listaPlaylists.getSelectionModel().getSelectedItem();
        if (sel == null){
            dadosDaPlaylist.clear();
            lblInfo.setText("Nenhuma playlist selecionada.");
            return;
        }

        List<Musica> musicas = musicaDao.findByIds(sel.musicaIds());
        dadosDaPlaylist.setAll(musicas);

        // Duração total formatada em mm:ss
        int totalSeg = MusicaDao.somaDuracao(musicas);
        int mm = totalSeg / 60;
        int ss = totalSeg % 60;
        lblInfo.setText(musicas.size() + " música(s), duração total: "
                + String.format("%d:%02d", mm, ss));
    }

    // Depois de um refresh(), a seleção da playlist é zerada. Esse
    // método procura a playlist pelo id e reseleciona pra o usuário
    // não "perder" o contexto.
    private void selecionarPlaylistPorId(String id){
        for (Playlist p : dadosPlaylists){
            if (p.id().equals(id)){
                listaPlaylists.getSelectionModel().select(p);
                return;
            }
        }
    }
}
