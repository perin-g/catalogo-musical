package br.cesul.catalogomusical.ui;

import br.cesul.catalogomusical.dao.MusicaDao;
import br.cesul.catalogomusical.model.Genero;
import br.cesul.catalogomusical.model.Musica;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

// Aba "Músicas".
// Listar e adicionar já vem pronto, igual a aba de novo jogador do quiz.
// Faltam editar e excluir (estão como // TODO).

// Esta classe herda de BorderPane (um layout do JavaFX que tem 5 regiões:
// top, bottom, left, right, center). A gente coloca o formulário no
// topo e a tabela no centro.

public class MusicaPane extends BorderPane implements Refreshable {

    // DAO da camada de banco
    private final MusicaDao dao = new MusicaDao();

    // Lista observable que alimenta a tabela.
    // Toda vez que a gente chama data.setAll(...) a tabela atualiza sozinha.
    private final ObservableList<Musica> data = FXCollections.observableArrayList();
    private final TableView<Musica> table = new TableView<>(data);

    // Campos do formulário (declarados aqui em cima porque vários métodos
    // mexem com eles: adicionar, editar, limpar...).
    private final TextField tTitulo  = txt("Título");
    private final TextField tArtista = txt("Artista");
    private final TextField tAlbum   = txt("Álbum");
    private final TextField tDuracao = txt("Duração (segundos)");
    private final ComboBox<Genero> cGenero = new ComboBox<>(
            FXCollections.observableArrayList(Genero.values()));

    public MusicaPane(){
        setPadding(new Insets(10));
        cGenero.getSelectionModel().selectFirst();

        // ===== TABELA (centro) =====
        // O TableView precisa saber, para cada coluna, "que texto eu mostro
        // pra cada linha?". Quem responde isso é a setCellValueFactory.
        //
        // O JavaFX espera receber uma "Property" (sla SimpleStringProperty,
        // SimpleIntegerProperty, etc). Pra colunas simples, a gente cria
        // um SimpleStringProperty na hora com o texto que quer mostrar.
        //
        // Pra cada linha (cell.getValue() é a Musica daquela linha) a gente
        // pega o campo certo e devolve dentro de um SimpleStringProperty.

        table.setPlaceholder(new Label("Nenhuma música cadastrada"));

        TableColumn<Musica, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setMinWidth(180);
        colTitulo.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().titulo()));

        TableColumn<Musica, String> colArtista = new TableColumn<>("Artista");
        colArtista.setMinWidth(140);
        colArtista.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().artista()));

        TableColumn<Musica, String> colAlbum = new TableColumn<>("Álbum");
        colAlbum.setMinWidth(140);
        colAlbum.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().album()));

        TableColumn<Musica, String> colGenero = new TableColumn<>("Gênero");
        colGenero.setMinWidth(90);
        colGenero.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().genero().rotulo()));

        TableColumn<Musica, String> colDuracao = new TableColumn<>("Duração");
        colDuracao.setMinWidth(80);
        colDuracao.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().duracaoFormatada()));

        table.getColumns().add(colTitulo);
        table.getColumns().add(colArtista);
        table.getColumns().add(colAlbum);
        table.getColumns().add(colGenero);
        table.getColumns().add(colDuracao);

        setCenter(table);

        // ===== FORMULÁRIO (topo) =====
        Button btnAdd = new Button("Adicionar");
        btnAdd.setOnAction(e -> adicionar());

        Button btnLimpar = new Button("Limpar");
        btnLimpar.setOnAction(e -> limpar());

        // GridPane organiza em linha x coluna, tipo uma planilha.
        GridPane form = new GridPane();
        form.setHgap(6);
        form.setVgap(6);
        form.addRow(0, new Label("Título:"),   tTitulo);
        form.addRow(1, new Label("Artista:"),  tArtista);
        form.addRow(2, new Label("Álbum:"),    tAlbum);
        form.addRow(3, new Label("Duração:"),  tDuracao, new Label("Gênero:"), cGenero);
        form.addRow(4, btnAdd, btnLimpar);
        setTop(form);

        // ===== BOTÕES DE AÇÃO (rodapé) =====
        Button btnEditar  = new Button("Editar selecionada");
        btnEditar.setOnAction(e -> editarSelecionada());     // TODO

        Button btnExcluir = new Button("Excluir selecionada");
        btnExcluir.setOnAction(e -> excluirSelecionada());   // TODO

        setBottom(new ToolBar(btnEditar, btnExcluir));

        // Carga inicial dos dados na tabela
        refresh();
    }

    // Lê o formulário e manda inserir.
    private void adicionar(){
        String titulo = tTitulo.getText().trim();
        if (titulo.isBlank()) return;

        int duracao;
        try { duracao = Integer.parseInt(tDuracao.getText().trim()); }
        catch (NumberFormatException ex) { duracao = 0; }

        dao.insert(titulo, tArtista.getText(), tAlbum.getText(),
                   duracao, cGenero.getValue());
        limpar();
        refresh();
    }

    // TODO - editar a música selecionada na tabela
    // Passos:
    // 1 - pegar a música selecionada na tabela:
    //         Musica sel = table.getSelectionModel().getSelectedItem();
    //     Se sel for null, sai do método (nada selecionado).
    // 2 - ler os campos do formulário (igual ao adicionar()).
    // 3 - chamar dao.update(sel.id(), titulo, artista, album, duracao, genero).
    // 4 - chamar limpar() e refresh().
    private void editarSelecionada(){
        // TODO
    }

    // TODO - excluir a música selecionada na tabela
    // Passos:
    // 1 - pegar a selecionada (table.getSelectionModel().getSelectedItem()).
    // 2 - se for null, sai.
    // 3 - dao.delete(sel.id()).
    // 4 - refresh().
    private void excluirSelecionada(){
        // TODO
    }

    private void limpar(){
        tTitulo.clear();
        tArtista.clear();
        tAlbum.clear();
        tDuracao.clear();
        cGenero.getSelectionModel().selectFirst();
    }

    // Vem da interface Refreshable. Chamado pelo MainApp quando a aba
    // ganha foco, e por nós mesmos depois de cada alteração.
    @Override
    public void refresh(){
        data.setAll(dao.findAll());
    }

    // Helper bobo só pra não repetir "new TextField + setPromptText".
    private TextField txt(String placeholder){
        TextField t = new TextField();
        t.setPromptText(placeholder);
        return t;
    }
}
