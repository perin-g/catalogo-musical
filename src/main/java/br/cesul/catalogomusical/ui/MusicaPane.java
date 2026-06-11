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

// Aba "Músicas" — CRUD completo (cria, lê, edita, apaga).
// Versão piloto: tudo já implementado, inclusive editar e excluir.

// Layout:
//   - topo:    formulário (título, artista, album, duração, gênero)
//              + botões "Adicionar/Salvar" e "Limpar"
//   - centro:  tabela de músicas (TableView)
//   - rodapé:  botões "Editar selecionada" e "Excluir selecionada"

public class MusicaPane extends BorderPane implements Refreshable {

    private final MusicaDao dao = new MusicaDao();

    // Lista observable que alimenta a tabela.
    // Toda vez que a gente chama data.setAll(...) a tabela atualiza sozinha.
    private final ObservableList<Musica> data = FXCollections.observableArrayList();
    private final TableView<Musica> table = new TableView<>(data);

    private final TextField tTitulo  = txt("Título");
    private final TextField tArtista = txt("Artista");
    private final TextField tAlbum   = txt("Álbum");
    private final TextField tDuracao = txt("Duração (segundos)");
    private final ComboBox<Genero> cGenero = new ComboBox<>(
            FXCollections.observableArrayList(Genero.values()));

    // Guarda a música que está em edição.
    // Se for null, o botão "Adicionar" cria nova.
    // Se não for null, o mesmo botão atualiza essa música.
    private Musica emEdicao;

    // Referência ao botão pra trocar o texto entre "Adicionar" e "Salvar"
    private final Button btnSalvar = new Button("Adicionar");

    public MusicaPane(){
        setPadding(new Insets(10));
        cGenero.getSelectionModel().selectFirst();

        // ===== TABELA (centro) =====
        // O TableView precisa saber, para cada coluna, "que texto eu mostro
        // pra cada linha?". Quem responde isso é a setCellValueFactory.
        //
        // O JavaFX espera receber uma "Property" (SimpleStringProperty,
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
        btnSalvar.setOnAction(e -> salvar());

        Button btnLimpar = new Button("Limpar");
        btnLimpar.setOnAction(e -> limpar());

        GridPane form = new GridPane();
        form.setHgap(6);
        form.setVgap(6);
        form.addRow(0, new Label("Título:"),   tTitulo);
        form.addRow(1, new Label("Artista:"),  tArtista);
        form.addRow(2, new Label("Álbum:"),    tAlbum);
        form.addRow(3, new Label("Duração:"),  tDuracao, new Label("Gênero:"), cGenero);
        form.addRow(4, btnSalvar, btnLimpar);
        setTop(form);

        // ===== BOTÕES DE AÇÃO (rodapé) =====
        Button btnEditar  = new Button("Editar selecionada");
        btnEditar.setOnAction(e -> carregarParaEdicao());

        Button btnExcluir = new Button("Excluir selecionada");
        btnExcluir.setOnAction(e -> excluirSelecionada());

        setBottom(new ToolBar(btnEditar, btnExcluir));

        refresh();
    }

    // O botão "Adicionar/Salvar" faz CREATE ou UPDATE dependendo se
    // estamos em modo edição ou não (emEdicao != null).
    private void salvar(){
        String titulo = tTitulo.getText().trim();
        if (titulo.isBlank()) return;

        int duracao;
        try { duracao = Integer.parseInt(tDuracao.getText().trim()); }
        catch (NumberFormatException ex) { duracao = 0; }

        if (emEdicao == null){
            // modo CREATE
            dao.insert(titulo, tArtista.getText(), tAlbum.getText(),
                       duracao, cGenero.getValue());
        } else {
            // modo UPDATE
            dao.update(emEdicao.id(), titulo, tArtista.getText(), tAlbum.getText(),
                       duracao, cGenero.getValue());
        }
        limpar();
        refresh();
    }

    // "Editar selecionada" copia os dados da linha selecionada
    // pro formulário e troca o botão pra "Salvar".
    private void carregarParaEdicao(){
        Musica sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        emEdicao = sel;
        tTitulo.setText(sel.titulo());
        tArtista.setText(sel.artista());
        tAlbum.setText(sel.album());
        tDuracao.setText(String.valueOf(sel.duracaoSegundos()));
        cGenero.setValue(sel.genero());

        btnSalvar.setText("Salvar");
    }

    private void excluirSelecionada(){
        Musica sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        dao.delete(sel.id());
        limpar();
        refresh();
    }

    // Limpa o formulário e sai do "modo edição"
    private void limpar(){
        emEdicao = null;
        tTitulo.clear();
        tArtista.clear();
        tAlbum.clear();
        tDuracao.clear();
        cGenero.getSelectionModel().selectFirst();
        btnSalvar.setText("Adicionar");
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
